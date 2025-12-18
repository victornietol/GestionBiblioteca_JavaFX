package org.victornieto.gestionbiblioteca.repository;

import org.victornieto.gestionbiblioteca.database.ConnectionDBImpl_MySQL;
import org.victornieto.gestionbiblioteca.dto.PrestamoListDTO;
import org.victornieto.gestionbiblioteca.dto.SancionClienteListDTO;
import org.victornieto.gestionbiblioteca.dto.SancionesListDTO;
import org.victornieto.gestionbiblioteca.utility.SancionesViewTitlesMenuBtn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SancionRepositoryImpl implements SancionRepository{

    HashMap<String, String> columnsValueConverts = new HashMap<>();
    private final Set<String> numericColums = Set.of("san.id", "san.id_prestamo", "san.id_ejemplar");
    private final Set<String> dateColumns = Set.of("san.fecha");

    public SancionRepositoryImpl() {
        loadValuesColumns();
    }

    @Override
    public List<SancionClienteListDTO> getByIdCliente(Long id) throws SQLException {
        List<SancionClienteListDTO> list;

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
    public List<SancionesListDTO> getAllList(String columnToSearch, String coincidenceToSearch, String orderByColumn, boolean orderDes) throws SQLException {
        /**
         * Devuelve la información necesaria para mostrar la tabla de la pestaña Sanciones
         * @param columnToSearch un string con la columna a buscar, en caso de ingresar "todos" no se aplica clausula where
         * @param coincidenceToSearch un string con la palabra para buscar una coincidencia
         * @param orderByColumn un string para order el resultado según la columna indicada
         * @param orderDesc un boolean para indicar si el orden es descendente o ascendente
         */

        List<SancionesListDTO> list;

        columnToSearch = columnsValueConverts.get(columnToSearch);
        orderByColumn = columnsValueConverts.get(orderByColumn);

        String query = createQuery(columnToSearch, coincidenceToSearch, orderByColumn, orderDes);

        try(Connection conn = ConnectionDBImpl_MySQL.getInstance().getConection();
            PreparedStatement stmt = conn.prepareStatement(query)) {

            if(!columnToSearch.equals("todos")) {
                // Se aplica WHERE
                if (numericColums.contains(columnToSearch)) {
                    // si es numerico
                    stmt.setInt(1, Integer.parseInt(coincidenceToSearch));

                } else if (dateColumns.contains(columnToSearch)) {
                    // si es fecha
                    stmt.setDate(1, java.sql.Date.valueOf(coincidenceToSearch));

                } else {
                    stmt.setString(1, coincidenceToSearch);
                }
            }

            ResultSet resultSet = stmt.executeQuery();
            list = transformResultToList(resultSet);
            return list;

        } catch (SQLException e) {
            System.out.println("Error al ejecutar la consulta getAllList en SancionRepositoryImpl: " + Arrays.toString(e.getStackTrace()));
            throw new SQLException("Error al obtener las sanciones.");
        }
    }

    @Override
    public Integer getActiveAmount() throws SQLException {
        String query = "SELECT count(*) AS activ FROM sanciones WHERE activo = 1";

        try (Connection conn = ConnectionDBImpl_MySQL.getInstance().getConection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("activ");
            }
            return 0;

        } catch (SQLException e) {
            System.out.println("Error: " + Arrays.toString(e.getStackTrace()));
            throw new SQLException("Error al obtener las sanciones activas.");
        }
    }

    @Override
    public Boolean delete(Long id) throws SQLException {
        String query = "UPDATE sanciones SET activo = 0 WHERE id = ?";

        try (Connection conn = ConnectionDBImpl_MySQL.getInstance().getConection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, id);

            int result = stmt.executeUpdate();
            if (result!=0) {
                return true;
            } else {
                throw new SQLException();
            }
        } catch (SQLException e) {
            throw new SQLException("Ocurrió un error al eliminar el sanción.");
        }
    }

    private List<SancionClienteListDTO> transformToDTOList(ResultSet resultSet) throws SQLException {
        List<SancionClienteListDTO> list = new ArrayList<>();

        while(resultSet.next()) {
            list.add(
                    new SancionClienteListDTO(
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

    private List<SancionesListDTO> transformResultToList(ResultSet resultSet) throws SQLException {
        List<SancionesListDTO> list = new ArrayList<>();

        while(resultSet.next()) {
            list.add(
                    SancionesListDTO.builder()
                            .id(resultSet.getLong("id"))
                            .sancion(resultSet.getString("sancion"))
                            .descripcion(resultSet.getString("descripcion"))
                            .fecha(resultSet.getDate("fecha").toLocalDate())
                            .cliente(resultSet.getString("cliente"))
                            .idPrestamo(resultSet.getLong("id_prestamo"))
                            .libro(resultSet.getString("titulo_libro"))
                            .idEjemplar(resultSet.getLong("id_ejemplar"))
                            .build()
            );
        }

        return list;
    }

    private String createQuery(String columnToSearch, String coincidenceToSeach, String orderByColumn, boolean orderDesc) {
        String query = """
                SELECT
                	san.id AS id,
                    san.sancion AS sancion,
                    san.descripcion  AS descripcion,
                	san.fecha AS fecha,
                    san.cliente AS cliente,
                    san.id_prestamo AS id_prestamo,
                    ejem.titulo AS titulo_libro,
                    san.id_ejemplar as id_ejemplar
                FROM (
                	SELECT
                		s.id AS id,
                		t.tipo AS sancion,
                		t.descripcion  AS descripcion,
                		s.fecha AS fecha,
                		TRIM(CONCAT(c.nombre, ' ', c.apellido_p, ' ', COALESCE(c.apellido_m, ''))) AS cliente,
                		s.fk_prestamo AS id_prestamo,
                		p.id_ejemplar as id_ejemplar
                	FROM sanciones s
                	JOIN tipo_sancion t ON (s.id_tipo = t.id)
                	JOIN cliente c ON (s.id_cliente = c.id)
                	JOIN prestamo p ON (s.fk_prestamo = p.id)
                	WHERE s.activo = 1
                ) san
                JOIN (
                	SELECT
                		l.titulo AS titulo,
                		ej.id AS id_ejemplar
                	FROM ejemplar_libro ej
                	JOIN libro l ON (ej.id_libro = l.id)
                ) ejem ON (san.id_ejemplar = ejem.id_ejemplar)
                """;

        String orderBy = "ORDER BY " + orderByColumn + " " + (orderDesc ? "DESC" : "ASC");

        if (columnToSearch.equals("todos")) {
            query += orderBy;

        } else {
            // implementar WHERE
            String where;
            if (numericColums.contains(columnToSearch) || dateColumns.contains(columnToSearch)) {
                where = " WHERE " + columnToSearch + " = ? "; // campo numerico
            } else {
                where = " WHERE " + columnToSearch + " LIKE CONCAT('%', ? , '%') "; // campo no numerico
            }
            query = query + where + orderBy;
        }

        return query;
    }

    private void loadValuesColumns() {
        columnsValueConverts.put(SancionesViewTitlesMenuBtn.ID, "san.id");
        columnsValueConverts.put(SancionesViewTitlesMenuBtn.SANCION, "san.sancion");
        columnsValueConverts.put(SancionesViewTitlesMenuBtn.DESCRIPCION, "san.descripcion");
        columnsValueConverts.put(SancionesViewTitlesMenuBtn.FECHA, "san.fecha");
        columnsValueConverts.put(SancionesViewTitlesMenuBtn.CLIENTE, "san.cliente");
        columnsValueConverts.put(SancionesViewTitlesMenuBtn.ID_PRESTAMO, "san.id_prestamo");
        columnsValueConverts.put(SancionesViewTitlesMenuBtn.LIBRO, "ejem.titulo");
        columnsValueConverts.put(SancionesViewTitlesMenuBtn.ID_EJEMPLAR, "san.id_ejemplar");
        columnsValueConverts.put(SancionesViewTitlesMenuBtn.TODOS, "todos");
    }

}
