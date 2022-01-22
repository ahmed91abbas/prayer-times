javac -d . DayParts.java
javac -d . DataCenter.java
javac -encoding utf8 -d . PrayerTimes.java
mkdir -p output
jar cmvf META-INF/MANIFEST.MF output/prayer-times.jar assets prayertimes
java -jar output/prayer-times.jar
