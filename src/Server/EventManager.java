package Server;

import Shared.Event;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Random;

public class EventManager {

    /**
     * this function is meant to register a user in an event
     * @param event
     * @param username
     * @param presenceCode
     * @return
     */
    public static boolean registerUserInEvent(Event event, String username, int presenceCode){

        if(!userAlreadyInEvent(event, username) && checkCode(event, presenceCode)){
            /* fzr isto dps do stamm fzr a base de dados
            return DatabaseManager.executeUpdate("INSERT INTO USERS (name, id, username, password)" +
                    " VALUES (" + register.getName()     + ", "
                    + register.getId()       + ", "
                    + register.getUsername() + ", "
                    + register.getPassword() + ");");*/
        }
        return false;
    }

    /**
     * This function will create a new event in the database
     * @param event
     * @return if success
     */
    public static boolean createEvent(Event event){

        if(!eventAlreadyExists(event.getDesignation())){
            return DatabaseManager.executeUpdate("INSERT INTO eventos (idevento, designation, place, date, time)" +
                    " VALUES ('"
                    + event.getPresenceCode()     + "', '" //temos que mudar dps
                    + event.getDesignation()      + "', '"
                    + event.getPlace()            + "', '"
                    + event.getDate()             + "', '"
                    + event.getTime()             + "');");
        }
        return false;
    }

    /**
     * This function will return a random number that has 6 digits
     * @return random number
     */
    public static int generateCode(){
        Random random = new Random();
        StringBuilder randomDigits = new StringBuilder();
        for(int i = 0; i < 6; i++){
            int digit = random.nextInt(10);
            randomDigits.append(digit);
        }
        return Integer.parseInt(randomDigits.toString());
    }


    /**
     * this is meant to receive multiple strings that will be our filters, AKA
     * SQL stuff that will be appended in one string that we can use in our querys
     * @param filters
     * @return string with filters
     */
    public static String createFilter(String ...filters){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" AND ");
        for (String filter : filters) {
            stringBuilder.append(filter);
            stringBuilder.append(" AND ");
        }
        stringBuilder.append(" ;");
        return stringBuilder.toString();
    }
    /**
     * This function will search the events that the user has been and will be able to have filters
     * @param username,filters
     */
    public static void queryEvents(String username, String filters){
        try{

            String query = (filters == null ? "SELECT * FROM eventos_utilizadores WHERE username = '" + username + "';" : "SELECT * FROM eventos WHERE username = '" + username +"'" + filters + ";");
            ResultSet rs = DatabaseManager.executeQuery(query);
            if(rs == null)
                return;
            ResultSetMetaData metaData = rs.getMetaData();
            int nColunas = metaData.getColumnCount();
            //escreve o nome das colunas na consola pro user saber o que raio está a ver
            for(int i = 1; i <= nColunas; i++){
                System.out.print(metaData.getColumnName(i) + ",");
            }
            System.out.println();
            //bora escrever as cenas todas
            while(rs.next()){
                for(int i = 1; i <= nColunas; i++){
                    System.out.println(rs.getString(i) + ",");
                }
                System.out.println();
            }
        }catch (SQLException sqlException){
            System.out.println("Error with the database: " + sqlException);
        }
    }

    /**
     * this function is meant to execute a query and store the result in a CSV file
     * @param query
     */
    public static void queryToCSV(String query){
        try{
            ResultSet rs = DatabaseManager.executeQuery(query);
            if(rs!=null){
                //para sacar o nome das colunas
                ResultSetMetaData metaData = rs.getMetaData();
                int nColunas = metaData.getColumnCount();
                FileWriter csv = new FileWriter("result.csv");

                // Write the column headers to the CSV file isto era do chatgpt lmao e adaptei dps tenho que testar
                /*for (int i = 1; i <= columnCount; i++) {
                    csvWriter.append(metaData.getColumnName(i));
                    if (i < columnCount) {
                        csvWriter.append(",");
                    } else {
                        csvWriter.append("\n");
                    }
                }*/
                //vamos escrever o nome das colunas no csv (talvez seja necessario nem sei)
                //começa no 1 pq a primeira cena é o 1, consultar a documentação do getColumnName
                for(int i = 1; i <= nColunas; i++){
                    csv.append(metaData.getColumnName(i));
                    csv.append(",");
                }
                csv.append("\n");
                //vamos escrever as cenas
                while(rs.next()){
                    for(int i = 1; i <= nColunas; i++){
                        csv.append(rs.getString(i));
                        csv.append(",");
                    }
                    csv.append("\n");
                }
                csv.close();
            }

        }catch (SQLException sqlException){
            System.out.println("Error with the database: " + sqlException);
        } catch (IOException e) {
            System.out.println("Error the IO: " + e);
        }
    }

    public static boolean eventAlreadyExists(String designation) {
        try{
            ResultSet rs = DatabaseManager.executeQuery("SELECT * FROM eventos WHERE designacao = '" + designation + "';");
            return rs != null ? rs.next() : false;
        }catch (SQLException sqlException){
            System.out.println("Error with the database: " + sqlException);
        }
        return false;
    }

    /**
     * this function returns either if a user is already in the event or not
     * @param event
     * @param username
     * @return
     */
    public static boolean userAlreadyInEvent(Event event, String username){
        try{
            ResultSet rs = DatabaseManager.executeQuery("SELECT * FROM eventos_utilizadores WHERE idevento = " + getIdEvent(event.getDesignation()) + "" +
                    " AND username ='" + username + "';");
            return rs != null ? rs.next() : false;
        }catch (SQLException sqlException){
            System.out.println("Error with the database: " + sqlException);
        }
        return false;
    }

    /**
     * this function is meant to check if the code the user introduced is valid or not
     * @param event
     * @param presenceCode
     * @return
     */
    public static boolean checkCode(Event event, int presenceCode){
        try{
            ResultSet rs = DatabaseManager.executeQuery("SELECT codigo FROM codigos_registo WHERE idevento = " + getIdEvent(event.getDesignation()) + ";");
            if(rs == null)
                return false;
            while(rs.next()){
                if(rs.getInt("codigo") == presenceCode)
                    return true;
            }
        }catch (SQLException sqlException){
            System.out.println("Error with the database: " + sqlException);
        }
        return false;
    }

    /**
     * this function is meant to return the ID of the event, given a specific designation,
     * returns 0 if it didnt find anything.
     * @param designacao
     * @return
     */
    public static int getIdEvent(String designacao){
        try{
            ResultSet rs = DatabaseManager.executeQuery("SELECT idevento FROM eventos WHERE designacao = '" + designacao + "';");
            while(rs.next()){
                return rs.getInt("idevento");
            }
        }catch (SQLException sqlException){
            System.out.println("Error with the database: " + sqlException);
        }
        return 0;
    }
}
