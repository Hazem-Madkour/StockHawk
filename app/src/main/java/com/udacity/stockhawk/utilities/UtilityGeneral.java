package com.udacity.stockhawk.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.udacity.stockhawk.data.Contract;

import java.util.ArrayList;
import java.util.Collections;

public class UtilityGeneral {
    final static public String SYMBOL_HISTORY_KEY = "symbol";

    final static public String ACTION_DATA_UPDATED = "com.udacity.stockhawk.ACTION_DATA_UPDATED";

    static public boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    static public void sendUpdateIntentToReceiver(Context context) {
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED);
        context.sendBroadcast(dataUpdatedIntent);
    }

    static public boolean isStockExist(Activity activity, String symbol) {
        Cursor cursor = activity.getContentResolver().query(
                Contract.Quote.URI,
                new String[]{Contract.Quote.COLUMN_SYMBOL},
                "( " + Contract.Quote.COLUMN_SYMBOL + " = ? );",
                new String[]{symbol},
                null);
        boolean found = cursor.moveToFirst();
        cursor.close();
        return found;
    }

    static public ArrayList<String> getSymbols(Activity activity) {
        Cursor cursor = activity.getContentResolver().query(
                Contract.Quote.URI,
                new String[]{Contract.Quote.COLUMN_SYMBOL},
                null, null, Contract.Quote.COLUMN_SYMBOL);
        ArrayList<String> lstTemp = new ArrayList<>();
        if (cursor.moveToFirst()) {
            int symbolIndex = cursor.getColumnIndex(Contract.Quote.COLUMN_SYMBOL);
            do {
                lstTemp.add(cursor.getString(symbolIndex));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lstTemp;
    }


    static public ArrayList<String> getHistory(Activity activity, String symbol) {
        Cursor cursor = activity.getContentResolver().query(
                Contract.Quote.URI,
                new String[]{Contract.Quote.COLUMN_HISTORY},
                "( " + Contract.Quote.COLUMN_SYMBOL + " = ? );",
                new String[]{symbol},
                null);
        ArrayList<String> lstTemp = new ArrayList<>();
        if (cursor.moveToFirst()) {
            int historyIndex = cursor.getColumnIndex(Contract.Quote.COLUMN_HISTORY);
            Collections.addAll(lstTemp, cursor.getString(historyIndex).split("\n"));
        }
        cursor.close();
        return lstTemp;
    }
}
