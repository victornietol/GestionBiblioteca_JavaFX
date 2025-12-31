package org.victornieto.gestionbiblioteca.service;

import org.victornieto.gestionbiblioteca.dto.CategoriaFormDTO;
import org.victornieto.gestionbiblioteca.model.CategoriaModel;
import org.victornieto.gestionbiblioteca.repository.CategoriaRepository;
import org.victornieto.gestionbiblioteca.repository.CategoriaRepositoryImpl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CategoriaService {

    private CategoriaRepository categoriaRepository;
    private final Integer MAX_LENGTH_NOMBRE = 20;

    public CategoriaService () {
        categoriaRepository = new CategoriaRepositoryImpl();
    }

    public List<CategoriaModel> getAll() {
        List<CategoriaModel> list = new ArrayList<>();

        try {
            return categoriaRepository.getAll();
        } catch (SQLException e) {
            System.out.println("Error al cargar categorias en CategoriaService: " + Arrays.toString(e.getStackTrace()));
        }

        return list;
    }

    public List<CategoriaFormDTO> getAllNombres() {
        try {
            return categoriaRepository.getNombres();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Optional<CategoriaModel> getById(Integer id) {
        try {
            return categoriaRepository.getById(id);
        } catch (SQLException e) {
            System.out.println("Error al buscar categoria por id en CategoriaService: " + Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(e.getMessage());
        }
    }

    public Optional<CategoriaModel> getByNombre(String categoria) {
        try {
            return categoriaRepository.getByNombre(categoria);
        } catch (SQLException e) {
            System.out.println("Error al buscar categoria por nombre en CategoriaService: " + Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(e.getMessage());
        }
    }

    public Optional<CategoriaModel> create(CategoriaFormDTO categoria) throws IllegalArgumentException{
        if (categoria.nombre().length()>MAX_LENGTH_NOMBRE || categoria.nombre().isEmpty()) {
            throw new IllegalArgumentException("Nombre de categoria invalido");
        }

        try {
            return categoriaRepository.save(categoria);
        } catch (SQLException e) {
            System.out.println("Error al crear categoria en CategoriaService: " + Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(e.getMessage());
        }
    }

    public Boolean delete(Long id) {
        try {
            return categoriaRepository.delete(id);
        } catch (SQLException e) {
            System.out.println("Error al eliminar categoria en CategoriaService: " + Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(e.getMessage());
        }
    }

}
