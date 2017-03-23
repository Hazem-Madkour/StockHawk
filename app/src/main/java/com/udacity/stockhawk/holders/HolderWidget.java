package com.udacity.stockhawk.holders;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.utilities.UtilityGeneral;

import java.text.DecimalFormat;

public class HolderWidget {

    Context mContext;
    RemoteViews mRemoteViews;

    public HolderWidget(RemoteViews remoteViews, Context context) {
        mContext = context;
        mRemoteViews = remoteViews;
    }

    public void fillViews(Cursor cursor,
                          DecimalFormat dollarFormat,
                          DecimalFormat dollarFormatWithPlus,
                          DecimalFormat percentageFormat) {
        String symbol = cursor.getString(Contract.Quote.POSITION_SYMBOL);
        String price = dollarFormat.format(cursor.getFloat(Contract.Quote.POSITION_PRICE));
        float rawAbsoluteChange = cursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
        float percentageChange = cursor.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);

        if (rawAbsoluteChange > 0) {
            mRemoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
        } else {
            mRemoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
        }
        mRemoteViews.setTextViewText(R.id.symbol, symbol);
        mRemoteViews.setTextViewText(R.id.price, price);

        String change = dollarFormatWithPlus.format(rawAbsoluteChange);
        String percentage = percentageFormat.format(percentageChange / 100);

        if (PrefUtils.getDisplayMode(mContext)
                .equals(mContext.getString(R.string.pref_display_mode_absolute_key))) {
            mRemoteViews.setTextViewText(R.id.change, change);
        } else {
            mRemoteViews.setTextViewText(R.id.change, percentage);
        }
        mRemoteViews.setOnClickFillInIntent(R.id.lytWidgeListItem, new Intent().putExtra(UtilityGeneral.SYMBOL_HISTORY_KEY, symbol));
    }
}
