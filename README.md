# FamKidMem (Family Moments)
'Selbstgedrehte Videos'

# CCMS
This repository contains the sources for the FamKidMem Control & Content Management System / Admin & Editor Tool.\


# Features
* **User-Maintenance**
  * **Add Users**
  * **Update Users** (/admin)
  * **Delete Users** (/admin)
* **Add, update and delete Videos**

# Requirements
## ffmpeg
* ffmpeg must be installed
* The bin directory of ffmpeg must be added to the systems PATH variable

## HLS support
* A browser with hls support is required

# settings.json
You will need a settings.json with following format:\
{\
&nbsp;&nbsp;&nbsp;&nbsp;"backendUrl": "string",\
&nbsp;&nbsp;&nbsp;&nbsp;"frontendUrl": "string",\
&nbsp;&nbsp;&nbsp;&nbsp;"apiKey": "string",\
&nbsp;&nbsp;&nbsp;&nbsp;"masterKey": "string"\
}\
**apiKey has to be a secure random 128 bit key (base64 encoded) or a random uuid**\
**masterKey has to be secure random 128 bit key (base 64 encoded)** (**open-ssl rand** for example)\
**backendUrl is the url of the backend** (example: https://ccms.example.de/)\
**frontendUrl is the public base url of famkidmem** (example: https://example.de/)

# Build
**mvn clean package**

# Run
**java -jar famkidmem-ccms...jar**

# Unit Testing
## ffmpegDummy
If you run unit tests under non-windows-systems, you may have to run following command before:\
**chmod a+x ffmpegDummy.sh**\
The ffmpegDummy.sh file is a ffmpeg dummy for testing. The command allows its execution

# Credits
* Spring boot and Thymeleaf
* javax.crypto and crypto-js
* FFmpeg and HLS
