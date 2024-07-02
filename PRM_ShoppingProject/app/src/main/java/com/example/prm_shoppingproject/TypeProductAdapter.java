package com.example.prm_shoppingproject;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.prm_shoppingproject.Model.TypeProduct;

import java.util.List;

public class TypeProductAdapter extends ArrayAdapter<TypeProduct> {
    public TypeProductAdapter(Context context, List<TypeProduct> typeProducts) {
        super(context, 0, typeProducts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(getContext(), android.R.layout.simple_spinner_item, null);
        }
        TextView textView = (TextView) convertView;
        textView.setText(getItem(position).getTypeName());
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(getContext(), android.R.layout.simple_spinner_dropdown_item, null);
        }
        TextView textView = (TextView) convertView;
        textView.setText(getItem(position).getTypeName());
        return convertView;
    }
}
