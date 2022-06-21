package controller;

import Database.DBAppointment;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Appointment;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AllAppointmentsController implements Initializable {
    public TableColumn id;
    public TableColumn title;
    public TableView Table;
    public TableColumn contact;
    public TableColumn type;
    public TableColumn customer;
    public TableColumn user;
    public TableColumn start;
    public TableColumn end;
    public TableColumn location;
    public TableColumn description;
    public TableColumn startDate;
    public int userID;
    private Parent scene;
    private Appointment selectedAppointment = null;







    public void setUser(int user) {
        userID = user;
    }

    public void onHome(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainScreen.fxml"));
        scene = loader.load();
        MainScreenController controller = loader.getController();
        controller.setUser(userID);
        stage.setScene(new Scene(scene));
        stage.show();
    }

    public void onDelete(ActionEvent actionEvent) {
        selectedAppointment = ((Appointment) Table.getSelectionModel().getSelectedItem());
        if(selectedAppointment != null){
            DBAppointment.deleteAppointment(selectedAppointment);
            Table.setItems(DBAppointment.getAllAppointments());
        }
    }
    public void onAdd(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AddAppointment.fxml"));
        scene = loader.load();
        AddAppointmentController controller = loader.getController();
        controller.setUser(userID);
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * initialize method
     * used to populate table
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        id.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        description.setCellValueFactory(new PropertyValueFactory<>("description"));
        location.setCellValueFactory(new PropertyValueFactory<>("location"));
        contact.setCellValueFactory(new PropertyValueFactory<>("contactID"));
        type.setCellValueFactory(new PropertyValueFactory<>("type"));
        start.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        end.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        startDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        customer.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        user.setCellValueFactory(new PropertyValueFactory<>("userID"));

        Table.setItems(DBAppointment.getAllAppointments());
    }
}
