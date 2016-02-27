package com.thracecodeinc.shopcalculator;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by Samurai on 2/24/16.
 */
public class Customize extends AppCompatActivity {
    private Bitmap bmp;
    private String titleToEdit = "";
    private EditText titleEdit;
    private int PICK_IMAGE_REQUEST = 1;
    private ImageView pref_img;
    private static float MAX_IMAGE_DIMENSION = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cust_layout);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        bmp = UserPrefs.getSavedPrefsImg(this);

        Button saveBtn = (Button) findViewById(R.id.done_prefs);
        titleEdit = (EditText) findViewById(R.id.titleChangeID);
        pref_img = (ImageView) findViewById(R.id.person_image);

        if (bmp != null)
            pref_img.setImageBitmap(bmp);
        else
            pref_img.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bakery));

        try {
            titleToEdit = UserPrefs.getSavedPrefsTitle(this);
            if (!titleToEdit.isEmpty()) {
                setTitle(titleToEdit);
                titleEdit.setText(titleToEdit);
            }
        }catch (NullPointerException e){}


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!titleEdit.getText().toString().isEmpty()) {
                    UserPrefs.saveTosharedPref(null, titleEdit.getText().toString(), Customize.this);
                }

                Intent intent = new Intent(Customize.this, MainActivity.class);
                startActivity(intent);
            }
        });

        pref_img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                vibrator.vibrate(50);
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
                return false;
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            try {
                // When an Image is picked
                if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                        && null != data) {
                    // Get the Image from data

                    Uri selectedImage = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };

                    // Get the cursor
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String imgDecodableString = cursor.getString(columnIndex);
                    cursor.close();

                    //Bitmap decodedBitmam = BitmapFactory.decodeFile(imgDecodableString);

                    final Bitmap resized = scaleImage(this, selectedImage);


                    Palette.generateAsync(resized, new Palette.PaletteAsyncListener() {
                        public void onGenerated(Palette palette) {
                            UserPrefs.saveTosharedPref(resized, "", Customize.this);
                            // Set the Image in ImageView after decoding the String
                            pref_img.setImageBitmap(resized);
                        }
                    });

                } else {
                    Toast.makeText(this, "You haven't picked Image",
                            Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    public static Bitmap scaleImage(Context context, Uri photoUri) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, dbo);

        try{
            is.close();
        }catch (NullPointerException e){}


        int rotatedWidth, rotatedHeight;
        int orientation = getOrientation(context, photoUri);

        if (orientation == 90 || orientation == 270) {
            rotatedWidth = dbo.outHeight;
            rotatedHeight = dbo.outWidth;
        } else {
            rotatedWidth = dbo.outWidth;
            rotatedHeight = dbo.outHeight;
        }

        Bitmap srcBitmap;
        is = context.getContentResolver().openInputStream(photoUri);
        if (rotatedWidth > MAX_IMAGE_DIMENSION || rotatedHeight > MAX_IMAGE_DIMENSION) {
            float widthRatio = ((float) rotatedWidth) / ((float) MAX_IMAGE_DIMENSION);
            float heightRatio = ((float) rotatedHeight) / ((float) MAX_IMAGE_DIMENSION);
            float maxRatio = Math.max(widthRatio, heightRatio);

            // Create the bitmap from file
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = (int) maxRatio;
            srcBitmap = BitmapFactory.decodeStream(is, null, options);
        } else {
            srcBitmap = BitmapFactory.decodeStream(is);
        }
        try{
            is.close();
        }catch (NullPointerException e){}

        /*
         * if the orientation is not 0 (or -1, which means we don't know), we
         * have to do a rotation.
         */
        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                    srcBitmap.getHeight(), matrix, true);
        }

        String type = context.getContentResolver().getType(photoUri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try{
            if (type.equals("image/png")) {
                srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            } else if (type.equals("image/jpg") || type.equals("image/jpeg")) {
                srcBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            }

        }catch (NullPointerException e){}
        byte[] bMapArray = baos.toByteArray();
        baos.close();
        return BitmapFactory.decodeByteArray(bMapArray, 0, bMapArray.length);
    }

    public static int getOrientation(Context context, Uri photoUri) {
        /* it's on the external media. */
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);


        if (cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
    }
}
