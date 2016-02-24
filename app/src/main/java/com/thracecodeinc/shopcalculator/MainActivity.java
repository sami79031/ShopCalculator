package com.thracecodeinc.shopcalculator;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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

import com.thracecodeinc.shopcalculator.helper.OnStartDragListener;
import com.thracecodeinc.shopcalculator.helper.SimpleItemTouchHelperCallback;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnStartDragListener {

    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mStaggeredLayoutManager;
    private ShopListAdapter mAdapter;
    private JsonClass jsonClass;
    private ArrayList<ModelClass> modelList;
    private ItemTouchHelper mItemTouchHelper;
    private int PICK_IMAGE_REQUEST = 1;
    private ImageView tabImage;

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setElevation(7);


        try {
            String title = UserPrefs.getSavedPrefsTitle(this);
            if (!title.isEmpty())
                getSupportActionBar().setTitle(title);
        }catch (NullPointerException e){}


        toolbar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                vibrator.vibrate(50);
                titlePopup();
                return false;
            }
        });

        //toolbar.setBackgroundColor(getResources().getColor(R.color.black_transparent));


        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        tabImage = (ImageView) findViewById(R.id.tabBarImage);

        Bitmap image = UserPrefs.getSavedPrefsImg(this);
        if (image != null){
            tabImage.setImageBitmap(image);
        }

        tabImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                vibrator.vibrate(50);
                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                return false;
            }
        });
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Bitmap resized = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 0.6),
                        (int) (bitmap.getHeight() * 0.6), true);

                UserPrefs.saveTosharedPref(bitmap, "", this);

                tabImage.setImageBitmap(resized);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

    public void titlePopup() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Въдеди нов надпис");
        //alertDialog.setMessage();

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.popup_dialog, null);
        alertDialog.setView(promptsView);

        final EditText input = (EditText) promptsView.findViewById(R.id.inputPopup);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        alertDialog.setPositiveButton("ГОТОВО",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (!input.getText().toString().isEmpty()) {
                            UserPrefs.saveTosharedPref(null, input.getText().toString(), MainActivity.this);
                        }



                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                    }
                });

        alertDialog.setNegativeButton("ОТКАЗ",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }


}
