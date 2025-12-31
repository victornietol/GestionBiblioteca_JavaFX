package org.victornieto.gestionbiblioteca.repository;

import org.victornieto.gestionbiblioteca.database.ConnectionDBImpl_MySQL;
import org.victornieto.gestionbiblioteca.dto.CategoriaFormDTO;
import org.victornieto.gestionbiblioteca.dto.EditorialFormDTO;
import org.victornieto.gestionbiblioteca.model.CategoriaModel;
import org.victornieto.gestionbiblioteca.model.EditorialModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EditorialRepositoryImpl implements EditorialRepository{

    @Override
    public List<EditorialModel> getAll() throws SQLException {
        List<EditorialModel> list = new ArrayList<>();

        String query = "SELECT * FROM editorial WHERE activo = 1 ORDER BY nombre ASC";

        try(Connection conn = ConnectionDBImpl_MySQL.getInstance().getConection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet resultSet = stmt.executeQuery()) {

            list = transformToList(resultSet); // Modificar lista para agregar el resultado de la consulta

        } catch (SQLException e) {
            System.out.println("Error al ejecutar la consulta en EditorialRepositoryImpl");
            throw e;
        }

        return list;
    }

    @Override
    public List<EditorialFormDTO> getNombres() throws SQLException {
        List<EditorialFormDTO> list;

        String query = "SELECT nombre FROM editorial WHERE activo = 1";

        try(Connection conn = ConnectionDBImpl_MySQL.getInstance().getConection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet resultSet = stmt.executeQuery()) {

            list = transformToDTO(resultSet);

        } catch (SQLException e) {
            System.out.println("Error al ejecutar la consulta en getNombres() en EditorialRepositoryImpl");
            throw new SQLException("Error al obtener nombres de editoriales.");
        }

        return list;
    }

    @Override
    public Optional<EditorialModel> getById(Integer id) throws SQLException {
        /**
         * Obtener una editorial, en caso de no encontrarlo se devuelve empty
         */
        String query = "SELECT * FROM editorial WHERE id = ? AND activo = 1 ORDER BY nombre ASC";

        try(Connection conn = ConnectionDBImpl_MySQL.getInstance().getConection();
            PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            try (ResultSet resultSet = stmt.executeQuery()) {
                EditorialModel editorial = transformToModel(resultSet);
                return Optional.ofNullable(editorial);
            }

        } catch (SQLException e) {
            System.out.println("Error al ejecutar la consulta en EditorialRepositoryImpl");
            throw e;
        }
    }

    @Override
    public Optional<EditorialModel> save(EditorialFormDTO editorial) throws SQLException {
        /**
         * Guardar una nueva editorial, en caso de guardarse se devuelve la editorial creada, de lo contrario se regresa empty
         */
        String query = "INSERT INTO editorial (nombre, activo) VALUES (?,?)";

        try(Connection conn = ConnectionDBImpl_MySQL.getInstance().getConection();
            PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, editorial.nombre());
            stmt.setLong(2, 1);

            int result = stmt.executeUpdate();

            if (result!=0) {
                return getByNombre(editorial.nombre());
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Error al preparar la consulta en EditorialRepositoryImpl. Key duplicada");
            throw new SQLException("Error: editorial ya existente.");

        } catch (SQLException e) {
            System.out.println("Error al preparar la consulta en EditorialRepositoryImpl");
            throw new SQLException("Error al crear nueva editorial.");
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

        String query = "UPDATE editorial SET activo = 0 WHERE id = ?";

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

    private List<EditorialModel> transformToList(ResultSet result) {
        List<EditorialModel> list = new ArrayList<>();
        /**
         * Transforma el resultado de la consulta a una lista con editoriales.
         */
        try {
            while(result.next()) {
                list.add(new EditorialModel.Builder()
                        .setId(result.getLong("id"))
                        .setNombre(result.getString("nombre"))
                        .setActivo(result.getInt("activo"))
                        .build()
                );
            }
        } catch (SQLException e) {
            System.out.println("Error al transformar el resultado de la consulta a objetos en EditorialRepositoryImpl");;
        }

        return list;
    }

    public Optional<EditorialModel> getByNombre(String nombre) throws SQLException {
        /**
         * Obtener una editorial, en caso de no encontrarlo se devuelve empty
         */
        String query = "SELECT * FROM editorial WHERE nombre = ? AND activo = 1 ORDER BY nombre ASC";


        try(Connection conn = ConnectionDBImpl_MySQL.getInstance().getConection();
            PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nombre);

            try (ResultSet resultSet = stmt.executeQuery()) {
                EditorialModel editorial = transformToModel(resultSet);
                return Optional.ofNullable(editorial);
            }

        } catch (SQLException e) {
            System.out.println("Error al ejecutar la consulta en EditorialRepositoryImpl");
            throw e;
        }
    }

    private EditorialModel transformToModel(ResultSet resultSet) {
        try {
            if (resultSet.next()) {
                return new EditorialModel.Builder()
                        .setId(resultSet.getLong("id"))
                        .setNombre(resultSet.getString("nombre"))
                        .setActivo(resultSet.getInt("activo"))
                        .build();
            }
        } catch (SQLException e) {
            System.out.println("Error al transformar el resultado de la consulta a objeto EditorialModel");;
        }

        return null;
    }

    private List<EditorialFormDTO> transformToDTO(ResultSet resultSet) throws SQLException {
        List<EditorialFormDTO> list = new ArrayList<>();

        while(resultSet.next()) {
            list.add(
                    new EditorialFormDTO(resultSet.getString("nombre"))
            );
        }

        return list;
    }
}
