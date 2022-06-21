package Database;


import Database.DBConnnection;
import model.User;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBlogin {
    private static User testUsr;

    /**
     * submit method
     * This Method trys to find a username and password math in the database
     *
     * @param Username this is collected from the login form
     * @param Password Collected from login form
     * @return returns value true if username and password are found
     */
    public static Boolean submit(String Username, String Password) {
        try {
            Statement statement = DBConnnection.getConnection().createStatement();
            String sql = "SELECT * FROM client_schedule.users WHERE User_Name='" + Username + "' AND Password='" + Password + "'";
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                testUsr = new User();
                testUsr.setUsername(resultSet.getString("User_Name"));
                statement.close();
                return true;

            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * validUser method
     * returns true if the username exists withing the database
     * @param Username
     * @return
     */
    public static Boolean validUser(String Username) {
        try {
            Statement statement = DBConnnection.getConnection().createStatement();
            String sql = "SELECT * FROM client_schedule.users WHERE User_Name='" + Username + "'";
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                testUsr = new User();
                testUsr.setUsername(resultSet.getString("User_Name"));
                statement.close();
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * getUserID method
     * returns user id based off username provided
     * @param userName
     * @return
     */
    public static int getUserID(String userName) {
        try {
            Statement statement = DBConnnection.getConnection().createStatement();
            String sql = "SELECT User_ID FROM client_schedule.users WHERE User_Name ='" + userName + "';";
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()){
                int userid = resultSet.getInt(1);
                return userid;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
