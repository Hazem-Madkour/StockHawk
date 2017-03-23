package com.udacity.stockhawk.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.adapters.AdapterHistoryPager;
import com.udacity.stockhawk.utilities.UtilityGeneral;
import com.udacity.stockhawk.utilities.UtilityViews;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stocks_history_main);
        initPager();
    }

    private void initPager() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        int index = setupViewPager(viewPager);
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        tabs.getTabAt(index).select();
    }

    private void showMessage(String msg) {
        UtilityViews.snackMessage(findViewById(android.R.id.content), msg).show();
    }

    private int setupViewPager(ViewPager viewPager) {
        AdapterHistoryPager adapter = new AdapterHistoryPager(getSupportFragmentManager());
        HistoryFragment fragment;
        String symbol = getIntent().getExtras().getString(UtilityGeneral.SYMBOL_HISTORY_KEY);
        int index = 0;
        ArrayList<String> symbols = UtilityGeneral.getSymbols(this);
        for (int i = 0; i < symbols.size(); i++) {
            fragment = new HistoryFragment();
            if (!fragment.isAdded()) fragment.setArguments(new Bundle());
            fragment.symbol = symbols.get(i);
            adapter.addFragment(fragment, symbols.get(i));
            if (symbol.equals(symbols.get(i)))
                index = i;
        }
        viewPager.setAdapter(adapter);
        return index;
    }
}
