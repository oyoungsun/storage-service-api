echo "@@@@ build artifacts start : @@@@"
echo "===> wrapper : "
./gradlew wrapper
echo "===> build : "
./gradlew build -x test
# 만약 artifacts 디렉토리가 존재하지 않는다면 생성
echo "===> mkdir ROOT : "
mkdir -p out/artifacts/ROOT
echo "===> copy ROOT.war : "
echo "===> build artifacts st : "
cp build\libs\KioskUpdateApi-1.0.1.war out\artifacts\ROOT\ROOT.war
echo "===> build artifacts success : "
echo "@@@sleep 10 sec, but mean nothing@@@"
sleep 10



