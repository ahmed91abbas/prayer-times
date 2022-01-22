package prayertimes;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;
import java.io.File;


public class DataCenter {

	private HashMap<Integer, DayParts> data;

	public DataCenter() {
		data = new HashMap<Integer, DayParts>();
		for (int i=1; i <= 12; i++) {
			fillDataCenter("assets/" + i + ".txt", i);
		}
	}

	private void fillDataCenter(String path, int monthNbr) {
		try {
			Scanner scan = null;
			// used when reading files from the jar file
			InputStream iStream = DataCenter.class.getResourceAsStream("/" + path);
			if (iStream != null) {
				scan = new Scanner(iStream);
			} else {
				File file = new File(path);
				scan = new Scanner(file);
			}
			while (scan != null && scan.hasNext()) {
				String line = scan.nextLine();
				String[] times = line.split(" ");
				int key = monthNbr * 100 + Integer.parseInt(times[0]); // MMdd
				int fajr = Integer.parseInt(times[1]);
				int sunrise = Integer.parseInt(times[2]);
				int zohar = Integer.parseInt(times[3]);
				int asar = Integer.parseInt(times[4]);
				int magrib = Integer.parseInt(times[5]);
				int isha = Integer.parseInt(times[6]);
				data.put(key, new DayParts(fajr, sunrise, zohar, asar, magrib, isha));
			}

			if (scan != null)
				scan.close();

		} catch (Exception e) {
			System.out.println("Couldn't fill data center for path: " + path + "\n" + e);
		}
	}

	public DayParts getDayParts(int MMdd) {
		return data.get(MMdd);
	}
}
