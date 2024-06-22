package com.fuse.sql.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

public class PostgresJDBCDriverConnector implements com.fuse.sql.constants.Database {
    Logger logger = Logger.getLogger(PostgresJDBCDriverConnector.class.getName());
    public Connection conn;
    public PostgresJDBCDriverConnector() {
        String url = String.format("jdbc:postgresql://%s:%s/%s", host, port, database);

        Properties props = new Properties();
        props.setProperty("user", user);
        props.setProperty("password", password);
        props.setProperty("ssl", sslSecurity);

        try {
            conn = DriverManager.getConnection(url, props);

            if (conn.isValid(5)) {
                logger.info("The connection was successfully established with Postgres.");
            } else {
                logger.info("The connection can't be established with Postgres.");
            }

        } catch (SQLException sqlException) {
            logger.severe(sqlException.toString());
        }
    }
}
