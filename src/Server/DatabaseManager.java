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


/*
CREATE SCHEMA IF NOT EXISTS `ClassCodes` DEFAULT CHARACTER SET utf8 ;
USE `ClassCodes` ;

CREATE TABLE IF NOT EXISTS `ClassCodes`.`eventos` (
  `idevento` INT NOT NULL AUTO_INCREMENT,
  `designacao` VARCHAR(255) NOT NULL,
  `place` VARCHAR(255) NOT NULL,
  `datetime` DATETIME NOT NULL,
  PRIMARY KEY (`idevento`))
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `ClassCodes`.`utilizadores` (
  `idutilizador` VARCHAR(255) NOT NULL,
  `username` VARCHAR(255) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `nome` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`idutilizador`),
  UNIQUE INDEX `username_UNIQUE` (`username` ASC),
  UNIQUE INDEX `idutilizador_UNIQUE` (`idutilizador` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `ClassCodes`.`codigos_registo`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ClassCodes`.`codigos_registo` (
  `idcodigo_registo` INT NOT NULL AUTO_INCREMENT,
  `codigo` INT NOT NULL,
  `idevento` INT NOT NULL,
  PRIMARY KEY (`idcodigo_registo`, `idevento`),
  INDEX `fk_codigos_registo_eventos1_idx` (`idevento` ASC),
  CONSTRAINT `fk_codigos_registo_eventos1`
    FOREIGN KEY (`idevento`)
    REFERENCES `ClassCodes`.`eventos` (`idevento`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `ClassCodes`.`eventos_utilizadores`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ClassCodes`.`eventos_utilizadores` (
  `idevento` INT NOT NULL,
  `idutilizador` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`idevento`, `idutilizador`),
  INDEX `fk_eventos_has_utilizadores_utilizadores1_idx` (`idutilizador` ASC),
  INDEX `fk_eventos_has_utilizadores_eventos_idx` (`idevento` ASC),
  CONSTRAINT `fk_eventos_has_utilizadores_eventos`
    FOREIGN KEY (`idevento`)
    REFERENCES `ClassCodes`.`eventos` (`idevento`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_eventos_has_utilizadores_utilizadores1`
    FOREIGN KEY (`idutilizador`)
    REFERENCES `ClassCodes`.`utilizadores` (`idutilizador`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

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

    public static void testUser(){
        try (Connection conn = DriverManager.getConnection(url);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT password FROM utilizadores WHERE username = 'a2020133653@isec.pt';")) {
            while(rs.next()){
                System.out.println(
                        //rs.getInt("idutilizador") + "," +
                          //      rs.getString("username")   + "," +
                                rs.getString("password")   + ",");
                            //    rs.getString("nome"));
            }
        } catch (SQLException e) {
            System.out.println("error while executing the query: " + e.getMessage());
        }
    }
    /**
     * this function receives a string, which will be the query we want to execute and returns
     * a result set
     * @param query
     * @return ResultSet
     */
    public static synchronized ResultSet executeQuery(String query) throws SQLException {
        Connection conn = DriverManager.getConnection(url);
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(query);
    }
    /**
     * this function receives a string, which will be the query we want to execute, and returns
     * if the operation either succeeded or not
     * @param query query
     * @return Boolean if success
     */
    public static synchronized boolean executeUpdate(String query) {
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            return stmt.execute(query);
        } catch (SQLException e) {
            System.out.println("error while executing the update: " + e.getMessage());
        }
        return false;
    }
}