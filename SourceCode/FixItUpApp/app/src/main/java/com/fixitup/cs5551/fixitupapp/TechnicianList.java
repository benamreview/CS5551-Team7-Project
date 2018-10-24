package com.fixitup.cs5551.fixitupapp;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class TechnicianList extends ArrayAdapter<TechnicianDetails> {
    private Activity context;
    private List<TechnicianDetails> technicianDetailsList;
    public TechnicianList(Activity context, List<TechnicianDetails> technicianDetailsList){
        super(context, R.layout.list_layout, technicianDetailsList);
        this.context = context;
        this.technicianDetailsList = technicianDetailsList;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflator = context.getLayoutInflater();
        View listViewItem = inflator.inflate(R.layout.list_layout,null, true);
        TextView name =(TextView) listViewItem.findViewById(R.id.editTextName);
        TextView contact =(TextView) listViewItem.findViewById(R.id.editTextConatact);
        TextView zip =(TextView) listViewItem.findViewById(R.id.editTextZip);
        TextView type =(TextView) listViewItem.findViewById(R.id.editTextType);
        TechnicianDetails td = technicianDetailsList.get(position);
         name.setText(td.getName());
         contact.setText(td.getContact());
         zip.setText(td.getZipcode());
         type.setText(td.getType());
         return listViewItem;
    }
}
