package com.example.prayertimes;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    private ListView scheduleList;
    private TextView dateTextView;
    private TextView timeRemainingTextView;
    private TextView infoTextView;

    private DataCenter dc;
    private int month;
    private int day;
    private int fajr;
    private int sunrise;
    private int zohar;
    private int asar;
    private int magrib;
    private int isha;
    private long diff, t;
    private Thread myThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateTextView = (TextView) findViewById(R.id.dateTextView);
        infoTextView = (TextView) findViewById(R.id.infoTextView);
        timeRemainingTextView = (TextView) findViewById(R.id.timeRemainingTextView);
        scheduleList = (ListView) findViewById(R.id.scheduleListView);
        scheduleList.setEnabled(false);

        t = System.currentTimeMillis();
        myThread = new CountDownThread();
        myThread.start();
    }

    private void onStartUp() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String date = sdf.format(c.getTime());
        String[] data = date.split("-");
        month = Integer.parseInt(data[1]);
        day = Integer.parseInt(data[2]);

        int mdd = month * 100 + day;
        System.out.println("key from main: " + mdd);
        DayParts dp = dc.getDayParts(mdd);
        fajr = dp.getFajr();
        System.out.println("fajr from main: " + fajr);
        sunrise = dp.getSunrise();
        zohar = dp.getZohar();
        asar = dp.getAsar();
        magrib = dp.getMagrib();
        isha = dp.getIsha();

        String[] countryList = {
                "فجر: " + fixFormatHHmm(fajr),
                "شمس: " + fixFormatHHmm(sunrise),
                "ظهر: " + fixFormatHHmm(zohar),
                "عصر: " + fixFormatHHmm(asar),
                "مغرب: " + fixFormatHHmm(magrib),
                "عشاء: " + fixFormatHHmm(isha),
        };
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_listview, R.id.scheduleTextView, countryList);
        runOnUiThread(() -> {
            dateTextView.setText(date);
            scheduleList.setAdapter(arrayAdapter);
            ListViewHelper.adjustListViewSize(scheduleList);
        });
    }

    private String fixFormatHHmm(int nbr) {
        int hh = nbr / 100;
        int mm = nbr % 100;
        String s = "";
        if (hh < 10)
            s += "0";
        s += hh + ":";
        if (mm < 10)
            s += "0";
        return s + mm;
    }

    public void doWork() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH mm");
        String date = sdf.format(c.getTime());
        String[] data = date.split(" ");
        int hour = Integer.parseInt(data[0]);
        int min = Integer.parseInt(data[1]);
        int HHmm = hour * 100 + min;
        setNextPrayerInfo(HHmm);
    }

    private void setNextPrayerInfo(int HHmm) {
        String timeRemaining;
        String s1 = "الوقت المتبقي ";
        String s2 = "لأذان ";
        String s3 = "";
        String s4 = ":";
        if (HHmm < fajr) {
            timeRemaining = countDiff(fajr, HHmm);
            s3 = "الفجر";
        } else if (HHmm < sunrise) {
            timeRemaining = countDiff(sunrise, HHmm);
            s2 = "لطلوع ";
            s3 = "الشمس";
        } else if (HHmm < zohar) {
            timeRemaining = countDiff(zohar, HHmm);
            s3 = "الظهر";
        } else if (HHmm < asar) {
            timeRemaining = countDiff(asar, HHmm);
            s3 = "العصر";
        } else if (HHmm < magrib) {
            timeRemaining = countDiff(magrib, HHmm);
            s3 = "المغرب";
        } else if (HHmm < isha) {
            timeRemaining = countDiff(isha, HHmm);
            s3 = "العشاء";
        } else { // count diff to fajr of next day
            String timeToMidNight = countDiff(2400, HHmm);
            int HH = Integer.parseInt(timeToMidNight.substring(0, timeToMidNight.indexOf(':')));
            int mm = Integer.parseInt(timeToMidNight.substring(timeToMidNight.indexOf(':') + 1));
            int nextfajr = getTimeToNextFajr();
            int HHFajr = nextfajr / 100;
            int mmFajr = nextfajr % 100;
            int hour = HH + HHFajr;
            int minute = mm + mmFajr;
            if (minute >= 60) {
                minute = minute - 60;
                hour++;
            }
            timeRemaining = fixFormat(hour) + ":" + fixFormat(minute);
            s3 = "فجر";
            s4 = " يوم الغد:";
        }
        timeRemainingTextView.setText(timeRemaining);

        String info = s1 + s2 + s3 + s4;
        int startPos = (s1 + s2).length();
        int endPos = startPos + s3.length();
        SpannableString ss = new SpannableString(info);
        ss.setSpan(new RelativeSizeSpan(2f), startPos, endPos, 0);
        infoTextView.setText(ss);
        infoTextView.requestLayout();
    }

    private String countDiff(int prayerTime, int currentTime) {
        int HHprayer = prayerTime / 100;
        int MMprayer = prayerTime % 100;
        int HHcurrentTime = currentTime / 100;
        int MMcurrentTime = currentTime % 100;
        int newHH = HHprayer - HHcurrentTime;
        int newMM = MMprayer - MMcurrentTime;
        if (newMM < 0) {
            newMM = 60 + newMM;
            newHH--;
        }
        return fixFormat(newHH) + ":" + fixFormat(newMM);
    }

    private String fixFormat(int nbr) {
        String s = "";
        if (nbr < 10)
            s += "0";
        return s + nbr;
    }

    private int getTimeToNextFajr() {
        int result;
        try {
            DayParts parts = dc.getDayParts((month * 100) + (day + 1));
            result = parts.getFajr();
        } catch (Exception e) {
            int nextDay = 1;
            int nextMonth = month + 1;
            if (nextMonth == 13)
                nextMonth = 1;
            DayParts parts = dc.getDayParts(nextMonth * 100 + nextDay);
            result = parts.getFajr();
        }
        return result;
    }

    class CountDownThread extends Thread {
        // @Override
        public void run() {
            dc = new DataCenter();
            onStartUp();
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    doWork();
                    t += 1000;
                    diff = t - System.currentTimeMillis();
                    if (diff > 0)
                        Thread.sleep(diff);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } catch (Exception e) {
                }
            }
        }
    }
}
