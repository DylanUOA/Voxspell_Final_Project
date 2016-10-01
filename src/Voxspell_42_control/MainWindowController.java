package Voxspell_42_control;

import Voxspell_42_data.SessionStats;
import Voxspell_42_data.WordList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * This class is responsible for controlling the main menu - MainWindow.fxml.
 * It effectively makes each button press change the scene within the current
 * stage. as such theres quite a few methods needed.
 */
public class MainWindowController implements Initializable{

    @FXML
    private Button _newQuizButton;
    @FXML
    private Button _reviewQuizButton;
    @FXML
    private Button _reviewStatsButton;
    @FXML
    private Button _clearStatsButton;
    @FXML
    private TextField _answerTextField;
    @FXML
    private Label _levelLabel;
    @FXML
    private Label _accuracyLabel;
    @FXML
    private TextArea _mainTextArea;

    private SessionStats _sessionStats;
    private SpellingTest _currentTest;
    private WordList _wordList;
    private SceneChanger _parentChanger;
    private SceneChanger _changer;

    /**
     * Initialize method that occurs each time, always get instance of the session
     * stats, and sets level label for user to see.
     * @param location handled by JavaFX framework
     * @param resources handled by JavaFX framework
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _sessionStats = SessionStats.getInstance();
        _changer = SceneChanger.getInstance();
        _levelLabel.setText("Current Level: "+_sessionStats.getLevel().toString());

    }

    /**
     * Associated with _newQuizButton, loads in the SpellingTestWindow.fxml,
     * loaded through the SceneChanger (refer to documentation in SceneChanger)
     */
    public void newQuizPressed(){
        Stage stage = (Stage) _newQuizButton.getScene().getWindow();
        _changer.setScene(stage, "SpellingTestWindow.fxml");
    }

    /**
     * Associated with _reviewQuizButton, loads in the ReviewTestWindow.fxml,
     * loaded through the SceneChanger (refer to documentation in SceneChanger)
     */
    public void reviewQuizPressed() {
        Stage stage = (Stage) _reviewQuizButton.getScene().getWindow();
        _changer.setScene(stage, "ReviewTestWindow.fxml");
    }

    /**
     * Associated with _reviewStatsButton, loads in the StatisticsWindow.fxml,
     * loaded through the SceneChanger (refer to documentation in SceneChanger)
     */
    public void reviewStatsPressed() {
        Stage stage = (Stage) _reviewStatsButton.getScene().getWindow();
        _changer.setScene(stage, "StatisticsWindow.fxml");
    }

    /**
     * Associated with _clearStatsButton, produces a prompt to query the user
     * Only clears the current sessions stats, as that's the only stats that are
     * saved (as per assignment handout)
     */
    public void clearStatsPressed() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Confirmation:");
        alert.setContentText("Are you sure you wish to clear all statistics?");
        ButtonType buttonYes = new ButtonType("Yes");
        ButtonType buttonNo = new ButtonType("No");
        alert.getButtonTypes().setAll(buttonYes,buttonNo);
        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == buttonYes){
            _sessionStats.clearStats();
            Alert alertConfirm = new Alert(Alert.AlertType.INFORMATION);
            alertConfirm.setTitle("Confirmation");
            alertConfirm.setHeaderText(null);
            alertConfirm.setContentText("The statistics have been cleared.");
            alertConfirm.showAndWait();
            Stage stage = (Stage) _clearStatsButton.getScene().getWindow();
            SceneChanger changer = SceneChanger.getInstance();
            changer.setScene(stage, "IntroWindow.fxml");
        } //Do nothing if no.
    }
}