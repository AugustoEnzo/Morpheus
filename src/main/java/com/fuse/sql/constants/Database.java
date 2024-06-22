package com.fuse.sql.constants;

public interface Database {
    String host = "192.168.3.20";
    String port = "5432";
    String database = "morpheus";
    String user = "morpheus";
    String password = "MORPHEUS@ADMIN2023";
    String sslSecurity = "false";
    String scriptToCreateDatabaseAndAllowAccess = String.format("""
            CREATE USER %s WITH PASSWORD '%s' IF NOT EXISTS;
              CREATE DATABASE %s3 IF NOT EXISTS;
              GRANT ALL PRIVILEGES ON DATABASE %s TO %s;
              GRANT ALL ON SCHEMA public TO %s;
            """, user, password, database, database, user, user);
}
