package org.victornieto.gestionbiblioteca.service;

import org.victornieto.gestionbiblioteca.dto.AutorFormDTO;
import org.victornieto.gestionbiblioteca.model.AutorModel;
import org.victornieto.gestionbiblioteca.repository.AutorRepository;
import org.victornieto.gestionbiblioteca.repository.AutorRepositoryImpl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class AutorService {

    private final AutorRepositoryImpl autorRepository;
    private final Integer MAX_LENGTH_NOMBRE = 50;
    private final Integer MAX_LENGTH_APELLIDOS = 50;

    public AutorService() {
        autorRepository = new AutorRepositoryImpl();
    }

    public List<AutorModel> getAllModels() {
        List<AutorModel> list = new ArrayList<>();

        try {
            list = autorRepository.getAll();
        } catch (SQLException e) {
            System.out.println("Error al cargar autores en AutorService: " + Arrays.toString(e.getStackTrace()));;
        }

        return list;
    }

    public Optional<AutorModel> getModelById(Integer id) {
        try {
          return autorRepository.getById(id);
        } catch (SQLException e) {
            System.out.println("Error al cargar autor por id en AutorService: " + Arrays.toString(e.getStackTrace()));;
        }
        return Optional.empty();
    }

    public Optional<AutorModel> getModelByNombreAndApellidos(String nombre, String apellidoP, String apellidoM) {
        try {
            return autorRepository.getByNameComplete(nombre, apellidoP, apellidoM);
        } catch (SQLException e) {
            System.out.println("Error al cargar autor por nombre completo en AutorService: " + Arrays.toString(e.getStackTrace()));;
        }
        return Optional.empty();
    }

    public Optional<AutorModel> getModelByNombreCompleto(String nombreCompleto) {
        try {
            return autorRepository.getByNameComplete(nombreCompleto);
        } catch (SQLException e) {
            System.out.println("Error al cargar autor por nombre completo en AutorService: " + Arrays.toString(e.getStackTrace()));;
        }
        return Optional.empty();
    }

    public Optional<AutorModel> create(AutorFormDTO autor) throws IllegalArgumentException {
        String apellidoM;
        if (autor.apellidoM().isEmpty()) {
            apellidoM = null;
        } else {
            apellidoM = autor.apellidoM().toLowerCase();
        }


        if (autor.nombre()==null || autor.nombre().length()>MAX_LENGTH_NOMBRE || autor.nombre().isEmpty()) {
            throw new IllegalArgumentException("Campo nombre no valido");
        }

        if (autor.apellidoP()==null || autor.apellidoP().length()>MAX_LENGTH_APELLIDOS || autor.apellidoP().isEmpty()) {
            throw new IllegalArgumentException("Campo apellido paterno no valido");
        }

        if (apellidoM!=null && apellidoM.length()>MAX_LENGTH_APELLIDOS) {
            throw new IllegalArgumentException("Campo apellido materno no valido");
        }

        AutorFormDTO newAutor = new AutorFormDTO(
                autor.nombre().toLowerCase(),
                autor.apellidoP().toLowerCase(),
                apellidoM
        );

        try {
            return autorRepository.save(newAutor);
        } catch (SQLException e) {
            System.out.println("Error al crear autor por id en WindowAddController: " + Arrays.toString(e.getStackTrace()));;
            throw new RuntimeException(e.getMessage());
        }
    }

    public Boolean delete(Long id) {
        try {
            return autorRepository.delete(id);
        } catch (SQLException e) {
            System.out.println("Error al eliminar autor por id en WindowAddController: " + Arrays.toString(e.getStackTrace()));;
        }
        return false;
    }
}
