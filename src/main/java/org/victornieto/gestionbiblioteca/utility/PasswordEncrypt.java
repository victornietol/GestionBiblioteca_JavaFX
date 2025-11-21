package org.victornieto.gestionbiblioteca.utility;

public interface PasswordEncrypt {

    String generateHash(String password);
    boolean verifyPassword(String hash, String password);
}
