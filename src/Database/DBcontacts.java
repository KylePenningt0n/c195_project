package Database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBcontacts {
    private static ObservableList<String> allContacts = FXCollections.observableArrayList();

    /**
     * getAllContacts method
     * used to return a list of all contacts
     * @return
     */
    public static ObservableList<String> getAllContacts(){
        allContacts.clear();
        try{
            Statement statement = DBConnnection.getConnection().createStatement();
            String query = "SELECT * FROM client_schedule.contacts;";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()){
                String contactName = resultSet.getString(2);
                allContacts.add(contactName);
            }
            statement.close();
            return allContacts;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * getContactName method
     * used to return conctact name based off of contactid provided
     * @param contactID
     * @return
     */
    public static String getContactName(int contactID){
        try{
            Statement statement = DBConnnection.getConnection().createStatement();
            String query = "SELECT Contact_Name FROM client_schedule.contacts WHERE Contact_ID=" + contactID + ";";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()){
                return resultSet.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * getContactID method
     * used to return contact id from contactName provided
     * @param contactName
     * @return
     */
    public static int getContactID(String contactName){
        try{
            Statement statement = DBConnnection.getConnection().createStatement();
            String query = "SELECT Contact_ID FROM client_schedule.contacts WHERE Contact_Name = '" + contactName + "';";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()){
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}