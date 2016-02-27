package com.thracecodeinc.shopcalculator;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.thracecodeinc.shopcalculator.helper.ItemTouchHelperAdapter;
import com.thracecodeinc.shopcalculator.helper.ItemTouchHelperViewHolder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;


/**
 * Created by katiahristova on 9/17/15.
 */
    public class ShopListAdapter extends RecyclerView.Adapter<ShopListAdapter.ViewHolder> implements ItemTouchHelperAdapter {
        private Context mContext;
        private ArrayList<ModelClass> modelClassesList;
        // 2
        public ShopListAdapter(Context context, ArrayList<ModelClass> m) {
            this.mContext = context;
            this.modelClassesList = m;
        }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(modelClassesList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return false;
    }



    @Override
    public void onItemDismiss(int position) {
        modelClassesList.remove(position);
        notifyItemRemoved(position);
    }

    // 3
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ItemTouchHelperViewHolder {
            public FrameLayout placeNameHolder;
            public TextView kind;
            public TextView price;
            public TextView quantity;
            public TextView total;

            public ViewHolder(View itemView) {
                super(itemView);
                placeNameHolder = (FrameLayout) itemView.findViewById(R.id.placeNameHolder);
                kind = (TextView) itemView.findViewById(R.id.zakuska);
                price = (TextView) itemView.findViewById(R.id.price);
                quantity = (TextView) itemView.findViewById(R.id.quantity);
                total = (TextView) itemView.findViewById(R.id.total);

                placeNameHolder.setOnClickListener(this);
                kind.setOnClickListener(this);
                quantity.setOnClickListener(this);
                price.setOnClickListener(this);
            }
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.zakuska){
                    popup(String.valueOf(kind.getTag()), kind, getAdapterPosition(), total);
                }
                else if (v.getId() == R.id.price){
                    popup(String.valueOf(price.getTag()), price, getAdapterPosition(), total);
                }
                else if (v.getId() == R.id.quantity){
                    popup(String.valueOf(quantity.getTag()), quantity, getAdapterPosition(), total);
                }
            }


        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(Color.WHITE);
        }
    }


    public void popup(final String vid, final TextView txt, final int position, final TextView tl) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Въведи " + vid + " за " + modelClassesList.get(position).getKind());
        //alertDialog.setMessage();

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(mContext);
        View promptsView = li.inflate(R.layout.popup_dialog, null);
        alertDialog.setView(promptsView);

        final EditText input = (EditText) promptsView.findViewById(R.id.inputPopup);
        InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        if (vid.equals("ЗАКУСКА")) input.setInputType(InputType.TYPE_CLASS_TEXT);


        alertDialog.setPositiveButton("ГОТОВО",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (vid.equals("ЗАКУСКА")){
                            modelClassesList.get(position).setKind(String.valueOf(input.getText()));
                            txt.setText(input.getText());
                        }else if(vid.equals("Цена")){
                            modelClassesList.get(position).setPrice(String.valueOf(input.getText()));
                            txt.setText(input.getText());
                        }else{
                            modelClassesList.get(position).setQuantity(String.valueOf(input.getText()));
                            txt.setText(input.getText());
                        }

                        if (!modelClassesList.get(position).getPrice().isEmpty() && !modelClassesList.get(position).getQuantity().isEmpty()) {
                            float t = Float.parseFloat(modelClassesList.get(position).getPrice()) *
                                    Float.parseFloat(modelClassesList.get(position).getQuantity());
                            modelClassesList.get(position).setTotal(new DecimalFormat("#.##").format(t));
                            tl.setText(new DecimalFormat("#.##").format(t));
                        }

                        InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                    }
                });

        alertDialog.setNegativeButton("ОТКАЗ",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    public ArrayList<ModelClass> getArrayOfModels(){
        return modelClassesList;
    }


    // 1
    @Override
    public int getItemCount() {
        return modelClassesList.size();
    }

    // 2
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_layout, parent, false);
        return new ViewHolder(view);
    }

    // 3
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ModelClass model = modelClassesList.get(position);//jsonClass.load().get(position);
        holder.kind.setText(model.getKind());
        holder.price.setText(model.getPrice());
        holder.quantity.setText(model.getQuantity());
        holder.total.setText(model.getTotal());

    }
}
