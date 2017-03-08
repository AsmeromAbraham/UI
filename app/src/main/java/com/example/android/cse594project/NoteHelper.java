package com.example.android.cse594project;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NoteHelper extends AppCompatActivity {

    DBHandler dbHandler;
    int id;
    String noteText;
    EditText noteField;
    Button b_pick;
    Crypt crypt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_helper);

        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "baskerville_old_face.ttf");
        EditText myEditText = (EditText) findViewById(R.id.updatenotetext);
        Button myDeleteButton = (Button) findViewById(R.id.deletebutton);
        Button myAlarmButton = (Button) findViewById(R.id.alarmbutton);
        Button mySaveButton = (Button) findViewById(R.id.savebutton);
        myEditText.setTypeface(myTypeface);
        myDeleteButton.setTypeface(myTypeface);
        myAlarmButton.setTypeface(myTypeface);
        mySaveButton.setTypeface(myTypeface);

        Bundle extras = getIntent().getExtras();
        id = extras.getInt("id");
        noteText = extras.getString("notetext");
        dbHandler = new DBHandler(this, null, null, 1);
        crypt = new Crypt();
        noteField = (EditText) findViewById(R.id.updatenotetext);
        noteField.setText(noteText);
        noteField.setSelection(noteField.getText().length());
        getDate();
    }

    public void deleteNote(View view) {

        AlertDialog.Builder a_builder = new AlertDialog.Builder(this);
        a_builder.setMessage("Are you sure you want to delete this note?");
        a_builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbHandler.deleteNote(id);
                Intent resultIntent = new Intent();
                resultIntent.putExtra("noteinfo", "Note Deleted");
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
        a_builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = a_builder.create();
        alert.setTitle("WARNING");
        alert.show();

    }

    public void updateNote(View view) {
        String n = noteField.getText().toString();
        String encryptedNote = Crypt.encrypt(n);
        dbHandler.updateNote(id, encryptedNote );
        Intent resultIntent = new Intent();
        resultIntent.putExtra("noteinfo", "Note Updated");
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    public  void alarm (View view){

        registerForContextMenu(view);
            openContextMenu(view);
        /*
        PopupMenu popupMenu = new PopupMenu(this, view);

        popupMenu.inflate(R.menu.alarm_menu);

        MenuItem menuItem =

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.alarmbutton) {
                    item.title
                    //set notification alarm
                }
                else if (item.getItemId() == R.id.voicebutton) {
                    //set voice alarm
                }
                else if (item.getItemId() == R.id.cancelbutton) {
                    //cancel alarm
                }
                return false;
            }
        });

        popupMenu.show();
        */
    }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo){
            super.onCreateContextMenu(menu, view, menuInfo);
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.alarm_menu, menu);

            MenuItem menuItem = menu.findItem(R.id.cancelbutton);
        }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.alarmbutton:
                // do something
                Toast.makeText(getApplicationContext(), "Notification Alarm Set", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.voicebutton:
                //do somthing
                Toast.makeText(getApplicationContext(), "Voice Alarm Set", Toast.LENGTH_SHORT).show();
                return  true;
            case R.id.cancelbutton:
                Toast.makeText(getApplicationContext(), "Alarm Canceled", Toast.LENGTH_SHORT).show();
                return  true;
        }
        return super.onContextItemSelected(item);
    }





    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //      POP UP ALARM ABOVE IS NOT FUNCTIONAL. REGULAR ALARM BELOW IS FUNCTIONAL
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*
    public void alarm(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("notetext", noteText);
        bundle.putInt("id", id);
        Intent intent = new Intent(getApplicationContext(), Alarm.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, 1);
    }
*/
    public void getDate() {
       TextView alarmField = (TextView) findViewById(R.id.alarmfield);
        String date =  dbHandler.getDate(id);
        if(!date.equals("null")) {
            alarmField.setText("Alarm: " + date);
        }
        else {
            alarmField.setText("Alarm: ");
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        getDate();
    }
}

