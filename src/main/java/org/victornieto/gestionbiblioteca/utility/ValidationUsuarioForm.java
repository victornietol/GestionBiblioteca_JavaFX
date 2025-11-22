package org.victornieto.gestionbiblioteca.utility;

import javax.crypto.spec.PSource;

public class ValidationUsuarioForm implements ValidateForm {
    /**
     * Clase para validar las entradas de TextField.
     * Si la entrada es válida entonces se regresa true, de lo contrario false.
     */

    private final Integer LENGTH_USERNAME = 12;
    private final Integer LENGTH_PASSW_MAX = 12;
    private final Integer LENGTH_PASSW_MIN = 8;
    private final Integer LENGTH_NOMBRE = 50;
    private final Integer LENGTH_APELLIDOS = 50;
    private final Integer LENGTH_CORREO = 50;
    private final Integer LENGTH_TELEFONO = 10;


    @Override
    public boolean validateUsername(String username) {
        if (username.matches(".*\\s.*")) {
            return false;
        }
        if (username.length()>LENGTH_USERNAME || username.isEmpty()) {
            return false;
        }
        if (!username.matches("[A-Za-z0-9_.-]+")){
            return false;
        }
        return true;
    }

    @Override
    public boolean validatePassword(String password) {
        if (password.matches(".*\\s.*")) {
            return false;
        }
        if (password.length()<LENGTH_PASSW_MIN || password.length()>LENGTH_PASSW_MAX) {
            return false;
        }
        if (!password.matches("[A-Za-z0-9_.#()*+/-]+")){
            return false;
        }
        return true;
    }

    @Override
    public boolean validateNombre(String nombre) {
        if (!nombre.matches("^[a-záéíóúñ]+( [a-záéíóúñ]+)*$")) {
            return false;
        }
        if (nombre.length()>LENGTH_NOMBRE || nombre.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean validateApellidoP(String apellidoP) {
        if (!apellidoP.matches("^[a-záéíóúñ]+( [a-záéíóúñ]+)*$")) {
            return false;
        }
        if (apellidoP.length()>LENGTH_APELLIDOS || apellidoP.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean validateApellidoM(String apellidoM) {
        if (!apellidoM.matches("^[a-záéíóúñ]+( [a-záéíóúñ]+)*$")) {
            return false;
        }
        if (apellidoM.length()>LENGTH_APELLIDOS || apellidoM.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean validateCorreo(String correo) {
        long count = correo.chars().filter(c -> c == '@').count();

        if (correo.matches(".*\\s.*")) {
            return false;
        }
        if (correo.length()>LENGTH_CORREO) {
            return false;
        }
        if (!correo.matches("[A-Za-z0-9_@.-]+")){
            return false;
        }
        if (!correo.matches(".+@.+\\..+")) {
            return false;
        }
        if (count!=1) {
            return false;
        }
        return true;
    }

    @Override
    public boolean validateTelefono(String telefono) {
        if (telefono.matches(".*\\s.*")) {
            return false;
        }
        if (telefono.length()>LENGTH_TELEFONO) {
            return false;
        }
        if (!telefono.matches("[0-9]+")){
            return false;
        }
        return true;
    }
}
