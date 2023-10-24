package Server;

import Shared.Register;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserManager {

    public static boolean registerUser(Register register) throws SQLException {

        //acabar amanha estou prestes a morrer tbh
        if(!userExists(register.getUsername())){
            return DatabaseManager.executeUpdate("INSERT INTO USERNAME BLABLLBALBA");
        }

        return false;
    }

    public static boolean userExists(String username) throws SQLException {
        ResultSet rs = DatabaseManager.executeQuerry("SELECT * FROM USERS WHERE USERNAME = " + username);
        return rs != null ? rs.next() : false;
    }
}
