package com.example.zaliczenie;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    TextView wordToBeGuessedText;
    String wordToBeGuessed;
    String wordDisplayedStr;
    char[] wordDisplayedChar;
    ArrayList<String> myListOfWords;
    EditText edtInput;
    TextView lettersTriedText;
    String lettersTried;
    final String MESSAGE_WITH_LETTERS_TRIED = "Litery zużyte: ";
    TextView triesLeftText;
    String triesLeft;
    final String WINNING_MESSAGE = "Wygrałeś!";
    final String LOSING_MESSAGE = "Przegrana :(";

    void revealLetterInWord(char letter){
        int indexOfLetter = wordToBeGuessed.indexOf(letter);

        while(indexOfLetter >= 0){
            wordDisplayedChar[indexOfLetter] = wordToBeGuessed.charAt(indexOfLetter);
            indexOfLetter = wordToBeGuessed.indexOf(letter, indexOfLetter + 1);
        }

        wordDisplayedStr = String.valueOf(wordDisplayedChar);
    }

    void displayWordOnScreen(){
        String formattedString = "";
        for(char character : wordDisplayedChar){
            formattedString += character + " ";
        }
        wordToBeGuessedText.setText(formattedString);
    }

    void initializeGame(){
        Collections.shuffle(myListOfWords);
        wordToBeGuessed = myListOfWords.get(0);
        myListOfWords.remove(0);

        wordDisplayedChar = wordToBeGuessed.toCharArray();

        for(int i = 1; i < wordDisplayedChar.length - 1; i++){
            wordDisplayedChar[i] = '_';
        }

        revealLetterInWord(wordDisplayedChar[0]);

        revealLetterInWord(wordDisplayedChar[wordDisplayedChar.length - 1]);

        wordDisplayedStr = String.valueOf(wordDisplayedChar);

        displayWordOnScreen();

        edtInput.setText("");

        lettersTried = " ";

        lettersTriedText.setText(MESSAGE_WITH_LETTERS_TRIED);

        triesLeft = " X X X X X";
        triesLeftText.setText(triesLeft);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myListOfWords = new ArrayList<String>();
        wordToBeGuessedText = findViewById(R.id.wordToBeGuessedText);
        edtInput = findViewById(R.id.edtInput);
        lettersTriedText = findViewById(R.id.lettersTriedText);
        triesLeftText = findViewById(R.id.triesLeftText);

        InputStream myInputStream = null;
        Scanner in = null;
        String aWord = "";

        try {
            myInputStream = getAssets().open("database_file.txt");
            in = new Scanner(myInputStream);
            while(in.hasNext()){
                aWord = in.next();
                myListOfWords.add(aWord);
            }
        } catch (IOException e) {
            Toast.makeText(this, e.getClass().getSimpleName() + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finally {
            if(in != null){
                in.close();
            }
            try {
                if(myInputStream != null){
                    myInputStream.close();
                }
            } catch (IOException e) {
                Toast.makeText(this, e.getClass().getSimpleName() + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        initializeGame();

        edtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0){
                    checkIfLetterIsInWord(s.charAt(0));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    void checkIfLetterIsInWord(char letter){
        if(wordToBeGuessed.indexOf(letter) >= 0){
            if(wordDisplayedStr.indexOf(letter) < 0){
                revealLetterInWord(letter);

                displayWordOnScreen();

                if(!wordDisplayedStr.contains("_")){
                    triesLeftText.setText(WINNING_MESSAGE);
                }
            }
        }
        else{
            decreaseAndDisplayTriesLeft();

            if(triesLeft.isEmpty()){
                triesLeftText.setText(LOSING_MESSAGE);
                wordToBeGuessedText.setText(wordToBeGuessed);
            }
        }

        if(lettersTried.indexOf(letter) < 0){
            lettersTried += letter + ", ";
            String messageToBeDisplayed = MESSAGE_WITH_LETTERS_TRIED + lettersTried;
            lettersTriedText.setText(messageToBeDisplayed);
        }
    }

    void decreaseAndDisplayTriesLeft(){
        if(!triesLeft.isEmpty()){
            triesLeft = triesLeft.substring(0, triesLeft.length() - 2);
            triesLeftText.setText(triesLeft);
        }
    }

    void resetGame(View v){
        initializeGame();
    }
}
