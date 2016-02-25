package com.thracecodeinc.shopcalculator;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;

/**
 * Created by Samurai on 2/21/16.
 */
public class CalculateForItem extends AppCompatActivity {
    private EditText expenses;
    private EditText materialUsed;
    private EditText daysWorked;
    private Button calculate;
    private EditText averageItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculate_layout);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        try {
            String title = UserPrefs.getSavedPrefsTitle(this);
            if (!title.isEmpty()) {
                setTitle(title);
            }
        }catch (NullPointerException e){}

        averageItems = (EditText) findViewById(R.id.averageItemsEditId);
        expenses = (EditText) findViewById(R.id.expesesEditId);
        materialUsed = (EditText) findViewById(R.id.materialEditId);
        daysWorked = (EditText) findViewById(R.id.workedaysEditId);
        calculate = (Button) findViewById(R.id.calculateEstimate);

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float expense = 0, material = 0, days = 0, averageItms = 0;
                if (!expenses.getText().toString().isEmpty()) {
                    expense = Float.parseFloat(expenses.getText().toString());
                }
                if (!materialUsed.getText().toString().isEmpty()) {
                    material = Float.parseFloat(materialUsed.getText().toString());
                }
                if (!daysWorked.getText().toString().isEmpty()) {
                    days = Float.parseFloat(daysWorked.getText().toString());
                }

                if (!averageItems.getText().toString().isEmpty()) {
                    averageItms = Float.parseFloat(averageItems.getText().toString());
                }

                estimateAlgorithm(expense, material, days, averageItms);
            }
        });

    }

    public void estimateAlgorithm(float ex, float mtr, float d, float avrgItms){
        float expensesForDay = 0, expensAddToItem = 0, priceForOneItem = 0, priceFinal;
        expensesForDay = ex / d;
        expensAddToItem = expensesForDay / avrgItms;
        priceForOneItem = mtr / 100;

        priceFinal = expensAddToItem + priceForOneItem;
System.out.print("Pop up abover");
        popup(ex, expensesForDay, mtr, priceForOneItem, priceFinal);
    }


    public void popup(float expensMoth, float expensDay, float material, float priceForItem, float priceFinal) {
        System.out.print("Pop up inside the popup");
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Калкулирана цена за една закуска");
        //alertDialog.setMess
        alertDialog.setInverseBackgroundForced(true);

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.calculated_item_popup, null);
        alertDialog.setView(promptsView);

        TextView mnthExp = (TextView) promptsView.findViewById(R.id.monthExpensesBrokeId);
        mnthExp.setText(decimalFormat.format(expensMoth).concat("лв"));
        TextView dalyExp = (TextView) promptsView.findViewById(R.id.dailyExpensesId);
        dalyExp.setText(decimalFormat.format(expensDay).concat("лв"));
        TextView matr = (TextView) promptsView.findViewById(R.id.materialBrokeId);
        matr.setText(decimalFormat.format(material).concat("лв"));
        TextView mtrForOne = (TextView) promptsView.findViewById(R.id.materialForItemId);
        mtrForOne.setText(decimalFormat.format(priceForItem).concat("лв"));
        TextView finalPrice = (TextView) promptsView.findViewById(R.id.finalPrice);
        finalPrice.setText(decimalFormat.format(priceFinal).concat("лв"));

        alertDialog.setNegativeButton("Благодаря",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }


}
