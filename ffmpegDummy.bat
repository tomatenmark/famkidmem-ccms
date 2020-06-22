@echo off

echo %*>.\files\call.txt

echo  Duration: 00:00:24.00 1>&2
echo  Stream Video , 10 fps, 1>&2

echo [hls Opening 'crypto:fileSequence0' 1>&2
echo [hls Opening 'crypto:fileSequence1' 1>&2
echo frame= 80 fps 1>&2

ping /n 5 localhost >nul

echo [hls Opening 'crypto:fileSequence2' 1>&2
echo frame= 150 fps 1>&2
echo frame= 160 fps 1>&2

ping /n 5 localhost >nul

echo frame= 240 fps 1>&2
echo video 1>&2
