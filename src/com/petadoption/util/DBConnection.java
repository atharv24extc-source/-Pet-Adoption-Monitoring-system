package com.petadoption.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.prefs.Preferences;

/**
 * Utility class to manage database connections using JDBC.
 * Allows runtime configuration and stores preferences locally for seamless reuse.
 */
public class DBConnection {
    private static final String PREF_HOST = "db_host";
    private static final String PREF_PORT = "db_port";
    private static final String PREF_NAME = "db_name";
    private static final String PREF_USER = "db_user";
    private static final String PREF_PASS = "db_password";

    private static final Preferences prefs = Preferences.userNodeForPackage(DBConnection.class);

    private static String host = prefs.get(PREF_HOST, "localhost");
    private static String port = prefs.get(PREF_PORT, "3306");
    private static String dbName = prefs.get(PREF_NAME, "pet_adoption_db");
    private static String user = prefs.get(PREF_USER, "root");
    private static String password = prefs.get(PREF_PASS, "root"); // default common password

    static {
        try {
            // Load MySQL Driver Class
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            try {
                // Fallback for older MySQL Connector JARs
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException ignored) {
                System.err.println("Warning: MySQL JDBC Driver not found on classpath!");
            }
        }
    }

    public static String getUrl() {
        return "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getUrl(), user, password);
    }

    public static boolean testConnection(String testHost, String testPort, String testDb, String testUser, String testPass) {
        String url = "jdbc:mysql://" + testHost + ":" + testPort + "/" + testDb + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        try (Connection conn = DriverManager.getConnection(url, testUser, testPass)) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public static void updateConfig(String newHost, String newPort, String newDb, String newUser, String newPass) {
        host = newHost;
        port = newPort;
        dbName = newDb;
        user = newUser;
        password = newPass;

        prefs.put(PREF_HOST, host);
        prefs.put(PREF_PORT, port);
        prefs.put(PREF_NAME, dbName);
        prefs.put(PREF_USER, user);
        prefs.put(PREF_PASS, password);
    }

    public static String getHost() {
        return host;
    }

    public static String getPort() {
        return port;
    }

    public static String getDbName() {
        return dbName;
    }

    public static String getUser() {
        return user;
    }

    public static String getPassword() {
        return password;
    }
}
