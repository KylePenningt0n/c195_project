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
import model.Customer;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class AddCustomerController implements Initializable {
    public TableView customersTable;
    public TableColumn customerNameColumn;
    public TableColumn customerAddressColumn;
    public TableColumn customerPhoneColumn;
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
    private int userID;

    /**
     * oncountry method
     * changes the division based off what country the user selects
     * @param actionEvent
     */
    public void onCountry(ActionEvent actionEvent) {
        String selectedCountry = (String) countryBox.getValue();
        if(Objects.equals(selectedCountry, "U.S")){
            divisionBox.setItems(DBDivision.getUsDivisions());
        }
        else if(Objects.equals(selectedCountry, "UK")){
            divisionBox.setItems(DBDivision.getUkDivisions());
        }
        else{
            divisionBox.setItems(DBDivision.getCanadaDivisions());
        }
    }

    /**
     * oncancel method
     * takes the user back to the main screen
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
     * onadd method
     * sends customer object to DBcustomer to add the customer to the database if all fields are valid
     * @param actionEvent
     */
    public void onAdd(ActionEvent actionEvent) {
        boolean valid = true;
        while (valid) {
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
            } else if(countryBox.getValue() == null) {
                errorText.setText("Country field MUST be filled out");
                valid = false;
            }
            else if(divisionBox.getValue() == null) {
                errorText.setText("Division field MUST be filled out");
                valid = false;
            }
            else{
                    try {
                        Customer customer = new Customer(DBcustomer.getID(),nameTxt.getText(),addressTxt.getText(),postalTxt.getText(),phoneTxt.getText(),countryBox.getValue().toString(),divisionBox.getValue().toString(),DBDivision.getDivisionInt((String) divisionBox.getValue()));
                        System.out.println(customer);
                        DBcustomer.addCustomer(customer);
                        errorText.setText("Successfully added Customer");
                        customersTable.setItems(DBcustomer.getAllCustomers());
                        valid = false;
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        valid = false;
                    }
            }
        }
    }

    /**
     * initialize method
     * This gives options for the country box. This also sets up the table to display all customers
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        countryBox.setItems(DBcustomer.getAllCountries());

        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        customerAddressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        customerPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        //populates customer table with customer objects generated from the database
        customersTable.setItems(DBcustomer.getAllCustomers());
    }
    public void setUser(int user) {
        userID = user;
    }

}
