package com.example.android.cse594project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmPublisher extends BroadcastReceiver {

    String note;
    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";
    DBHandler dbHandler;
    Crypt crypt;
    Context mcontext;
    int id;
    public void onReceive(Context context, Intent intent) {
        mcontext = context;
        Intent intent1 = new Intent(context, TTS.class);
        id = intent.getIntExtra("id", 0);
        getNote();
        if(note != null) {
            note = crypt.decrypt(note);
            intent1.putExtra(NOTIFICATION, note);
            context.startService(intent1);
        }
        dbHandler.updateDate(id, "null");
        dbHandler.updateAlarmType(id, -1);
        dbHandler.updateAlarm(id, -1);
    }

    public void getNote() {
        dbHandler = new DBHandler(mcontext, null, null, 1);
        note = dbHandler.getNote(id);
    }
}

