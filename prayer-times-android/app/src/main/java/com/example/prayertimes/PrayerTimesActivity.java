package com.example.prayertimes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class PrayerTimesActivity extends Activity {
    private HashMap<Integer, ArrayList<ArrayList<String>>> prayerTimesMap;
    private HashMap<Integer, String> monthMap = new HashMap<Integer, String>() {{
        put(1, "January");
        put(2, "February");
        put(3, "March");
        put(4, "April");
        put(5, "May");
        put(6, "June");
        put(7, "July");
        put(8, "August");
        put(9, "September");
        put(10, "October");
        put(11, "November");
        put(12, "December");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prayer_times_table);

        Intent intent = getIntent();
        prayerTimesMap =
                (HashMap<Integer, ArrayList<ArrayList<String>>>) intent.getSerializableExtra("prayerTimesMap");

        TableLayout table = findViewById(R.id.prayerTimesTable);
        populateTable(table);
    }

    private void populateTable(TableLayout table) {
        for (int month = 1; month <= 12; month++) {
            ArrayList<ArrayList<String>> prayerTimes = prayerTimesMap.get(month);
            if (prayerTimes == null) {
                continue;
            }

            TableRow monthRow = new TableRow(this);
            TextView monthTextView = new TextView(this);
            monthTextView.setText(monthMap.get(month));
            monthTextView.setTextSize(25);
            monthTextView.setTextColor(0xFF000000);
            monthTextView.setPadding(8, 16, 8, 16);

            TableRow.LayoutParams monthParams = new TableRow.LayoutParams();
            monthParams.span = 7;
            monthTextView.setLayoutParams(monthParams);
            monthRow.addView(monthTextView);
            table.addView(monthRow);

            TableRow headerRow = new TableRow(this);
            for (String prayerName : new String[]{"Day", "Fajr", "Shms", "Zuhr", "Asr", "Mgrb", "Isha"}) {
                TextView headerTextView = new TextView(this);
                headerTextView.setText(prayerName);
                headerTextView.setTextColor(0xFFFFFFFF);
                headerTextView.setBackgroundColor(0xFFb28fc7);
                headerTextView.setPadding(8, 8, 8, 8);

                TableRow.LayoutParams headerParams = new TableRow.LayoutParams(
                        0, TableRow.LayoutParams.WRAP_CONTENT, 1f
                );
                headerTextView.setLayoutParams(headerParams);
                headerRow.addView(headerTextView);
            }
            table.addView(headerRow);

            boolean alternate = false;
            for (ArrayList<String> prayerTime : prayerTimes) {
                TableRow row = new TableRow(this);
                for (String time : prayerTime) {
                    TextView timeTextView = new TextView(this);
                    timeTextView.setText(time);
                    timeTextView.setTextColor(0xFF000000);
                    timeTextView.setPadding(8, 8, 8, 8);

                    TableRow.LayoutParams timeParams = new TableRow.LayoutParams(
                            0, TableRow.LayoutParams.WRAP_CONTENT, 1f
                    );
                    timeTextView.setLayoutParams(timeParams);
                    row.addView(timeTextView);
                }
                row.setBackgroundColor(alternate ? 0xFFe6e6ff: 0xFFf7e4d0);
                alternate = !alternate;
                table.addView(row);
            }
        }
    }
}
