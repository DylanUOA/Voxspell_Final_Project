package Voxspell_42_control;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main class that simply begins running the program. Has a few static constants,
 * we used this in scenebuilder as well. Updated for mac.
 */
public class VoxspellMain extends Application {

    public static final Integer NO_OF_LEVELS = 11;
    public static final Integer WINDOW_WIDTH = 1050;
    public static final Integer WINDOW_HEIGHT = 600;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("IntroWindow.fxml"));//first scene to load
        primaryStage.setTitle("Voxspell Spelling Aid");
        primaryStage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
        primaryStage.setResizable(false); //making sure its not resizable so users cant break ratio.
        primaryStage.show(); //show the actual scene
    }

    public static void main(String[] args) {
        launch(args);
    }

}
