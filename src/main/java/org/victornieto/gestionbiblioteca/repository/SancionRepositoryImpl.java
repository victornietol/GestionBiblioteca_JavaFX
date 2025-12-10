package org.victornieto.gestionbiblioteca.repository;

import org.victornieto.gestionbiblioteca.database.ConnectionDBImpl_MySQL;
import org.victornieto.gestionbiblioteca.dto.SancionListDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SancionRepositoryImpl implements SancionRepository{
    @Override
    public List<SancionListDTO> getByIdCliente(Long id) throws SQLException {
        List<SancionListDTO> list;

        String query = """
                SELECT
                	s.id AS id,
                    t.tipo AS sancion,
                    t.descripcion  AS descripcion,
                	s.fecha AS fecha,
                    s.fk_prestamo AS id_prestamo
                FROM sanciones s
                JOIN tipo_sancion t ON (s.id_tipo = t.id)
                WHERE s.id_cliente = ? AND s.activo = 1
                """;

        try (Connection conn = ConnectionDBImpl_MySQL.getInstance().getConection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, id);

            ResultSet resultSet = stmt.executeQuery();
            list = transformToDTOList(resultSet);
            return list;

        } catch (SQLException e) {
            System.out.println("Error: " + Arrays.toString(e.getStackTrace()));
            throw new SQLException("Error al obtener las sanciones.");
        }
    }

    @Override
    public Boolean delete(Long id) throws SQLException {
        return null;
    }

    private List<SancionListDTO> transformToDTOList(ResultSet resultSet) throws SQLException {
        List<SancionListDTO> list = new ArrayList<>();

        while(resultSet.next()) {
            list.add(
                    new SancionListDTO(
                        resultSet.getLong("id"),
                            resultSet.getString("sancion"),
                            resultSet.getString("descripcion"),
                            resultSet.getDate("fecha").toLocalDate(),
                            resultSet.getLong("id_prestamo")
                    )
            );
        }

        return list;
    }
}
