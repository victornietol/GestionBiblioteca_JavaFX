package org.victornieto.gestionbiblioteca.repository;

import org.victornieto.gestionbiblioteca.model.UsuarioModel;

import java.util.List;

public interface UsuarioRepository {

    List<UsuarioModel> getAll();
    UsuarioModel getById(Integer id);
    UsuarioModel save(UsuarioModel user);
    UsuarioModel delete(Integer id);
    UsuarioModel update(UsuarioModel user);
}
