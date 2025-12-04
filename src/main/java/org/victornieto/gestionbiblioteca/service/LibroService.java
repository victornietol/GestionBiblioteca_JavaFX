package org.victornieto.gestionbiblioteca.service;

import org.victornieto.gestionbiblioteca.dto.*;
import org.victornieto.gestionbiblioteca.model.AutorModel;
import org.victornieto.gestionbiblioteca.model.CategoriaModel;
import org.victornieto.gestionbiblioteca.model.EditorialModel;
import org.victornieto.gestionbiblioteca.model.LibroModel;
import org.victornieto.gestionbiblioteca.repository.LibroRepository;
import org.victornieto.gestionbiblioteca.repository.LibroRepositoryImp;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;

public class LibroService {

    private final LibroRepository libroRepository;
    private final EditorialService editorialService;
    private final AutorService autorService;
    private final CategoriaService categoriaService;
    private HashMap<String, String> columnsValueConverts = new HashMap<>();

    public LibroService() {
        this.libroRepository = new LibroRepositoryImp();
        this.editorialService = new EditorialService();
        this.autorService = new AutorService();
        this.categoriaService = new CategoriaService();

        loadValuesColumns();
    }

    public List<LibroInventarioDTO> getLibrosForInventario(
            boolean showTitles,
            String columnToSearch,
            String coincidenceToSearch,
            String orderByColumn,
            boolean orderDesc
    ) {
        /**
         * @return una lista con los resultados de la busqueda en la base de datos, en caso de que la base no regrese resultados entonces se regresa una lista vacia
         */
        List<LibroInventarioDTO> books = new ArrayList<>();

        if (coincidenceToSearch==null || coincidenceToSearch.isEmpty()) {
            columnToSearch = "Todos";
        }

        // Ajustar nombres de las categorias de acuerdo a las columnas de la BD
        columnToSearch = columnsValueConverts.get(columnToSearch);
        orderByColumn = columnsValueConverts.get(orderByColumn);

        try {
            books = libroRepository.getBooksInfoComplete(showTitles, columnToSearch, coincidenceToSearch, orderByColumn, orderDesc);
        } catch (Exception e) {
            System.out.println("Error: "+ Arrays.toString(e.getStackTrace()));
        }

        return books;
    }

