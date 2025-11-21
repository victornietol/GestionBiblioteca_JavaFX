package org.victornieto.gestionbiblioteca.service;

import org.victornieto.gestionbiblioteca.dto.UsuarioFormDTO;
import org.victornieto.gestionbiblioteca.model.UsuarioModel;
import org.victornieto.gestionbiblioteca.repository.UsuarioRepository;
import org.victornieto.gestionbiblioteca.repository.UsuarioRepositoryImpl;
import org.victornieto.gestionbiblioteca.utility.PasswordEncrypt;
import org.victornieto.gestionbiblioteca.utility.PasswordEncryptArgon2;
import org.victornieto.gestionbiblioteca.utility.ValidateForm;
import org.victornieto.gestionbiblioteca.utility.ValidationUsuarioForm;

import java.util.HashMap;

public class UsuarioService {

    private final ValidateForm validateForm;
    private final PasswordEncrypt encrypt;
    private final UsuarioRepository userRepository;

    public UsuarioService(ValidationUsuarioForm validateForm, PasswordEncryptArgon2 encrypt, UsuarioRepositoryImpl userRepository) {
        this.validateForm = validateForm;
        this.encrypt = encrypt;
        this.userRepository = userRepository;
    }

    public UsuarioService() {
        this.validateForm = new ValidationUsuarioForm();
        this.encrypt = new PasswordEncryptArgon2();
        this.userRepository = new UsuarioRepositoryImpl();
    }

    public HashMap<String, Object> createUsuario(UsuarioFormDTO userFormDTO) {
        /**
         * Crea un UsuarioModel y devuelve como respuesta un HashMap con las keys y values:
         * "created": boolean
         * "user": UsuarioModel
         * "error": String   *En caso de no haber error se asignará null
         */
        HashMap<String, Object> result = new HashMap<>();

        // Validar datos del formulario
        HashMap<String, Object> validation = validateData(userFormDTO);
        if ((boolean) validation.get("valid")) {
            // Insertar en BD
            UsuarioModel newUser = new UsuarioModel.Builder()
                    .setUsername(userFormDTO.getUsername())
                    .setPassword(encrypt.generateHash(userFormDTO.getPassword1()))
                    .setNombre(userFormDTO.getNombre())
                    .setApellidoP(userFormDTO.getApellido_p())
                    .setApellidoM(userFormDTO.getApellido_p())
                    .setCorreo(userFormDTO.getCorreo())
                    .setTelefono(userFormDTO.getTelefono())
                    .setActivo(1)
                    .build();

            try {
                newUser = userRepository.save(newUser); // Devuelve el objeto insertado
                result.put("created", true);
                result.put("user", newUser);
                result.put("error", null);
            } catch (Exception e) {
                // Devolver error
                result.put("created", false);
                result.put("user", null);
                result.put("error", "Problema al tratar de insertar"); // Crear logs
            }

        } else {
            // Devolver error
            result.put("created", false);
            result.put("user", null);
            result.put("error", validation.get("error"));
        }

        return result;
    }

    private HashMap<String, Object> validateData(UsuarioFormDTO userDTO) {
        /**
         * Se validan las distintas cadenas y se regresa un HashMap con keys
         * "valid": boolean
         * "error": String  *En caso de no haber errores se asignará null
         */

        HashMap<String, Object> result = new HashMap<>();
        String error = null;
        result.put("error", error);
        result.put("valid", false);

        if (!validateForm.validateUsername(userDTO.getUsername())) {
            error = "campo usuario invalido";
        } else if (!validateForm.validatePassword(userDTO.getPassword1())) {
            error = "campo password invalido";
        } else if (userDTO.getPassword1().equals(userDTO.getPassword2())) {
            error = "las contraseñas no coinciden";
        } else if (!validateForm.validateNombre(userDTO.getNombre())) {
            error = "campo nombre invalido";
        } else if (!validateForm.validateApellidoP(userDTO.getApellido_p())) {
            error = "campo apellido paterno invalido";
        } else if (!validateForm.validateApellidoM(userDTO.getApellido_m())) {
            error = "campo apellido materno invalido";
        } else if (!validateForm.validateCorreo(userDTO.getCorreo())) {
            error = "campo correo invalido";
        } else if (!validateForm.validateTelefono(userDTO.getTelefono())) {
            error = "campo teléfono invalido";
        }

        result.put("error", error);
        result.put("valid", error == null); // Si se asigno error entonces se indica
        return result;
    }
}
