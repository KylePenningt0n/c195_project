package controller;

import Database.DBDivision;
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
import java.util.Objects;
import java.util.ResourceBundle;

public class ModifyCustomerController implements Initializable {
    public TableColumn customerNameColumn;
    public TableColumn customerAddressColumn;
    public TableColumn customerPhoneColumn;
    public TableView customersTable;
    public Customer selectedCustomer;
    public TextField idTxt;
    public TextField nameTxt;
    public TextField addressTxt;
    public TextField postalTxt;
    public Label division;
    public TextField phoneTxt;
    public ComboBox countryBox;
    public ComboBox divisionBox;
    public Label errorText;
    private Parent scene;


    private int divisionID;
    private int customerID;
    private int userID;

    /**
     * initialize Method
     * This sets up the tableview to reflect all customers in the database
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        customerAddressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        customerPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        //populates customer table with customer objects generated from the database
        customersTable.setItems(DBcustomer.getAllCustomers());

    }

    /**
     * Set customer method
     * This pulls the selected customer from the main Screen and set default values on the ModifyCustomer screen
     * @param selectedCustomer
     */
    public void setCustomer(Customer selectedCustomer) {
        countryBox.setItems(DBcustomer.getAllCountries());

        this.selectedCustomer = selectedCustomer;
        divisionID = selectedCustomer.getDivisionID();
        customerID = selectedCustomer.getID();
        idTxt.setText(Integer.toString(selectedCustomer.getID()));
        nameTxt.setText(selectedCustomer.getName());
        addressTxt.setText(selectedCustomer.getAddress());
        postalTxt.setText(selectedCustomer.getPostalCode());
        phoneTxt.setText(selectedCustomer.getPhoneNumber());
        countryBox.setValue(selectedCustomer.getCountry());
        divisionBox.setValue(selectedCustomer.getDivision());
        String country = selectedCustomer.getCountry();
        if(Objects.equals(country, "U.S")){
            divisionBox.setItems(DBDivision.getUsDivisions());
        }
        else if(Objects.equals(country, "UK")){
            divisionBox.setItems(DBDivision.getUkDivisions());
        }
        else{
            divisionBox.setItems(DBDivision.getCanadaDivisions());
        }


    }

    /**
     * onCountry button method
     * This gets the value of the country box the user selects, then sets the division combo box to divisions in that country and gives a default value
     * @param actionEvent
     */
    public void onCountry(ActionEvent actionEvent) {
        String countryValue = (String) countryBox.getValue();
        if (Objects.equals(countryValue, "U.S")) {
            divisionBox.setItems(DBDivision.getUsDivisions());
            divisionBox.setValue("Alabama");
            divisionID = 1;
        }
        else if(Objects.equals(countryValue, "UK")){
            divisionBox.setItems(DBDivision.getUkDivisions());
            divisionBox.setValue("Scotland");
            divisionID = 103;
        }
        else{
            divisionBox.setItems(DBDivision.getCanadaDivisions());
            divisionBox.setValue("Ontario");
            divisionID = 67;
        }
    }

    /**
     * Cancel Method
     * This takes the User back to the main screen and does not update any customers in the database
     * @param actionEvent
     * @throws IOException
     */
    public void onCancel(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainScreen.fxml"));
        scene = loader.load();
        MainScreenController controller = loader.getController();
        controller.setUser(userID);
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Update method for database
     * This checks the input fields for valid inputs first, sets the division ID, then calls the method in DBcustomer to update the customer, that runs sql then returns user back to main screen
     * @param actionEvent
     */
    public void onUpdate(ActionEvent actionEvent) throws IOException {
        boolean valid = true;
        while (valid){
            if(nameTxt.getText() == ""){
                errorText.setText("Name field MUST be filled out");
                valid = false;
            }
            else if(addressTxt.getText() == ""){
                errorText.setText("Address field MUST be filled out");
                valid = false;
            }
            else if(postalTxt.getText() == ""){
                errorText.setText("Postal field MUST be filled out");
                valid = false;
            }
            else if(phoneTxt.getText() == ""){
                errorText.setText("Phone field MUST be filled out");
                valid = false;
            }
            else {
                try {
                    String divisionName = (String) divisionBox.getValue();
                    this.divisionID = DBDivision.getDivisionInt(divisionName);
                    Customer customer = new Customer(customerID,nameTxt.getText(),addressTxt.getText(),postalTxt.getText(),phoneTxt.getText(),(String) countryBox.getValue(),(String) divisionBox.getValue(), divisionID);
                    DBcustomer.updateCustomer(customer);
                    customersTable.setItems(DBcustomer.getAllCustomers());
                    valid = false;
                    Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainScreen.fxml"));
                    scene = loader.load();
                    MainScreenController controller = loader.getController();
                    controller.setUser(userID);
                    stage.setScene(new Scene(scene));
                    stage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                    valid = false;
                }
            }
        }
    }

    public void setUser(int userID) {
        this.userID = userID;
    }
}

















