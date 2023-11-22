package Server;

import Shared.ErrorMessages;
import Shared.Event;
import Shared.EventResult;
import Shared.Time;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;

public class EventManager {

    /**
     * this function is meant to register a user in an event
     * @param username
     * @param presenceCode
     * @return
     */
    public static boolean registerUserInEvent(String username, int presenceCode){
        if(!userAlreadyInEvent(username) && checkCode(presenceCode)){
            return DatabaseManager.executeUpdate("INSERT INTO eventos_utilizadores (idevento,username) " +
                    "VALUES ("
                    + getIdEventByPresenceCode(presenceCode) + ", '"
                    + username + "');");
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
            return DatabaseManager.executeUpdate("INSERT INTO eventos (designacao, place, horaInicio, horaFim)" +
                    " VALUES ('"
                    + event.getDesignation()            + "', '"
                    + event.getPlace()                  + "', '"
                    + event.getTimeBegin().toString()   + "', '"
                    + event.getTimeEnd().toString()     + "');");
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
     * This function is meant to register a new presence code (in a new event), given a specific idevento
     * @param event
     * @return
     */
    public static String registerPresenceCode(Event event, int duracao, Time atual){
        if(isBetweenTime(event.getTimeBegin(), event.getTimeEnd(), atual)){
            int code = generateCode();
            return (!DatabaseManager.executeUpdate("INSERT INTO codigos_registo (codigo, duracao, idevento, horaRegisto)" +
                    " VALUES ("
                    + code                                            + ", "
                    + duracao                                         + ", "
                    + getIdEventByDesignation(event.getDesignation()) + ", '"
                    + (atual.toString())                              + "');")) ? String.valueOf(code) : ErrorMessages.FAIL_REGISTER_PRESENCE_CODE.toString();
        }
        return ErrorMessages.FAIL_REGISTER_PRESENCE_CODE.toString();
    }

    public static boolean checkIfCodeAlreadyCreated(String designation){
        try(ResultSet rs = DatabaseManager.executeQuery("SELECT codigo FROM codigos_registo WHERE idevento = " + getIdEventByDesignation(designation) + ";")){
            return rs != null ? rs.next() : false;
        }catch (SQLException sqlException){
            System.out.println("Error with the database: " + sqlException);
        }
        return false;
    }
    /**
     * this function is meant to update the presenceCode associated to a given event
     * @param presenceCode
     * @param presenceCodeDuration
     * @param designation
     * @return
     */
    public static boolean updatePresenceCode(int presenceCode, int presenceCodeDuration , String designation){
        return DatabaseManager.executeUpdate("UPDATE codigos_registo SET codigo = " + presenceCode + ", duracao = " + presenceCodeDuration +" WHERE idevento = " + getIdEventByDesignation(designation) + ";");
    }

    /**
     * this is meant to receive multiple strings that will be our filters, AKA
     * SQL stuff that will be appended in one string that we can use in our querys
     * @param ids
     * @return string with filters
     */
    public static String createFilterOr(ArrayList<Integer> ids){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" WHERE idevento = ");
        for(int i = 0; i < ids.size(); i++){
            stringBuilder.append(ids.get(i));
            if(i != ids.size() - 1)
                stringBuilder.append(" OR idevento =  ");
        }
        stringBuilder.append(";");
        return stringBuilder.toString();
    }
    /**
     * this is meant to receive multiple strings that will be our filters, AKA
     * SQL stuff that will be appended in one string that we can use in our querys
     * @param filters
     * @return string with filters
     */
    public static String createFilterAnd(String ...filters){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" AND ");
        for (String filter : filters) {
            stringBuilder.append(filter);
            stringBuilder.append(" AND ");
        }
        stringBuilder.append(";");
        return stringBuilder.toString();
    }
    /**
     * This function will search the events that the user has been and will be able to have filters
     * @param username,filters
     * @return String a string completely filled
     */
    public static EventResult queryEvents(String username, String filters){
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder stringBuilderData = new StringBuilder();
        String query = (filters == null || username == null ? "SELECT * FROM eventos;" : "SELECT * FROM eventos " + filters + ";");
        try(ResultSet rs = DatabaseManager.executeQuery(query)){
            if(rs == null)
                return null;
            ResultSetMetaData metaData = rs.getMetaData();
            int nColunas = metaData.getColumnCount();
            //escreve o nome das colunas
            for(int i = 1; i <= nColunas; i++){
                stringBuilder.append(metaData.getColumnName(i)).append(",");
            }
            //stringBuilder.append("\n");
            //bora escrever as cenas todas
            EventResult eventResult = new EventResult(stringBuilder.toString());

            while(rs.next()){
                stringBuilderData.setLength(0);
                for(int i = 1; i <= nColunas; i++){
                    stringBuilderData.append(rs.getString(i));
                    stringBuilderData.append(",");
                }
                eventResult.events.add(stringBuilderData.toString());
            }
            //eventResult.events.add(stringBuilderData.toString());
            return eventResult;
        }catch (SQLException sqlException){
            System.out.println("Error with the database: " + sqlException);
        }
        return null;
    }

