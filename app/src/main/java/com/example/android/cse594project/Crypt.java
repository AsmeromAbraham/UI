package com.example.android.cse594project;

import android.util.Base64;

import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class Crypt {

    public static String encrypt(String plaintext) {
        try {
            GetKey key = new GetKey();
            SecretKey secretKey = key.getKey();
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            byte[] ivbytes = new byte[ 16 ];
            IvParameterSpec iv = new IvParameterSpec(ivbytes);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
           // byte[] cipherText = cipher.doFinal(plaintext.getBytes("utf-8"));
            //byte[] combinedBytes = combine(ivbytes, cipherText);
            byte[] cipherText = cipher.doFinal(plaintext.getBytes("utf-8"));
            byte[] combinedBytes= new byte[cipherText.length + ivbytes.length];
            System.arraycopy(ivbytes,0,combinedBytes,0,ivbytes.length);
            System.arraycopy(cipherText,0,combinedBytes,ivbytes.length,cipherText.length);
            return Base64.encodeToString(combinedBytes, Base64.NO_WRAP);
        } catch (Exception e) {
            return null;
        }
    }

    public static String decrypt(String encoded) {
        try {
            GetKey key = new GetKey();
            SecretKey secretKey = key.getKey();
            byte[] combinedBytes = Base64.decode(encoded, Base64.NO_WRAP);
            byte[] ivBytes = Arrays.copyOfRange(combinedBytes, 0, 16);
            byte[] cipherText = Arrays.copyOfRange(combinedBytes, 16, combinedBytes.length);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            return new String(cipher.doFinal(cipherText), "utf-8");
        } catch (Exception e) {

            return null;
        }
    }

    private static byte[] combine(byte[] one, byte[] two) {
        byte[] combined = new byte[one.length + two.length];
        for (int i = 0; i < combined.length; ++i) {
            combined[i] = i < one.length ? one[i] : two[i - one.length];
        }
        return combined;
    }

}