package voxspell_data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * The SessionStats class is a Singleton class that retains all the statistics in regards to all quizzes for the current
 * session. It records the number of mastered, faulted, and failed attempts for each respective levels, which is
 * used to calculate accuracy percentages for the overall level.
 * Session Stats also keeps a track of all failed words that need to be tested again in review quiz.
 * The SessionStats is only instantiated once and is therefore used everywhere; it is essentially the data used by all
 * other methods and features of the quiz, such as holding the current level.
 */
public class SessionStats {
    //Singleton Field
    private static SessionStats instance = null;
    private final WordList _wordList;
    private final int _noOfLevels;
    private final HashMap<String, Integer> _nameToNumberMap;
    //Counts for number of mastered, faulted, and failed, for respective levels as index
    private int[] _masteredList;
    private int[] _faultedList;
    private int[] _failedList;
    private HashMap<Integer, ArrayList<Word>> _failedWordsMap;
    private Integer _currentLevel;
    //Keeps track of all words that have been tested, no need to loop through every word
    private HashMap<Integer, ArrayList<Word>> _mapOfTestedWords;
    //For each quiz, instead of every session
    private int _currentQuizCorrect;
    private int _currentQuizIncorrect;
    private int _currentQuizFaulted;
    private String _levelName;

    /**
     * The getInstance method is used to obtain the Singleton instance from anywhere, therefore all classes can use
     * and access this data.
     */
    public static SessionStats getInstance() {
        if (instance == null) {
            instance = new SessionStats();
        }
        return instance;
    }

    /**
     * The constructor for the SessionStats object, only called once when the object is initially constructed.
     * Creates/assigns all the necessary fields with the correct parameters, and creates all data structures.
     * Only ever called again when statistics are cleared, which is done by essentially assigning and constructing
     * a new SessionStats object to instance.
     */
    private SessionStats() {
        _wordList = WordList.getInstance();
        _noOfLevels = _wordList.getLevelCount();
        _nameToNumberMap = new HashMap<String, Integer>();
        ArrayList<String> levelNames = _wordList.getLevelNameList();
        int counter=1;
        //establishes a map that holds strings to names.
        for(String levelName : levelNames){
            _nameToNumberMap.put(levelName,counter);
            counter++;
        }
        _masteredList = new int[_noOfLevels + 1];
        _faultedList = new int[_noOfLevels + 1];
        _failedList = new int[_noOfLevels + 1];
        _failedWordsMap = new HashMap<Integer, ArrayList<Word>>();
        _mapOfTestedWords = new HashMap<Integer, ArrayList<Word>>();
        for (int i = 1; i <= _noOfLevels; i++) {
            _failedWordsMap.put(i, new ArrayList<Word>());
            _mapOfTestedWords.put(i, new ArrayList<Word>());
        }
        _currentLevel = 1;
        _currentQuizCorrect = 0;
        _currentQuizIncorrect = 0;
    }

    /**
     * Method used to clear all statistics; creates a new SessionStats and therefore resets all the values, and
     * assigns to the instance field.
     */
    public void clearStats() {
        instance = new SessionStats();
    }


    /**
     * This method updates the necessary statistic fields within the SessionStats, as well as incrementing the
     * individual fields in the words. Using an enum, a switch is used to determine whether the word was tested as
     * mastered, faulted, or failed, and incremented accordingly.
     *
     * @param status is the status of the word during the quiz; if the user was mastered, faulted, or failed.
     * @param word   is the word that was being tested
     */
    public void updateStats(WordStatus status, Word word) {
        word.incrementAttempts();
        switch (status) {
            case MASTERED:
                _masteredList[_currentLevel] += 1;
                word.incrementCorrect();
                _currentQuizCorrect += 1;
                break;
            case FAULTED:
                _faultedList[_currentLevel] += 1;
                word.incrementFaulted();
                _currentQuizFaulted += 1;
                break;
            case FAILED:
                _failedList[_currentLevel] += 1;
                word.incrementIncorrect();
                _currentQuizIncorrect += 1;
                break;
        }
        this.addToTestedMap(word);
    }

    /**
     * Adds the failed word to the respective level's failed map. The word is added to the ArrayList in the Map
     * respective to it's level. This map is used during review quiz.
     *
     * @param word = failed word
     */
    public void addToFailed(Word word) {
        ArrayList<Word> currentFailedList = _failedWordsMap.get(_currentLevel);
        if (!(currentFailedList.contains(word))) {
            currentFailedList.add(word);
        }
    }

