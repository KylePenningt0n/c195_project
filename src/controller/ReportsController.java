package controller;

import Database.DBAppointment;
import Database.DBcontacts;
import Database.DBcustomer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Appointment;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class ReportsController implements Initializable {
    public int userid;
    public ComboBox months;
    public ComboBox years;
    public ComboBox types;
    public TableView table;
    public TableColumn id;
    public TableColumn title;
    public TableColumn type;
    public TableColumn description;
    public TableColumn startDate;
    public TableColumn startTime;
    public TableColumn endTime;
    public TableColumn customerID;
    public ComboBox contactBox;
    public Label appointmentNumber;
    public Label totalCustomers;
    private Parent scene;
    public ObservableList<String> monthsList = FXCollections.observableArrayList();
    public ObservableList<String> yearsList = FXCollections.observableArrayList();
    public ObservableList<Appointment> appointments = FXCollections.observableArrayList();


    public void setUser(int userID) {
        userid = userID;
    }

    /**
     * onnumber method
     * used to get number of appointments correlating with month,year,and type provided from user
     * @param actionEvent
     */
    public void onNumber(ActionEvent actionEvent) {
        if((types.getValue() == null) || (years.getValue() == null) || (months.getValue() == null)){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error");
            alert.setContentText("You must select a value in every combo box");
            alert.showAndWait();
            return;
        }

        String type = types.getValue().toString();
        String year = years.getValue().toString();
        int yearNumber = 0;
        int monthNumber = 0;
        if(year.equals("2022")){
            yearNumber = 2022;
        }
        if(year.equals("2023")){
            yearNumber = 2023;
        }
        if(year.equals("2024")) {
            yearNumber = 2024;
        }
        if(months.getValue().equals("January")){
            monthNumber = 1;
        }
        if(months.getValue().equals("February")){
            monthNumber = 2;
        }
        if(months.getValue().equals("March")){
            monthNumber = 3;
        }
        if(months.getValue().equals("April")){
            monthNumber = 4;
        }
        if(months.getValue().equals("May")){
            monthNumber = 5;
        }
        if(months.getValue().equals("June")){
            monthNumber = 6;
        }
        if(months.getValue().equals("July")){
            monthNumber = 7;
        }
        if(months.getValue().equals("August")){
            monthNumber = 8;
        }
        if(months.getValue().equals("September")){
            monthNumber = 9;
        }
        if(months.getValue().equals("October")){
            monthNumber = 10;
        }
        if(months.getValue().equals("November")){
            monthNumber = 11;
        }
        if(months.getValue().equals("December")){
            monthNumber = 12;
        }


        LocalDateTime localDateTime = LocalDateTime.of(yearNumber,monthNumber,1,1,1);
        int number = DBAppointment.getMonthTypeNumber(localDateTime,type);
        String number1 = String.valueOf(number);
        appointmentNumber.setText(number1);

    }

    /**
     * oncontact method
     * used to select appointments correlating with contact name
     * @param actionEvent
     */
    public void onContact(ActionEvent actionEvent) {
        if(contactBox.getValue() == null){
            //do nothing
        }
        else{
            String contact = contactBox.getValue().toString();
            appointments = DBAppointment.getContactAppointments(contact);
            table.setItems(appointments);
        }


    }

    public void onHome(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainScreen.fxml"));
        scene = loader.load();
        MainScreenController controller = loader.getController();
        controller.setUser(userid);
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * initialize method
     * used to populate months list, years list, and populate table
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        monthsList.clear();
        yearsList.clear();

        monthsList.add("January");monthsList.add("February");monthsList.add("March");monthsList.add("April");monthsList.add("May");monthsList.add("June");monthsList.add("July");monthsList.add("August");monthsList.add("September");monthsList.add("October");monthsList.add("November");monthsList.add("December");
        yearsList.add("2022");yearsList.add("2023");yearsList.add("2024");
        months.setItems(monthsList);
        years.setItems(yearsList);
        try {
            types.setItems(DBAppointment.getTypes());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        contactBox.setItems(DBcontacts.getAllContacts());

        id.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        description.setCellValueFactory(new PropertyValueFactory<>("description"));
        startDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        startTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTime.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        customerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        type.setCellValueFactory(new PropertyValueFactory<>("type"));



    }

    /**
     * ontotal method
     * used to return total number of customers
     * @param actionEvent
     */
    public void onTotal(ActionEvent actionEvent) {
        int i = DBcustomer.getAllCustomers().size();
        totalCustomers.setText(String.valueOf(i));
    }
}
