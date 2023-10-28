package Server;

import java.sql.*;
/*
-- Tabela 'eventos'
CREATE TABLE IF NOT EXISTS eventos (
  idevento INTEGER PRIMARY KEY AUTOINCREMENT,
  designacao TEXT NOT NULL,
  place TEXT NOT NULL,
  datetime TEXT NOT NULL
);

-- Tabela 'utilizadores'
CREATE TABLE IF NOT EXISTS utilizadores (
  idutilizador TEXT NOT NULL PRIMARY KEY,
  username TEXT NOT NULL UNIQUE,
  password TEXT NOT NULL,
  nome TEXT NOT NULL
);

-- Tabela 'codigos_registo'
CREATE TABLE IF NOT EXISTS codigos_registo (
  idcodigo_registo INTEGER PRIMARY KEY AUTOINCREMENT,
  codigo INTEGER NOT NULL,
  idevento INTEGER NOT NULL,
  FOREIGN KEY (idevento) REFERENCES eventos (idevento)
);

-- Tabela 'eventos_utilizadores'
CREATE TABLE IF NOT EXISTS eventos_utilizadores (
  idevento INTEGER NOT NULL,
  idutilizador TEXT NOT NULL,
  PRIMARY KEY (idevento, idutilizador),
  FOREIGN KEY (idevento) REFERENCES eventos (idevento),
  FOREIGN KEY (idutilizador) REFERENCES utilizadores (idutilizador)
);

 */



public class DatabaseManager {
    private final static String url = "jdbc:sqlite:./Database/tp.db";

    public static void createNewDatabase() {

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * A method to create a connection to the database
     */
    public static void connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public static void createNewTable() {
        //eventos, utilizadores, codigos_registo, eventos_utilizadores

        String eventos = "CREATE TABLE IF NOT EXISTS eventos (\n" +
                "  idevento INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "  designacao TEXT NOT NULL,\n" +
                "  place TEXT NOT NULL,\n" +
                "  datetime TEXT NOT NULL\n" +
                ");";
        String utilizadores = "CREATE TABLE IF NOT EXISTS utilizadores (\n" +
                "  idutilizador TEXT NOT NULL UNIQUE,\n" +
                "  username TEXT NOT NULL PRIMARY KEY,\n" +
                "  password TEXT NOT NULL,\n" +
                "  nome TEXT NOT NULL\n" +
                ");";
        String codigos_registo = "CREATE TABLE IF NOT EXISTS codigos_registo (\n" +
                "  idcodigo_registo INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "  codigo INTEGER NOT NULL,\n" +
                "  idevento INTEGER NOT NULL,\n" +
                "  FOREIGN KEY (idevento) REFERENCES eventos (idevento)\n" +
                ");";
        String eventos_utilizadores = "CREATE TABLE IF NOT EXISTS eventos_utilizadores (\n" +
                "  idevento INTEGER NOT NULL,\n" +
                "  username TEXT NOT NULL,\n" +
                "  PRIMARY KEY (idevento, username),\n" +
                "  FOREIGN KEY (idevento) REFERENCES eventos (idevento),\n" +
                "  FOREIGN KEY (username) REFERENCES utilizadores (username)\n" +
                ");";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(eventos);
            stmt.execute(utilizadores);
            stmt.execute(codigos_registo);
            stmt.execute(eventos_utilizadores);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * This function is meant to clear all tables from the database
     */
    public static void clearDatabase(){
        //there is no DROP DATABASE, so we have to drop each table manually...
        String eventos = "DROP TABLE IF EXISTS eventos;";
        String utilizadores ="DROP TABLE IF EXISTS utilizadores;";
        String codigos_registo = "DROP TABLE IF EXISTS codigos_registo;";
        String eventos_utilizadores = "DROP TABLE IF EXISTS eventos_utilizadores;";
        //eventos, utilizadores, codigos_registo, eventos_utilizadores
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS warehouses");
            stmt.execute(eventos);
            stmt.execute(utilizadores);
            stmt.execute(codigos_registo);
            stmt.execute(eventos_utilizadores);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * this function receives a string, which will be the query we want to executem and returns
     * a result set
     * @param query
     * @return ResultSet
     */
    public static ResultSet executeQuery(String query) {
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // create a new table
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    /**
     * this function receives a string, which will be the query we want to execute, and returns
     * if the operation either succeeded or not
     * @param query query
     * @return Boolean if success
     */
    public static boolean executeUpdate(String query) {
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // create a new table
            return stmt.execute(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}