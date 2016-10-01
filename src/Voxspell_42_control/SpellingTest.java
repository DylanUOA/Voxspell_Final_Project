package Voxspell_42_control;

import Voxspell_42_data.*;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Optional;

/**
 * This class represents the logic involved in moving through the spelling
 * test. It has many fields to hold all the required information throughout
 * the test. It can safely access GUI component because its executed on the
 * current thread - it does however delegate to another class for working
 * on background threads (FestivalSpelling, note this class has methods ensurign
 * it can access GUI safely).
 */
@SuppressWarnings("Since15")
public class SpellingTest {
    private SpellingTestWindowController _controller; //instance of the controller that made this object
    private WordList _wordList; //holds singleton WordList to obtain words
    private SessionStats _statsObject; //holds singleton SessionStats to update statistics.
    private TextArea _textArea; //Reference to TextArea to show user information.
    private boolean _isReview; //determines if review or not.
    private ArrayList<Word> _testingList; //the ArrayList obtained from WordList.
    private Word _currentWord; //Current Word object - can get string out of it.
    private TextField _answerField; //reference to TextField - clears it to show user clarity.
    private boolean _finished; //boolean to determine if finished
    private boolean _jumpUp; //boolean to determine if to "jump up" a level.
    //booleans set here as defaults.
    private boolean _isNZVoice = false;
    private boolean _stopped = true;
    private boolean _wasFaulted = false;
    //default numbers
    private int _correct = 0;
    private int _wordNumber = 0;
    private String _extraString ="";


    /**
     * Constructor for this class, takes arguments to be able to work on GUI.
     * @param textArea Area to print information to the user
     * @param answerField Area to take information from the user
     * @param controller Controller class using this Object, can be either
     *                   New/Review - both implement the required interface.
     */
    public SpellingTest(TextArea textArea, TextField answerField, SpellingTestWindowController controller) {
        _wordList = WordList.getInstance();//singleton therefore gets the instance.
        _controller = controller; //setting references from constructor into fields.
        _answerField = answerField;
        _statsObject = SessionStats.getInstance();//again another singleton.
        _textArea = textArea;
        _finished = false; //setting booleans
        _jumpUp = false;
        _testingList = new ArrayList<Word>(); //making new Array list and then adding to it.
        _testingList = _wordList.getLevelList(_statsObject.getLevel());
    }

    /**
     * Public method used to initiate the spelling test, has error checks
     * in place to ensure it starts correctly. (Mainly for review)
     */
    public void start(){
        if(_testingList.size()==0){//error check, especially for review.
            _textArea.setText("No words to review on Level "+_statsObject.getLevel()+"!");
            _textArea.appendText("\nGo attempt a quiz!");
            _stopped = true;//quiz should be stopped now
        }else{//has at least some words to speak, so do normal flow.
            speakWordFirst();
            _stopped = false;
        }
    }

    /**
     * Method to set the current object to review mode.
     * Changes the testing list accordingly.
     */
    public void setReview(){
        _isReview=true;
        _testingList = _statsObject.getFailedWords();
    }

    /**
     * This method is the first in the logic flow. Private because shouldn't
     * be accessed other by this class. Effectively the first method that
     * assigns the string field, then appends to text field/speaks word.
     */
    private void speakWordFirst(){
        _currentWord = _testingList.get(_wordNumber);
        String speak;
        if(_wasFaulted){//if faulted then need to say "Incorrect" - provided by extra string.
            speak = "\""+_extraString+" Spell "+_currentWord.getWordKey()+" . \""; //added spaces.
            _textArea.setText("Try spell it again...");
            wordCheck();
        }else{//Non faulted branch.
            speak = "\""+_extraString+" Spell "+_currentWord.getWordKey()+".\"";
            _textArea.setText("Spell word "+(_wordNumber+1)+" of "+_testingList.size()+"\n");
            wordCheck();
        }
        _extraString="";
        festivalSpeak(speak);//speak string assigned, send to background thread to speak.
    }

    /**
     * Method that begins checking for potential problems with word to display
     * to user, so that even if sound the same it should provide with extra info
     * to make them spell it right - would be expanded for project.
     */
    private void wordCheck() {
        if (_currentWord.getWordKey().contains("'")) {//check for apostrophe
            _textArea.appendText("Contains an Apostrophe.");
        }
    }

    /**
     * Method to increment and check if the logic needs to stop. If not
     * it continues with the next logic flow.
     * If flow is to terminate, then it will check for what kind of
     * prompt it needs to display/whether user gets reward.
     */
    private void nextWord(){
        _wordNumber++;//increment to next word.
        if(_wordNumber==_testingList.size()){//terminate flow.
            _stopped = true;
            String speak = "\""+_extraString+" .\""; //added space here
            festivalSpeak(speak);//speak final outcome while showing prompt
            if(_correct<9){//CHECK. Spelled less that 9/10 therefore dont get a reward.
                //prompt should show user how many they gor correct on first attempt.
                _jumpUp = false;
            }else {//Spelled enough correct to get reward, so set extra field - maybe call here?
                _jumpUp = true;//signifies that it should increase level.
            }
            _finished = true; //sets flag to be interpreted by
        }else{
            speakWordFirst();//calls the next word to be spelled.
        }
    }

