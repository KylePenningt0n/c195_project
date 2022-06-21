package controller;

import Database.DBAppointment;
import Database.DBcustomer;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Appointment;
import model.Customer;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;


public class MainScreenController implements Initializable {
    public TableView customersTable;
    public TableColumn customerNameColumn;
    public TableColumn customerAddressColumn;
    public Button addCustomer;
    public Button modifyCustomer;
    public Button deleteCustomer;
    public Button exit;
    public TableColumn customerCountryColumn;
    public TableColumn customerDivisionColumn;
    public TableView weekTable;
    public TableColumn idWeek;
    public TableColumn titleWeek;
    public TableColumn descriptionWeek;
    public TableColumn locationWeek;
    public TableColumn contactWeek;
    public TableColumn typeWeek;
    public TableColumn customerWeek;
    public TableColumn userWeek;
    public TableView monthTable;
    public TableColumn idMonth;
    public TableColumn titleMonth;
    public TableColumn descriptionMonth;
    public TableColumn locationMonth;
    public TableColumn typeMonth;
    public TableColumn customerMonth;
    public TableColumn userMonth;
    public TableColumn startWeek;
    public TableColumn endWeek;
    public TableColumn startMonth;
    public TableColumn endMonth;
    public Button onAddAppointment;
    public Button onModifyAppointment;
    public Button onDeleteAppointment;
    public Tab thisMonthTab;
    public Tab thisWeekTab;
    public TableColumn startDate;
    public TableColumn contactMonth;
    public TableColumn startDate1;
    private Appointment selectedAppointment = null;
    private Customer selectedCustomer = null;

    private Parent scene;
    public int userID;
    public boolean appointmentsWithing15;

    /**
     * add Customer method
     * pulls up add customer screen once addcustomer button is clicked
     * @param actionEvent
     * @throws IOException
     */
    public void onAddCustomer(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AddCustomer.fxml"));
        scene = loader.load();
        AddCustomerController controller = loader.getController();
        controller.setUser(userID);
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * onModify Customer Method
     * This grabs the selected Customer from the table and passes the value to the modifycustomercontroller. It then loads the ModifyCustomer fxml.
     * If there is no customer selected the button will do nothing.
     * @param actionEvent
     * @throws IOException
     */
    public void onModifyCustomer(ActionEvent actionEvent) throws IOException {
        Customer selectedCustomer = (Customer) customersTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null){
            return;
        }
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ModifyCustomer.fxml"));
        scene = loader.load();
        ModifyCustomerController controller = loader.getController();
        controller.setCustomer(selectedCustomer);
        controller.setUser(userID);
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * ondeletecustomer method
     * asks the user if they want to delete the customer selected then deletes it if the user says yes
     * @param actionEvent
     */
    public void onDeleteCustomer(ActionEvent actionEvent) {
        selectedCustomer = ((Customer) customersTable.getSelectionModel().getSelectedItem());
        if(selectedCustomer != null) {
            Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
            alert1.setHeaderText("Delete");
            alert1.setContentText("Do you want to delete this customer?");
            Optional<ButtonType> result = alert1.showAndWait();
            if (result.get() == ButtonType.OK) {
                if(DBAppointment.checkForAppointments(selectedCustomer)){
                    DBcustomer.deleteCustomer(selectedCustomer);
                    customersTable.setItems(DBcustomer.getAllCustomers());
                    return;
                }
                else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Appointments Exist");
                    alert.setContentText("You may not delete a customer with appointments scheduled :(");
                    alert.showAndWait();
                }
            } else {
                alert1.close();
            }
        }
    }

