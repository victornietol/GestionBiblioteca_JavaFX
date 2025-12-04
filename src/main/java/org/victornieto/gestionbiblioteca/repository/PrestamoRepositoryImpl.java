package org.victornieto.gestionbiblioteca.repository;

import org.victornieto.gestionbiblioteca.database.ConnectionDBImpl_MySQL;
import org.victornieto.gestionbiblioteca.dto.PrestamoDTO;
import org.victornieto.gestionbiblioteca.dto.PrestamoListDTO;
import org.victornieto.gestionbiblioteca.model.PrestamoModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class PrestamoRepositoryImpl implements PrestamoRepository{

    private final Set<String> numericColums = Set.of("pre.id_prestamo", "pre.fecha_inicio", "pre.fecha_entrega", "pre.id_ejemplar");
    private HashMap<String, String> columnsValueConverts = new HashMap<>();

    public PrestamoRepositoryImpl() {
        loadValuesColumns();
    }

    @Override
    public List<PrestamoListDTO> getPrestamosList(String columnToSearch, String coincidenceToSearch, String orderByColumn, boolean orderDes) throws SQLException {
        /**
         * Devuelve la información necesaria para mostrar la tabla de la pestaña Préstamos
         * @param columnToSearch un string con la columna a buscar, en caso de ingresar "todos" no se aplica clausula where
         * @param coincidenceToSearch un string con la palabra para buscar una coincidencia
         * @param orderByColumn un string para order el resultado según la columna indicada
         * @param orderDesc un boolean para indicar si el orden es descendente o ascendente
         */

        List<PrestamoListDTO> list;

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
                } else {
                    stmt.setString(1, coincidenceToSearch);
                }
            }

            ResultSet resultSet = stmt.executeQuery();
            list = transformResultToList(resultSet);
            return list;

        } catch (SQLException e) {
            System.out.println("Error al ejecutar la consulta getPrestamosList en PrestamoRepositoryImpl: " + Arrays.toString(e.getStackTrace()));
            throw new SQLException("Error al obtener los prestamos.");
        }
    }

    @Override
    public Optional<PrestamoModel> newPrestamo(PrestamoDTO prestamo) throws SQLException {
        return Optional.empty();
    }

    @Override
    public Boolean returnPrestamo(Long idPrestamo) throws SQLException {
        return null;
    }

    private String createQuery(String columnToSearch, String coincidenceToSeach, String orderByColumn, boolean orderDesc) {
        String query = """
                SELECT
                	pre.id_prestamo AS id_prestamo,
                	pre.fecha_inicio AS fecha_inicio,
                    pre.fecha_entrega AS fecha_entrega,
                	pre.id_ejemplar AS id_ejemplar,
                    ejem.titulo AS titulo,
                    aut.autor AS autor,
                	pre.cliente AS cliente,
                	pre.usuario AS usuario
                FROM (
                	SELECT
                		p.id AS id_prestamo,
                		DATE(p.fecha_inicio) AS fecha_inicio,
                		DATE(p.fecha_fin) AS fecha_entrega,
                		p.id_ejemplar AS id_ejemplar,
                		TRIM(CONCAT(c.nombre, ' ', c.apellido_p, ' ', COALESCE(c.apellido_m, ''))) AS cliente,
                		TRIM(CONCAT(u.nombre, ' ', u.apellido_p, ' ', COALESCE(u.apellido_m, ''))) AS usuario
                	FROM prestamo p
                	JOIN cliente c ON (p.fk_cliente = c.id)
                	JOIN usuario u ON (p.fk_usuario = u.id)
                	WHERE p.activo = 1
                ) pre
                JOIN (
                	SELECT
                		ej.id AS id_ejemplar,
                		l.id AS id_libro,
                		l.titulo AS titulo
                	FROM libro l
                	JOIN ejemplar_libro ej ON (l.id = ej.id_libro)
                ) ejem ON (pre.id_ejemplar = ejem.id_ejemplar)
                JOIN (
                	SELECT
                		l.id AS id_libro,
                		GROUP_CONCAT(TRIM(CONCAT(a.nombre, ' ', a.apellido_p, ' ', COALESCE(a.apellido_m, ''))) SEPARATOR ', ') AS autor
                	FROM libro_autor l_a
                	JOIN autor a ON (l_a.id_autor = a.id)
                	JOIN libro l ON (l.id = l_a.id_libro)
                	GROUP BY l.id
                ) aut ON (ejem.id_libro = aut.id_libro)
                """;

        String orderBy = "ORDER BY " + orderByColumn + " " + (orderDesc ? "DESC" : "ASC");

        if (columnToSearch.equals("todos")) {
            query += orderBy;

        } else {
            // implementar WHERE
            String where;
            if (numericColums.contains(columnToSearch)) {
                where = " WHERE " + columnToSearch + " = ? "; // campo numerico
            } else {
                where = " WHERE " + columnToSearch + " LIKE CONCAT('%', ? , '%') "; // campo no numerico
            }
            query = query + where + orderBy;
        }

        return query;
    }

    private List<PrestamoListDTO> transformResultToList(ResultSet resultSet) throws SQLException {
        List<PrestamoListDTO> list = new ArrayList<>();

        while(resultSet.next()) {
            list.add(
                    new PrestamoListDTO(
                            resultSet.getLong("id_prestamo"),
                            resultSet.getDate("fecha_inicio").toLocalDate(),
                            resultSet.getDate("fecha_entrega").toLocalDate(),
                            resultSet.getLong("id_ejemplar"),
                            resultSet.getString("titulo"),
                            resultSet.getString("autor"),
                            resultSet.getString("cliente"),
                            resultSet.getString("usuario")
                    )
            );
        }

        return list;
    }

    private void loadValuesColumns() {
        /**
         * columnsValueConverts contiene los nombres de las columnas en la UI y su equivalencia a los nombres de las columnas de la consulta resultante de la BD
         */
        columnsValueConverts.put("ID", "pre.id_prestamo");
        columnsValueConverts.put("Fecha inicio", "pre.fecha_inicio");
        columnsValueConverts.put("Fecha entrega", "pre.fecha_entrega");
        columnsValueConverts.put("ID_Ejemplar", "pre.id_ejemplar");
        columnsValueConverts.put("Título", "ejem.titulo");
        columnsValueConverts.put("Autor", "aut.autor");
        columnsValueConverts.put("Cliente", "pre.cliente");
        columnsValueConverts.put("Atendió", "pre.usuario");
        columnsValueConverts.put("Todos", "todos");
    }


}