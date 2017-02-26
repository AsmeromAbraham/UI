package com.example.android.cse594project;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {


    private Context context;

    public FingerprintHandler(Context mContext) {
        context = mContext;
    }
    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        CancellationSignal cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }


    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        this.update("Fingerprint Authentication Error" + errString, false);
    }


    @Override
    public void onAuthenticationFailed() {
        this.update("Fingerprint Authentication Failed.", false);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        this.update("Fingerprint Authentication Succeeded.", true);
    }


    public void update(String result, Boolean success){
        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
        if(success){
            Toast.makeText(context, result, Toast.LENGTH_LONG).show();
            FingerPrint fingerPrint = (FingerPrint) context;
            fingerPrint.success();
        }
    }
}