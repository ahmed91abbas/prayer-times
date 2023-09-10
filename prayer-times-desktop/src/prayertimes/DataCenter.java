package prayertimes;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;
import java.io.File;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;


public class DataCenter {

    private HashMap<Integer, DayParts> data;

    public DataCenter() {
        JSONObject jsonResponse = fetchPrayerTimes();
        data = new HashMap<Integer, DayParts>();
        fillDataCenter(jsonResponse);
    }

    private JSONObject fetchPrayerTimes() {
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
                return new JSONObject(response.toString());
            } else {
                System.out.println("HTTP GET request failed with response code: " + responseCode);
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
