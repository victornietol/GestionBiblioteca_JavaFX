package org.victornieto.gestionbiblioteca.utility;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class PasswordEncryptArgon2 implements PasswordEncrypt{

    @Override
    public String generateHash(String password) {

        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

        try {
            return argon2.hash(
                    2, // iteraciones
                    65536, // memoria (64 MB)
                    1, // parallelism
                    password
            );
        } finally {
            argon2.wipeArray(password.toCharArray());
        }
    }

    @Override
    public boolean verifyPassword(String hash, String password) {
        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        return argon2.verify(hash, password);
    }
}
