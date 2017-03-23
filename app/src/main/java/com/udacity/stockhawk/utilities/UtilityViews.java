package com.udacity.stockhawk.utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.holders.HolderDialogResponse;

public class UtilityViews {

    static private ProgressDialog loadingProgress;

    static public void showLoadingDialog(Activity activity, String title, String content, boolean visibility) {
        if (visibility) {
            if (!(loadingProgress != null && loadingProgress.isShowing())) {
                loadingProgress = ProgressDialog.show(activity, title,
                        content, true);
            }
        } else {
            if (loadingProgress.isShowing()) loadingProgress.dismiss();
        }
    }

    static public void showDialogResponse(final Activity activity, boolean isSuccess, String message) {
        final AlertDialog.Builder bldShowInfo = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.item_dialog_response, null);
        bldShowInfo.setView(dialogView);
        HolderDialogResponse infoHolder = new HolderDialogResponse(dialogView);
        AlertDialog alertDialog = bldShowInfo.create();
        infoHolder.fillViews(isSuccess, message, alertDialog);
        alertDialog.show();
    }

    static public Snackbar snackMessage(View v, String msg) {
        return Snackbar.make(v, msg, Snackbar.LENGTH_LONG);
    }
}
