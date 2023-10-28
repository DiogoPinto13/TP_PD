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
     * This function will create a new event in the database
     * @param event
     * @return if success
     */
    public static boolean createEvent(Event event){

        if(!eventAlreadyExists(event.getDesignation())){
            return DatabaseManager.executeUpdate("INSERT INTO EVENTS (designation, place, date, time, presenceCode)" +
                    " VALUES (" + event.getDesignation()      + ", "
                    + event.getPlace()                        + ", "
                    + event.getDate()                         + ", "
                    + event.getTime()                         + ", "
                    + event.getPresenceCode()                 + ");");
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
     * @param username
     */
    public static void queryEvents(String username, String filters){
        try{
            String query = (filters == null ? "SELECT * FROM EVENTS WHERE USERNAME = " + username : "SELECT * FROM EVENTS WHERE USERNAME = " + username + filters);
            ResultSet rs = DatabaseManager.executeQuerry(query);
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

    public static void queryToCSV(String query){
        try{
            ResultSet rs = DatabaseManager.executeQuerry(query);
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
            }

        }catch (SQLException sqlException){
            System.out.println("Error with the database: " + sqlException);
        } catch (IOException e) {
            System.out.println("Error the IO: " + e);
        }
    }

    public static boolean eventAlreadyExists(String designation) {
        try{
            ResultSet rs = DatabaseManager.executeQuerry("SELECT * FROM EVENTS WHERE DESIGNATION = " + designation);
            return rs != null ? rs.next() : false;
        }catch (SQLException sqlException){
            System.out.println("Error with the database: " + sqlException);
        }
        return false;
    }
}
