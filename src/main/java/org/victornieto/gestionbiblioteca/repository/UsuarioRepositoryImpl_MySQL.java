package org.victornieto.gestionbiblioteca.repository;

import org.victornieto.gestionbiblioteca.database.ConnectionDB;
import org.victornieto.gestionbiblioteca.database.ConnectionDBImpl_MySQL;
import org.victornieto.gestionbiblioteca.model.UsuarioModel;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class UsuarioRepositoryImpl_MySQL implements UsuarioRepository{

    public UsuarioRepositoryImpl_MySQL() {
    }

    /**
     * Se crea una instancia para poder obtener una conexion
     */

//    private Connection conn;
//
//    public UsuarioRepositoryImpl_MySQL() {
//        /**
//         * Obtener una conexión a la base de datos.
//         */
//        try {
//            this.conn = ConnectionDBImpl_MySQL.getInstance().getConection();
//        } catch (SQLException e) {
//            System.out.println("Error al obtener la conexión a la DB.");
//        }
//    }



    @Override
    public List<UsuarioModel> getAll() {
        return List.of();
    }

    @Override
    public UsuarioModel getById(Integer id) {
        return null;
    }

    @Override
    public UsuarioModel getByUsername(String username) throws SQLException {
        String query = "SELECT * FROM usuario WHERE username = ?";
        UsuarioModel newUser;
        Connection conn = ConnectionDBImpl_MySQL.getInstance().getConection();

        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet resultSet = stmt.executeQuery();

            newUser = transformResultToUser(resultSet);

            resultSet.close();
            stmt.close();

        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {
            conn.close();
        }

        return newUser;
    }

    @Override
    public String getPasswordByUsername(String username) throws SQLException {
        /**
         * Funcion para obtener el valor hash de una contraseña de un usuario según su username
         * @param un string correspondiente al username
         * @return un string con el valor hash
         */
        String query = "SELECT passw FROM usuario WHERE username = ?";
        String password = null;
        Connection conn = ConnectionDBImpl_MySQL.getInstance().getConection();

        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                password = resultSet.getString("passw");
            }

            resultSet.close();
            stmt.close();

        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {
            conn.close();
        }

        return password;
    }

    @Override
    public UsuarioModel save(UsuarioModel user) throws SQLException {
        String query = "INSERT INTO usuario (" +
                "username, passw, nombre, apellido_p, apellido_m, correo, telefono, activo, fk_tipo_usuario" +
                ") values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        UsuarioModel usuarioModel;
        Connection conn = ConnectionDBImpl_MySQL.getInstance().getConection();

        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getNombre());
            stmt.setString(4, user.getApellido_p());
            stmt.setString(5, user.getApellido_m());
            stmt.setString(6, user.getCorreo());
            stmt.setString(7, user.getTelefono());
            stmt.setInt(8, 1);
            stmt.setInt(9, user.getFk_tipo_usuario());

            int inserted = stmt.executeUpdate();

            stmt.close();

            // Recuperar elemento insertado
            usuarioModel = getByUsername(user.getUsername());

        } catch (SQLException e) {
            throw new RuntimeException(e);

        } finally {
            conn.close();
        }

        return usuarioModel;
    }

    @Override
    public UsuarioModel delete(Integer id) {
        return null;
    }

    @Override
    public UsuarioModel update(UsuarioModel user) {
        return null;
    }

    private UsuarioModel transformResultToUser(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            return new UsuarioModel.Builder()
                    .setId(resultSet.getLong("id"))
                    .setUsername(resultSet.getString("username"))
                    .setPassword(resultSet.getString("passw"))
                    .setNombre(resultSet.getString("nombre"))
                    .setApellidoP(resultSet.getString("apellido_p"))
                    .setApellidoM(resultSet.getString("apellido_m"))
                    .setCorreo(resultSet.getString("correo"))
                    .setTelefono(resultSet.getString("telefono"))
                    .setActivo(resultSet.getInt("activo"))
                    .setFK_TipoUsuario(resultSet.getInt("fk_tipo_usuario"))
                    .build();
        }

        return null;
    }
}
