package Voxspell_42_data;

/**
 * This class represents the abstraction of a single "word", it holds a string
 * representing the actual work key it represents, and has other int fields holding
 * stats for that tested word. These words are created at the start of a session,
 * and will hold stats for each time they're attempted, which are then used to
 * populate specifics statistics if the user specifies.
 * The overall stats of a quiz/level are held within SessionStats object.
 */
public class Word implements Comparable<Word>{

    private String _wordKey; //actual string representing the word.
    private int _level; //int representing the level.
    private int _correct;
    private int _faulted;
    private int _incorrect;
    private int _noOfAttempts;

    /**
     * Constructor holds the actual string representing the level and is provided
     * with the level the word comes from. Other fields start at 0.
     */
    Word(String wordKey, int level){
        _wordKey = wordKey;
        _level = level;
        _correct = 0;
        _faulted = 0;
        _incorrect = 0;
        _noOfAttempts = 0;
    }

    /**
     * Overrides Object#equals(Object), allowing it to be used in .contains() while
     * in a hashmap, useful functionality that reduces logic needed.
     * @return true if the objects have same STRING represeting the word - doesn't care
     * about the stats.
     */
    @Override
    public boolean equals(Object obj){
        String otherWordValue = ((Word) obj)._wordKey;
        if(otherWordValue.equals(this._wordKey)){
            return true;
        } else{
            return false;
        }
    }

    //Getter/incrementer type methods for use on these objects.
    public void incrementCorrect(){ _correct++; }
    public void incrementFaulted(){ _faulted++; }
    public void incrementIncorrect(){ _incorrect++; }
    public void incrementAttempts(){ _noOfAttempts++; }

    public String getWordKey(){
        return _wordKey;
    }
    public int getCorrect(){return _correct; };
    public int getFaulted(){return _faulted; };
    public int getIncorrect(){return _incorrect; };
    public int getAttempts(){return _noOfAttempts; };

    /**
     * Ability to sort based on word ordering, simply uses the ordering given by
     * String. Natural ordering. But must be utilized to allow for Collections.sort()
     * used in WordData
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Word other) {
        return _wordKey.compareTo(other._wordKey);
    }

}