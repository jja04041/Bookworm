package com.example.bookworm;

import android.app.AlertDialog;
import android.content.Context;

public class customDialog extends AlertDialog {

    protected customDialog(Context context) {
        super(context);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }
}
