package com.example.jeancarla.bigsomer.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.jeancarla.bigsomer.classes.Opcion;

/**
 * Created by Jean Carla on 07/11/2016.
 */
public class SpinAdapter extends ArrayAdapter<Opcion> {

    // Your sent context
    private Context context;
    // Your custom values for the spinner (User)
    private Opcion[] values;

    public SpinAdapter(Context context, int textViewResourceId,
                       Opcion[] values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;
    }

    public int getCount(){
        return values.length;
    }



    public Opcion getItem(int position){
        return values[position];
    }

    public long getItemId(int position){
        return position;
    }


    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);
        label.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        label.setPadding(20,15,0,15);

        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        label.setText(values[position].getValor());

        // And finally return your dynamic (or custom) view for each spinner item
        return label;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);
        label.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        label.setPadding(0,10,0,10);
        label.setText(values[position].getValor());


        return label;
    }
}