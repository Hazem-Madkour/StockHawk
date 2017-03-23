package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.adapters.StockAdapter;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.interfaces.ILoadHandler;
import com.udacity.stockhawk.interfaces.StockAdapterOnClickHandler;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.utilities.UtilityGeneral;
import com.udacity.stockhawk.utilities.UtilityViews;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        SwipeRefreshLayout.OnRefreshListener,
        StockAdapterOnClickHandler {

    //region fields
    private static final int STOCK_LOADER = 0;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.recycler_view)
    RecyclerView stockRecyclerView;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.error)
    TextView error;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.fab)
    FloatingActionButton fab;
    private StockAdapter adapter;
    private ILoadHandler<String> loadHandler;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        adapter = new StockAdapter(this, this);
        stockRecyclerView.setAdapter(adapter);
        stockRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        stockRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    if (fab.isShown()) {
                        fab.hide();
                    }
                } else if (dy < 0) {
                    if (!fab.isShown()) {
                        fab.show();
                    }
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(true);

        loadHandler = new ILoadHandler<String>() {
            @Override
            public void onInitialize(String symbol) {
                super.onInitialize(symbol);
                if (symbol != null)
                    UtilityViews.showLoadingDialog(MainActivity.this, getString(R.string.get_stock, symbol), getString(R.string.loading), true);
            }

            @Override
            public void onFinish() {
                if (symbol != null) {
                    UtilityViews.showLoadingDialog(MainActivity.this, null, null, false);
                    symbol = null;
                }
            }

            @Override
            public void onSuccess(String loadedObject) {
                UtilityViews.showDialogResponse(MainActivity.this, true, loadedObject);
            }

            @Override
            public void onFail(String errorMessage) {
                UtilityViews.showDialogResponse(MainActivity.this, false, errorMessage);
            }
        };

        onRefresh();


        QuoteSyncJob.initialize(this);
        getSupportLoaderManager().initLoader(STOCK_LOADER, null, this);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                String symbol = adapter.getSymbolAtPosition(viewHolder.getAdapterPosition());
                PrefUtils.removeStock(MainActivity.this, symbol);
                getContentResolver().delete(Contract.Quote.makeUriForStock(symbol), null, null);
            }
        }).attachToRecyclerView(stockRecyclerView);

    }

    @Override
    public void onRefresh() {

        QuoteSyncJob.syncImmediately(this);
        if (!checkInternetUI()) return;
        if (PrefUtils.getStocks(this).size() == 0) {
            swipeRefreshLayout.setRefreshing(false);
            error.setText(getString(R.string.error_no_stocks));
            error.setVisibility(View.VISIBLE);
        } else {
            error.setVisibility(View.GONE);
        }
    }

    void addStock(String symbol) {
        if (symbol != null && !symbol.isEmpty()) {
            loadHandler.onInitialize(symbol);
            checkInternetUI();
            if (UtilityGeneral.isOnline(getBaseContext())) {
                swipeRefreshLayout.setRefreshing(true);
            } else {
                String message = getString(R.string.toast_stock_added_no_connectivity, symbol);
                loadHandler.onFail(message);
                loadHandler.onFinish();
            }
            if (UtilityGeneral.isStockExist(this, symbol)) {
                loadHandler.onSuccess(getString(R.string.already_exist, symbol));
                loadHandler.onFinish();
            }
            PrefUtils.addStock(this, symbol);
            QuoteSyncJob.syncImmediately(this);
        }
    }

    //region click methods
    @Override
    public void onClick(String symbol) {
        startActivity(new Intent(getBaseContext(), HistoryActivity.class).putExtra(UtilityGeneral.SYMBOL_HISTORY_KEY, symbol));
        Timber.d("Symbol clicked: %s", symbol);
    }

    public void button(@SuppressWarnings("UnusedParameters") View view) {
        new AddStockDialog().show(getFragmentManager(), "StockDialogFragment");
    }
    //endregion

    //region loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                Contract.Quote.URI,
                Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                null, null, Contract.Quote.COLUMN_SYMBOL);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        swipeRefreshLayout.setRefreshing(false);
        if (loadHandler.symbol != null)
            if (UtilityGeneral.isStockExist(this, loadHandler.symbol))
                loadHandler.onSuccess(getString(R.string.stock_loaded_successfully, loadHandler.symbol));
            else {
                loadHandler.onFail(getString(R.string.not_find_stock, loadHandler.symbol));
            }
        loadHandler.onFinish();
        adapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        swipeRefreshLayout.setRefreshing(false);
        loadHandler.onFinish();
        adapter.setCursor(null);
    }
    //endregion

    //region helper methods

    private boolean checkInternetUI() {
        if (!UtilityGeneral.isOnline(getBaseContext())) {
            error.setText(getString(R.string.error_no_network));
            error.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
            return false;
        } else {
            error.setVisibility(View.GONE);
            return true;
        }
    }

    private void showMessage(String msg) {

        UtilityViews.snackMessage(findViewById(android.R.id.content), msg).show();
    }
    //endregion

    //region menu
    private void setDisplayModeMenuItemIcon(MenuItem item) {
        if (PrefUtils.getDisplayMode(this)
                .equals(getString(R.string.pref_display_mode_absolute_key))) {
            item.setIcon(R.drawable.ic_percentage);
        } else {
            item.setIcon(R.drawable.ic_dollar);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_settings, menu);
        MenuItem item = menu.findItem(R.id.action_change_units);
        setDisplayModeMenuItemIcon(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_change_units) {
            PrefUtils.toggleDisplayMode(this);
            setDisplayModeMenuItemIcon(item);
            adapter.notifyDataSetChanged();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //endregion
}
