package org.victornieto.gestionbiblioteca.repository;

import org.victornieto.gestionbiblioteca.database.ConnectionDBImpl_MySQL;
import org.victornieto.gestionbiblioteca.dto.ClienteFormDTO;
import org.victornieto.gestionbiblioteca.dto.ClienteListDTO;
import org.victornieto.gestionbiblioteca.model.ClienteModel;
import org.victornieto.gestionbiblioteca.utility.ClientesViewTitlesMenuBtn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ClienteRepositoryImpl implements ClienteRepository{

    private final Set<String> numericColumns = Set.of("prestamos", "sanciones");
    private final HashMap<String, String> columnsValueConverts = new HashMap<>();

    public ClienteRepositoryImpl() {
        loadValuesColumns();
    }

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

    @Override
    public List<ClienteListDTO> getListDTOAll(String columnToSearch, String coincidenceToSearch, String orderByColumn, boolean orderDesc) throws SQLException {
        /**
         * Obtiene todos los clientes que se muestran en la tabla de la pestaña Clientes
         */
        List<ClienteListDTO> list;

        columnToSearch = columnsValueConverts.get(columnToSearch);
        orderByColumn = columnsValueConverts.get(orderByColumn);

        String query = createQuery(columnToSearch, coincidenceToSearch, orderByColumn, orderDesc);

        try(Connection conn = ConnectionDBImpl_MySQL.getInstance().getConection();
            PreparedStatement stmt = conn.prepareStatement(query)) {

            if(!columnToSearch.equals("todos")) {
                // Se aplica WHERE
                if (numericColumns.contains(columnToSearch)) {
                    // si es numerico
                    stmt.setInt(1, Integer.parseInt(coincidenceToSearch));

                } else {
                    stmt.setString(1, coincidenceToSearch);
                }
            }

            ResultSet resultSet = stmt.executeQuery();
            list = transformResultToList(resultSet);
            return list;

        } catch (SQLException e) {
            System.out.println("Error al ejecutar la consulta getPrestamosList en ClienteRepositoryImpl: " + Arrays.toString(e.getStackTrace()));
            throw new SQLException("Error al obtener los clientes.");
        }
    }

    @Override
    public Optional<ClienteModel> save(ClienteFormDTO clienteFormDTO) throws SQLException {
        return Optional.empty();
    }

    @Override
    public Boolean delete(Long id) throws SQLException {
        return null;
    }

    private String createQuery(String columnToSearch, String coincidenceToSearch, String orderByColumn, boolean orderDesc) {
        String query = """
                SELECT
                	cli.id AS id,
                    cli.username AS username,
                    cli.nombre AS nombre,
                    cli.correo AS correo,
                    COALESCE(pre.prestamos, 0) AS prestamos,
                	COALESCE(san.sanciones, 0) AS sanciones
                FROM (
                	SELECT
                		c.id AS id,
                		c.username AS username,
                		TRIM(CONCAT(c.nombre, ' ', c.apellido_p, ' ', COALESCE(c.apellido_m, ''))) AS nombre,
                		c.correo AS correo
                	FROM cliente c
                	WHERE c.activo = 1
                ) AS cli
                LEFT JOIN (
                	SELECT
                		s.id_cliente AS id_cliente,
                		count(s.id_cliente) AS sanciones
                	FROM sanciones s
                	WHERE s.activo = 1
                	GROUP BY s.id_cliente
                ) AS san ON (cli.id = san.id_cliente)
                LEFT JOIN (
                	SELECT
                		p.fk_cliente AS id_cliente,
                		COUNT(p.fk_cliente) AS prestamos
                	FROM prestamo p
                	WHERE p.activo = 1
                	GROUP BY p.fk_cliente
                ) AS pre ON (cli.id = pre.id_cliente)
                """;

        String orderBy = "ORDER BY " + orderByColumn + " " + (orderDesc ? "DESC" : "ASC");

        if (columnToSearch.equals("todos")) {
            query += orderBy;
        } else {
            String where;
            if (numericColumns.contains(columnToSearch)) {
                where = "WHERE " + columnToSearch + " = ? ";
            } else {
                where = " WHERE " + columnToSearch + " LIKE CONCAT('%', ? , '%') ";
            }
            query = query + where + orderBy;
        }

        return query;
    }

    private List<ClienteListDTO> transformResultToList(ResultSet resultSet) throws SQLException {
        List<ClienteListDTO> list = new ArrayList<>();

        while(resultSet.next()) {
            list.add(
                    new ClienteListDTO(
                            resultSet.getLong("id"),
                            resultSet.getString("username"),
                            resultSet.getString("nombre"),
                            resultSet.getString("correo"),
                            resultSet.getInt("prestamos"),
                            resultSet.getInt("sanciones")
                    )
            );
        }
        return list;
    }

    private void loadValuesColumns() {
        columnsValueConverts.put(ClientesViewTitlesMenuBtn.USERNAME, "cli.username");
        columnsValueConverts.put(ClientesViewTitlesMenuBtn.NOMBRE, "cli.nombre");
        columnsValueConverts.put(ClientesViewTitlesMenuBtn.CORREO, "cli.correo");
        columnsValueConverts.put(ClientesViewTitlesMenuBtn.PRESTAMOS, "prestamos");
        columnsValueConverts.put(ClientesViewTitlesMenuBtn.SANCIONES, "sanciones");
        columnsValueConverts.put(ClientesViewTitlesMenuBtn.TODOS, "todos");
    }
}
