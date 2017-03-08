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
import android.widget.TextView;


public class AddNote extends AppCompatActivity {
    DBHandler dbHandler;
    EditText noteField;
    ListView noteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_note);

        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "baskerville_old_face.ttf");
        Button myButton = (Button) findViewById(R.id.savebutton);
        myButton.setTypeface(myTypeface);

        dbHandler = new DBHandler(this, null, null, 1);
        noteField = (EditText) findViewById(R.id.notetext);
        noteList = (ListView) findViewById(R.id.list);
    }

    public void addNote(View view) {
        String n = noteField.getText().toString();
        String encryptedNote = Crypt.encrypt(n);
        dbHandler.addNote(encryptedNote);
        Intent resultIntent = new Intent();
        resultIntent.putExtra("noteinfo", "Note Added");
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
