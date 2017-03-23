package com.udacity.stockhawk.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.utilities.UtilityGeneral;
import com.udacity.stockhawk.utilities.UtilityViews;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class HistoryFragment extends Fragment {

    public String symbol;
    private List<String> lstHistory;
    @BindView(R.id.chart)
    LineChart chart;
    @BindView(R.id.txtCloseValue)
    TextView txtCloseValue;
    @BindView(R.id.txtDate)
    TextView txtDate;

    List<Entry> mEntries;
    LineDataSet mDataSet;
    LineData mLineData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stock_history, container, false);
        ButterKnife.bind(this, rootView);
        lstHistory = UtilityGeneral.getHistory(getActivity(), symbol);
        clickConfig(rootView);
        initChart();
        return rootView;
    }

    private void initChart() {
        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setDrawAxisLine(false);
        yAxisLeft.setDrawGridLines(false);
        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setDrawAxisLine(false);
        yAxisRight.setDrawGridLines(false);
        chart.getLegend().setEnabled(false);
        chart.setViewPortOffsets(0, 0, 0, 0);
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setMarkerView(new CustomMarkerView(getContext(), R.layout.item_marker));
        Description description = new Description();
        description.setText("");
        chart.setDescription(description);
        mEntries = new ArrayList<>();
        mDataSet = new LineDataSet(mEntries, symbol);
        mDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        mDataSet.setDrawValues(false);
        mDataSet.setCircleRadius(5f);
        mDataSet.setCircleColor(Color.RED);
        mDataSet.setCircleColorHole(Color.RED);
        mDataSet.setLineWidth(2f);
        mDataSet.setColor(Color.BLACK);
        mDataSet.setHighlightEnabled(true);
        mDataSet.setDrawHighlightIndicators(true);
        mDataSet.setHighLightColor(Color.BLACK);
        mLineData = new LineData(mDataSet);
        mLineData.setValueTextSize(10f);
        mLineData.setValueTextColor(Color.RED);
        chart.setData(mLineData);
        draw(R.id.btn3M);
    }

    private void draw(int id) {
        List<String> lstData = new ArrayList<>();
        Calendar calendar;
        Long time = 0L;
        switch (id) {
            case R.id.btn3M:
                calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, -3);
                time = calendar.getTime().getTime();
                break;
            case R.id.btn6M:
                calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, -6);
                time = calendar.getTime().getTime();
                break;
            case R.id.btn1Y:
                calendar = Calendar.getInstance();
                calendar.add(Calendar.YEAR, -1);
                time = calendar.getTime().getTime();
                break;
            default:
                lstData = new ArrayList<>();
                lstData.addAll(lstHistory);
                break;
        }

        for (int i = 0; i < lstHistory.size(); i++) {
            if (Long.parseLong(lstHistory.get(i).split(",")[0]) >= time)
                lstData.add(lstHistory.get(i));
        }

        Collections.reverse(lstData);
        mEntries.clear();
        for (String data : lstData) {
            mEntries.add(new Entry(Float.parseFloat(data.split(",")[0]), Float.parseFloat(data.split(",")[1])));
        }
        mDataSet.notifyDataSetChanged();
        mLineData.notifyDataChanged();
        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    private void clickConfig(View rootView) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                draw(v.getId());
            }
        };
        rootView.findViewById(R.id.btn3M).setOnClickListener(listener);
        rootView.findViewById(R.id.btn6M).setOnClickListener(listener);
        rootView.findViewById(R.id.btn1Y).setOnClickListener(listener);
    }

    private void showMessage(String msg) {
        if (isAdded() && !isDetached() && isVisible())
            UtilityViews.snackMessage(getActivity().findViewById(android.R.id.content), msg);
    }

    private class CustomMarkerView extends MarkerView {
        public CustomMarkerView(Context context, int layoutResource) {
            super(context, layoutResource);
        }

        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            txtCloseValue.setText(String.format(Locale.getDefault(), "$%.2f", e.getY()));
            SimpleDateFormat SimpleFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            txtDate.setText(SimpleFormat.format(e.getX()));
        }
    }

}
