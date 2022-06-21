package controller;

import Database.DBAppointment;
import Database.DBcontacts;
import Database.DBcustomer;
import Utilities.timeStuff;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Appointment;
import model.Customer;

import javax.swing.*;
import java.sql.Timestamp;

import java.io.IOException;
import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AddAppointmentController implements Initializable {
    public TextField title;
    public DatePicker date;
    public ComboBox customer;
    public ComboBox startTime;
    public ComboBox endTime;
    public ComboBox contact;
    public TextField location;
    public TextField type;
    public TextArea description;
    public Label errorText;
    private ObservableList<String> customerNames = FXCollections.observableArrayList();
    private int userid;
    private ObservableList<LocalTime> startTimes = FXCollections.observableArrayList();
    private ObservableList<LocalTime> endTimes = FXCollections.observableArrayList();
    private Parent scene;
    boolean validAppointment;
    boolean error;
    private String label;


    /**
     * initialize method
     * sets up times and customer names ands uses a lambda function to get available dates
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        int i = 0;
        customerNames.clear();
        while(i < DBcustomer.getAllCustomers().size()){
            ObservableList<Customer> customers = DBcustomer.getAllCustomers();
            String customerName = customers.get(i).getName();
            customerNames.add(i,customerName);
            i++;
        }
        int index = 0;
        startTimes.clear();
        while (index < timeStuff.getAvailableTimes().size()){
            LocalDateTime localDateTime = timeStuff.getAvailableTimes().get(index);
            LocalTime localTime = localDateTime.toLocalTime();
            startTimes.add(localTime);
            index++;
        }
        int index2 = 0;
        endTimes.clear();
        while (index2 < timeStuff.getEndAvailableTimes().size()){
            LocalDateTime localDateTime = timeStuff.getEndAvailableTimes().get(index2);
            LocalTime localTime = localDateTime.toLocalTime();
            endTimes.add(localTime);
            index2++;
        }
        customer.setItems(customerNames);
        contact.setItems(DBcontacts.getAllContacts());
        startTime.setItems(startTimes);
        endTime.setItems(endTimes);

        //lambda function to get dates not past the current date of business
        date.setDayCellFactory(datePicker -> new DateCell(){
            public void updateItem(LocalDate date, boolean empty){
                super.updateItem(date,empty);
                setDisable(empty || date.compareTo(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDate()) < 0);
            }
        });

    }

    public void onCancel(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainScreen.fxml"));
        scene = loader.load();
        MainScreenController controller = loader.getController();
        controller.setUser(userid);
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * onadd method
     * creates an appointment objects and sends it to dbappointment if the appointment is valid
     * converts time to localdatetime to make code more readable
     * @param actionEvent
     */
    public void onAdd(ActionEvent actionEvent) {
        validAppointment = true;
        errorText.setText("");
        LocalDate dateSelected = date.getValue();
        LocalTime startTimeSelected = (LocalTime) startTime.getValue();
        LocalTime endTimeSelected = (LocalTime) endTime.getValue();

        LocalDateTime startDateTime = LocalDateTime.of(dateSelected,startTimeSelected);
        LocalDateTime endDateTime = LocalDateTime.of(dateSelected,endTimeSelected);

        ZonedDateTime zonedStartDateTime = startDateTime.atZone(ZoneId.systemDefault());
        ZonedDateTime zonedStartDateTimeEST = zonedStartDateTime.withZoneSameInstant(ZoneId.of("America/New_York"));

        ZonedDateTime zonedEndDateTime = endDateTime.atZone(ZoneId.systemDefault());
        ZonedDateTime zonedEndDateTimeEST = zonedEndDateTime.withZoneSameInstant(ZoneId.of("America/New_York"));

        ZonedDateTime ZonedESTNOW = ZonedDateTime.now(ZoneId.of("America/New_York"));

        while (validAppointment){
            if(zonedStartDateTimeEST.isBefore(ZonedESTNOW)){
                errorText.setText("The start date/time is set before the current time");
                validAppointment = false;
            }
            if(zonedEndDateTimeEST.toLocalTime().isAfter(LocalTime.of(22, 0))){
                errorText.setText("The end time is set after the closing time");
                validAppointment = false;
            }
            if(zonedStartDateTimeEST.toLocalTime().isBefore(LocalTime.of(8,0))){
                errorText.setText("The selected start time is before business hours");
                validAppointment = false;
            }
            if(zonedStartDateTimeEST.isAfter(zonedEndDateTimeEST)){
                errorText.setText("The start time is set after the end Time");
                validAppointment = false;
            }
            if(zonedStartDateTimeEST.isEqual(zonedEndDateTimeEST)){
                errorText.setText("You cannot have an equal start and end time");
                validAppointment = false;
            }
            break;
        }
        error = false;
        if (customer.getValue() == null || contact.getValue() == null || startTime.getValue() == null || endTime.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Combo Box Error");
            alert.setContentText("All combo boxes MUST have something selected");
            alert.showAndWait();
            return;
        }
        if (title.getText().length() > 50 || description.getText().length() > 50 || type.getText().length() > 50 || location.getText().length() > 50) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Text Box error");
            alert.setContentText("All text boxes must have less than 50 characters");
            alert.showAndWait();
            return;
        }
        if(title.getText().length() < 1){
            error = true;
            label = "Title";
        }
        if(description.getText().length() <1){
            error = true;
            label = "Description";
        }
        if(type.getText().length() < 1){
            error = true;
            label = "Type";
        }
        if(location.getText().length() <1){
            error = true;
            label = "Location";
        }
        if(error){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(label + " Field Error");
            alert.setContentText(label + " field must not be empty");
            alert.showAndWait();
            return;
        }
        if (validAppointment){
            LocalDateTime localDateTime = zonedStartDateTimeEST.toLocalDateTime();
            LocalDateTime localDateTime1 = zonedEndDateTimeEST.toLocalDateTime();

            Timestamp start = Timestamp.valueOf(localDateTime);
            Timestamp end = Timestamp.valueOf(localDateTime1);

            Appointment appointment = new Appointment(DBAppointment.getID(),title.getText(),description.getText(),location.getText(),type.getText(),start,end,DBcustomer.getCustomerID(customer.getValue().toString()),userid,
                    DBcontacts.getContactID(contact.getValue().toString()),date.getValue(),localDateTime.toLocalTime(),localDateTime1.toLocalTime());
            if (DBAppointment.addAppointment(appointment)){
                errorText.setText("Successfully added appointment :)");

            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid Appointment");
                alert.setContentText("Please check the appointment table to make sure appointments do not collide");
                alert.showAndWait();
            }
        }
    }

    public void setUser(int userID) {
        userid = userID;
    }
}

