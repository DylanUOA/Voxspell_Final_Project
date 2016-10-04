package voxspell_control;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.stage.Stage;
import voxspell_data.SessionStats;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class is the Controller class for the IntroWindow.fxml. The IntroWindow has a ComboBox, that is populated
 * in the initialize method when the window is first instantiated, and a start button which calls the start button pressed
 * method when pressed, which changes the scene to the MainWindow.fxml.
 */
public class EndingWindowController implements Initializable{

    @FXML
    private Label _currentLevelAccuracy;
    @FXML
    private Label _accuracyChange;
    @FXML
    private Button _backToMainMenu;
    @FXML
    private PieChart _pieChart;

    SessionStats _sessionStats;

    /**
     * This method initializes/populates the ComboBox of levels, and obtains the Singleton Instance sessionStats
     * @param location handled by JavaFX framework
     * @param resources handled by JavaFX framework
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _sessionStats = SessionStats.getInstance();
        _currentLevelAccuracy.setText(_sessionStats.getAccuracyString());
        updateAccuracyChange();
        setUpPieChart();
    }

    /**
     * Method for updating the accuracy change label accordingly -
     * should colour green if postive or red if negative change
     */
    private void updateAccuracyChange() {
		
		
	}
    
    private void setUpPieChart(){
    	
    }

	/**
     * This method changes the scene to the MainWindow.fxml, the main menu.
     * Method is called when user presses the start button.
     */
    public void mainMenuButtonPressed() {
        Stage stage = (Stage) _backToMainMenu.getScene().getWindow();
        SceneChanger changer = SceneChanger.getInstance();
        changer.setScene(stage, "MainWindow.fxml");
    }


}
