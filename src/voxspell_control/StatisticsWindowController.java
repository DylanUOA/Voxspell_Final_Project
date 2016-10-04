package voxspell_control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.effect.Reflection;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import voxspell_data.SessionStats;
import voxspell_data.Word;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.Set;

import com.sun.scenario.effect.Glow;

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
    @FXML
    private PieChart _pieChart;
    
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
        if (listOfWords == null) {
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
        setupPieChart();
    }

    private void setupPieChart(){
    	if(!_sessionStats.hasCurrentLevelBeenTested()){//change to see if nothing tested,
    		_pieChart.setVisible(false);
    		//then have label been shown here
    	}else{//make label nothing here.
    		_pieChart.setVisible(true);
    	  	ObservableList<PieChart.Data> pieChartData = 
        			FXCollections.observableArrayList(
        					new PieChart.Data("Correct", 10),
        					new PieChart.Data("Incorrect", 2));
        	String[] colourList = {"#62f442","#f44242"};//green and red.
        	_pieChart.setData(pieChartData);
        	int counter =0;
        	for (PieChart.Data data : pieChartData) {
                String color = colourList[counter];
                data.getNode().setStyle("-fx-pie-color: " + color + ";");
                counter++;
            }
        	Set<Node> items = _pieChart.lookupAll("Label.chart-legend-item");
        	Color[] colourArray = {Color.web("#62f442"), Color.web("#f44242")}; //same green and red as special colours
        	counter=0;
        	for(Node item : items){
        		Label label = (Label) item;
        		final Rectangle rectangle = new Rectangle(10,10,colourArray[counter]);
        		label.setGraphic(rectangle);
        		counter++;
        	}
    	}
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