    /**
     * This method returns 10 (or how many words there are) of the failed words of the current level.
     * Shuffles the words, and gives 10 random.
     *
     * @return a list of the failed words.
     */
    public ArrayList<Word> getFailedWords() {
        ArrayList<Word> currentFailedList = _failedWordsMap.get(_currentLevel);
        Integer numberOfWords;
        if (currentFailedList.size() == 0) {
            return currentFailedList;
        } else if (currentFailedList.size() >= 10) {
            numberOfWords = 10;
        } else {
            numberOfWords = currentFailedList.size();
        }
        Collections.shuffle(currentFailedList);
        ArrayList<Word> returnList = new ArrayList<Word>();
        for (int i = 0; i < numberOfWords; i++) {
            returnList.add(currentFailedList.get(i));
        }
        return returnList;
    }

    /**
     * This method removes the given word from the Failed Map, if it exsits. Method is called when a user gets
     * the mastered attempt during review quiz.
     *
     * @param word is the correct word that needs to be removed from failed.
     */
    public void removeFromFailed(Word word) {
        ArrayList<Word> currentFailedList = _failedWordsMap.get(_currentLevel);
        currentFailedList.remove(word);
    }

    /**
     * This method returns the current accuracy, as a percentage with two decimal places, of type Double. The current
     * accuracy value is the percentage of mastered over all attempts in the respective level.
     *
     * @return the accuracy percentage.
     */
    public Double getAccuracy() {
        int numerator = _masteredList[_currentLevel];
        int denominator = _masteredList[_currentLevel] + _faultedList[_currentLevel] + _failedList[_currentLevel];
        if (numerator == 0) { //Has not gotten a single word correct, or a word has not been tested
            return 0.00;
        } else {
            return Math.round((((double) numerator / denominator) * 100) * 100.0) / 100.0;
        }
    }

    /**
     * This method adds the given word to the Map that stores all the words that have been tested throughout this session,
     * so that only the words that have been tested need to be viewed for review statistics.
     *
     * @param word is the word that has been tested.
     */
    public void addToTestedMap(Word word) {
        ArrayList<Word> currentLevelArray = _mapOfTestedWords.get(_currentLevel);
        if (!currentLevelArray.contains(word)) {
            currentLevelArray.add(word);
        }
    }

    /**
     * This method is a getter for the ArrayList at a particular level of the Tested Map.
     *
     * @param level is the level of which list is returned
     * @return the list at the given level.
     */
    public ArrayList<Word> getTestedList(int level) {
        return _mapOfTestedWords.get((Integer) level);
    }


    /**
     * This method increments the level. Maximises at 11.
     */
    public void addLevel() {
        if (_currentLevel < _noOfLevels) {
            _currentLevel += 1;
        }
    }

    /**
     * This method sets the level.
     *
     * @param levelName is the level that will be set
     */
    public void setLevel(String levelName) {
        _levelName = levelName;
        Integer levelNumber = _nameToNumberMap.get(levelName);
        _currentLevel = levelNumber;
    }

    /**
     * This method returns the current level value.
     *
     * @return current level value.
     */
    public Integer getLevel() {
        return _currentLevel;
    }

    /**
     * This method resets the current quiz statistics, which are the counts for correct, incorrect, and faulted.
     */
    public void resetCurrentQuizStats() {
        _currentQuizCorrect = 0;
        _currentQuizIncorrect = 0;
        _currentQuizFaulted = 0;
    }

    /**
     * A getter for the number of correct attempts during the current quiz.
     *
     * @return number of correct/mastered.
     */
    public int getCurrentQuizCorrect() {
        return _currentQuizCorrect;
    }

    /**
     * A getter for the number of incorrect attempts during the current quiz.
     *
     * @return number of incorrect/failed
     */
    public int getCurrentQuizIncorrect() {
        return _currentQuizIncorrect;
    }

    /**
     * A getter for the number of faulted attempts during the current quiz.
     *
     * @return number of faulted
     */
    public int getCurrentQuizFaulted() {
        return _currentQuizFaulted;
    }

    public boolean hasCurrentLevelBeenTested() {
        if (_mapOfTestedWords.get(_currentLevel).size()==0) {
            return false;
        } else {
            return true;
        }
    }

    public Integer getlevelCorrect() {
        return _masteredList[_currentLevel];
    }

    public Integer getlevelFaulted() {
        return _faultedList[_currentLevel];
    }

    public Integer getlevelFailed() {
        return _failedList[_currentLevel];
    }

    public String getLevelName() {
        return _levelName;
    }
}