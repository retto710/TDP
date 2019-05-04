package com.yashsoni.visualrecognitionsample.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.yashsoni.visualrecognitionsample.Clases.Paciente;
import com.yashsoni.visualrecognitionsample.R;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PacienteAdapter extends ArrayAdapter<Paciente> {

    public PacienteAdapter(Context context, ArrayList<Paciente> androidFlavors) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, androidFlavors);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Get the {@link AndroidFlavor} object located at this position in the list
        Paciente currentAndroidFlavor = getItem(position);

        // Find the TextView in the list_item.xml layout with the ID version_name
        TextView dniTextView = (TextView) listItemView.findViewById(R.id.dni);
        // Get the version name from the current AndroidFlavor object and
        // set this text on the name TextView
        dniTextView.setText(currentAndroidFlavor.getDni());

        // Find the TextView in the list_item.xml layout with the ID version_number
        TextView apePatTextView = (TextView) listItemView.findViewById(R.id.apellido);
        // Get the version number from the current AndroidFlavor object and
        // set this text on the number TextView
        apePatTextView.setText(currentAndroidFlavor.getApePat());

        // Find the ImageView in the list_item.xml layout with the ID list_item_icon
        CircleImageView iconView =  listItemView.findViewById(R.id.list_item_icon);
        // Get the image resource ID from the current AndroidFlavor object and
        // set the image to iconView

        File imgFile = new  File(currentAndroidFlavor.getPhotoUrl());
        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        iconView.setImageBitmap(myBitmap);

        CheckBox checkBox = listItemView.findViewById(R.id.checkboxDiag);
        if (currentAndroidFlavor.getScore()=="")
        {
            checkBox.setChecked(false);
        }
        else   {

            checkBox.setChecked(true);
        }
        // Return the whole list item layout (containing 2 TextViews and an ImageView)
        // so that it can be shown in the ListView
        return listItemView;
    }

    public PacienteAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }
}