    /**
     * Method to increase level (jump up) and display video by switching to the
     * new scene.
     * Should we decouple by simply calling this method?
     */
    protected void jumpAndVideo() {
        Alert alertFirst = new Alert(Alert.AlertType.INFORMATION);
        alertFirst.setHeaderText("Quiz Finished");
        alertFirst.setContentText("Quiz finished, you spelt "+_correct+" word(s) correct!");
        alertFirst.showAndWait();
        if ((_jumpUp) && (_isReview == false)) { //checks if should jump up AND not review.
            //sets up alert.
            if (_statsObject.getLevel() == 11) {
                Alert alertFinish = new Alert(Alert.AlertType.INFORMATION);
                alertFinish.setHeaderText("Quiz Finished");
                alertFinish.setContentText("You have reached the max level!");
                alertFinish.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText("Congratulations");
                alert.setContentText("Would you like to progress to the next level?");
                ButtonType buttonYes = new ButtonType("Yes");
                ButtonType buttonNo = new ButtonType("No");
                alert.getButtonTypes().setAll(buttonYes, buttonNo);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == buttonYes) {
                    _statsObject.addLevel();
                } //do nothing otherwise - stay on same level.
            }
            Alert videoReward = new Alert(Alert.AlertType.CONFIRMATION);
            videoReward.setTitle("Video Reward!");
            videoReward.setHeaderText("Video Reward Unlocked!");
            videoReward.setContentText("Would you Like to watch a video?");
            //checking for the result of the dialog box.
            Optional<ButtonType> result2 = videoReward.showAndWait();
            if (result2.get() == ButtonType.OK) {
                Stage stage = (Stage) _textArea.getScene().getWindow();
                SceneChanger changer = SceneChanger.getInstance();
                changer.setScene(stage, "videoPlayer.fxml");
            }
        }
    }

    /**
     * Method that receives input from the TextField and passes onto the
     * conditional flow of this class. Design decision to make everything
     * lower case (also makes words read in lower case so capitals not influcencing)
     * @param userInput String corresponding to what the user entered.
     */
    protected void sendUserInput(String userInput){
        if(!_stopped){//if stopped, shouldn't take any more inputs.
            _controller.enableRepeatField();
            userInput = userInput.toLowerCase();//design decision.
            checkInput(userInput);
        }else{
            _textArea.setText("Please start a new test!");
        }
    }

    /**
     * First method called to check if the user input from textfield was correct.
     * Logic flows to next method if was incorrect (different talking/logic)
     * @param userInput String corresponding to what the user input.
     */
    private void checkInput(String userInput) {
        if (_wasFaulted) { //if faulted go to other method - logic flow
            checkFault(userInput);
        } else {
            if (userInput.equals(_currentWord.getWordKey())) {
                _correct++; //increment correct to show at end
                _statsObject.updateStats(WordStatus.MASTERED, _currentWord);
                if (_isReview) { //if correct in review, should remove from failed as well.
                    _statsObject.removeFromFailed(_currentWord);
                }
                _textArea.setText("Correct!!\n");
                _extraString = "Correct . ";
                nextWord();//increments
            } else {//if incorrect, set a flag to be true, will flow to other method.
                //does not increment or update word.
                _textArea.setText("Incorrect\n");
                _extraString="Incorrect . ";
                _wasFaulted = true;
                speakWordFirst();
            }
        }
    }

    /**
     * Method for secondary checking, then puts on faulted/failed list but MUST
     * be one or the other as have failed word once.
     * @param userInput String corresponding to what the user input.
     */
    private void checkFault(String userInput){
        if(userInput.equals(_currentWord.getWordKey())){//correct on second attempt.
            _statsObject.updateStats(WordStatus.FAULTED, _currentWord);
            if(_isReview){//design decision here - remove even if faulted.
                _statsObject.removeFromFailed(_currentWord);
            }
            _textArea.setText("Correct!!\n");
            _extraString="Correct . ";
        }else{//failed, will be added to fail list while updating word.
            _statsObject.updateStats(WordStatus.FAILED, _currentWord);
            _textArea.setText("Incorrect\n");
            _extraString="Incorrect . ";
            _statsObject.addToFailed(_currentWord);
        }
        _wasFaulted=false;//always set back to false, anticipating new word.
        nextWord();//increment now.
    }


    /**
     * Method for speaking words using festival process on another thread.
     * Instantiates a FestivalSpelling object, and then runs it.
     * @param speak - string corresponding to words to be spoken.
     */
    private void festivalSpeak(String speak){
        FestivalSpelling worker = new FestivalSpelling(speak,_answerField,_isNZVoice, _textArea,_controller);
        worker.run();
    }

    /**
     * This method should be called when the user wants to hear the word
     * repeated whenever they want. Should be another button that they can press
     * to rehear the word.
     */
    protected void queryRepeat(){
        String speak = "\" Spell "+_currentWord.getWordKey()+" . \"";
        FestivalSpelling worker = new FestivalSpelling(speak,_answerField,_isNZVoice,_textArea, _controller);
        worker.run();
    }

    /**
     * Method to be able to change the default voice
     * @param input boolean representing choice, true = set to NZ voice.
     */
    protected void setNZVoice(boolean input){
        _isNZVoice = input; //this field will be passed along to constructed FestivalSpelling object.
        if(_isNZVoice){
            _textArea.setText("Voice Changed to NZ Voice");
        }else{
            _textArea.setText("Voice Changed to Default Voice");
        }

    }

    //Methods for querying state.
    protected boolean isStopped(){
        return _stopped;
    }

    protected boolean isFaulted() {
        return _wasFaulted;
    }

    protected boolean isFinished() {
        return _finished;
    }
}