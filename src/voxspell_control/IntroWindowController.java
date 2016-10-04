package voxspell_control;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import voxspell_data.SessionStats;
import voxspell_data.WordList;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * This class is the Controller class for the IntroWindow.fxml. The IntroWindow has a ComboBox, that is populated
 * in the initialize method when the window is first instantiated, and a start button which calls the start button pressed
 * method when pressed, which changes the scene to the MainWindow.fxml.
 */
public class IntroWindowController implements Initializable{

    @FXML
    private ComboBox _levelSelector;
    @FXML
    private Button _startButton;

    SessionStats _sessionStats;

    /**
     * This method initializes/populates the ComboBox of levels, and obtains the Singleton Instance sessionStats
     * @param location handled by JavaFX framework
     * @param resources handled by JavaFX framework
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _sessionStats = SessionStats.getInstance();
        WordList wordlist = WordList.getInstance();
        ArrayList<String> levelNames = wordlist.getLevelNameList();
        _levelSelector.getItems().setAll(levelNames);
    }

    /**
     * This methods sets the level of the sessionStats object to the level selected in the comboBox.
     * Method is called when user clicks an option in the combobox.
     */
    public void levelSelected(){
        _sessionStats.setLevel((String)_levelSelector.getValue());
    }

    /**
     * This method changes the scene to the MainWindow.fxml, the main menu.
     * Method is called when user presses the start button.
     */
    public void startButtonPressed() {
        Stage stage = (Stage) _startButton.getScene().getWindow();
        SceneChanger changer = SceneChanger.getInstance();
        changer.setScene(stage, "MainWindow.fxml");
    }


}
