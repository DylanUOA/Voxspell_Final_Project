
package voxspell_control;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.io.*;

/**
 * This class represents the background worker used to speak the actual word
 * using festival. It ensures that the gui is accessed in a thread safe way
 * by using the Service class provided by the JavaFX framework - this produces a
 * task, which is worked on a background thread. Within the main body, there is
 * effectively a set listener that listens to when the Worker changes its state
 * to finished, and will then update the gui safely.
 * Uses festival by writing a new festival scheme file, and then uses that in the
 * festival shell/process.
 */
public class FestivalSpelling {
    private SpellingTestWindowController _controller;
    private String _speak;
    private TextField _answerField;
    private boolean _isNZVoice;
    private TextArea _textArea;
    final private File textFile = new File("festivalSpeak.scm"); //set file structure.

    /**
     * Constructor for FestivalSpelling, passes in a few arguments to configure how the
     * scheme file should be created.
     * @param speak String corresponding to what text should be spoken
     * @param answerField TextField that should be disabled during speaking
     * @param isNZVoice boolean corresponding to set NZ voice or not
     * @param textArea Text area to set info in
     * @param controller The reference to the controller that made this object.
     */
    FestivalSpelling(String speak, TextField answerField, boolean isNZVoice, TextArea textArea,
                     SpellingTestWindowController controller){
        _speak = speak;
        _answerField = answerField;
        _isNZVoice = isNZVoice;
        _textArea = textArea;
        _controller = controller;
    }

    /**
     * Method to run the Background thread. Disables TextField to show intuitively to
     * the user that they cannot type, and have a Timeline that allows for a Please Wait...
     * string to keep progressing in the textField to show the gui hasn't frozen.
     * It also sets up and defines the Service/Task, within it it runs the festival
     * scheme file, that will change voice and potentially parameters.
     */
    protected void run(){
        try {
            _answerField.setDisable(true); //disable so the users cant type on it.
            _textArea.requestFocus();
            _controller.disableRepeatButton();
            final String replace = _textArea.getText();
            //This TimeLine is used to progress a string to show the GUI isn't freezing.
            final Timeline loader = new Timeline(new KeyFrame(Duration.seconds(0.2), new EventHandler<ActionEvent>() {
                String loadString = "Please Wait.";
                @Override
                public void handle(ActionEvent event) {//listens each time and then presents new string.
                    loadString = loadString+".";
                    if(loadString.contains(".....")){
                        loadString = "Please Wait";
                    }
                    _answerField.setText(loadString);
                }
            }));
            loader.setCycleCount(Timeline.INDEFINITE);
            loader.play();
            //changing up the style of the TextField manually to be intuitive.
            if(_speak.contains("Correct")){
                _answerField.setStyle("-fx-control-inner-background: green;"+
                        "-fx-font-size: 25px;");
                _textArea.setText("Correct!");
            }else{
                _answerField.setStyle("-fx-control-inner-background: red;"+
                        "-fx-font-size: 25px;");
            }

            this.fileCheck(); //clears file.
            //writer to write into the new file
            BufferedWriter writer = new BufferedWriter(new FileWriter(textFile));
            if(_isNZVoice){ //since can only be two set voices simply uses a boolean.
                writer.write("(voice_akl_nz_jdt_diphone)\n");
            }else{
                writer.write("(voice_kal_diphone)\n");
            }
            writer.write("(Parameter.set 'Duration_Stretch 1.1)\n"); //easier to hear by making slower.
            writer.write("(SayText "+_speak+")"); //text to actually speak
            writer.close();
            //The Service is defined here; for making the background thread and exectuing the task
            Service<Void> service = new Service<Void>() {
                //task defined, makes ProcessBuilder to make festival, -b used to read in scheme file.
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            ProcessBuilder builder = new ProcessBuilder("festival", "-b", "festivalSpeak.scm");
                            try {
                                Process process = builder.start();
                                process.waitFor();//pauses the background thread until execution finishes.
                            } catch (Exception ex) {
                            }
                            return null;
                        }
                    };
                }
            };
            //setting a listener here to make sure that it accesses in a gui in a thread safe manner.
            service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                //handle method to be intercepted on GUI thread
                @Override
                public void handle(WorkerStateEvent event) {
                    _answerField.setDisable(false); //reenable the textfield
                    _answerField.setStyle("-fx-control-inner-background: white;"+
                            "-fx-font-size: 25px;"); //reset style. back to normal.
                    _answerField.requestFocus();//set the text field so user sees can type
                    _answerField.setText(""); //set the TextField back to nothing to show to retype.
                    loader.stop(); //stop the Please Wait.. from continuing.
                    _textArea.setText(replace); //Give info to user on TextArea
                    _controller.repeatButtonCheck(); //check if repeat button should be renabled.
                }
            });
            service.start(); //start the service AFTER everything defined.
        } catch (IOException e){
            //nothing
        }
    }

    /**
     * Double checking method - checks if the file exists or not, if so it'll create it.
     * it will also clear the file to empty before rewriting into it.
     */
    private void fileCheck(){
        try{
            if (!textFile.exists()) {
                textFile.createNewFile();
            }
            PrintWriter clearFile = new PrintWriter(textFile);
            clearFile.print(""); //effectively just empties the file.
            clearFile.close();
        }catch (IOException e){
            //do nothing
        }
    }
}