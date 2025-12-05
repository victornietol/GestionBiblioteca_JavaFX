package org.victornieto.gestionbiblioteca.repository;

import org.victornieto.gestionbiblioteca.database.ConnectionDBImpl_MySQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;

public class ClienteRepositoryImpl implements ClienteRepository{

    @Override
    public Optional<String> getNombreCompletoById(Long id) throws SQLException {
        String query = "SELECT TRIM(CONCAT(nombre, ' ', apellido_p, ' ', COALESCE(apellido_m, ''))) AS nombre FROM cliente WHERE id = ? AND activo = 1";

        try (Connection conn = ConnectionDBImpl_MySQL.getInstance().getConection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, id);

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return Optional.ofNullable(resultSet.getString("nombre"));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            System.out.println("Error: " + Arrays.toString(e.getStackTrace()));
            throw new SQLException("Error al obtener el nombre del cliente");
        }
    }
}
