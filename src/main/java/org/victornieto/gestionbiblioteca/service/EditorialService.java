package org.victornieto.gestionbiblioteca.service;

import org.victornieto.gestionbiblioteca.dto.EditorialFormDTO;
import org.victornieto.gestionbiblioteca.model.EditorialModel;
import org.victornieto.gestionbiblioteca.repository.EditorialRepositoryImpl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class EditorialService {

    private EditorialRepositoryImpl editorialRepository;
    private final Integer MAX_LENGTH_NOMBRE = 70;

    public EditorialService () {
        editorialRepository = new EditorialRepositoryImpl();
    }

    public List<EditorialModel> getAll() {
        List<EditorialModel> list = new ArrayList<>();

        try {
            return editorialRepository.getAll();
        } catch (SQLException e) {
            System.out.println("Error al cargar editoriales en CategoriaService: " + Arrays.toString(e.getStackTrace()));
        }

        return list;
    }

    public Optional<EditorialModel> getById(Integer id) {
        try {
            return editorialRepository.getById(id);
        } catch (SQLException e) {
            System.out.println("Error al buscar editorial por id en CategoriaService: " + Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(e.getMessage());
        }
    }

    public Optional<EditorialModel> create(EditorialFormDTO editorial) throws IllegalArgumentException{
        if (editorial.nombre().length()>MAX_LENGTH_NOMBRE || editorial.nombre().isEmpty()) {
            throw new IllegalArgumentException("Nombre de editorial invalido");
        }

        try {
            return editorialRepository.save(editorial);
        } catch (SQLException e) {
            System.out.println("Error al crear editorial en CategoriaService: " + Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(e.getMessage());
        }
    }

    public Boolean delete(Long id) {
        try {
            return editorialRepository.delete(id);
        } catch (SQLException e) {
            System.out.println("Error al eliminar editorial en CategoriaService: " + Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(e.getMessage());
        }
    }
}
