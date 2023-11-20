rm -rf build
mkdir -p build
javac -d build -encoding utf8 -cp libs/*.jar src/prayertimes/*.java
unzip libs/*.jar -d build
jar cvfm build/prayer-times.jar META-INF/MANIFEST.MF -C build .
chmod +x build/prayer-times.jar
java -jar build/prayer-times.jar
