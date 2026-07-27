package com.example.portalnexus.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyTextWatcher implements TextWatcher {
    private final WeakReference<EditText> editTextWeakReference;
    private final Locale locale = new Locale("pt", "BR");

    public CurrencyTextWatcher(EditText editText) {
        this.editTextWeakReference = new WeakReference<>(editText);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        EditText editText = editTextWeakReference.get();
        if (editText == null) return;

        editText.removeTextChangedListener(this);

        String cleanString = s.toString().replaceAll("[^0-9]", "");
        if (cleanString.isEmpty()) cleanString = "0";

        try {
            BigDecimal parsed = new BigDecimal(cleanString)
                    .divide(new BigDecimal(100), 2, java.math.RoundingMode.HALF_UP);
            
            String formatted = NumberFormat.getCurrencyInstance(locale).format(parsed);

            editText.setText(formatted);
            editText.setSelection(formatted.length());
        } catch (Exception e) {
            android.util.Log.e("CurrencyTextWatcher", "Error formatting currency", e);
        }

        editText.addTextChangedListener(this);
    }

    public static double parseCurrencyValue(String value) {
        if (value == null || value.isEmpty()) return 0.0;
        String clean = value.replaceAll("[^0-9]", "");
        if (clean.isEmpty()) return 0.0;
        try {
            return Double.parseDouble(clean) / 100.0;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