    /**
     * this function is meant to retrieve the information of an event, given a specific designation
     * to fill info in the UI
     * @param designation
     * @return
     */
    public static String getEventInfo(String designation){
        StringBuilder stringBuilder = new StringBuilder();
        String query =  "SELECT designacao, place, horaInicio, horaFim FROM eventos WHERE idevento = " + getIdEventByDesignation(designation) +";";
        try(ResultSet rs = DatabaseManager.executeQuery(query)) {
            if(rs == null)
                return ErrorMessages.INVALID_EVENT_NAME.toString();

            while(rs.next()){
                stringBuilder.append(rs.getString("designacao")).append(",");
                stringBuilder.append(rs.getString("place")).append(",");
                stringBuilder.append(rs.getString("horaInicio")).append(",");
                stringBuilder.append(rs.getString("horaFim"));
            }
            return stringBuilder.toString();
        }catch (SQLException sqlException){
            System.out.println("Error with the database: " + sqlException);
        }
        return ErrorMessages.INVALID_EVENT_NAME.toString();
    }
    /**
     * this function is meant to execute a query and store the result in a CSV file
     * @param query
     */
    public static void queryToCSV(String query){
        try(ResultSet rs = DatabaseManager.executeQuery(query)){
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
        try(ResultSet rs = DatabaseManager.executeQuery("SELECT * FROM eventos WHERE designacao = '" + designation + "';")){
            return rs != null ? rs.next() : false;
        }catch (SQLException sqlException){
            System.out.println("Error with the database: " + sqlException);
        }
        return false;
    }

    /**
     * this function returns either if a user is already in the event or not
     * @param username
     * @return
     */
    public static boolean userAlreadyInEvent(String username){
        try(ResultSet rs = DatabaseManager.executeQuery("SELECT * FROM eventos_utilizadores WHERE idevento = " + getIdEventByUsername(username) +
                " AND username ='" + username + "';")){
            return rs != null ? rs.next() : false;
        }catch (SQLException sqlException){
            System.out.println("Error with the database: " + sqlException);
        }
        return false;
    }

    /**
     * this function is meant to check if the code the user introduced is valid or not
     * @param presenceCode
     * @return
     */
    public static boolean checkCode(int presenceCode){
        try(ResultSet rs = DatabaseManager.executeQuery("SELECT codigo FROM codigos_registo WHERE idevento = " + getIdEventByPresenceCode(presenceCode) + ";")){
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
    public static int getIdEventByDesignation(String designacao){
        try(ResultSet rs = DatabaseManager.executeQuery("SELECT idevento FROM eventos WHERE designacao = '" + designacao + "';")){
            while(rs.next()){
                return rs.getInt("idevento");
            }
        }catch (SQLException sqlException){
            System.out.println("Error with the database: " + sqlException);
        }
        return 0;
    }
    /**
     * this function is meant to return the ID of the event, given a specific username,
     * returns 0 if it didnt find anything.
     * @param username
     * @return
     */
    public static int getIdEventByUsername(String username){
        try(ResultSet rs = DatabaseManager.executeQuery("SELECT idevento FROM eventos_utilizadores WHERE username = '" + username + "';")){
            while(rs.next()){
                return rs.getInt("idevento");
            }
        }catch (SQLException sqlException){
            System.out.println("Error with the database: " + sqlException);
        }
        return 0;
    }
    /**
     * this function is meant to return all IDs of all events, given a specific username,
     * returns null if it didnt find anything.
     * @param username
     * @return null
     */
    public static ArrayList<Integer> getIdsEventsByUsername(String username){
        ArrayList<Integer> ids = new ArrayList<Integer>();
        try(ResultSet rs = DatabaseManager.executeQuery("SELECT idevento FROM eventos_utilizadores WHERE username = '" + username + "';")){
            while(rs.next()){
                ids.add(rs.getInt("idevento"));
            }
            return ids;
        }catch (SQLException sqlException){
            System.out.println("Error with the database: " + sqlException);
        }
        return null;
    }
    /**
     * this function is meant to return the ID of the event, given a specific presenceCode,
     * returns 0 if it didnt find anything.
     * @param presenceCode
     * @return
     */
    public static int getIdEventByPresenceCode(int presenceCode){
        try(ResultSet rs = DatabaseManager.executeQuery("SELECT idevento FROM codigos_registo WHERE codigo = '" + presenceCode + "';")){
            while(rs.next()){
                return rs.getInt("idevento");
            }
        }catch (SQLException sqlException){
            System.out.println("Error with the database: " + sqlException);
        }
        return 0;
    }

    /**
     * this function returns either if a user is already in the event or not
     * @param designation
     * @return
     */
    public static boolean usersInEvent(String designation){
        try(ResultSet rs = DatabaseManager.executeQuery("SELECT * FROM eventos_utilizadores WHERE idevento = " + getIdEventByDesignation(designation) + ";")){
            return rs != null && rs.next();
        }catch (SQLException sqlException){
            System.out.println("Error with the database: " + sqlException);
        }
        return false;
    }
    /**
     * this function is meant to delete an event, passing the name of the event by argument
     * returns true or false if success
     * @param designation
     * @return
     */
    public static boolean deleteEvent(String designation){
        if(!usersInEvent(designation)){
            return DatabaseManager.executeUpdate("DELETE FROM eventos WHERE idevento = " +
                    getIdEventByDesignation(designation) + ";");
        }
        return false;
    }

    /**
     * this function is meant to delete manually a user from a event
     * @param eventDesignation
     * @param username
     * @return
     */
    public static boolean deleteManualPresences(String eventDesignation, String username){
        if(usersInEvent(eventDesignation)){
            return DatabaseManager.executeUpdate("DELETE from eventos_utilizadores WHERE idevento = " +
                    getIdEventByDesignation(eventDesignation) + " AND username = '" + username + "';");
        }
        return false;
    }

    /**
     * this function is meant to verify if a date is between the begginng and the end date
     * @param inicio
     * @param fim
     * @param atual
     * @return
     */
    public static boolean isBetweenTime(Time inicio, Time fim, Time atual) {
        LocalDateTime inicioLocalDateTime = LocalDateTime.of(inicio.getYear(), inicio.getMonth(), inicio.getDay(), inicio.getHour(), inicio.getMinute());
        LocalDateTime fimLocalDateTime = LocalDateTime.of(fim.getYear(), fim.getMonth(), fim.getDay(), fim.getHour(), fim.getMinute());
        LocalDateTime atualLocalDateTime = LocalDateTime.of(atual.getYear(), atual.getMonth(), atual.getDay(), atual.getHour(), atual.getMinute());
        return atualLocalDateTime.isAfter(inicioLocalDateTime) && atualLocalDateTime.isBefore(fimLocalDateTime);
    }

    /**
     * this function is meant to get the time as a string to be used in other functions, it receives the designation of the event
     * @param designation
     * @return
     */
    public static String getTime(String designation){
        StringBuilder stringBuilder = new StringBuilder();
        try(ResultSet rs = DatabaseManager.executeQuery("SELECT horaInicio, horaFim FROM eventos WHERE idevento = '" + getIdEventByDesignation(designation) + "';")){
            while(rs.next()){

                stringBuilder.append(rs.getString("horaInicio").toString()).append(",");
                stringBuilder.append(rs.getString("horaFim").toString());
            }
            System.out.println(stringBuilder.toString());
            return stringBuilder.toString();
        }catch (SQLException sqlException){
            System.out.println("Error with the database: " + sqlException);
        }
        return "NULL";
    }

    public static boolean checkPresences(String designation){
        return usersInEvent(designation);
    }

    public static EventResult getPresencesEvent(String designation) {
        StringBuilder stringBuilder = new StringBuilder();
        String query =  "SELECT utilizadores.nome, utilizadores.idutilizador, utilizadores.username " +
                "FROM utilizadores, eventos_utilizadores  " +
                "WHERE utilizadores.username = eventos_utilizadores.username" +
                " AND eventos_utilizadores.idevento = " + getIdEventByDesignation(designation) +";";
        try(ResultSet rs = DatabaseManager.executeQuery(query)){
            if(rs == null)
                return null;
            ResultSetMetaData metaData = rs.getMetaData();
            int nColunas = metaData.getColumnCount();
            //escreve o nome das colunas
            for(int i = 1; i <= nColunas; i++){
                stringBuilder.append(metaData.getColumnName(i)).append(",");
            }
            //stringBuilder.append("\n");
            //bora escrever as cenas todas
            EventResult eventResult = new EventResult(stringBuilder.toString());

            while(rs.next()){
                stringBuilder.setLength(0);
                for(int i = 1; i <= nColunas; i++){
                    stringBuilder.append(rs.getString(i));
                    stringBuilder.append(",");
                }
                eventResult.events.add(stringBuilder.toString());
            }
            //eventResult.events.add(stringBuilderData.toString());
            return eventResult;
        }catch (SQLException sqlException){
            System.out.println("Error with the database: " + sqlException);
        }
        return null;
    }

    public static boolean editEvent(Event event) {
        return DatabaseManager.executeUpdate("UPDATE eventos SET horaInicio = '" + event.getTimeBegin().toString() + "', horaFim = '" + event.getTimeEnd().toString() +"' WHERE idevento = " + getIdEventByDesignation(event.getDesignation()) + ";");
    }
}
