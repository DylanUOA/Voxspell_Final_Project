package voxspell_control;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Class representing the controller for the video player Scene/GUI layout.
 * Very basic functionality, but has calls within the methods that ensures
 * the MediaPlayer wont continue on the background (after pressing go back button).
 */
public class VideoPlayerController implements Initializable {
    @FXML
    private MediaView _mediaView;
    @FXML
    private Button _controlButton;
    @FXML
    private Button _goBackButton;

    private MediaPlayer _player;
    private boolean isPaused = false;

    /**
     * Initialize method that grabs the video already supplied, and makes that into the
     * Media object. That media object is then put into the MediaPlayer object, and then
     * thats set in the media view.
     * @param location handled by JavaFX framework
     * @param resources handled by JavaFX framework
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        File file = new File("big_buck_bunny_1_minute.mp4"); //predetermined file
        Media bunnyVid = new Media(file.toURI().toString()); //Converts to media.
        _player = new MediaPlayer(bunnyVid); //Sets up new MediaPlayer
        _player.play();
        _mediaView.setMediaPlayer(_player); //Sets the MediaPlayer in the MediaView.
    }

    /**
     * Method associated with the Play/Pause button, and either plays or pauses
     * it depending on what it was previously.
     */
    public void onButtonPress(){
        if(!isPaused){//will check this to determine whether to play/pause player.
            _player.pause();
            isPaused = true;
        }else{
            _player.play();
            isPaused =false;
        }
    }

    /**
     * Method associated with the go back button - it also ensures that the player
     * is stopped, to ensure it doesn't continue in the background.
     */
    public void goBackPress(){
        _player.stop();//make sure this background process stops before switching.
        Stage stage = (Stage) _goBackButton.getScene().getWindow();
        SceneChanger changer = SceneChanger.getInstance();
        changer.setScene(stage, "MainWindow.fxml");
    }
}