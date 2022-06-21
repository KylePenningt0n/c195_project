package Database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBDivision {
    private static ObservableList<String> usDivisions = FXCollections.observableArrayList();
    private static ObservableList<String> ukDivisions = FXCollections.observableArrayList();
    private static ObservableList<String> canadaDivisions = FXCollections.observableArrayList();
    private static int divisionID = 0;


    /**
     * getusdivisions method
     * this returns every division in the US from the database
     * @return
     */
    public static ObservableList<String> getUsDivisions(){
        try{ usDivisions.clear();
            Statement statement = DBConnnection.getConnection().createStatement();
            String query = "SELECT Division FROM client_schedule.first_level_divisions where Country_ID = 1;";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()){
                String divisionName = resultSet.getString("Division");
                usDivisions.add(divisionName);
            }
            return usDivisions;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * getukdivisions method
     * This returns every division apart of the UK
     * @return
     */
    public static ObservableList<String> getUkDivisions(){
        try{ukDivisions.clear();
            Statement statement = DBConnnection.getConnection().createStatement();
            String query = "SELECT Division FROM client_schedule.first_level_divisions where Country_ID = 2;";
            ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()){
                String divisionName = resultSet.getString("Division");
                ukDivisions.add(divisionName);
        }
            return ukDivisions;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * getcanadadivisions method
     * This returns a list of all divisions in canada
     * @return
     */
    public static ObservableList<String> getCanadaDivisions(){
        try{canadaDivisions.clear();
            Statement statement = DBConnnection.getConnection().createStatement();
            String query = "SELECT Division FROM client_schedule.first_level_divisions where Country_ID = 3;";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()){
                String divisionName = resultSet.getString("Division");
                canadaDivisions.add(divisionName);
            }
            return canadaDivisions;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * getDivisionInt method
     * This method returns the division ID from the database based on the division name provided by the combo box in the form.
     * @param divisionName
     * @return
     */
    public static int getDivisionInt(String divisionName){
        try {
            Statement statement = DBConnnection.getConnection().createStatement();
            String query = "SELECT Division_ID FROM client_schedule.first_level_divisions WHERE Division='" + divisionName + "';";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()){
                divisionID = resultSet.getInt(1);
            }
            return divisionID;

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

    }
}
