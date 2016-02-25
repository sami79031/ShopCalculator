package com.thracecodeinc.shopcalculator;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.thracecodeinc.shopcalculator.helper.OnStartDragListener;
import com.thracecodeinc.shopcalculator.helper.SimpleItemTouchHelperCallback;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnStartDragListener {
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mStaggeredLayoutManager;
    private ShopListAdapter mAdapter;
    private JsonClass jsonClass;
    private ArrayList<ModelClass> modelList;
    private ItemTouchHelper mItemTouchHelper;
    private ImageView tabImage;
    private Bitmap pref_image;
    private String title = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        modelList = new ArrayList<>();
        jsonClass = new JsonClass(this);
        modelList = jsonClass.load();

        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mStaggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mAdapter = new ShopListAdapter(this, modelList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mStaggeredLayoutManager);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setElevation(7);


        try {
             title = UserPrefs.getSavedPrefsTitle(this);
            if (!title.isEmpty())
                setTitle(title);
        }catch (NullPointerException e){}

        //toolbar.setBackgroundColor(getResources().getColor(R.color.black_transparent));


        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        tabImage = (ImageView) findViewById(R.id.tabBarImage);

        pref_image = UserPrefs.getSavedPrefsImg(this);
        if (pref_image != null){
            tabImage.setImageBitmap(pref_image);
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupNewItem();
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        FloatingActionButton grantTotal = (FloatingActionButton) findViewById(R.id.gettotal);
        grantTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float total = 0;
                for (ModelClass m : mAdapter.getArrayOfModels()){
                    if (!m.getTotal().isEmpty())
                        total += Float.parseFloat(m.getTotal());
                }

                final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                        .coordinatorLayout);

                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Оборот за днес: " + total, Snackbar.LENGTH_INDEFINITE)
                        .setAction("Изчисти", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                for (ModelClass m : mAdapter.getArrayOfModels()) {
                                    m.setQuantity("0");
                                    m.setTotal("0.0");
                                }
                                mAdapter.notifyDataSetChanged();
                                Snackbar snackbar1 = Snackbar.make(coordinatorLayout, "Изчистено", Snackbar.LENGTH_SHORT);
                                snackbar1.show();
                            }
                        });

                snackbar.show();

            }
        });

    }



    @Override
    public void onBackPressed() {
            super.onBackPressed();
    }


    private void addItemsToList(String kind, String price, String qnt, String total) {
        modelList.add(new ModelClass(kind, price, qnt, total));
        mAdapter.notifyDataSetChanged();
    }


    public void popupNewItem() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Въдеди нова закуска");
        //alertDialog.setMess
        alertDialog.setInverseBackgroundForced(true);

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.popup_new_entry, null);
        alertDialog.setView(promptsView);

        final EditText inputKind = (EditText) promptsView.findViewById(R.id.inputKind);
        final EditText inputPrice = (EditText) promptsView.findViewById(R.id.inputPrice);
        final EditText inputQuantity = (EditText) promptsView.findViewById(R.id.inputQuantity);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);


        alertDialog.setPositiveButton("ГОТОВО",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        float t = 0;
                        if (!inputPrice.getText().toString().isEmpty() && !inputQuantity.getText().toString().isEmpty()){
                            t = Float.parseFloat(inputPrice.getText().toString()) *
                                    Float.parseFloat(inputQuantity.getText().toString());
                        }

                        addItemsToList(inputKind.getText().toString(), inputPrice.getText().toString(),
                                inputQuantity.getText().toString(), String.valueOf(t));

                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(inputKind.getWindowToken(), 0);
                    }
                });

        alertDialog.setNegativeButton("ОТКАЗ",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(inputKind.getWindowToken(), 0);
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.calculate_for_item) {
            Intent intent = new Intent(MainActivity.this, CalculateForItem.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.customize){
            Intent intent = new Intent(MainActivity.this, Customize.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onStop() {
        super.onStop();
        jsonClass.save(mAdapter.getArrayOfModels());
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }


}
