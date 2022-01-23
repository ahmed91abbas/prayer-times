package com.example.prayertimes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import android.content.res.AssetManager;

public class DataCenter {
	private AssetManager assets;
	private HashMap<Integer, DayParts> data;

	public DataCenter(AssetManager assets) {
		this.assets = assets;
		data = new HashMap<Integer, DayParts>();
		for (int i=1; i <= 12; i++) {
			fillDataCenter(i + ".txt", i);
		}
	}

	private void fillDataCenter(String path, int monthNbr) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(
					new InputStreamReader(assets.open(path)));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] times = line.split(" ");
				int key = monthNbr * 100 + Integer.parseInt(times[0]); // mdd
				int fajr = Integer.parseInt(times[1]);
				int sunrise = Integer.parseInt(times[2]);
				int zohar = Integer.parseInt(times[3]);
				int asar = Integer.parseInt(times[4]);
				int magrib = Integer.parseInt(times[5]);
				int isha = Integer.parseInt(times[6]);
				data.put(key, new DayParts(fajr, sunrise, zohar, asar, magrib, isha));
			}
		} catch (IOException e) {
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public DayParts getDayParts(int mdd) {
		return data.get(mdd);
	}
}
