package com.udacity.stockhawk.holders;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HolderStock extends RecyclerView.ViewHolder {

    @BindView(R.id.symbol)
    TextView symbol;

    @BindView(R.id.price)
    TextView price;

    @BindView(R.id.change)
    TextView change;

    public HolderStock(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void fillViews(Cursor cursor,
                          DecimalFormat dollarFormat,
                          DecimalFormat dollarFormatWithPlus,
                          DecimalFormat percentageFormat) {
        symbol.setText(cursor.getString(Contract.Quote.POSITION_SYMBOL));
        price.setText(dollarFormat.format(cursor.getFloat(Contract.Quote.POSITION_PRICE)));

        float rawAbsoluteChange = cursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
        float percentageChange = cursor.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);

        if (rawAbsoluteChange > 0) {
            change.setBackgroundResource(R.drawable.percent_change_pill_green);
        } else {
            change.setBackgroundResource(R.drawable.percent_change_pill_red);
        }

        String change = dollarFormatWithPlus.format(rawAbsoluteChange);
        String percentage = percentageFormat.format(percentageChange / 100);

        if (PrefUtils.getDisplayMode(itemView.getContext())
                .equals(itemView.getContext().getString(R.string.pref_display_mode_absolute_key))) {
            this.change.setText(change);
        } else {
            this.change.setText(percentage);
        }

    }

    public void setClickListener(View.OnClickListener listener) {
        itemView.setOnClickListener(listener);
    }
}
