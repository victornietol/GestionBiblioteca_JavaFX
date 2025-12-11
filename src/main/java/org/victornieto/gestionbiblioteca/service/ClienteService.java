package org.victornieto.gestionbiblioteca.service;

import org.victornieto.gestionbiblioteca.dto.ClienteFormControllerDTO;
import org.victornieto.gestionbiblioteca.dto.ClienteFormDTO;
import org.victornieto.gestionbiblioteca.dto.ClienteListDTO;
import org.victornieto.gestionbiblioteca.dto.PrestamoListDTO;
import org.victornieto.gestionbiblioteca.model.ClienteModel;
import org.victornieto.gestionbiblioteca.repository.ClienteRepository;
import org.victornieto.gestionbiblioteca.repository.ClienteRepositoryImpl;
import org.victornieto.gestionbiblioteca.utility.PasswordEncrypt;
import org.victornieto.gestionbiblioteca.utility.PasswordEncryptArgon2;
import org.victornieto.gestionbiblioteca.utility.ValidateClienteForm;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService() {
        this.clienteRepository = new ClienteRepositoryImpl();
    }

    public Optional<String> getNombreCompletoById(Long id) {
        try {
            return clienteRepository.getNombreCompletoById(id);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<ClienteListDTO> getListDTOAll(String columnToSearch, String coincidenceToSearch, String orderByColumn, boolean orderDesc) {
        /**
         * @return una lista con los resultados de la búsqueda en la base de datos, en caso de no encontrar resultados se regresa una lista vacía.
         */
        List<ClienteListDTO> clientes = new ArrayList<>();

        if (coincidenceToSearch==null || coincidenceToSearch.isEmpty()) {
            columnToSearch = "Todos";
        }

        try {
            clientes = clienteRepository.getListDTOAll(columnToSearch, coincidenceToSearch, orderByColumn, orderDesc);

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }

        return clientes;
    }

    public Optional<ClienteModel> create(ClienteFormControllerDTO clienteDTO) {
        PasswordEncrypt passwordEncrypt = new PasswordEncryptArgon2();

        try {
            validateClienteForm(clienteDTO); // Lanza Excepcion si algún campo no es valido

            return clienteRepository.save(
                    new ClienteFormDTO(
                            clienteDTO.username(),
                            passwordEncrypt.generateHash(clienteDTO.password1()),
                            clienteDTO.nombre().substring(0,1).toUpperCase()+clienteDTO.nombre().substring(1),
                            clienteDTO.apellidoP().substring(0,1).toUpperCase()+clienteDTO.apellidoP().substring(1),
                            clienteDTO.apellidoM().substring(0,1).toUpperCase()+clienteDTO.apellidoM().substring(1),
                            clienteDTO.correo()
                    )
            );

        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e.getMessage());

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Boolean remove(Long id) {
        try {
            return clienteRepository.delete(id);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void validateClienteForm(ClienteFormControllerDTO clienteDTO) throws IllegalArgumentException {
        ValidateClienteForm validateClienteForm = new ValidateClienteForm();

        if (!validateClienteForm.validateUsername(clienteDTO.username())) {
            throw new IllegalArgumentException("Username inválido.");
        }

        if (!clienteDTO.password1().equals(clienteDTO.password2())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }

        if (!validateClienteForm.validatePassword(clienteDTO.password1())) {
            throw new IllegalArgumentException("Contraseña inválida");
        }

        if (!validateClienteForm.validateNombre(clienteDTO.nombre())) {
            throw new IllegalArgumentException("Nombre inválido.");
        }

        if (!validateClienteForm.validateApellidoP(clienteDTO.apellidoP())) {
            throw new IllegalArgumentException("Apellido paterno inválido.");
        }

        if (!validateClienteForm.validateCorreo(clienteDTO.correo())) {
            throw new IllegalArgumentException("Correo inválido");
        }
    }
}
