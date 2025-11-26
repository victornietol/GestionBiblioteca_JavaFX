package org.victornieto.gestionbiblioteca.service;

import org.victornieto.gestionbiblioteca.dto.LibroInventarioDTO;
import org.victornieto.gestionbiblioteca.repository.LibroRepository;
import org.victornieto.gestionbiblioteca.repository.LibroRepositoryImp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class LibroService {

    private final LibroRepository libroRepository;
    private HashMap<String, String> columnsValueConverts = new HashMap<>();

    public LibroService() {
        this.libroRepository = new LibroRepositoryImp();
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

    private void loadValuesColumns() {
        columnsValueConverts.put("ID", "id_libro");
        columnsValueConverts.put("Titulo", "titulo");
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
