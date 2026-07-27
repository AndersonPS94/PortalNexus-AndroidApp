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

        String cleanString = s.toString().replaceAll("[R$,.\\s]", "");
        if (cleanString.isEmpty()) cleanString = "0";

        try {
            BigDecimal parsed = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
            String formatted = NumberFormat.getCurrencyInstance(locale).format(parsed);

            editText.setText(formatted);
            editText.setSelection(formatted.length());
        } catch (Exception e) {
        }

        editText.addTextChangedListener(this);
    }

    public static double parseCurrencyValue(String value) {
        if (value == null || value.isEmpty()) return 0.0;
        String clean = value.replaceAll("[R$,\\s]", "").replace(".", "").replace(",", ".");
        try {
            return Double.parseDouble(clean);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
