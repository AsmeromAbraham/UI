package com.example.android.cse594project;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NoteHelper extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    DBHandler dbHandler;
    int id;
    String noteText;
    EditText noteField;
    Button b_pick;
    TextView tv_result;
    Crypt crypt;
    int day, month, year, hour, minute;
    int dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal;

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

        b_pick = (Button) findViewById(R.id.alarmbutton);
        Bundle extras = getIntent().getExtras();
        id = extras.getInt("id");
        noteText = extras.getString("notetext");
        dbHandler = new DBHandler(this, null, null, 1);
        crypt = new Crypt();
        noteField = (EditText) findViewById(R.id.updatenotetext);
        noteField.setText(noteText);
        b_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(NoteHelper.this, NoteHelper.this,
                        year, month, day);
                datePickerDialog.show();
            }
        });
    }

    public void deleteNote(View view) {
        dbHandler.deleteNote(id);
        Intent resultIntent = new Intent();
        resultIntent.putExtra("noteinfo", "Note Deleted");
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
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


    public void onDateSet(DatePicker view, int i, int i1, int i2) {
        yearFinal = i;
        monthFinal = i1 + 1;
        dayFinal = i2;
        Calendar c = Calendar.getInstance();
        hour=c.get(Calendar.HOUR_OF_DAY);
        minute=c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(NoteHelper.this, NoteHelper.this,
                hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }


    public void onTimeSet(TimePicker view, int i, int i1) {
        hourFinal   = i;
        minuteFinal = i1;
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh:mm");
        String test = yearFinal + "-" + monthFinal + "-" + dayFinal + " " + hourFinal + ":" + minuteFinal;
        Date date = new Date();
        Date d1 = new Date();
        try {
            d1 = ft.parse(test);
        }catch (ParseException e) {
            System.out.println("Unparseable using " + ft);
        }
        long diff = d1.getTime() - date.getTime();
        System.out.println(diff);
        String encryptedalarmNote = dbHandler.getNote(id);
        String alarmNote = crypt.decrypt(encryptedalarmNote);
        scheduleNotification(getNotification(alarmNote), diff);
    }

    private void scheduleNotification(Notification notification, long delay) {
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 20);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 23433, notificationIntent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, pendingIntent);
    }

    private Notification getNotification(String content) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Scheduled Notification");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_launcher);
        return builder.build();
    }
}

