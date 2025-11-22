package org.victornieto.gestionbiblioteca.repository;

import org.victornieto.gestionbiblioteca.model.UsuarioModel;

import java.sql.SQLException;
import java.util.List;

public interface UsuarioRepository {

    List<UsuarioModel> getAll();
    UsuarioModel getById(Integer id);
    UsuarioModel getByUsername(String username) throws SQLException;
    String getPasswordByUsername(String username) throws SQLException;
    UsuarioModel save(UsuarioModel user) throws SQLException;
    UsuarioModel delete(Integer id);
    UsuarioModel update(UsuarioModel user);
}
