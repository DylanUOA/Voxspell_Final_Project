package voxspell_control;

import javafx.scene.control.TextArea;

/**
 * This is just to make a custom document to stop any input during spelling test.
 */
public class NoInputTextArea extends TextArea {

    @Override
    public void replaceText(int i, int j, String string){
       //empty - never replace.
    }

    @Override
    public void replaceSelection(String string){
        //empty.
    }
}
