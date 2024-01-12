package com.fuse.sql.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

public class PostgresJDBCDriverConnector {
    Logger logger = Logger.getLogger(PostgresJDBCDriverConnector.class.getName());
    public Connection conn;
    public PostgresJDBCDriverConnector() {
        String url = "jdbc:postgresql://192.168.3.20:5432/morpheus";
        Properties props = new Properties();
        props.setProperty("user", "morpheus");
        props.setProperty("password", "MORPHEUS@ADMIN2023");
        props.setProperty("ssl", "false");

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
