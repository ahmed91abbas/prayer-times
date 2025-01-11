package com.example.prayertimes;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class DataCenter {

    public HashMap<Integer, DayParts> data;
    public HashMap<Integer, ArrayList<ArrayList<String>>> prayerTimesMap;
    private final Context context;
    private static final String CACHE_NAME = "PrayerTimesCache";
    private static final String CACHE_KEY = "cachedData";


    public DataCenter(Context context) {
        this.context = context;
    }

    public void init() {
        JSONObject jsonResponse = getCachedData();
        if (jsonResponse == null) {
            System.out.println("Fetching data from the server.");
            jsonResponse = fetchPrayerTimes();
        }
        data = new HashMap<Integer, DayParts>();
        prayerTimesMap = new HashMap<Integer,ArrayList<ArrayList<String>>>();
        fillDataCenter(jsonResponse);
    }

    public JSONObject fetchPrayerTimes() {
        try {
            String url = "https://time.my-masjid.com/api/TimingsInfoScreen/GetMasjidTimings?GuidId=fb062588-16ad-4b6a-b4f9-5821e01b7f8f";
            URL apiUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                saveToCache(response.toString());
                return new JSONObject(response.toString());
            } else {
                System.out.println("HTTP GET request failed with response code: " + responseCode);
            }
            connection.disconnect();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        System.out.println("Trying to fetch data from cache instead.");
        return getCachedData();
    }

    private JSONObject getCachedData() {
        SharedPreferences prefs = context.getSharedPreferences(CACHE_NAME, Context.MODE_PRIVATE);
        String cachedDataString = prefs.getString(CACHE_KEY, null);
        if (cachedDataString != null) {
            try {
                return new JSONObject(cachedDataString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void saveToCache(String data) {
        SharedPreferences prefs = context.getSharedPreferences(CACHE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(CACHE_KEY, data);
        editor.apply();
    }

    private void fillDataCenter(JSONObject jsonResponse) {
        try {
            JSONArray salahTimingsArray = jsonResponse.getJSONObject("model").getJSONArray("salahTimings");
            for (int i = 0; i < salahTimingsArray.length(); i++) {
                JSONObject salahTiming = salahTimingsArray.getJSONObject(i);
                int key = salahTiming.getInt("month") * 100 + salahTiming.getInt("day"); // mdd
                DayParts dp = new DayParts(
                        toInt(salahTiming.getString("fajr")),
                        toInt(salahTiming.getString("shouruq")),
                        toInt(salahTiming.getString("zuhr")),
                        toInt(salahTiming.getString("asr")),
                        toInt(salahTiming.getString("maghrib")),
                        toInt(salahTiming.getString("isha"))
                );
                data.put(key, dp);
                int newKey = salahTiming.getInt("month");
                ArrayList<String> value = new ArrayList<String>();
                value.add(String.valueOf(salahTiming.getInt("day")));
                value.add(salahTiming.getString("fajr"));
                value.add(salahTiming.getString("shouruq"));
                value.add(salahTiming.getString("zuhr"));
                value.add(salahTiming.getString("asr"));
                value.add(salahTiming.getString("maghrib"));
                value.add(salahTiming.getString("isha"));
                if (prayerTimesMap.containsKey(newKey)) {
                    prayerTimesMap.get(newKey).add(value);
                } else {
                    ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
                    list.add(value);
                    prayerTimesMap.put(newKey, list);
                }
            }
        } catch (Exception e) {
            System.out.println("Couldn't fill data center." + e.getMessage());
        }
    }

    private int toInt(String str) {
        return Integer.parseInt(str.replace(":", ""));
    }

    public DayParts getDayParts(int mdd) {
        return data.get(mdd);
    }
}
