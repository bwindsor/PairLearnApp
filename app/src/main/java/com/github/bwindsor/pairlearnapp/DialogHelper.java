package com.github.bwindsor.pairlearnapp;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;

/**
 * Created by Ben on 25/05/2017.
 */

public class DialogHelper {
    public static void ShowOKDialog(Context context, String message, String title, final DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(title)
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onClickListener.onClick(dialog, which);
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void ShowOKDialog(Context context, String message, String title) {
        ShowOKDialog(context, message, title, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Does nothing
            }
        });
    }

    public static void ShowOKDialog(Context context, @StringRes int message, @StringRes int title) {
        ShowOKDialog(context, context.getResources().getString(message), context.getResources().getString(title));
    }
    public static void ShowOKDialog(Context context, @StringRes int message, @StringRes int title, DialogInterface.OnClickListener onClick) {
        ShowOKDialog(context, context.getResources().getString(message), context.getResources().getString(title), onClick);
    }
}
