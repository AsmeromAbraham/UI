package com.example.android.cse594project;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.KeyGenerator;

public class MainActivity extends AppCompatActivity {

    KeyguardManager mKeyguardManager;
    EditText noteField;
    ListView noteList;
    DBHandler dbHandler;

    //Key to encrypt notes
    String KEY_NAME = "note_key";

    //Key used during pinpad and fingerprint unlock
    String PIN_KEY = "pin_key";

    //The boolean to show notes if pinpad and fingerpad unlock works
    Boolean showBool = false;
    int pinBool;
    int fingerBool;
    //This is the test byte that is used by the pinpad and fingerpad unlock
    private static final byte[] SECRET_BYTE_ARRAY = new byte[] {1, 2, 3, 4, 5, 6};
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "baskerville_old_face.ttf");
        Button mySettingsButton = (Button) findViewById(R.id.settings);
        Button myCloudNotesButton = (Button) findViewById(R.id.cloudNotes);
        Button myAddNoteButton = (Button) findViewById(R.id.addNote);
        mySettingsButton.setTypeface(myTypeface);
        myCloudNotesButton.setTypeface(myTypeface);
        myAddNoteButton.setTypeface(myTypeface);
        mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        dbHandler = new DBHandler(this, null, null, 1);
        noteField = (EditText) findViewById(R.id.notetext);
        noteList = (ListView) findViewById(R.id.list);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        pinBool = pref.getInt("pinpadInt", 0);
        fingerBool = pref.getInt("fingerInt", 0);
        keyCheck();

        if(pinBool == 1 && fingerBool == 1)
        {
            fingerprintwithpin();
        }
        else if(pinBool == 1) {
            tryEncrypt();
        }
        else if(fingerBool == 1)
        {
            fingerprint();
        }
        else {
            showBool = true;
        }
        showNotes();
    }

    public void fingerprint()
    {
        Intent intent = new Intent(this, FingerPrint.class);
        startActivityForResult(intent, 2);
    }

    public void fingerprintwithpin()
    {
        Intent intent = new Intent(this, FingerPrint.class);
        startActivityForResult(intent, 3);
    }

    /*
    This is used to check if the keys needed to encrypt notes or unlock the screen have been created.
    If not, this will call the respective generate keys.
     */
    public void keyCheck() {
        try {
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);
            KeyStore.Entry entry = ks.getEntry(KEY_NAME, null);
            if (entry == null)
            {
                createKey();
            }

                createPinKey();
        } catch ( KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException | UnrecoverableEntryException e) {
            throw new RuntimeException(e);
        }
    }

    //Create the key to encrypt notes
    private void createKey() {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .setRandomizedEncryptionRequired(false)
                    .build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | NoSuchProviderException
                | InvalidAlgorithmParameterException | KeyStoreException
                | CertificateException | IOException e) {
            throw new RuntimeException("Failed to create a symmetric key", e);
        }
    }

    //Create the key to unlock screen
    private void createPinKey() {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            KeyGenerator keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            keyGenerator.init(new KeyGenParameterSpec.Builder(PIN_KEY,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setUserAuthenticationValidityDurationSeconds(1)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | NoSuchProviderException
                | InvalidAlgorithmParameterException | KeyStoreException
                | CertificateException | IOException e) {
            throw new RuntimeException("Failed to create a symmetric key", e);
        }
    }

    /*
    Used in the pinpad lock screen. It gets the key that was generated previously and calls a
    cipher instance using the same properties (AES, CBC, and Padding PKCS7) used to create the key.
    It will then attempt to encrypt a block of bytes with this key. Because the secret key when
    generated required UserAuthentication, if the encryption works, the user has recently authenticated.
    If they have not recently authenticated, showAtuthenticationScreen is called.
     */
    private void tryEncrypt() {
        showAuthenticationScreen();
        /*
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            SecretKey secretKey = (SecretKey) keyStore.getKey(PIN_KEY, null);
            Cipher cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            cipher.doFinal(SECRET_BYTE_ARRAY);
            showBool = true;
        } catch (UserNotAuthenticatedException e) {
            showAuthenticationScreen();
        } catch (KeyPermanentlyInvalidatedException e) {
        } catch (BadPaddingException | IllegalBlockSizeException | KeyStoreException |
                CertificateException | UnrecoverableKeyException | IOException
                | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        */
    }

    //Calls the Confirm credential API to show the Pinpad login
    private void showAuthenticationScreen() {
        Intent intent = mKeyguardManager.createConfirmDeviceCredentialIntent(null, null);
        if (intent != null) {
            startActivityForResult(intent, 2);
        }
    }

    //Starts settings activitiy when settings button is pressed.
    public void settings(View view) {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    //Starts newNote activity when add note button is pressed.
    public void newNote(View view) {
        Intent intent = new Intent(this, AddNote.class);
        //startActivityForResult is a callback. When
        startActivityForResult(intent, 1);
    }

    //This
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {

            if(data != null) {
                String newText = data.getStringExtra("noteinfo");
                Toast.makeText(this, newText, Toast.LENGTH_LONG).show();
            }
            showNotes();
        }

        if (requestCode == 2){
            showBool = true;
            showNotes();
        }

        if (requestCode == 3){
           tryEncrypt();
        }
    }

    public void showNotes() {
        if(showBool == true) {
            Cursor cursor = dbHandler.getNotes();
            if (cursor != null) {
                noteCursor c = new noteCursor(this, cursor);
                noteList.setAdapter(c);
                noteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> adaptView, View view, int newInt,
                                            long newLong) {
                        LinearLayout parent = (LinearLayout) view;
                        LinearLayout child = (LinearLayout) parent.getChildAt(0);
                        TextView m = (TextView) child.getChildAt(1);
                        TextView k = (TextView) child.getChildAt(0);
                        String noteText = k.getText().toString();
                        Bundle bundle = new Bundle();
                        int id = Integer.parseInt(m.getText().toString());
                        bundle.putInt("id", id);
                        bundle.putString("notetext", noteText);
                        Intent intent = new Intent(getApplicationContext(), NoteHelper.class);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, 1);
                    }
                });
            }
        }
    }
}
