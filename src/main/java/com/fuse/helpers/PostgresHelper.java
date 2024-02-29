package com.fuse.helpers;

import java.sql.SQLException;
import java.util.logging.Logger;

public class PostgresHelper {

    public void logSQLException(SQLException sqlException, Logger logger) {
        for (Throwable e: sqlException) {
            if (e instanceof SQLException) {
                logger.severe(e.toString());
                logger.severe("SQLState: " + ((SQLException) e).getSQLState());
                logger.severe("Error Code: " + ((SQLException) e).getErrorCode());
                logger.severe("Message: " + e.getMessage());
                Throwable t = sqlException.getCause();
                while (t != null) {
                    logger.severe("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }
}
