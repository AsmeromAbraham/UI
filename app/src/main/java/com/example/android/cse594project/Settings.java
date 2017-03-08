package com.example.android.cse594project;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class Settings extends AppCompatActivity {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    KeyguardManager mKeyguardManager;
    FingerprintManager fingerprintManager;
    RadioGroup radioGroup;
    RadioGroup fingerradioGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "baskerville_old_face.ttf");
        TextView mySetTextview = (TextView) findViewById(R.id.settingsText);
        TextView myPinTextview = (TextView) findViewById(R.id.pintext);
        TextView myFingTextview = (TextView) findViewById(R.id.fingertext);
        mySetTextview.setTypeface(myTypeface);
        myPinTextview.setTypeface(myTypeface);
        myFingTextview.setTypeface(myTypeface);

        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        int pinBool = pref.getInt("pinpadInt", 0);
        int fingerBool = pref.getInt("fingerInt", 0);
        radioGroup = (RadioGroup) findViewById(R.id.pinGroup);
        mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        fingerradioGroup = (RadioGroup) findViewById(R.id.fingerGroup);
        if(pinBool == 1) {
            radioGroup.check(R.id.pinOnButton);
        }
        else {
           radioGroup.check(radioGroup.getChildAt(1).getId());
        }

        if(fingerBool == 1) {
            fingerradioGroup.check(R.id.fingerOnButton);
        }
        else {
            fingerradioGroup.check(fingerradioGroup.getChildAt(1).getId());
        }

    }


    public void Pinpad (View view) {
        editor = pref.edit();
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.pinOnButton:
                if (checked) {
                    if (mKeyguardManager.isKeyguardSecure()) {
                        editor.putInt("pinpadInt", 1);
                        editor.commit();
                    }
                    else {
                        Toast.makeText(this, "Please set a lock screen", Toast.LENGTH_LONG).show();
                        radioGroup.check(radioGroup.getChildAt(1).getId());
                    }

                }
                    break;
            case R.id.pinOffButton:
                if (checked) {
                    editor.putInt("pinpadInt", 0);
                    editor.commit();
                }
                    break;
        }
    }

    public void fingerprint (View view) {
        editor = pref.edit();
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.fingerOnButton:
                if (checked) {

                    if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Fingerprint authentication permission not enabled", Toast.LENGTH_LONG).show();
                        break;
                    }

                    if (fingerprintManager.hasEnrolledFingerprints()) {
                        editor.putInt("fingerInt", 1);
                        editor.commit();
                    }
                    else {
                        Toast.makeText(this, "Please enroll a fingerprint", Toast.LENGTH_LONG).show();
                        fingerradioGroup.check(fingerradioGroup.getChildAt(1).getId());
                    }
                }
                break;
            case R.id.fingerOffButton:
                if (checked) {
                    editor.putInt("fingerInt", 0);
                    editor.commit();
                }
                break;
        }
    }
}

