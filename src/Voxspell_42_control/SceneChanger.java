package Voxspell_42_control;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

import static Voxspell_42_control.VoxspellMain.WINDOW_HEIGHT;
import static Voxspell_42_control.VoxspellMain.WINDOW_WIDTH;

/**
 * This class is associated with changing the scenes within the window - effectively
 * which fxml document is active at any given time. It is a singleton so it's instance
 * can be obtained anywhere, as it effectively just needs to provide a single functionality
 * allowing for the scene to be changed in the CURRENT STAGE.
 * Singleton design pattern.
 */
public class SceneChanger extends StackPane {

    private static SceneChanger sceneChanger;

    //Private as its Singleton type pattern
    private SceneChanger(){
        //nothing.
    }

    public static SceneChanger getInstance(){
        if (sceneChanger == null) {
            sceneChanger = new SceneChanger();
        }
        return sceneChanger;
    }

    /**
     * Method actually associated with changing the scene. It firstly uses
     * the FXMLLoader to load the specified fxml doc, and it then uses the stage
     * that it has been passed to change what scene is held within the stage.
     * Stage means the current window; Scene means the current FXML document.
     * @param currentStage The stage/window passed to this method to change it.
     * @param fxmlDoc string representing the document to be loaded in.
     */
    public void setScene(Stage currentStage, String fxmlDoc ){
        Parent root = null;
        try { //ensuring that the fxml document is here - will be bundled so always correct.
            root = FXMLLoader.load(getClass().getResource(fxmlDoc));
        } catch (IOException e) {
            //nothing here cause pre-bundled.
        }
        currentStage.setTitle("Voxspell Spelling Aid");
        currentStage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
        currentStage.show(); //shows the new set scene.
    }
}