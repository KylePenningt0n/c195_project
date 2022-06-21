package controller;

import Database.DBConnnection;
import Database.DBlogin;
import Database.DBtime;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class LoginFormController implements Initializable {

    public Label zoneID;
    public Label loginForm;
    public Button submit;
    public Label username;
    public Label password;
    public Label usernameError;
    public Label passwordError;
    public TextField usernameField;
    public TextField passwordField;
    public Button exit;

    private Parent scene;

    /**
     * submit method for login form
     * This gets the resource bundle to translate errors
     * This also grabs the password and username from the text fields and compares them to the database to see if they match
     * @param actionEvent
     * @throws IOException
     */
    public void onSubmit(ActionEvent actionEvent) throws IOException {
        //brings in resource bundles
        Locale locale = Locale.getDefault();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("Utilities/ResourceBundle", locale);

        //writes to login activity text file
        FileWriter loginAttempts = new FileWriter("login_acitivity.txt", true);
        PrintWriter loginAttempt = new PrintWriter(loginAttempts);


        String usrnm = usernameField.getText();
        boolean validUsername = DBlogin.validUser(usrnm);
        if (validUsername == true) {
            String pssword = passwordField.getText();
            boolean isUser = DBlogin.submit(usrnm, pssword);
            if (isUser != true) {
                passwordError.setText(resourceBundle.getString("PasswordError"));
                usernameError.setText("");
                loginAttempt.println(DBtime.getTimeStamp() + " Unsuccessful Login UserName: " + usernameField.getText() + " Invalid password");
                loginAttempt.close();
            }

            else {
                loginAttempt.println(DBtime.getTimeStamp() + " Successful Login UserName: " + usernameField.getText());
                loginAttempt.close();
                Stage stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainScreen.fxml"));
                scene = loader.load();
                MainScreenController controller = loader.getController();
                controller.setUser(DBlogin.getUserID(usernameField.getText()));
                stage.setScene(new Scene(scene));
                stage.show();
            }
        } else {
            loginAttempt.println(DBtime.getTimeStamp() + " Unsuccessful Login UserName: " + usernameField.getText());
            loginAttempt.close();
            usernameError.setText(resourceBundle.getString("UsernameError"));
            passwordError.setText("");
        }
    }


    /**
     * initialize method
     * used for translating login screen using resource bundles
     * commented out locale.french for testing purposes will translate if line is commented back in and first line is commented out
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Locale locale = Locale.getDefault();
        //Locale locale = Locale.FRENCH;
        resourceBundle = ResourceBundle.getBundle("Utilities/ResourceBundle", locale);
        username.setText(resourceBundle.getString("Username"));
        loginForm.setText(resourceBundle.getString("Login"));
        password.setText(resourceBundle.getString("Password"));
        submit.setText(resourceBundle.getString("Submit"));


        ZoneId zone = ZoneId.systemDefault();
        zoneID.setText(zone.toString());
    }

    /**
     * onexit method
     * exits the java application
     * @param actionEvent
     */
    public void onExit(ActionEvent actionEvent) {
        Stage stage = (Stage) exit.getScene().getWindow();
        stage.close();
    }
}
