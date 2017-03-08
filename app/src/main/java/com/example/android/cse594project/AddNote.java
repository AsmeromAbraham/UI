package com.example.android.cse594project;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class AddNote extends AppCompatActivity {
    DBHandler dbHandler;
    EditText noteField;
    ListView noteList;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_note);

        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "baskerville_old_face.ttf");
        Button myButton = (Button) findViewById(R.id.savebutton);
        EditText myEditText = (EditText) findViewById(R.id.notetext);
        myButton.setTypeface(myTypeface);
        myEditText.setTypeface(myTypeface);

        dbHandler = new DBHandler(this, null, null, 1);
        noteField = (EditText) findViewById(R.id.notetext);
        noteList = (ListView) findViewById(R.id.list);
    }

    public void addNote(View view) {
        String n = noteField.getText().toString();
        if(!n.equals("")){
        String encryptedNote = Crypt.encrypt(n);
        dbHandler.addNote(encryptedNote);
        Intent resultIntent = new Intent();
        resultIntent.putExtra("noteinfo", "Note Added");
        setResult(Activity.RESULT_OK, resultIntent);
        }
        finish();
    }

/*
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "SPEAK");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "SPEAK",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void speak(View view) {
        promptSpeechInput();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    noteField.setText(result.get(0));
                    noteField.setSelection(noteField.getText().length());
                }
                break;
            }
        }
    }
    */



}
