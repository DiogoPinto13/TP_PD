package Server;

import Shared.Login;
import Shared.Register;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class has a bunch of static methods to perform operations in the database to perform
 * updates and checks for users
 */
public class UserManager {

    /**
     * This method will register an user in the database, it will return true if the operation
     * succeed or false if the user already exists or an error occurred
     * @param register
     * @return boolean
     * @throws SQLException
     */
    public static boolean registerUser(Register register) throws SQLException {
        if(!userExists(register.getUsername())){
            return DatabaseManager.executeUpdate("INSERT INTO USERS (name, id, username, password)" +
                    " VALUES (" + register.getName()     + ", "
                                + register.getId()       + ", "
                                + register.getUsername() + ", "
                                + register.getPassword() + ");");
        }
        return false;
    }

    /**
     * This method will return true if the password is correct or otherwise false
     * @param login
     * @return boolean
     * @throws SQLException
     */
    public static boolean checkPassword(Login login) throws SQLException {
        //I m doing this check here just because if it doesnt exist why should the program waste time connecting to the database to execute a query for a user that we already know it doesnt exist?
        if(!userExists(login.getUsername())){
            ResultSet rs = DatabaseManager.executeQuerry("SELECT PASSWORD FROM USERS WHERE USERNAME = " + login.getUsername());
            if(rs==null)
                return false;
            while(rs.next()){
                String password = rs.getString("PASSWORD");
                return login.getPassword().equals(password);
            }
        }
        return false;
    }

    /**
     * This method is to check if a user exists in the database or not, returning true or false
     * @param username
     * @return boolean
     * @throws SQLException
     */
    public static boolean userExists(String username) throws SQLException {
        ResultSet rs = DatabaseManager.executeQuerry("SELECT * FROM USERS WHERE USERNAME = " + username);
        return rs != null ? rs.next() : false;
    }
}
