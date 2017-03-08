package com.example.android.cse594project;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.SecretKey;

public class GetKey {
    SecretKey secretKey;
    KeyStore keyStore;
    String KEY_NAME = "note_key";
    String PIN_KEY = "pin_key";

    public SecretKey getKey()
    {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            secretKey = (SecretKey) keyStore.getKey(KEY_NAME, null);
        } catch ( KeyStoreException | NoSuchAlgorithmException | IOException |CertificateException| UnrecoverableEntryException e) {
            throw new RuntimeException(e);
        }
        return secretKey;
    }

}


