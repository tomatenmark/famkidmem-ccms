@echo off

echo %*>.\files\call.txt

echo  Duration: 00:00:27.00 1>&2
echo  Opening 'crypto:fileSequence1' 1>&2

ping /n 5 localhost >nul

echo frame 1>&2
echo  Opening 'crypto:fileSequence2' 1>&2

ping /n 5 localhost >nul

echo video 1>&2
