# FamKidMem (Familien-Kindheits-Erinnerungen)
Remember those 'selbstgedrehte videos' from mid ninetees?\
Maybe you have them on a stick or your pc, but do you know where to look for it?\
Are you sure, you not lost it?\
Imagine you could always watch this videos and experience childhood memories with just one click in the internet.\
This is what this web based application does.\
You don't even have to turn your tower pc on. You can watch the videos online on your smartphone (better via wlan)

# CCMS
This repository contains the sources for the FamKidMem Control & Content Management System.\


# Features
* **User-Maintenance**
  * **Add Users**
  * **Update Users** (/admin)
  * **Delete Users** (/admin)
* **Add, update and delete Videos**

# settings.json
You will need a settings.json with following format:\
{\
&nbsp;&nbsp;&nbsp;&nbsp;"backendHost": "string",\
&nbsp;&nbsp;&nbsp;&nbsp;"backendPort": "string",\
&nbsp;&nbsp;&nbsp;&nbsp;"backendProtocol": "string",\
&nbsp;&nbsp;&nbsp;&nbsp;"backendFilesDir": "string",\
&nbsp;&nbsp;&nbsp;&nbsp;"apiKey": "string",\
&nbsp;&nbsp;&nbsp;&nbsp;"sshHost": "string",\
&nbsp;&nbsp;&nbsp;&nbsp;"sshPort": "string",\
&nbsp;&nbsp;&nbsp;&nbsp;"sshKeyPath": "string",\
&nbsp;&nbsp;&nbsp;&nbsp;"sshTunnelLocalPort": "string"\
}\
**apiKey can be empty if no apiKey is needed**

# Build
**mvn clean package**

# Run
**java -jar famkidmem-ccms...jar**

# All Repos for FamKidMem
* Web-Backend: https://github.com/tomatenmark/famkidmem-backend
* Control & Content-Management-System (CCMS): https://github.com/tomatenmark/famkidmem-ccms
* Frontend: https://github.com/tomatenmark/famkidmem-frontend

# Security Architecture
https://cloud.markherrmann.de/index.php/s/DoK6MV7uHZx0wy2