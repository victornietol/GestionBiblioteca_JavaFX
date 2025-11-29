package org.victornieto.gestionbiblioteca.repository;

import org.victornieto.gestionbiblioteca.database.ConnectionDBImpl_MySQL;
import org.victornieto.gestionbiblioteca.dto.CategoriaFormDTO;
import org.victornieto.gestionbiblioteca.model.AutorModel;
import org.victornieto.gestionbiblioteca.model.CategoriaModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoriaRepositoryImpl implements CategoriaRepository{

    @Override
    public List<CategoriaModel> getAll() throws SQLException {
        List<CategoriaModel> list = new ArrayList<>();

        String query = "SELECT * FROM categoria WHERE activo = 1 ORDER BY nombre ASC";

        try(Connection conn = ConnectionDBImpl_MySQL.getInstance().getConection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet resultSet = stmt.executeQuery()) {

            list = transformToList(resultSet); // Modificar lista para agregar el resultado de la consulta

        } catch (SQLException e) {
            System.out.println("Error al ejecutar la consulta en CategoriaRepositoryImpl");
            throw e;
        }

        return list;
    }

    @Override
    public Optional<CategoriaModel> getById(Integer id) throws SQLException {
        /**
         * Obtener una categoria, en caso de no encontrarlo se devuelve empty
         */
        String query = "SELECT * FROM categoria WHERE id = ? AND activo = 1 ORDER BY nombre ASC";

        try(Connection conn = ConnectionDBImpl_MySQL.getInstance().getConection();
            PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            try (ResultSet resultSet = stmt.executeQuery()) {
                CategoriaModel categoria = transformToModel(resultSet);
                return Optional.ofNullable(categoria);
            }

        } catch (SQLException e) {
            System.out.println("Error al ejecutar la consulta en CategoriaRepositoryImpl");
            throw e;
        }
    }

    @Override
    public Optional<CategoriaModel> save(CategoriaFormDTO categoria) throws SQLException {
        /**
         * Guardar una nueva categoria, en caso de guardarse se devuelve la categoria creada, de lo contrario se regresa empty
         */
        String query = "INSERT INTO categoria (nombre, activo) VALUES (?,?)";

        try(Connection conn = ConnectionDBImpl_MySQL.getInstance().getConection();
            PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, categoria.nombre());
            stmt.setLong(2, 1);

            int result = stmt.executeUpdate();

            if (result != 0) {
                return getByNombre(categoria.nombre());
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Error al preparar la consulta en CategoriaRepositoryImpl. Key duplicada");
            throw new SQLException("Error: categoria ya existente.");

        } catch (SQLException e) {
            System.out.println("Error al preparar la consulta en CategoriaRepositoryImpl");
            throw new SQLException("Error al crear nueva categoria.");
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

        String query = "UPDATE categoria SET activo = 0 WHERE id = ?";

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

    private List<CategoriaModel> transformToList(ResultSet result) {
        List<CategoriaModel> list = new ArrayList<>();
        /**
         * Transforma el resultado de la consulta a una lista con autores.
         */
        try {
            while(result.next()) {
                list.add(new CategoriaModel.Builder()
                        .setId(result.getLong("id"))
                        .setNombre(result.getString("nombre"))
                        .setActivo(result.getInt("activo"))
                        .build()
                );
            }
        } catch (SQLException e) {
            System.out.println("Error al transformar el resultado de la consutla a objetos en CategoriaRepositoryImpl");;
        }

        return list;
    }

    public Optional<CategoriaModel> getByNombre(String nombre) throws SQLException {
        /**
         * Obtener una cateogria, en caso de no encontrarlo se devuelve empty
         */
        String query = "SELECT * FROM categoria WHERE nombre = ? AND activo = 1 ORDER BY nombre ASC";


        try(Connection conn = ConnectionDBImpl_MySQL.getInstance().getConection();
            PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nombre);

            try (ResultSet resultSet = stmt.executeQuery()) {
                CategoriaModel categoria = transformToModel(resultSet);
                return Optional.ofNullable(categoria);
            }

        } catch (SQLException e) {
            System.out.println("Error al ejecutar la consulta en CategoriaRepositoryImpl");
            throw e;
        }
    }

    private CategoriaModel transformToModel(ResultSet resultSet) {
        try {
            if (resultSet.next()) {
                return new CategoriaModel.Builder()
                        .setId(resultSet.getLong("id"))
                        .setNombre(resultSet.getString("nombre"))
                        .setActivo(resultSet.getInt("activo"))
                        .build();
            }
        } catch (SQLException e) {
            System.out.println("Error al transformar el resultado de la consutla a objeto CategoriaModel");;
        }

        return null;
    }
}
