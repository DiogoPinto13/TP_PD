package Server;

import java.sql.*;


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
        String sql = "CREATE TABLE IF NOT EXISTS warehouses (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	name text NOT NULL,\n"
                + "	capacity real\n"
                + ");";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * This function is meant to clear all tables from the database
     */
    public static void ClearDatabase(){
        //there is no DROP DATABASE, so we have to drop each table manually...
        String sql = "DROP TABLE IF EXISTS warehouses;";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
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
    public static ResultSet executeQuerry(String query) {
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