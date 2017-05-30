package com.github.bwindsor.pairlearnapp;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;

/**
 * Created by Ben on 25/05/2017.
 * DialogHelper provides functionality to make it easier to display dialogs warning the user of
 * something
 */
public class DialogHelper {
    /**
     * Show a simple dialog with a message and an OK button
     * @param context context in which the dialog is being created
     * @param message message to display
     * @param title title of the dialog box
     * @param onClickListener listener interface with commands to run when the OK button is clicked.
     *                        The dialog is always dismissed when OK is clicked, and any further
     *                        commands are run before it is dismissed.
     */
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

    /**
     * Show a simple dialog with a message and an OK button, which when OK is clicked it just closes
     * @param context context in which the dialog is being created
     * @param message message to display
     * @param title title of the dialog box
     */
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
