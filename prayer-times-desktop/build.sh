rm -rf build
mkdir -p build
javac -d build -encoding utf8 -cp "libs/*.jar" src/prayertimes/*.java
# java -cp "build;libs/json-20230618.jar" prayertimes/PrayerTimes
unzip "libs/*.jar" -d build
jar cvfm build/prayer-times.jar META-INF/MANIFEST.MF -C build .
java -jar build/prayer-times.jar
