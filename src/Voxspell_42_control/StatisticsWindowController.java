package Voxspell_42_control;

import Voxspell_42_data.SessionStats;
import Voxspell_42_data.Word;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

/**
 * This class is the Controller class for the StatisticsWindow.fxml. The StatisticsWindow has several components
 * that respond to user input. The main functionality is to show the various statistics, such as the number
 * of correct, faulted, failed, and attempts for each word, as well as the accuracy and number of attempt values
 * for each level. The levels are selected using a combobox which updates the TextArea.
 */
public class StatisticsWindowController implements Initializable{

    @FXML
    private TextArea _statsTextArea;
    @FXML
    private ComboBox<Integer> _levelDrop;
    @FXML
    private Button _goBackButton;
    @FXML
    private Label _levelAccuracy;
    @FXML
    private Label _noOfAttempts;

    private int _savedLevel;
    SessionStats _sessionStats;

    /**
     * This method initializes/populates the combobox with the levels, and obtains the Singleton sessionStats object.
     * It also saves the level so that the user's current level is maintained when they return to the main menu.
     * @param location handled by JavaFX framework
     * @param resources handled by JavafX framework
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _sessionStats = SessionStats.getInstance();
        _levelDrop.getItems().setAll(1,2,3,4,5,6,7,8,9,10,11);
        _savedLevel = _sessionStats.getLevel();
    }

    /**
     * This method updates the TextArea with the statistics of the level selected from the ComboBox. The respective
     * list of words from the map of tested words is obtained, and looped through, displaying all relevant statistic for
     * each word. The total accuracy and number of attempts is also displayed in a label.
     * This method is called when an option is selected from the ComboBox.
     */
    public void comboBoxChoice() {
        int totalAttempts = 0;
        _sessionStats.setLevel(_levelDrop.getValue());
        ArrayList<Word> listOfWords = _sessionStats.getTestedList(_sessionStats.getLevel());
        if (listOfWords.size() == 0) {
            _statsTextArea.setText("No words have been tested yet for this level!\nPlease try doing a quiz first!");
        } else {
            _statsTextArea.setText("Words tested in level " + _sessionStats.getLevel() + ": \n\n");
            Collections.sort(listOfWords);
            for (Word word : listOfWords) {
                String currentText = _statsTextArea.getText();
                currentText += word.getWordKey() + "\n" + "Correct: " + word.getCorrect() + " | Faulted: " + word.getFaulted() + " | Incorrect: " + word.getIncorrect() + " | Number Of Attempts: " +
                        + word.getAttempts() + "\n\n";
                _statsTextArea.setText(currentText);
                totalAttempts += word.getAttempts();
            }
        }
        _noOfAttempts.setText(Integer.toString(totalAttempts));
        _levelAccuracy.setText(Double.toString(_sessionStats.getAccuracy()) + "%");
    }


    /**
     * This method changes the scene back to the main menu MainWindow.fxml. It also sets the level back to the
     * original level.
     * Method is called when the go back button is pressed.
     */
    public void goBackButton(){
        _sessionStats.setLevel(_savedLevel);
        Stage stage = (Stage) _goBackButton.getScene().getWindow();
        SceneChanger changer = SceneChanger.getInstance();
        changer.setScene(stage, "MainWindow.fxml");
    }
}
