package com.thracecodeinc.shopcalculator;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by sami on 2/17/16.
 */
public class JsonClass {
    Gson gson;
    Context context;

    public JsonClass(Context context) {
        this.context = context;
        gson = new GsonBuilder().serializeNulls().create();
    }

    public void save(ArrayList<ModelClass> models){
        FileOutputStream outputStream;
        String serializingToGson = gson.toJson(models);
        try {
            outputStream = context.openFileOutput("banichki.txt", Context.MODE_PRIVATE);
            outputStream.write(serializingToGson.getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.d("JSON ERROR", String.valueOf(e));
        }
    }

    public ArrayList<ModelClass> load(){
        ArrayList<ModelClass> modelList = new ArrayList<>();
        FileInputStream fis = null;
        try {
            fis = context.openFileInput("banichki.txt");
        } catch (FileNotFoundException e) {
            Log.d(context.getString(R.string.app_name), "File not found exception: " + e.toString());
        }
        if (fis == null){
            return modelList;
        }
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;

        try {
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String readJson = sb.toString();
        ModelClass[] profilesBO = gson.fromJson(readJson, ModelClass[].class);

        for (int i=0;i<profilesBO.length;i++) {
            modelList.add(profilesBO[i]);
        }

        return modelList;
    }
}
