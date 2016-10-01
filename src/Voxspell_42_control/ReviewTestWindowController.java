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
 * This class is the Controller class for the ReviewTestWindow.fxml. The ReviewTestWindow has several components that
 * are linked to the ReviewTestWindow.fxml, which call several methods when pressed/interacted.
 * The ReviewTestWindow runs the spelling quiz in review mode, where only words that have failed are tested.
 * This controller also implements SpellingTestWindowController, thus allowing the rest of the code to interchange
 * between ReviewTest and the normal SpellingTest without any problems.
 */
public class ReviewTestWindowController implements SpellingTestWindowController,Initializable {

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
    private Label _noCorrect;
    @FXML
    private Label _noIncorrect;
    @FXML
    private Label _noFaulted;
    @FXML
    private Label _accuracyPercentage;
    @FXML
    private ComboBox _reviewingLevel;

    private SessionStats _sessionStats;
    private WordList _wordList;// this should be singleton == easyyy
    private SpellingTest _currentTest;
    private boolean _isNZVoice = false;
    private boolean _repeatDisable = false;

    private Integer _savedLevel;


    /**
     * This method initializes/populates the ComboBox, as well as the obtaining the Singleton sessionStats object.
     * It also saves the current level, so that the user is maintained at the same level after they leave review quiz.
     * Calls the restart method which initializes all the fields and components again.
     * @param location handled by JavaFX framework
     * @param resources handled by JavaFX framework
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _reviewingLevel.getItems().setAll(1,2,3,4,5,6,7,8,9,10,11);
        _sessionStats = SessionStats.getInstance();
        _savedLevel = _sessionStats.getLevel();
        _reviewingLevel.setValue(_savedLevel);
        restart();
    }

    /**
     * This method resets all the fields and respective values so that the quiz can start again. Disables and enables
     * the components respectively and updates the labels.
     */
    public void restart(){
        _repeatWordButton.setDisable(true);
        _currentTest = new SpellingTest(_mainTextArea, _answerTextField, this);
        _currentTest.setReview();//set to review now.
        updateVoiceLabel();
        _startButton.setDisable(false);
        _mainTextArea.setText("");
        _accuracyPercentage.setText(Double.toString(_sessionStats.getAccuracy()) + "%");
        _noCorrect.setText(Integer.toString(_sessionStats.getCurrentQuizCorrect()));
        _noFaulted.setText(Integer.toString(_sessionStats.getCurrentQuizFaulted()));
        _noIncorrect.setText(Integer.toString(_sessionStats.getCurrentQuizIncorrect()));
    }

    /**
     * This method changes the scene back to MainWindow.fxml.
     * Method is called when goBackButton is pressed.
     */
    public void goBackButton(){
        _sessionStats.setLevel(_savedLevel);
        _sessionStats.resetCurrentQuizStats();
        Stage stage = (Stage) _goBackButton.getScene().getWindow();
        SceneChanger changer = SceneChanger.getInstance();
        changer.setScene(stage, "MainWindow.fxml");
    }

    /**
     * This method does not change the level straight away, however enables the start button to be pressed again,
     * which then changes the level.
     * Method is called when an option is selceted from the level combobox.
     */
    public void levelSelected() {
        _startButton.setDisable(false);
        _mainTextArea.setText("");
        _accuracyPercentage.setText("-");
    }

    /**
     *  This method changes the level to the value selected in the combobox, and starts the test spelling test.
     *  It also resets all the current stats values to 0 for the new quiz, and disables the start button again.
     *  Method is called when the start button is pressed.
     */
    public void startButtonPress(){
        _sessionStats.setLevel((int)_reviewingLevel.getValue());
        _accuracyPercentage.setText(_sessionStats.getAccuracy() + "%");
        _currentTest = new SpellingTest(_mainTextArea, _answerTextField, this);
        _currentTest.setReview();
        _currentTest.start();
        _startButton.setDisable(true);
        _sessionStats.resetCurrentQuizStats();
        _noCorrect.setText("0");
        _noIncorrect.setText("0");
        _noFaulted.setText("0");
    }

    /**
     * This method sends the user's input to the test object, which checks if the answer is correct and acts
     * accordingly. The progress bar is only progressed once the word has completely finished, and is therefore
     * not faulted after the word.
     * The labels for statistics are all also updated.
     */
    public void textEntered() { //Doesnt end up progressing to 100%, dialog pops up before final is added
        _currentTest.sendUserInput(_answerTextField.getText());
        _answerTextField.setText("");
        if (!_currentTest.isFaulted()) {
            _accuracyPercentage.setText(Double.toString(_sessionStats.getAccuracy()) + "%");
            _noCorrect.setText(Integer.toString(_sessionStats.getCurrentQuizCorrect()));//get Current Test Correct.
            _noIncorrect.setText(Integer.toString(_sessionStats.getCurrentQuizIncorrect()));
            _noFaulted.setText(Integer.toString(_sessionStats.getCurrentQuizFaulted()));//get Current Test Wrong.
            if (_currentTest.isFinished()) {
                _currentTest.jumpAndVideo();
                restart();
            }
        }
    }

    /**
     * This method either turns the NZVoice on or off, depending on the current state of the voice.
     * It also updates the voice label.
     * Method is called when the change voice button is pressed.
     */
    public void changeVoiceButtonPressed(){
        if(!_isNZVoice){
            _currentTest.setNZVoice(true);
            _isNZVoice = true;
        } else {
            _currentTest.setNZVoice(false);
            _isNZVoice = false;
        }
        updateVoiceLabel();
    }

    /**
     * This method updates the current voice label to the opposite/other voice (from default to NZ, or NZ to default).
     */
    private void updateVoiceLabel(){
        if(_isNZVoice){
            _currentVoice.setText("NZ Voice");
        }else{
            _currentVoice.setText("Default Voice");
        }
    }

    /**
     * This method repeats the word that is being tested (repeated on festival). It also disables the button so that
     * it may not be pressed again.
     * Method is called when the repeat word button is pressed.
     */
    public void repeatWordButtonPressed(){
        _currentTest.queryRepeat();
        _repeatDisable = true;
        disableRepeatButton();
    }

    /**
     * This method disables the repeat button. This method is used by festival speaking class to disable the button.
     */
    public void disableRepeatButton(){
        _repeatWordButton.setDisable(true);
    }

    /**
     * This method is used to reenable the repeat field/flag. This method is also used by festival speaking class
     * to allowed the field to be used.
     */
    public void enableRepeatField(){_repeatDisable = false;}

    /**
     * This method checks whether the repeat button should be re-enabled or not. Also used by fesitval speaking class
     * to allow the repeat button to be used.
     */
    public void repeatButtonCheck(){
        if(_repeatDisable){
            _repeatWordButton.setDisable(true);
        }else{
            _repeatWordButton.setDisable(false);
        }
    }
}
