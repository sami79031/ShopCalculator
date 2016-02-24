package com.thracecodeinc.shopcalculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Created by Samurai on 2/22/16.
 */
public class UserPrefs {
    public static final String MyPREFERENCES = "MyPrefs" ;
    // method for bitmap to base64
    public static String encodeTobase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        //Log.d("Image Log:", imageEncoded);
        return imageEncoded;
    }

    public static void saveTosharedPref(Bitmap imageurl, String text, Context context){
        SharedPreferences myPrefrence = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPrefrence.edit();

        if (!text.isEmpty()){
            editor.putString("namePreferance", text);
        }else if (imageurl != null){
            editor.putString("imagePreferance", encodeTobase64(imageurl));
        }

        editor.apply();
    }

    // method for base64 to bitmap
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }


    public static Bitmap getSavedPrefsImg(Context context){
        Bitmap decodedImg = null;
        SharedPreferences myPrefrence = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String strImg = myPrefrence.getString("imagePreferance", null);

        try {
            if (!strImg.isEmpty()){
                decodedImg = decodeBase64(strImg);
            }
        } catch (NullPointerException e){}


        return decodedImg;
    }

    public static String getSavedPrefsTitle(Context context){
        SharedPreferences myPrefrence = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String strImg = myPrefrence.getString("namePreferance", null);

        try {
            if (!strImg.isEmpty()){
                return strImg;
            }
        } catch (NullPointerException e){}


        return "";
    }
}
