package com.example.android.cse594project;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Alarm extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    DBHandler dbHandler;
    int id;
    String noteText;
    Button notificationButton;
    Button voiceButton;
    Crypt crypt;
    String dateString;
    int day, month, year, hour, minute;
    int dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal;
    int choice;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int alarmID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        notificationButton = (Button) findViewById(R.id.alarmbutton);
        voiceButton = (Button) findViewById(R.id.voicebutton);
        Bundle extras = getIntent().getExtras();
        noteText = extras.getString("notetext");
        id = extras.getInt("id");
        dbHandler = new DBHandler(this, null, null, 1);
        crypt = new Crypt();
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        alarmID = pref.getInt("AlarmID", 0);
        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice = 1;
                Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(Alarm.this, Alarm.this, year, month, day);
                datePickerDialog.show();
            }
        });
        voiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice = 2;
                Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(Alarm.this, Alarm.this, year, month, day);
                datePickerDialog.show();
            }
        });
    }

    public void onDateSet(DatePicker view, int i, int i1, int i2) {
        yearFinal = i;
        monthFinal = i1 + 1;
        dayFinal = i2;
        Calendar c = Calendar.getInstance();
        hour=c.get(Calendar.HOUR_OF_DAY);
        minute=c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(Alarm.this, Alarm.this,
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
        int alarmID = dbHandler.getAlarm(id);
        int alarmType = dbHandler.getAlarmType(id);
        if(alarmID != -1) {
            cancel(alarmID, alarmType);
        }
        try {
            d1 = ft.parse(test);
        }catch (ParseException e) {
            System.out.println("Unparseable using " + ft);
        }
        long diff = d1.getTime() - date.getTime();
        dateString = ft.format(d1);
        System.out.println(diff);
        if(diff > 0) {
            if (choice == 1) {
                scheduleNoteNotification(diff);
            } else if (choice == 2) {
                scheduleVoiceNotification(diff);
            } else {
                Toast.makeText(this, "something broke", Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(this, "Set a date/time in the future", Toast.LENGTH_LONG).show();
        }
    }

    private void scheduleNoteNotification(long delay) {
        editor = pref.edit();
        alarmID++;
        editor.putInt("AlarmID", alarmID);
        editor.commit();
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra("id", id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), alarmID, notificationIntent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, pendingIntent);
        dbHandler.updateDate(id, dateString );
        dbHandler.updateAlarm(id, alarmID);
        dbHandler.updateAlarmType(id, 1);
        finish();
    }


    private void scheduleVoiceNotification(long delay) {
        editor = pref.edit();
        alarmID++;
        editor.putInt("AlarmID", alarmID);
        editor.commit();
        Intent notificationIntent = new Intent(this, AlarmPublisher.class);
        notificationIntent.putExtra("id", id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), alarmID, notificationIntent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, pendingIntent);
        dbHandler.updateDate(id, dateString );
        dbHandler.updateAlarm(id, alarmID);
        dbHandler.updateAlarmType(id, 2);
        finish();
    }

    public void cancelAlarm(View view){

        int currentAlarmID = dbHandler.getAlarm(id);
        int alarmType = dbHandler.getAlarmType(id);
        if(alarmID != -1) {
            cancel(currentAlarmID, alarmType);
        }
        /*

        if (alarmType == 1){
            Intent notificationIntent = new Intent(this, NotificationPublisher.class);
            notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 20);
            notificationIntent.putExtra("id", id);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), alarmID, notificationIntent, 0);
            AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            dbHandler.updateDate(id, "null");
            finish();
        }
        else if(alarmType == 2) {
            Intent notificationIntent = new Intent(this, AlarmPublisher.class);
            notificationIntent.putExtra(AlarmPublisher.NOTIFICATION_ID, 20);
            notificationIntent.putExtra("id", id);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), currentAlarmID, notificationIntent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            dbHandler.updateDate(id, "null");
            finish();
        }
        else{
            Toast.makeText(this, "No alarm set", Toast.LENGTH_LONG).show();
        }
        */
    }

    public void cancel(int currentAlarmID, int alarmType){
        if (alarmType == 1){
            Intent notificationIntent = new Intent(this, NotificationPublisher.class);
            notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 20);
            notificationIntent.putExtra("id", id);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), alarmID, notificationIntent, 0);
            AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            finish();
        }
        else if(alarmType == 2) {
            Intent notificationIntent = new Intent(this, AlarmPublisher.class);
            notificationIntent.putExtra(AlarmPublisher.NOTIFICATION_ID, 20);
            notificationIntent.putExtra("id", id);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), currentAlarmID, notificationIntent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            finish();
        }
        else{
            Toast.makeText(this, "No alarm set", Toast.LENGTH_LONG).show();
        }
        dbHandler.updateDate(id, "null");
        dbHandler.updateAlarmType(id, -1);
        dbHandler.updateAlarm(id, -1);
        finish();
    }
}
