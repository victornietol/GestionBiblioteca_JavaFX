package org.victornieto.gestionbiblioteca.repository;

import org.victornieto.gestionbiblioteca.database.ConnectionDBImpl_MySQL;
import org.victornieto.gestionbiblioteca.dto.LibroInventarioDTO;
import org.victornieto.gestionbiblioteca.model.LibroModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public class LibroRepositoryImp implements LibroRepository {

    public LibroRepositoryImp() {
    }

    @Override
    public List<LibroInventarioDTO> getBooksInfoComplete (
            boolean showTitles,
            String columnToSearch,
            String coincidenceToSearch,
            String orderByColumn,
            boolean orderDesc
    ) throws SQLException {
        /**
         * Función para obtener todos los libros con su información completa.
         * Se utiliza para mostrar el inventario de libros por titulos.
         *
         * @param showTitles un boolean para indicar si se deben mostrar los libros por titulos o por unidades
         * @param columnToSearch un string con la columna a buscar, en caso de ingresar "todos" no se aplica clausula where
         * @param coincidenceToSearch un string con la palabra para buscar una coincidencia
         * @param orderByColumn un string para order el resultado según la columna indicada
         * @param orderDesc un boolean para indicar si el orden es descendente o ascendente
         * @return una lista con los libros obtenidos según los parametros indicados, en caso de que la quey no regrese resultados entonces el resultado de la función es una lista vacía
         */

        List<LibroInventarioDTO> list;

        String query = createQuery(showTitles, columnToSearch, orderByColumn, orderDesc);

        Connection conn = ConnectionDBImpl_MySQL.getInstance().getConection();

        try {
            PreparedStatement stmt = conn.prepareStatement(query);

            if (!columnToSearch.equals("todos")) {
                // Se aplica clausula WHERE
                stmt.setString(1, coincidenceToSearch);
            }

            ResultSet resultSet = stmt.executeQuery();

            list = generateBookList(resultSet);

            resultSet.close();
            stmt.close();

        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {
            conn.close();
        }

        return list;
    }

    @Override
    public LibroModel save(LibroModel book) {
        return null;
    }

    @Override
    public LibroModel delete(Integer id) {
        return null;
    }

    private String createQuery(
            boolean showTitles,
            String columnToSearch,
            String orderByColumn,
            boolean orderDesc
    ) {
        /**
         * Función para crear la query a ejecutar.
         *
         * @param showTitles un boolean para indicar si se deben mostrar los libros por titulos o por unidades
         * @param columnToSearch un string con la columna a buscar, en caso de ingresar "todos" no se aplica clausula where
         * @param orderDesc un boolean para indicar si el orden es descendente o ascendente
         * @return una lista con los libros obtenidos según los parametros indicados
         */
        String queryTitles = """
                SELECT
                        	lib.id_libro AS id_libro,
                            lib.titulo AS titulo,
                            lib.autores  AS autor,
                            cat.categoria AS categoria,
                            lib.editorial AS editorial,
                            lib.anio_publicacion as anio_publicacion,
                            lib.paginas AS paginas,
                            lib.edicion AS edicion,
                            uni.unidades AS unidades
                        FROM (
                        	SELECT
                        		l.id AS id_libro,
                        		l.titulo AS titulo,
                        		GROUP_CONCAT(CONCAT(a.nombre, ' ', a.apellido_p, ' ', a.apellido_m) SEPARATOR ', ') AS autores,
                        		ed.nombre AS editorial,
                        		l.anio_publicacion as anio_publicacion,
                        		l.no_paginas AS paginas,
                        		l.edicion AS edicion
                        	FROM libro_autor l_a
                        	JOIN autor a ON (l_a.id_autor = a.id)
                        	JOIN libro l ON (l.id = l_a.id_libro)
                        	JOIN editorial ed ON (l.id_editorial=ed.id)
                        	WHERE l.activo = 1
                        	GROUP BY l.id) AS lib
                        JOIN (
                        	SELECT
                        		l.id AS id_libro,
                        		GROUP_CONCAT(c.nombre SEPARATOR ', ') AS categoria
                        	FROM categoria_libro c_l
                        	JOIN libro l ON (c_l.id_libro = l.id)
                        	JOIN categoria c ON (c_l.id_categoria = c.id)
                        	WHERE l.activo = 1
                        	GROUP BY l.id
                        ) AS cat ON (lib.id_libro = cat.id_libro)
                        JOIN (
                        	SELECT
                        		e.id_libro id_libro,
                                count(id_libro) unidades
                        	FROM ejemplar_libro e
                        	JOIN libro l ON (e.id_libro=l.id)
                        	WHERE e.activo = 1
                        	GROUP BY id_libro
                        ) AS uni ON (lib.id_libro = uni.id_libro)
        """;

        String queryUnits = "";

        String where = " WHERE " + columnToSearch + " LIKE CONCAT('%', ?, '%') ";
        String orderBy = "ORDER BY " + orderByColumn + " " + (orderDesc ? "DESC" : "ASC");

        String query = showTitles ? queryTitles : queryUnits;

        if (columnToSearch.equals("todos")) {
            query += orderBy;
        } else {
            query = query + where + orderBy;
        }

        return query;
    }

    private List<LibroInventarioDTO> generateBookList(ResultSet resultSet) throws SQLException {
        List<LibroInventarioDTO> list = new ArrayList<>();

        while(resultSet.next()) {
            LibroInventarioDTO book = new LibroInventarioDTO.Builder()
                    .setId_libro(resultSet.getInt("id_libro"))
                    .setTitulo(resultSet.getString("titulo"))
                    .setAutor(resultSet.getString("autor"))
                    .setCategoria(resultSet.getString("categoria"))
                    .setEditorial(resultSet.getString("editorial"))
                    .setAnio_publicacion(Year.of(resultSet.getInt("anio_publicacion")))
                    .setPaginas(resultSet.getInt("paginas"))
                    .setEdicion(resultSet.getString("edicion"))
                    .setUnidades(resultSet.getInt("unidades"))
                    .build();

            list.add(book);
        }

        return list; // Si no devuelve nada la consulta regresa una lista vacía
    }
}
