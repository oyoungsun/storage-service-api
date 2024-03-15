# git pull하고 한 번도 실행한 적 없을 때 실행
./gradlew build -x test
cd build/libs
java -jar KioskUpdateApi-1.0.1.war