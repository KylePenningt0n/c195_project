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
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.*;
import java.util.ResourceBundle;

public class ModifyAppointmentController implements Initializable {
    public ComboBox contact;
    public ComboBox customer;
    public TextField location;
    public DatePicker date;
    public TextField type;
    public TextArea description;
    public TextField title;
    public Appointment selectedAppointment;
    public ComboBox startTime;
    public ComboBox endTime;
    public Button update;
    public Label errorText;
    private ObservableList<String> customerNames = FXCollections.observableArrayList();
    private Parent scene;
    private int userid;
    public String label;
    private ObservableList<LocalTime> startTimes = FXCollections.observableArrayList();
    private ObservableList<LocalTime> endTimes = FXCollections.observableArrayList();
    public int appointmentID;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    /**
     * setappointment method
     * gets the selected appointment from the tables and populates the data in the modifyappointment screen
     * @param selectedAppointment
     */
    public void setAppointment(Appointment selectedAppointment) {
        int i = 0;
        while(i < DBcustomer.getAllCustomers().size()){
            ObservableList<Customer> customers = DBcustomer.getAllCustomers();
            String customerName = customers.get(i).getName();
            customerNames.add(i,customerName);
            i++;
        }
        customer.setValue(DBcustomer.getCustomerName(selectedAppointment.getCustomerID()));
        contact.setValue(DBcontacts.getContactName(selectedAppointment.getContactID()));
        this.selectedAppointment = selectedAppointment;
        title.setText(selectedAppointment.getTitle());
        description.setText(selectedAppointment.getDescription());
        type.setText(selectedAppointment.getType());
        startTime.setValue(selectedAppointment.getStart().toLocalDateTime().toLocalTime());
        endTime.setValue(selectedAppointment.getEnd().toLocalDateTime().toLocalTime());
        date.setValue(selectedAppointment.getStartDate());
        location.setText(selectedAppointment.getLocation());
        appointmentID = selectedAppointment.getAppointmentID();

        //disables users from selecting a day before the current date
        startTimes.clear();
        int index = 0;
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

    public void setUser(int userID) {
        userid = userID;
    }

    /**
     * onupdate method
     * creates an appointment object to give to DBappointment if the appointment is valid
     * @param actionEvent
     */
    public void onUpdate(ActionEvent actionEvent) {
        boolean validAppointment = true;
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
        boolean error = false;
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

            Appointment appointment = new Appointment(appointmentID,title.getText(),description.getText(),location.getText(),type.getText(),start,end,DBcustomer.getCustomerID(customer.getValue().toString()),userid,
                    DBcontacts.getContactID(contact.getValue().toString()),date.getValue(),localDateTime.toLocalTime(),localDateTime1.toLocalTime());
            if (DBAppointment.modifyAppointment(appointment)){
                errorText.setText("Successfully updated appointment :)");
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
}
