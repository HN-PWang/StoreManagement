package com.mr.lib_base;

import android.text.Editable;
import android.text.TextWatcher;

public abstract class AfterTextChangedListener implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        afterChanged(editable);
    }

    public abstract void afterChanged(Editable editable);
}