    public Optional<LibroModel> createNewTitle(
            LibroFormDTO libroFormDTO, List<String> categorias, List<String> autores, String editorial, Integer unidades) {
        /**
         * Crea nuevo título de libro y se insertan las unidades indicadas
         */
         EditorialModel infoEditorial;
         LibroModel libroModel;
         boolean relateLibroAutor = false;
         boolean relateLibroCategoria = false;

        // validaciones
        if (unidades>500 || unidades<1) {
            throw new IllegalArgumentException("Cantidad de unidades por libro invalida.");
        }

        // obtener entidades para los id's de editorial, autores, y categorias
        try {
            Optional<EditorialModel> optEditorial = getInfoEditorialByNombre(editorial);
            if(optEditorial.isPresent()) {
                infoEditorial = optEditorial.get();
            } else {
                throw new Exception("Error con el campo editorial");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        List<AutorModel> autorModels = getAutoresByNombre(autores);
        if (autorModels.isEmpty()) {
            throw new RuntimeException("Error con el campo autores.");
        }

        List<CategoriaModel> categoriaModels = getCategoriasByNombre(categorias);
        if (categoriaModels.isEmpty()) {
            throw new RuntimeException("Error con el campo categorias");
        }

        // introducir nuevo título en tabla libro
        LibroDTO libroDTO = new LibroDTO(
                libroFormDTO.titulo(),
                libroFormDTO.anio_publicacion(),
                libroFormDTO.paginas(),
                libroFormDTO.edicion(),
                infoEditorial.getId()
        );
        try {
            Optional<LibroModel> newLibroOpt = create(libroDTO);
            if(newLibroOpt.isEmpty()) {
                throw new RuntimeException("Error al crear nuevo libro.");
            } else {
                libroModel = newLibroOpt.get();
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }


        // realiza relaciones entre libro con las tablas autores, categorias (en su respectiva tabla muchos a muchos)
        try {
            relateLibroAutor = generateRelationsLibroAutor(autorModels, libroModel);
            relateLibroCategoria = generateRelationsLibroCategoria(categoriaModels, libroModel);
            if (!relateLibroAutor || !relateLibroCategoria) {
                throw new Exception("Error al generar relaciones entre libro-autores y, libro-categorias ");
            }
        } catch (Exception e) {
            // Eliminar relaciones generadas y el libro en caso de fallar alguna operacion al relacionar tablas
            // Primero se eliminan las relaciones y después el libro (título introducido)
            try {
                deleteRelationsAndLibro(libroModel, autorModels, categoriaModels, relateLibroAutor, relateLibroCategoria);
            } catch (RuntimeException ex) {
                System.out.println("Error: " + Arrays.toString(ex.getStackTrace()));
                throw new RuntimeException("Error al creal nuevo libro.");
            }
            System.out.println("Error: " + Arrays.toString(e.getStackTrace()));
            throw new RuntimeException("Error al creal nuevo libro.");
        }

        // ingresar el numero de unidades indicado
        boolean inserted;
        try {
            inserted = libroRepository.addUnits(libroModel.getId(), unidades);
        } catch (Exception e) {
            System.out.println("Error: " + Arrays.toString(e.getStackTrace()));
            throw new RuntimeException("Error al creal nuevo libro.");
        }

        if (inserted) {
            return Optional.of(libroModel);
        } else {
            return Optional.empty();
        }
    }

    public Boolean addUnits(Long id_libro, Integer units) {
        try {
            boolean inserted = libroRepository.addUnits(id_libro, units);
            if (inserted) {
                return true;
            } else {
                throw new RuntimeException("Error al agregar nuevo ejemplar");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Boolean deleteById(Long id) {
        try {
            return libroRepository.delete(id);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Boolean removeEjemplarLibro(Long id_ejemplar) {
        try {
            if(libroRepository.removeEjemplarLibro(id_ejemplar)) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Optional<LibroModel> create(LibroDTO libro) throws IllegalArgumentException {
        /**
         * Guarda y crea la information de la entidad libro solamente
         */
        Optional<EditorialModel> infoEditorial;

        if (libro.titulo().length() > 200 || libro.titulo().isEmpty()) {
            throw new IllegalArgumentException("Titulo invalido. Es demasiado largo o el campo esta vacio");
        }

        if (libro.anio_publicacion()>9999 || libro.anio_publicacion()<1) {
            throw new IllegalArgumentException("Año invalido.");
        }

        if (libro.paginas()<1) {
            throw new IllegalArgumentException("Número de páginas incorrectas.");
        }

        if (libro.edicion().length() > 100 || libro.edicion().isEmpty()) {
            throw new IllegalArgumentException("Edición invalida. El nombre es demasiada larga o el campo esta vacio");
        }

        try {
            return libroRepository.save(libro);
        } catch (SQLException e) {
            System.out.println("Error al crear libro en LibroService: " + Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(e.getMessage());
        }
    }

    private List<AutorModel> getAutoresByNombre(List<String> autores) {
        List<AutorModel> list = new ArrayList<>();
        Optional<AutorModel> autor;

        for (String a: autores) {
            try {
                autor = autorService.getModelByNombreCompleto(a);
                autor.ifPresent(list::add);

            } catch (Exception e) {
                System.out.println("Error al obtener autor libro en LibroService: " + Arrays.toString(e.getStackTrace()));
                throw new RuntimeException(e.getMessage());
            }
        }

        return list;
    }

    private List<CategoriaModel> getCategoriasByNombre(List<String> categorias) {
        List<CategoriaModel> list = new ArrayList<>();
        Optional<CategoriaModel> categoria;

        for (String c: categorias) {
            try {
                categoria = categoriaService.getByNombre(c);
                categoria.ifPresent(list::add);

            } catch (Exception e) {
                System.out.println("Error al obtener categoria libro en LibroService: " + Arrays.toString(e.getStackTrace()));
                throw new RuntimeException(e.getMessage());
            }
        }

        return list;
    }

    private Optional<EditorialModel> getInfoEditorialByNombre(String editorial) throws IllegalArgumentException {
        try {
             Optional<EditorialModel> infoEditorial = editorialService.getByNombre(editorial);
            if (infoEditorial.isEmpty()) {
                throw new IllegalArgumentException("Editorial invalida.");
            } else {
                return infoEditorial;
            }

        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(ex.getMessage());

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Boolean generateRelationsLibroAutor(List<AutorModel> autores, LibroModel libro) {
        try {
            for (AutorModel a: autores) {
                if (!relateLibroAutor(libro.getId(), a.getId())) {
                    throw new RuntimeException("Error al generar relaciones entre libro y autor");
                }
            }
            return true;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Boolean generateRelationsLibroCategoria(List<CategoriaModel> categorias, LibroModel libro) {
        try {
            for (CategoriaModel c: categorias) {
                if (!relateLibroCategoria(libro.getId(), c.getId())) {
                    throw new RuntimeException("Error al generar relaciones entre libro y categoria");
                }
            }
            return true;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Boolean relateLibroAutor(Long id_libro, Long id_autor) {
        /**
         * Establecer relacion muchos a muchos entre libro y autor
         */
        try {
            return libroRepository.relateLibroAutor(id_libro, id_autor);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Boolean relateLibroCategoria(Long id_libro, Long id_categoria) {
        /**
         * Establecer relacion muchos a muchos entre libro y categoria
         */
        try {
            return libroRepository.relateLibroCategoria(id_libro, id_categoria);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Boolean deleteRelationsLibroAutor(Long id_libro, List<AutorModel> autores) {
        /**
         * Eliminar las relaciones muchos a muchos entre un libro y autores
         */
        for (AutorModel a : autores) {
            try {
                if (!libroRepository.deleteRelationLibroAutor(id_libro, a.getId())) {
                    throw new SQLException("No se realizó la eliminación de la relacion libro-autor");
                }
            } catch (SQLException e){
                System.out.println("Error: " + e.getMessage());
            }
        }
        return true;
    }

    private Boolean deleteRelationsLibroCategorias(Long id_libro, List<CategoriaModel> categorias) {
        /**
         * Eliminar las relaciones muchos a muchos entre un libro y categorias
         */
        for (CategoriaModel c: categorias) {
            try {
                if (!libroRepository.deleteRelationLibroCategoria(id_libro, c.getId())) {
                    throw new SQLException("No se realizó la eliminación de la relacion libro-categoria");
                }
            } catch (SQLException e){
                System.out.println("Error: " + e.getMessage());
            }
        }
        return true;
    }

    private void deleteRelationsAndLibro(
            LibroModel libroModel,
            List<AutorModel> autorModels,
            List<CategoriaModel> categoriaModels,
            boolean relateLibroAutor,
            boolean relateLibroCategoria) {
        /**
         * Función para elminar los cambios en relaciones y libro generados
         */
        try {
            if (!relateLibroAutor) {
                deleteRelationsLibroAutor(libroModel.getId(), autorModels);
            }
            if  (!relateLibroCategoria) {
                deleteRelationsLibroCategorias(libroModel.getId(), categoriaModels);
            }
            libroRepository.delete(libroModel.getId());
        } catch (Exception ex) {
            System.out.println("Error: " + Arrays.toString(ex.getStackTrace()));
            throw new RuntimeException("Error al eliminar nuevo libro o relaciones.");
        }

    }

    private void loadValuesColumns() {
        columnsValueConverts.put("ID", "id_libro");
        columnsValueConverts.put("Título", "titulo");
        columnsValueConverts.put("Autor", "autor");
        columnsValueConverts.put("Categoria", "categoria");
        columnsValueConverts.put("Editorial", "editorial");
        columnsValueConverts.put("Edición", "edicion");
        columnsValueConverts.put("Año", "anio_publicacion");
        columnsValueConverts.put("Páginas", "paginas");
        columnsValueConverts.put("Unidades", "unidades");
        columnsValueConverts.put("Todos", "todos");
    }
}
