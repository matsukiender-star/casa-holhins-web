package mx.holhins.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.sql.Statement;
import java.util.stream.Collectors;

public class ConexionDB {
    private static final String URL = "jdbc:h2:./data/holhins;INIT=RUNSCRIPT FROM 'classpath:schema.sql'\\;RUNSCRIPT FROM 'classpath:data.sql'";
    private static final String URL_TEST = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'classpath:schema.sql'\\;RUNSCRIPT FROM 'classpath:data.sql'";
    
    private static final String USER = "sa";
    private static final String PASSWORD = "";
    
    private static boolean isTest = false;
    
    public static void setTestMode(boolean testMode) {
        isTest = testMode;
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(isTest ? URL_TEST : URL, USER, PASSWORD);
    }
}
