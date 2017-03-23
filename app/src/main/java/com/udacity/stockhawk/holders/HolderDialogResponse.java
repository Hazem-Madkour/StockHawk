package com.udacity.stockhawk.holders;

import android.support.v7.app.AlertDialog;
import android.support.v7.view.menu.MenuView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.udacity.stockhawk.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;


public class HolderDialogResponse {
    @BindView(R.id.imgIcon)
    ImageView imgIcon;
    @BindView(R.id.txtMessage)
    TextView txtResponse;

    public HolderDialogResponse(View rootView) {
        ButterKnife.bind(this,rootView);
    }

    public void fillViews(boolean isSuccess, String message, final AlertDialog alertDialog) {
        if (isSuccess) {
            imgIcon.setImageResource(R.drawable.ic_thumb_up);
        }
        else {
            imgIcon.setImageResource(R.drawable.ic_thumb_down);
        }
        txtResponse.setText(message);
        imgIcon.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    alertDialog.dismiss();
                } catch (Exception ex) {
                    Timber.e(ex);
                }
            }
        }, 7000);
    }
}
