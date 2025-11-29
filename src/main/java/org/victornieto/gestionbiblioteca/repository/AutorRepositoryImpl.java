package org.victornieto.gestionbiblioteca.repository;

import org.victornieto.gestionbiblioteca.database.ConnectionDBImpl_MySQL;
import org.victornieto.gestionbiblioteca.dto.AutorFormDTO;
import org.victornieto.gestionbiblioteca.model.AutorModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AutorRepositoryImpl implements AutorRepository {

    @Override
    public List<AutorModel> getAll() throws SQLException {
        List<AutorModel> list = new ArrayList<>();

        String query = "SELECT * FROM autor WHERE activo = 1 ORDER BY nombre ASC";

        try(Connection conn = ConnectionDBImpl_MySQL.getInstance().getConection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet resultSet = stmt.executeQuery()) {

            list = transformToList(resultSet); // Modificar lista para agregar el resultado de la consulta

        } catch (SQLException e) {
            System.out.println("Error al ejecutar la consulta en AutorRepositoryImpl");
            throw e;
        }

        return list;
    }

    @Override
    public Optional<AutorModel> getById(Integer id) throws SQLException {
        /**
         * Obtener un usuario, en caso de no encontrarlo se devuelve empty
         */
        String query = "SELECT * FROM autor WHERE id = ? AND activo = 1 ORDER BY nombre ASC";

        try(Connection conn = ConnectionDBImpl_MySQL.getInstance().getConection();
            PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            try (ResultSet resultSet = stmt.executeQuery()) {
                AutorModel autor = transformToModel(resultSet);
                return Optional.ofNullable(autor);
            }

        } catch (SQLException e) {
            System.out.println("Error al ejecutar la consulta en AutorRepositoryImpl");
            throw e;
        }
    }

    @Override
    public Optional<AutorModel> save(AutorFormDTO autor) throws SQLException {
        /**
         * Guardar un nuevo usuario, en caso de guardarse se devuelve el usuario creado, de lo contrario se regresa empty
         */
        String query = "INSERT INTO autor (nombre, apellido_p, apellido_m, activo) VALUES (?,?,?,?)";

        try(Connection conn = ConnectionDBImpl_MySQL.getInstance().getConection();
            PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, autor.nombre());
            stmt.setString(2, autor.apellidoP());
            if (autor.apellidoM()!=null) {
                stmt.setString(3, autor.apellidoM());
            } else {
                stmt.setNull(3, Types.VARCHAR);
            }
            stmt.setInt(4, 1);

            int result = stmt.executeUpdate();

            if (result!=0) {
                return getByNameComplete(autor.nombre(), autor.apellidoP(), autor.apellidoM());
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Error al preparar la consulta en AutorRepositoryImpl. Key duplicada");
            throw new SQLException("Error: autor ya existente.");

        } catch (SQLException e) {
            System.out.println("Error al preparar la consulta en AutorRepositoryImpl");
            throw new SQLException("Error al crear nuevo Autor.");
        }

        return Optional.empty();
    }

    @Override
    public Boolean delete(Long id) throws SQLException {
        /**
         * Se realiza un borrado logico en la base de datos.
         *
         * @return un true en caso de eliminación, false en caso de no realizarse eliminación.
         */
        boolean result = false;

        String query = "UPDATE autor SET activo = 0 WHERE id = ?";

        try (Connection conn = ConnectionDBImpl_MySQL.getInstance().getConection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, id);
            int rowsUpdated = stmt.executeUpdate();
            if(rowsUpdated>0) {
                result = true;
            }
        }
        return result;
    }

    private List<AutorModel> transformToList(ResultSet result) {
        List<AutorModel> list = new ArrayList<>();
        /**
         * Transforma el resultado de la consulta a una lista con autores.
         */
        try {
            while(result.next()) {
                Object apellidoMObj = result.getObject("apellido_m");
                String apellidoM = (apellidoMObj==null) ? "" : apellidoMObj.toString();
                list.add(new AutorModel.Builder()
                        .setId(result.getLong("id"))
                        .setNombre(result.getString("nombre"))
                        .setApellido_p(result.getString("apellido_p"))
                        .setApellido_m(apellidoM)
                        .setActivo(result.getInt("activo"))
                        .build()
                );
            }
        } catch (SQLException e) {
            System.out.println("Error al transformar el resultado de la consutla a objetos");;
        }

        return list;
    }

    public Optional<AutorModel> getByNameComplete(String nombre, String apellidoP, String apellidoM) throws SQLException {
        /**
         * Obtener un autor, en caso de no encontrarlo se devuelve empty
         */
        String query, nombreCompleto;
        if (apellidoM == null) {
            query = "SELECT * " +
                    "FROM autor " +
                    "WHERE concat(nombre, ' ', apellido_p) = ? AND apellido_m IS NULL AND activo = 1 " +
                    "ORDER BY nombre ASC";
            nombreCompleto = nombre + " " + apellidoP;
        } else {
            query = "SELECT * " +
                    "FROM autor " +
                    "WHERE concat(nombre, ' ', apellido_p, ' ', apellido_m) = ? AND activo = 1 " +
                    "ORDER BY nombre ASC";
            nombreCompleto = nombre + " " + apellidoP + " " + apellidoM;
        }

        try(Connection conn = ConnectionDBImpl_MySQL.getInstance().getConection();
            PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nombreCompleto);

            try (ResultSet resultSet = stmt.executeQuery()) {
                AutorModel autor = transformToModel(resultSet);
                return Optional.ofNullable(autor);
            }

        } catch (SQLException e) {
            System.out.println("Error al ejecutar la consulta en AutorRepositoryImpl");
            throw e;
        }
    }

    private AutorModel transformToModel(ResultSet resultSet) {
        try {
            if (resultSet.next()) {
                Object apellidoMObj = resultSet.getObject("apellido_m");
                String apellidoM = (apellidoMObj==null) ? "" : apellidoMObj.toString();
                return new AutorModel.Builder()
                        .setId(resultSet.getLong("id"))
                        .setNombre(resultSet.getString("nombre"))
                        .setApellido_p(resultSet.getString("apellido_p"))
                        .setApellido_m(apellidoM)
                        .setActivo(resultSet.getInt("activo"))
                        .build();
            }
        } catch (SQLException e) {
            System.out.println("Error al transformar el resultado de la consutla a objeto AutorModel");;
        }

        return null;
    }

}
