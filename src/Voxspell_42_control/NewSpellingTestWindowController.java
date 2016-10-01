package Voxspell_42_control;

import Voxspell_42_data.SessionStats;
import Voxspell_42_data.WordList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class handles the New spelling quiz window; SpellingTestWindow.fxml
 * It implements the SpellingTestWindowController interface, allowing it to be used
 * within the SpellingTest class - that is so that the logic is able to talk to the
 * gui components safely and correctly, allowing for a nice user experience.
 * Initializable interface means it will always have a similar setup, making sure
 * it resets all components correctly.
 */
public class NewSpellingTestWindowController implements Initializable, SpellingTestWindowController{

    @FXML
    private TextArea _mainTextArea;
    @FXML
    protected Button _goBackButton;
    @FXML
    private TextField _answerTextField;
    @FXML
    private Button _startButton;
    @FXML
    private Button _changeVoiceButton;
    @FXML
    private Button _repeatWordButton;
    @FXML
    private Label _currentVoice;
    @FXML
    private ProgressBar _quizProgress;
    @FXML
    private Label _noCorrect;
    @FXML
    private Label _noFaulted;
    @FXML
    private Label _noIncorrect;
    @FXML
    private Label _accuracyPercentage;
    @FXML
    private Label _currentLevel;

    private SessionStats _sessionStats;
    private WordList _wordList;// this should be singleton == easyyy
    private SpellingTest _currentTest;
    private boolean _isNZVoice = false;
    private boolean _repeatDisable = false;

    private double _currentProgressValue;
    private boolean _hasStarted;

    /**
     * Effectively a start up method that is always executed - always gets the
     * session stats singleton.
     * @param location handled by JavaFX framework
     * @param resources handled by JavaFX framework
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _sessionStats = SessionStats.getInstance();
        _hasStarted = false;
        restart();
    }

    /**
     * Method for "restarting" the Scene, it will disable/enable buttons as needed
     * and makes a NEW spelling test object, that in turn resets the logic for the
     * quiz.
     */
    public void restart(){
        _hasStarted = false;
        _repeatWordButton.setDisable(true); //disables button so user cant click it
        updateVoiceLabel(); //do a check for voice and update.
        _startButton.setDisable(false); //disable start button
        _mainTextArea.setText(""); //reset textArea
        //update labels.
        _currentLevel.setText(Integer.toString(_sessionStats.getLevel()));
        _accuracyPercentage.setText(Double.toString(_sessionStats.getAccuracy()) + "%");
        _noCorrect.setText(Integer.toString(_sessionStats.getCurrentQuizCorrect()));
        _noFaulted.setText(Integer.toString(_sessionStats.getCurrentQuizFaulted()));
        _noIncorrect.setText(Integer.toString(_sessionStats.getCurrentQuizIncorrect()));
    }

    /**
     * Method that is linked to the _goBackButton (linked from fxml), on pressing back
     * it will reset the current quiz stats as well, so even if the user quits mid
     * way through a test they will be presented with a fresh setup for next test.
     */
    public void goBackButton(){
        _sessionStats.resetCurrentQuizStats();//reset to make sure comes back ok
        Stage stage = (Stage) _goBackButton.getScene().getWindow();
        SceneChanger changer = SceneChanger.getInstance();
        changer.setScene(stage, "MainWindow.fxml"); //back to main menu
    }

    /**
     * Method for _startButton press - runs the start method on the SpellingTest object
     * which begins progressing through its logic.
     * Sets text to 0 as well just incase the user starts a test staright after the other
     * finishes.
     */
    public void startButtonPress(){
    	 _currentTest = new SpellingTest(_mainTextArea, _answerTextField, this); //make NEW spelling test object
        _hasStarted = true;
        _currentProgressValue = 0; //resets value back to 0
        _quizProgress.setProgress(_currentProgressValue); //sets bar back to 0
        _currentTest.start(); //initiate the SpellingTest Object
        _startButton.setDisable(true);
        _sessionStats.resetCurrentQuizStats(); //reset so stats object back to 0s
        //reset labels
        _noCorrect.setText("0");
        _noFaulted.setText("0");
        _noIncorrect.setText("0");
    }

    /**
     * This method is associated with the TextField that the user enters into,
     * so on pressing enter this method will be called. Has other quality of life
     * choices to make the user see that its working properly.
     */
    public void textEntered() {
        _currentTest.sendUserInput(_answerTextField.getText()); //sends input to the spelling test object.
        _answerTextField.setText(""); //resets text field to SHOW user that its been accepted.
        if(_hasStarted) {
            if (!_currentTest.isFaulted()) {
                //rest of these values influence the gui

                _currentProgressValue += 0.1; //increment VALUE
                _quizProgress.setProgress(_currentProgressValue); //update the progress bar

                //updates all the labels.
                _accuracyPercentage.setText(Double.toString(_sessionStats.getAccuracy()) + "%");
                _noCorrect.setText(Integer.toString(_sessionStats.getCurrentQuizCorrect()));//get Current Test Correct.
                _noFaulted.setText(Integer.toString(_sessionStats.getCurrentQuizFaulted()));
                _noIncorrect.setText(Integer.toString(_sessionStats.getCurrentQuizIncorrect()));
                //check for finish tbecause need to update gui BEFORE running last dialog
                if (_currentTest.isFinished()) {
                    _currentTest.jumpAndVideo(); //check for jump and dialog
                    restart(); //restart again
                }
            }
        }
    }

    /**
     * Method connected to the change voice button, effectively goes between two voices
     * only - the NZ one and the standard default voice. Calls another method that
     * will update the TextArea to show user whats happening and update a label so user
     * can see what voice is always.
     */
    public void changeVoiceButtonPressed(){
        if(!_isNZVoice){ //toggles between the two voices.
            _currentTest.setNZVoice(true);
            _isNZVoice = true;
        } else {
            _currentTest.setNZVoice(false);
            _isNZVoice = false;
        }
        updateVoiceLabel(); //calls local method to change label
    }

    /**
     * private method that updates the label on the Scene.
     */
    private void updateVoiceLabel(){
        if(_isNZVoice){
            _currentVoice.setText("NZ Voice");
        }else{
            _currentVoice.setText("Default Voice");
        }
    }

    /**
     * Method associated with the repeat word button, sets a flag saying that it
     * shouldn't be REENABLED until next word is reset - this is to stop the user
     * from just hearing the word so many times.
     */
    public void repeatWordButtonPressed(){
        _currentTest.queryRepeat();
        _repeatDisable = true;
        disableRepeatButton();
    }

    /**
     * Method used to disable the repeat button - used by the FestivalSpeaking class
     * as well to disable button (accessed in a thread safe manner).
     */
    public void disableRepeatButton(){
        _repeatWordButton.setDisable(true);
    }

    /**
     * Method used to reenable the FIELD - this basically means the flag is reset,
     * such that it will turn the button back on.
     */
    public void enableRepeatField(){_repeatDisable = false;}

    /**
     * Method that checks whether repeat button should be renabled or not - used
     * by the FestivalSpeaking class again (accessed in a thread safe manner).
     */
    public void repeatButtonCheck(){
        if(_repeatDisable){
            _repeatWordButton.setDisable(true);
        }else{
            _repeatWordButton.setDisable(false);
        }
    }
}
