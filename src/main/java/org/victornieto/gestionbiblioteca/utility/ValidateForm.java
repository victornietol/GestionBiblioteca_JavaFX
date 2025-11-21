package org.victornieto.gestionbiblioteca.utility;

public interface ValidateForm {

    boolean validateUsername(String username);
    boolean validatePassword(String password);
    boolean validateNombre(String nombre);
    boolean validateApellidoP(String apellidoP);
    boolean validateApellidoM(String apellidoM);
    boolean validateCorreo(String correo);
    boolean validateTelefono(String telefono);
}