    /**
     * initialize used to populate table views and get any upcoming appointments
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Everything for Customer Table
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        customerAddressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        customerCountryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        customerDivisionColumn.setCellValueFactory(new PropertyValueFactory<>("division"));

        //populates customer table with customer objects generated from the database
        customersTable.setItems(DBcustomer.getAllCustomers());

        //Start of Weekly Appointments table
        idWeek.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        titleWeek.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionWeek.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationWeek.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactWeek.setCellValueFactory(new PropertyValueFactory<>("contactID"));
        typeWeek.setCellValueFactory(new PropertyValueFactory<>("type"));
        startWeek.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endWeek.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        startDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        customerWeek.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        userWeek.setCellValueFactory(new PropertyValueFactory<>("userID"));

        //populates weekly appointments' table with appointment objects from database
        weekTable.setItems(DBAppointment.getWeeklyAppointments());

        //Start of Monthly Appointments table
        idMonth.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        titleMonth.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionMonth.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationMonth.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactMonth.setCellValueFactory(new PropertyValueFactory<>("contactID"));
        typeMonth.setCellValueFactory(new PropertyValueFactory<>("type"));
        startMonth.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endMonth.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        startDate1.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        customerMonth.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        userMonth.setCellValueFactory(new PropertyValueFactory<>("userID"));

        //populates monthly appointments' table with appointment objects from database
        monthTable.setItems(DBAppointment.getMonthlyAppointments());


        //gets any appointments within 15 minutes
        int index = 0;
        while (index < DBAppointment.getWeeklyAppointments().size()){
            Timestamp startTimestamp = DBAppointment.getWeeklyAppointments().get(index).getStart();
            LocalDateTime startDateTime = startTimestamp.toLocalDateTime();
            if(startDateTime.isAfter(LocalDateTime.now()) && startDateTime.isBefore(LocalDateTime.now().plusMinutes(15))){
                appointmentsWithing15 = true;
                break;
            }
            else {
                index++;
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if(appointmentsWithing15){
            alert.setContentText("You have an Appointment within 15 minutes");
        }
        else {
            alert.setContentText("You have no Appointments within 15 minutes");
        }
        alert.show();

    }

    /**
     * Exit method
     * used to close the java application
     * @param actionEvent
     */
    public void onExit(ActionEvent actionEvent) {
        Stage stage = (Stage) exit.getScene().getWindow();
        stage.close();
    }

    /**
     * onaddappointment method
     * takes user to add appointment screen
     * @param actionEvent
     * @throws IOException
     */
    public void onAddAppointment(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AddAppointment.fxml"));
        scene = loader.load();
        AddAppointmentController controller = loader.getController();
        controller.setUser(userID);
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * onmodifyappointment method
     * takes user to modify appointment screen
     * @param actionEvent
     * @throws IOException
     */
    public void onModifyAppointment(ActionEvent actionEvent) throws IOException {
        if (thisWeekTab.isSelected()) {
            selectedAppointment = ((Appointment) weekTable.getSelectionModel().getSelectedItem());
        }
        else if(thisMonthTab.isSelected()) {
            selectedAppointment = ((Appointment) monthTable.getSelectionModel().getSelectedItem());
        }
        if (selectedAppointment == null) {
                return;
            }
        //checks to see if the selected appointment has already happened/ This returns an error popup if the Appointment has already occurred
        if (selectedAppointment.getStart().before(Timestamp.valueOf(LocalDateTime.now()))){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error");
            alert.setContentText("You may not alter an appointment that has already occurred :(");
            alert.showAndWait();
            return;
        }
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ModifyAppointment.fxml"));
            scene = loader.load();
            ModifyAppointmentController controller = loader.getController();
            controller.setAppointment(selectedAppointment);
            controller.setUser(userID);
            stage.setScene(new Scene(scene));
            stage.show();
        }

    /**
     * ondeleteappointment method
     * deletes the selected appointment from either the week table or month table
     * @param actionEvent
     */
    public void onDeleteAppointment(ActionEvent actionEvent) {
        if(thisWeekTab.isSelected()) {
            selectedAppointment = ((Appointment) weekTable.getSelectionModel().getSelectedItem());
            if(selectedAppointment == null){
                //do nothing if nothing is selected
                return;
            }
            DBAppointment.deleteAppointment(selectedAppointment);
            weekTable.setItems(DBAppointment.getWeeklyAppointments());
        }
        else if(thisMonthTab.isSelected()) {
            selectedAppointment = ((Appointment) monthTable.getSelectionModel().getSelectedItem());
            if(selectedAppointment == null){
                //do nothing if nothing is selected
                return;
            }
            DBAppointment.deleteAppointment(selectedAppointment);
            monthTable.setItems(DBAppointment.getMonthlyAppointments());
        }
    }

    /**
     * setuser method
     * sets userid from login screen
     * @param user
     */
    public void setUser(int user) {
        userID = user;
    }

    /**
     * onReports method
     * takes user to reports screen
     * @param actionEvent
     * @throws IOException
     */
    public void onReports(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/reports.fxml"));
        scene = loader.load();
        ReportsController controller = loader.getController();
        controller.setUser(userID);
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * onallAppointments method
     * This takes you to the allappointments screen
     * @param actionEvent
     * @throws IOException
     */
    public void onAllAppointments(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AllAppointments.fxml"));
        scene = loader.load();
        AllAppointmentsController controller = loader.getController();
        controller.setUser(userID);
        stage.setScene(new Scene(scene));
        stage.show();
    }
}
