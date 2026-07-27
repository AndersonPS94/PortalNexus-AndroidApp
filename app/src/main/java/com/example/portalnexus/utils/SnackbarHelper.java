package com.example.portalnexus.utils;

import android.view.View;
import android.widget.TextView;

import com.example.portalnexus.R;
import com.google.android.material.snackbar.Snackbar;

public class SnackbarHelper {

    public enum Type {
        SUCCESS, ERROR, WARNING, INFO
    }

    public static void show(View view, String message, Type type) {
        int duration = type == Type.ERROR ? Snackbar.LENGTH_LONG : Snackbar.LENGTH_SHORT;
        Snackbar snackbar = Snackbar.make(view, message, duration);
        
        styleSnackbar(snackbar, type);
        snackbar.show();
    }

    private static void styleSnackbar(Snackbar snackbar, Type type) {
        View snackbarView = snackbar.getView();
        int backgroundColor;
        int textColor = snackbarView.getContext().getColor(R.color.white);

        switch (type) {
            case SUCCESS:
                backgroundColor = snackbarView.getContext().getColor(R.color.success);
                break;
            case ERROR:
                backgroundColor = snackbarView.getContext().getColor(R.color.error);
                break;
            case WARNING:
                backgroundColor = snackbarView.getContext().getColor(R.color.warning);
                break;
            case INFO:
            default:
                backgroundColor = snackbarView.getContext().getColor(R.color.primary);
                break;
        }

        snackbarView.setBackgroundColor(backgroundColor);
        
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        if (textView != null) {
            textView.setTextColor(textColor);
            textView.setTextSize(14);
        }
    }
}
