package com.example.uptrenddelivery;



import static androidx.core.content.ContextCompat.getDrawable;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.core.content.ContextCompat;


public class ChangeColour {
    public static void changeColour(Context context, EditText editText, int background, int vector){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editText.setBackground(getDrawable(context,background));
                editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        getDrawable(context,vector),
                        null,
                        null,
                        null
                );
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    public static void errorColour(Context context,EditText editText,String errorMessage,int background,int vector){
        editText.requestFocus();
        editText.setError(errorMessage);
        editText.setBackground(getDrawable(context,background));
        editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                ContextCompat.getDrawable(context,vector),
                null,
                null,
                null
        );
    }
}
