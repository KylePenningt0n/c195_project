package main;

import Database.DBConnnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;

/**
 * Main class
 * @Author Kyle Pennington
 *
 */
public class Main extends Application{
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/loginForm.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }

    /**
     * Primary method of main class. launches the application
     * @param args
     */
    public static void main(String [] args){
        //Test your language
        //Locale.setDefault(new Locale("fr"));


        DBConnnection.openConnection();
        launch(args);
        DBConnnection.closeConnection();
    }
}
