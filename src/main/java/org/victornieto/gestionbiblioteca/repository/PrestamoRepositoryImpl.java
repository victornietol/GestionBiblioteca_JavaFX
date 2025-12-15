package org.victornieto.gestionbiblioteca.repository;

import org.victornieto.gestionbiblioteca.database.ConnectionDBImpl_MySQL;
import org.victornieto.gestionbiblioteca.dto.PrestamoDTO;
import org.victornieto.gestionbiblioteca.dto.PrestamoListDTO;
import org.victornieto.gestionbiblioteca.model.PrestamoModel;
import org.victornieto.gestionbiblioteca.utility.PrestamosViewTitlesMenuBtn;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class PrestamoRepositoryImpl implements PrestamoRepository{

    private final Set<String> numericColums = Set.of("pre.id_prestamo", "pre.id_ejemplar");
    private final Set<String> dateColumns = Set.of("pre.fecha_inicio", "pre.fecha_entrega");
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

        String query = createQuery(columnToSearch, coincidenceToSearch, orderByColumn, orderDes, false);

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
            System.out.println("Error al ejecutar la consulta getPrestamosList en PrestamoRepositoryImpl: " + Arrays.toString(e.getStackTrace()));
            throw new SQLException("Error al obtener los prestamos.");
        }
    }

    @Override
    public Optional<PrestamoModel> getById(Long id) throws SQLException {
        return Optional.empty();
    }

    @Override
    public Optional<PrestamoModel> getPrestamosActiveByAllColumns(PrestamoDTO prestamoDTO) throws SQLException {
        /**
         * Obtener un préstamo activo según los campos fecha_inicio, fecha_fin, fk_cliente, fk_usuario, id_ejemplar (PrestamoDTO)
         */
        String query = """
                SELECT * FROM prestamo WHERE
                    fecha_inicio = ? AND fecha_fin = ? AND fk_cliente = ? AND fk_usuario = ? AND id_ejemplar = ? AND activo = 1
                """;

        try (Connection conn = ConnectionDBImpl_MySQL.getInstance().getConection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setDate(1, java.sql.Date.valueOf(prestamoDTO.fechaInicio()));
            stmt.setDate(2, java.sql.Date.valueOf(prestamoDTO.fechaFin()));
            stmt.setLong(3, prestamoDTO.fkCliente());
            stmt.setLong(4, prestamoDTO.fkUsuario());
            stmt.setLong(5, prestamoDTO.idEjemplar());

            ResultSet resultSet = stmt.executeQuery();
            PrestamoModel prestamoModel = transformToModel(resultSet);
            return Optional.ofNullable(prestamoModel);

        } catch (SQLException e) {
            System.out.println("Error: " + Arrays.toString(e.getStackTrace()));
            throw new SQLException("Error al obtener préstamo activo según todos los campos.");
        }
    }

    @Override
    public Optional<PrestamoModel> newPrestamo(PrestamoDTO prestamo) throws SQLException {
        String query = """
                INSERT INTO prestamo (fecha_inicio, fecha_fin, fk_cliente, fk_usuario, id_ejemplar, activo) VALUES (?,?,?,?,?,1)
                """;

        try (Connection conn = ConnectionDBImpl_MySQL.getInstance().getConection();
             PreparedStatement stmt = conn.prepareStatement(query)) {


            stmt.setDate(1, java.sql.Date.valueOf(prestamo.fechaInicio()));
            stmt.setDate(2, java.sql.Date.valueOf(prestamo.fechaFin()));
            stmt.setLong(3,prestamo.fkCliente());
            stmt.setLong(4,prestamo.fkUsuario());
            stmt.setLong(5,prestamo.idEjemplar());

            int result = stmt.executeUpdate();

            if (result!=0) {
                return getPrestamosActiveByAllColumns(prestamo);
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Error al preparar la consulta en PrestamoRepositoryImpl. Key duplicada");
            throw new SQLException("Error: El préstamo ya existe.");

        } catch (SQLException e) {
            System.out.println("Error al preparar la consulta en PrestamoRepositoryImpl");
            throw new SQLException("Error al crear nuevo préstamo.");
        }

        return Optional.empty();
    }

    @Override
    public Integer getNumberActivePrestamos() throws SQLException {
        String query = "SELECT count(*) AS prestamos FROM prestamo WHERE activo = 1";

        try(Connection conn = ConnectionDBImpl_MySQL.getInstance().getConection();
            PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("prestamos");
            }
            return 0; // Error en la consulta si no devuelve nada

        } catch (SQLException e) {
            System.out.println("Error: " + Arrays.toString(e.getStackTrace()));
            throw new SQLException("Error al obtener la cantidad de prestamos actuales.");
        }
    }

    @Override
    public List<PrestamoListDTO> getReturnedPrestamosToday(String columnToSearch, String coincidenceToSearch, String orderByColumn, boolean orderDes) throws SQLException {
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

        String query = createQueryReturned(columnToSearch, coincidenceToSearch, orderByColumn, orderDes);

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
            System.out.println("Error al ejecutar la consulta getReturnedPrestamosList en PrestamoRepositoryImpl: " + Arrays.toString(e.getStackTrace()));
            throw new SQLException("Error al obtener los prestamos devueltos hoy.");
        }
    }

    @Override
    public List<PrestamoListDTO> getAllPrestamosToday(String columnToSearch, String coincidenceToSearch, String orderByColumn, boolean orderDes) throws SQLException {
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

        String query = createQuery(columnToSearch, coincidenceToSearch, orderByColumn, orderDes, true);

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
            System.out.println("Error al ejecutar la consulta getReturnedPrestamosList en PrestamoRepositoryImpl: " + Arrays.toString(e.getStackTrace()));
            throw new SQLException("Error al obtener los prestamos devueltos hoy.");
        }
    }

    @Override
    public Boolean returnPrestamo(Long idPrestamo) throws SQLException {
        String query = "UPDATE prestamo SET activo = 0, fecha_fin = ? WHERE id = ?";

        try(Connection conn = ConnectionDBImpl_MySQL.getInstance().getConection();
            PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setDate(1, java.sql.Date.valueOf(LocalDate.now())); // fecha actual
            stmt.setLong(2, idPrestamo);

            int result = stmt.executeUpdate();
            if (result!=0) {
                return true;
            } else {
                throw new SQLException("Error al realizar la devolución del préstamo.");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + Arrays.toString(e.getStackTrace()));
            throw new SQLException("Error al realizar la devolución del préstamo.");
        }
    }



    private String createQuery(String columnToSearch, String coincidenceToSeach, String orderByColumn, boolean orderDesc, boolean all) {
        String queryActive = """
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

        String queryAll = """
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


        String query = all ? queryAll : queryActive;


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

    private String createQueryReturned(String columnToSearch, String coincidenceToSeach, String orderByColumn, boolean orderDesc) {
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
                	WHERE p.activo = 0
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

    private PrestamoModel transformToModel(ResultSet resultSet) {
        try {
            if (resultSet.next()) {
                return PrestamoModel.builder()
                        .id(resultSet.getLong("id"))
                        .fechaInicio(resultSet.getDate("fecha_inicio").toLocalDate())
                        .fechaFin(resultSet.getDate("fecha_fin").toLocalDate())
                        .fkCliente(resultSet.getLong("fk_cliente"))
                        .fkUsuario(resultSet.getLong("fk_usuario"))
                        .idEjemplar(resultSet.getLong("id_ejemplar"))
                        .activo(resultSet.getInt("activo"))
                        .build();
            }
        } catch (SQLException e) {
            System.out.println("Error al transformar el resultado de la consulta objeto PrestamoModel");
        }

        return null;
    }

    private void loadValuesColumns() {
        /**
         * columnsValueConverts contiene los nombres de las columnas en la UI y su equivalencia a los nombres de las columnas de la consulta resultante de la BD
         */
        columnsValueConverts.put(PrestamosViewTitlesMenuBtn.ID, "pre.id_prestamo");
        columnsValueConverts.put(PrestamosViewTitlesMenuBtn.FECHA_INICIO, "pre.fecha_inicio");
        columnsValueConverts.put(PrestamosViewTitlesMenuBtn.FECHA_ENTREGA, "pre.fecha_entrega");
        columnsValueConverts.put(PrestamosViewTitlesMenuBtn.ID_EJEMPLAR, "pre.id_ejemplar");
        columnsValueConverts.put(PrestamosViewTitlesMenuBtn.TITULO, "ejem.titulo");
        columnsValueConverts.put(PrestamosViewTitlesMenuBtn.AUTOR, "aut.autor");
        columnsValueConverts.put(PrestamosViewTitlesMenuBtn.CLIENTE, "pre.cliente");
        columnsValueConverts.put(PrestamosViewTitlesMenuBtn.ATENDIO, "pre.usuario");
        columnsValueConverts.put(PrestamosViewTitlesMenuBtn.TODOS, "todos");
    }


}