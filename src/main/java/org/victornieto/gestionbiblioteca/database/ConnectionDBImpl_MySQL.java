package org.victornieto.gestionbiblioteca.database;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionDBImpl_MySQL implements ConnectionDB {

    private String url, user, passw;
    private DataSource dataSource;
    private static ConnectionDBImpl_MySQL instance;

    private ConnectionDBImpl_MySQL() {

        loadProperties();

        MysqlConnectionPoolDataSource ds = new MysqlConnectionPoolDataSource();
        ds.setURL(this.url);
        ds.setUser(this.user);
        ds.setPassword(this.passw);
        this.dataSource = ds;

    }

    public static synchronized ConnectionDBImpl_MySQL getInstance() {
        if (instance == null) instance = new ConnectionDBImpl_MySQL();
        return instance;
    }

    @Override
    public Connection getConection() throws SQLException {
        return this.dataSource.getConnection();
    }

    private void loadProperties() {
        Properties props = new Properties();

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if(input==null) {
                throw new RuntimeException("No se encontró el archivo db.properties");
            }
            props.load(input);
        } catch (IOException e) {
            System.out.println("Error al obtener propiedades db");
            e.printStackTrace();
        }

        this.url = props.getProperty("db.url_mysql");
        this.user = props.getProperty("db.user_mysql");
        this.passw = props.getProperty("db.password_mysql");
    }
}
