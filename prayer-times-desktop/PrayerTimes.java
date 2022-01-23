package prayertimes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PrayerTimes {
	private DataCenter dc;
	private int month;
	private int day;
	private int fajr;
	private int sunrise;
	private int zohar;
	private int asar;
	private int magrib;
	private int isha;
	private JLabel[] dayParts;
	private JLabel dateLabel;
	private JLabel firstLine;
	private JLabel timeRemainingLabel;
	private long diff, t;
	private JFrame frame;
	private Thread myThread;

	public PrayerTimes() {
		initGUI();
		dc = new DataCenter();
		onStartUp();
		t = System.currentTimeMillis();
		myThread = new CountDownThread();
		myThread.start();
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

	private void initGUI() {
		frame = new JFrame("Prayer times");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		JPanel panel = new JPanel();
		FlowLayout fl = new FlowLayout();
		panel.setLayout(fl);
		panel.setBackground(new Color(245, 245, 220));
		dateLabel = new JLabel();
		dateLabel.setFont(dateLabel.getFont().deriveFont(40f));
		dateLabel.setHorizontalAlignment(JLabel.RIGHT);
		firstLine = new JLabel();
		firstLine.setFont(firstLine.getFont().deriveFont(30f));
		firstLine.setHorizontalAlignment(JLabel.RIGHT);
		timeRemainingLabel = new JLabel();
		timeRemainingLabel.setForeground(new Color(10,10,170));
		timeRemainingLabel.setFont(timeRemainingLabel.getFont().deriveFont(100f));
		timeRemainingLabel.setFont(new Font(Font.SERIF, 500, 100));
		timeRemainingLabel.setHorizontalAlignment(JLabel.CENTER);
		panel.add(dateLabel);
		panel.add(firstLine);
		panel.add(timeRemainingLabel);

		JPanel times_panel = new JPanel();
		times_panel.setBackground(new Color(245, 245, 220));
		times_panel.setLayout(new GridLayout(0,4));
		panel.add(times_panel);
		dayParts = new JLabel[12];
		for (int i = 0; i < dayParts.length; i++) {
			dayParts[i] = new JLabel();
			dayParts[i].setFont(dayParts[i].getFont().deriveFont(30f));
			dayParts[i].setHorizontalAlignment(JLabel.RIGHT);
			if (i % 2 != 0)
				dayParts[i].setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 30));
			else
				dayParts[i].setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
			times_panel.add(dayParts[i]);
		}
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(dim.width / 3 - frame.getSize().width / 3, dim.height / 6 - frame.getSize().height / 6);
		frame.setPreferredSize(new Dimension(400, 480));
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {
			 @Override
			         public void windowIconified(WindowEvent event)
			         {
				 		frame.dispose();
				 		minGUI();
			         }
			 });
	}

	private void minGUI(){
	    JPanel panel = new JPanel();
	    timeRemainingLabel = new JLabel();
	    timeRemainingLabel.setForeground(new Color(10,10,170));
		timeRemainingLabel.setFont(new Font(Font.SERIF, 50, 20));
	    panel.add(timeRemainingLabel);
	    JDialog d = new JDialog();
	    d.setPreferredSize(new Dimension(50, 30));
	    d.setUndecorated(true);
	    d.setAlwaysOnTop(true);
		d.add(panel);
		d.pack();
		d.setVisible(true);
		timeRemainingLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
            }
            @Override
            public void mouseReleased(MouseEvent e) {
            	d.dispose();
            	myThread.interrupt();
            	new PrayerTimes();
            }
        });
	}

	private void onStartUp() {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy MM dd");
		String date = sdf.format(c.getTime());
		String[] data = date.split(" ");
		int year = Integer.parseInt(data[0]);
		month = Integer.parseInt(data[1]);
		day = Integer.parseInt(data[2]);

		int mdd = month * 100 + day;
		DayParts dp = dc.getDayParts(mdd);
		fajr = dp.getFajr();
		sunrise = dp.getSunrise();
		zohar = dp.getZohar();
		asar = dp.getAsar();
		magrib = dp.getMagrib();
		isha = dp.getIsha();

		if (TimeZone.getDefault().inDaylightTime(new Date()) && month == 3) {
			fajr += 100;
			sunrise += 100;
			zohar += 100;
			asar += 100;
			magrib += 100;
			isha += 100;
		}
		if (!TimeZone.getDefault().inDaylightTime(new Date()) && month == 10) {
			fajr -= 100;
			sunrise -= 100;
			zohar -= 100;
			asar -= 100;
			magrib -= 100;
			isha -= 100;
		}

		dateLabel.setText(year + "/" + month + "/" + day);
		dayParts[0].setText(fixFormatHHmm(fajr));
		dayParts[1].setText("فجر:");
		dayParts[2].setText(fixFormatHHmm(sunrise));
		dayParts[3].setText("شمس:");
		dayParts[4].setText(fixFormatHHmm(zohar));
		dayParts[5].setText("ظهر:");
		dayParts[6].setText(fixFormatHHmm(asar));
		dayParts[7].setText("عصر:");
		dayParts[8].setText(fixFormatHHmm(magrib));
		dayParts[9].setText("مغرب:");
		dayParts[10].setText(fixFormatHHmm(isha));
		dayParts[11].setText("عشاء:");

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

	// returns the next day fajr prayer time
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

	private String fixFormat(int nbr) {
		String s = "";
		if (nbr < 10)
			s += "0";

		return s + nbr;
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
		firstLine.setText(s1 + s2 + s3 + s4);
		timeRemainingLabel.setText(timeRemaining);
	}

	class CountDownThread extends Thread {
		// @Override
		public void run() {
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

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		PrayerTimes main = new PrayerTimes();
	}

}
