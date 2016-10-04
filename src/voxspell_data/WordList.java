package voxspell_data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * This class represents a list containing all the words specified from the
 * provided word list. It effectively splits the specified word file into
 * different lists corresponding to each level.
 * This class is also responsible for providing a random list from a specified
 * level - it will shuffle and provide 10 Word Objects for being used in tests.
 *
 * This class uses the singleton design pattern to ensure that theres only ever
 * one instance of this class, ensuring that all the word objects are kept
 * correctly, if need to reset, it will have to iterate through these word
 * objects.
 */
public class WordList {

    private ArrayList<String> _levelNameList;
    private HashMap<Integer, ArrayList<Word>> _spellingMap;
    private static WordList wordList;//Singleton type setup
    private int _levelCount;
    private File _file;

    /**
     * Private constructor to prevent other classes from instantiating it
     * Map used to hold level/list corresponding to that level.
     */
    private WordList(){
        _spellingMap = new HashMap<Integer, ArrayList<Word>>();
        _levelNameList = new ArrayList<String>();
        _file = null;
        this.readFile();
    }

    /**
     * Design pattern - singleton; effectively only way to get this object is
     * to use this method - cannot use the constructor, and since "wordlist"
     * is static, can only have one instance of it.
     * @return instance of the WordList class, holding all the Word objects.
     */
    public static WordList getInstance(){
        if (wordList == null) {
            wordList = new WordList();
        }
        return wordList;
    }
    //pretty unsafe but can change later.
    public void readNewFile(File newFile){
        _file = newFile;
        _spellingMap = new HashMap<Integer, ArrayList<Word>>();
        _levelNameList = new ArrayList<String>();
        this.readFile();
    }

    public int getLevelCount(){
        return _levelCount;
    }

    public ArrayList<String> getLevelNameList(){
        return _levelNameList;
    }

    /**
     * This is a private method to read in all the spelling lists into
     * the map for later reference. Could change up the constructor or
     * this method to take a argument to read from any path.
     * Path set for testing purposes.
     */
    private void readFile(){
        //Needs to read up to a percent sign, then increment by 1 and keep adding words.
        try{
            File textFile;
            if(_file==null){
                textFile = new File("NZCER-spelling-lists.txt"); //predetermined file given.
            }else{
                textFile = _file;
            }
            BufferedReader wordListRead = new BufferedReader(new FileReader(textFile));
            String currentLine = null;
            int levelCount =0;
            Word currentWord = null;
            currentLine=wordListRead.readLine();
            if(!currentLine.contains("%")){
                //rip, colud through down to that catch statement as well.
            }else{
                String levelName = currentLine.substring(1);
                _levelNameList.add(levelName);
                ArrayList<Word> levelList = new ArrayList<Word>();
                while((currentLine=wordListRead.readLine()) != null){
                    if(currentLine.contains("%")){//delimiting levels based on % char
                        _spellingMap.put(levelCount,levelList);
                        levelName = currentLine.substring(1);
                        _levelNameList.add(levelName);
                        levelCount++;
                        levelList = new ArrayList<Word>();//make a new list to then add next level to
                    }else{//effectively will have to be normal word now.
                        currentWord = new Word(currentLine.toLowerCase().trim(), levelCount); //Trims for whitespace
                        levelList.add(currentWord);
                    }
                }
                _spellingMap.put(levelCount,levelList);//just need to add last level
                _levelCount = levelCount;
                _levelNameList.add(levelName);
            }
        } catch (IOException e){
            //do nothing, as wordlist is already given.
        }
    }

    /**
     * Method to return a list of Word objects corresponding to the level
     * specified. Does randomisation and takes out words, returns list of 10.
     * @param level int representing the level the list should be taken from
     * @return ArrayList of Word Objects to be used in spelling test, can have
     * stats changed/take string directly for use.
     */
    public ArrayList<Word> getLevelList(int level){
        ArrayList<Word> fullLevelList = _spellingMap.get(level);
        Collections.shuffle(fullLevelList); //shuffles the list before taking Words from it.
        ArrayList<Word> returnList = new ArrayList<Word>();
        for(int i=0;i<2;i++){//could make it variable testing here.
            returnList.add(fullLevelList.get(i));
        }
        return returnList;
    }
}