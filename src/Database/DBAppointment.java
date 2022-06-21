package Database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Appointment;
import model.Customer;

import java.sql.*;
import java.time.*;

public class DBAppointment {
    private static ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    private static ObservableList<Appointment> customerAppointments = FXCollections.observableArrayList();
    private static ObservableList<Appointment> weeklyAppointments = FXCollections.observableArrayList();
    private static ObservableList<Appointment> monthlyAppointments = FXCollections.observableArrayList();
    private static ObservableList<Appointment> contactAppointments = FXCollections.observableArrayList();
    private static ObservableList<String> allTypes = FXCollections.observableArrayList();
    private static ObservableList<String> customerAppointmentExists = FXCollections.observableArrayList();
    private static boolean valid = true;
    public static boolean conflict = false;

    /**
     * getallappointments method
     * This is used to add all appointments to a returnable list
     * @return
     */
    public static ObservableList<Appointment> getAllAppointments(){
        try{
            allAppointments.clear();
            Statement statement = DBConnnection.getConnection().createStatement();
            String query = "SELECT * FROM client_schedule.appointments WHERE Title IS NOT NULL;";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()){
                Appointment appointment = new Appointment(resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        resultSet.getTimestamp(6),
                        resultSet.getTimestamp(7),
                        resultSet.getInt(12),
                        resultSet.getInt(13),
                        resultSet.getInt(14),
                        resultSet.getTimestamp(6).toLocalDateTime().toLocalDate(),
                        resultSet.getTimestamp(6).toLocalDateTime().toLocalTime(),
                        resultSet.getTimestamp(7).toLocalDateTime().toLocalTime()
                        );
                allAppointments.add(appointment);
            }
            statement.close();
            return allAppointments;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * addappointment method
     * adds appointment to database if no other appointments will collide
     * @param appointment
     * @return
     */
    public static boolean addAppointment(Appointment appointment){
        LocalDateTime possibleStart = appointment.getStart().toLocalDateTime();
        LocalDateTime possibleEnd = appointment.getEnd().toLocalDateTime();
        ZonedDateTime possibleStartZoned = possibleStart.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime possibleEndZoned = possibleEnd.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
        LocalDateTime testAppStart = possibleStartZoned.toLocalDateTime();
        LocalDateTime testAppEnd = possibleEndZoned.toLocalDateTime();
        valid = true;
        try{
            customerAppointments.clear();
            Statement statement = DBConnnection.getConnection().createStatement();
            String query = "SELECT * FROM client_schedule.appointments WHERE customer_ID=" + appointment.getCustomerID() + " AND Title IS NOT NULL;";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()){
                Appointment customerAppointment = new Appointment(resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        resultSet.getTimestamp(6),
                        resultSet.getTimestamp(7),
                        resultSet.getInt(12),
                        resultSet.getInt(13),
                        resultSet.getInt(14),
                        resultSet.getTimestamp(6).toLocalDateTime().toLocalDate(),
                        resultSet.getTimestamp(6).toLocalDateTime().toLocalTime(),
                        resultSet.getTimestamp(7).toLocalDateTime().toLocalTime()
                );
                customerAppointments.add(customerAppointment);
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        while (valid){
            int index = 0;
            while (index < customerAppointments.size()){
                Appointment app = customerAppointments.get(index);

                ZonedDateTime zonedDateTimeStart = app.getStart().toLocalDateTime().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
                ZonedDateTime zonedDateTimeEnd = app.getEnd().toLocalDateTime().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
                LocalDateTime startTime = zonedDateTimeStart.toLocalDateTime();
                LocalDateTime endTime = zonedDateTimeEnd.toLocalDateTime();

                if(((testAppStart.isAfter(startTime) || testAppStart.isEqual(startTime))) && testAppStart.isBefore(endTime)){
                    return false;
                }
                if(testAppEnd.isAfter(startTime) && ((testAppEnd.isBefore(endTime) || testAppEnd.isEqual(endTime)))){
                    return false;
                }
                if(((testAppStart.isBefore(startTime)) || testAppStart.isEqual(startTime)) && ((testAppEnd.isAfter(endTime) || testAppEnd.isEqual(endTime)))){
                    return false;
                }
                else{
                    index++;
                }
            }
            try{
                PreparedStatement preparedStatement = DBConnnection.getConnection().prepareStatement("INSERT INTO appointments VALUES(" + appointment.getAppointmentID() + ", '" +
                        appointment.getTitle() + "', '" + appointment.getDescription() + "', '" + appointment.getLocation() + "', '" + appointment.getType() + "', '" + Timestamp.valueOf(testAppStart)
                + "', '" + Timestamp.valueOf(testAppEnd) + "', NOW(), 'User', NOW(), 'User', " + appointment.getCustomerID() + ", " + appointment.getUserID() + ", " + appointment.getContactID() + ");");
                preparedStatement.executeUpdate();
                preparedStatement.close();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * modifyappointment method
     * modifies an appointment and checks to see if it will collide with any other existing appointments
     * @param appointment
     * @return
     */
    public static boolean modifyAppointment(Appointment appointment){
        LocalDateTime possibleStart = appointment.getStart().toLocalDateTime();
        LocalDateTime possibleEnd = appointment.getEnd().toLocalDateTime();
        ZonedDateTime possibleStartZoned = possibleStart.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime possibleEndZoned = possibleEnd.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
        LocalDateTime testAppStart = possibleStartZoned.toLocalDateTime();
        LocalDateTime testAppEnd = possibleEndZoned.toLocalDateTime();
        valid = true;
        try{
            customerAppointments.clear();
            Statement statement = DBConnnection.getConnection().createStatement();
            String query = "SELECT * FROM client_schedule.appointments WHERE customer_ID=" + appointment.getCustomerID() + " AND Title IS NOT NULL;";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()){
                conflict = false;
                Appointment customerAppointment = new Appointment(resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        resultSet.getTimestamp(6),
                        resultSet.getTimestamp(7),
                        resultSet.getInt(12),
                        resultSet.getInt(13),
                        resultSet.getInt(14),
                        resultSet.getTimestamp(6).toLocalDateTime().toLocalDate(),
                        resultSet.getTimestamp(6).toLocalDateTime().toLocalTime(),
                        resultSet.getTimestamp(7).toLocalDateTime().toLocalTime()
                );
                if(customerAppointment.getAppointmentID() == appointment.getAppointmentID()){
                    //does nothing with the appointment if it has the same id
                    conflict = true;
                }
                else if(!conflict){
                    customerAppointments.add(customerAppointment);
                }
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        while (valid){
            int index = 0;
            while (index < customerAppointments.size()){
                Appointment app = customerAppointments.get(index);

                ZonedDateTime zonedDateTimeStart = app.getStart().toLocalDateTime().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
                ZonedDateTime zonedDateTimeEnd = app.getEnd().toLocalDateTime().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
                LocalDateTime startTime = zonedDateTimeStart.toLocalDateTime();
                LocalDateTime endTime = zonedDateTimeEnd.toLocalDateTime();

                if(((testAppStart.isAfter(startTime) || testAppStart.isEqual(startTime))) && testAppStart.isBefore(endTime)){
                    return false;
                }
                if(testAppEnd.isAfter(startTime) && ((testAppEnd.isBefore(endTime) || testAppEnd.isEqual(endTime)))){
                    return false;
                }
                if(((testAppStart.isBefore(startTime)) || testAppStart.isEqual(startTime)) && ((testAppEnd.isAfter(endTime) || testAppEnd.isEqual(endTime)))){
                    return false;
                }
                else{
                    index++;
                }
            }
            try{
                PreparedStatement statement =DBConnnection.getConnection().prepareStatement("UPDATE client_schedule.appointments SET Title= '" + appointment.getTitle() + "" +
                        "', Description='" + appointment.getDescription() + "', Location='" + appointment.getLocation() + "', Type='" + appointment.getType() + "', Start='" + testAppStart + "', END='" +
                        testAppEnd + "', Create_Date=NOW(), Created_By='User', Last_Update=NOW(),  Last_Updated_By='USER', Customer_ID=" + appointment.getCustomerID() + ", User_ID=" + appointment.getUserID()
                        + ", Contact_ID=" + appointment.getContactID() + " WHERE Appointment_ID=" + appointment.getAppointmentID() + ";");
                statement.executeUpdate();
                statement.close();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    /**
     * getID method
     * This returns a unique id based off how many appointments are in the Database.
     * @return
     */
    public static int getID() {
        try {
            int lastID = 0;
            Statement statement = DBConnnection.getConnection().createStatement();
            String query = "SELECT Appointment_ID FROM client_schedule.appointments ORDER BY Appointment_ID;";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                if (resultSet.getInt(1) > lastID) {
                    lastID = resultSet.getInt(1);
                } else {
                    //do nothing if the id is less than last id
                }
            }
            lastID++;

            return lastID;
        } catch (SQLException sqlException) {
            return -1;
        }
    }

    /**
     * deleteappointment method
     * does a soft delete on the appointment provided
     * @param appointment
     * @return
     */
    public static boolean deleteAppointment(Appointment appointment){
        try{PreparedStatement statement =DBConnnection.getConnection().prepareStatement("UPDATE client_schedule.appointments SET Title=NULL, Description=NULL, Location=NULL, Type=NULL, Start=NULL, End=NULL," +
                        " Create_Date=NULL, Created_By=NULL, Last_Update=NULL,  Last_Updated_By=NULL, Customer_ID=1, User_ID=1, Contact_ID=1 WHERE Appointment_ID=" + appointment.getAppointmentID() + ";");
            statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * getweeklyappointments method
     * returns a list of appointments based of the local week
     * @return
     */
    public static ObservableList<Appointment> getWeeklyAppointments(){
        try{
            weeklyAppointments.clear();
            Statement statement = DBConnnection.getConnection().createStatement();
            String query = "SELECT * FROM client_schedule.appointments WHERE Title IS NOT NULL;";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()){
                Appointment appointment = new Appointment(resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        resultSet.getTimestamp(6),
                        resultSet.getTimestamp(7),
                        resultSet.getInt(12),
                        resultSet.getInt(13),
                        resultSet.getInt(14),
                        resultSet.getTimestamp(6).toLocalDateTime().toLocalDate(),
                        resultSet.getTimestamp(6).toLocalDateTime().toLocalTime(),
                        resultSet.getTimestamp(7).toLocalDateTime().toLocalTime()
                );
                LocalDateTime localDateTime = appointment.getStart().toLocalDateTime();
                LocalDateTime nowTime = LocalDateTime.now();
                ZonedDateTime nowTimeEST = nowTime.atZone(ZoneId.of("America/New_York"));
                LocalDateTime est = nowTimeEST.toLocalDateTime();
                if(localDateTime.isBefore(est.plusDays(7)) && localDateTime.isAfter(est)){
                    weeklyAppointments.add(appointment);
                }
                else {
                    //does nothing with the appointment if it is past 7 days out
                }
            }
            statement.close();
            return weeklyAppointments;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * getmonthlyappointments method
     * returns a list of monthly appointments for main screen
     * @return
     */
    public static ObservableList<Appointment> getMonthlyAppointments(){
        try{
            monthlyAppointments.clear();
            Statement statement = DBConnnection.getConnection().createStatement();
            String query = "SELECT * FROM client_schedule.appointments WHERE Title IS NOT NULL;";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()){
                Appointment appointment = new Appointment(resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        resultSet.getTimestamp(6),
                        resultSet.getTimestamp(7),
                        resultSet.getInt(12),
                        resultSet.getInt(13),
                        resultSet.getInt(14),
                        resultSet.getTimestamp(6).toLocalDateTime().toLocalDate(),
                        resultSet.getTimestamp(6).toLocalDateTime().toLocalTime(),
                        resultSet.getTimestamp(7).toLocalDateTime().toLocalTime()
                );
                LocalDateTime localDateTime = appointment.getStart().toLocalDateTime();
                LocalDateTime nowTime = LocalDateTime.now();
                ZonedDateTime nowTimeEST = nowTime.atZone(ZoneId.of("America/New_York"));
                LocalDateTime est = nowTimeEST.toLocalDateTime();
                if(localDateTime.isBefore(est.plusDays(30)) && localDateTime.isAfter(est)){
                    monthlyAppointments.add(appointment);
                }
                else {
                    //does nothing with the appointment if it is past 30 days out
                }
            }
            statement.close();
            return monthlyAppointments;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * gettypes method
     * returns a list of types for reports screen
     * @return
     * @throws SQLException
     */
    public static ObservableList<String> getTypes() throws SQLException {
        allTypes.clear();
        try {
            Statement statement = DBConnnection.getConnection().createStatement();
            String query = "SELECT Type FROM client_schedule.appointments WHERE Title IS NOT NULL;";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                allTypes.add(resultSet.getString(1));
            }
            return allTypes;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * getmonthtypenumber method
     * used for reports screen to get the number of appointments that match the month year and type provided
     * @param localDateTime
     * @param type
     * @return
     */
    public static int getMonthTypeNumber(LocalDateTime localDateTime, String type){
        int returnNumber = 0;
        try{
            Statement statement = DBConnnection.getConnection().createStatement();
            String query = "SELECT * FROM client_schedule.appointments WHERE Title IS NOT NULL;";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()){
                LocalDateTime YearAndMonth = resultSet.getTimestamp("Start").toLocalDateTime();
                String appointmentType = resultSet.getString("Type");
                if((localDateTime.getYear() == YearAndMonth.getYear()) && (localDateTime.getMonth() == YearAndMonth.getMonth()) && (type.equals(appointmentType))){
                    returnNumber++;
                }
                else {
                    // do nothing if the types are not the same
                }
            }
            return returnNumber;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * getcontactappointments method
     * returns a list of appointments for the contact provided
     * @param contact
     * @return
     */
    public static ObservableList<Appointment> getContactAppointments(String contact){
        int contactID = DBcontacts.getContactID(contact);
        try{
            contactAppointments.clear();
            Statement statement = DBConnnection.getConnection().createStatement();
            String query = "SELECT * FROM client_schedule.appointments WHERE Contact_ID=" + contactID + " AND Title IS NOT NULL;";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()){
                Appointment appointment = new Appointment(resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        resultSet.getTimestamp(6),
                        resultSet.getTimestamp(7),
                        resultSet.getInt(12),
                        resultSet.getInt(13),
                        resultSet.getInt(14),
                        resultSet.getTimestamp(6).toLocalDateTime().toLocalDate(),
                        resultSet.getTimestamp(6).toLocalDateTime().toLocalTime(),
                        resultSet.getTimestamp(7).toLocalDateTime().toLocalTime()
                );
                contactAppointments.add(appointment);
            }
            statement.close();
            return contactAppointments;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * checkforappointments method
     * used to see if the customer provided has any appointments in the database
     * with return false if appointments exists within the database
     * @param selectedCustomer
     * @return
     */
    public static boolean checkForAppointments(Customer selectedCustomer){
        int customerID = selectedCustomer.getID();
        customerAppointmentExists.clear();
        try{
            Statement statement = DBConnnection.getConnection().createStatement();
            String query = "SELECT * FROM client_schedule.appointments WHERE Customer_ID=" + customerID + " AND Title IS NOT NULL;";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()){
                customerAppointmentExists.add(resultSet.getString(2));
            }
            if(customerAppointmentExists.isEmpty()){
                return true;
            }
            else {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
