package com.example.android.cse594project;

import android.content.Context;
import android.graphics.Typeface;

import java.lang.reflect.Field;

import static android.R.attr.type;

/**
 * Created by Asmerom on 2/21/2017.
 */

public class ReplaceFont {
    public static void replaceDefaultFont(Context context, String nameOfFontBeingReplaced, String nameOfFontInAsset){
        Typeface customeFontTypefae = Typeface.createFromAsset(context.getAssets(), nameOfFontInAsset);
        replaceFont(nameOfFontBeingReplaced, customeFontTypefae);
    }

    private static void replaceFont(String nameOfFontBeingReplaced, Typeface customeFontTypeface) {
        try {
            Field myfield = Typeface.class.getDeclaredField(nameOfFontBeingReplaced);
            myfield.setAccessible(true);
            myfield.set(null, customeFontTypeface);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e){
            e.printStackTrace();
        }
    }
}
