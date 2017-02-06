#Webservice for Privacy Crash Cam

<p>In Germany and Austria the Data Protection Legislation forbids car drivers to use Dash Cams as evidence in case of car accidents. Dash Cams usually record faces and car tags. Tough, these are private information and should not be recorded unless one has permission to do so.</p>
<p>This project focuses on developing a solution which allows Dash Cams to be used as evividence in case of car accidents while ensuring that the requirements stated by the Data Protection Law are met. The Privacy Crash Cam Smartphone App records one minute of videomaterial when a crash occours and encrypts it. After storing the encrypted media file to the device storage the user can upload it to a server which will decrypt and anonymize the video. This will render faces and car tags unrecognizable and offer the result as mp4 download to the user. Managing and downloading the anonymized video as well as managing user accounts is done via the web interface.</p>
<p>As such, this project consists of three parts (App, Web Service and Web Interface). You can find each part in a separate git repository.</p>

##Preparation/ Installation
The following software is required for this project:
* Java
* Postgres
* Maven
* OpenCV

####Setup a Test-Database
```
psql
CREATE DATABASE PrivacyCrashCam;
\q
psql -d PrivacyCrashCam -a -f src/main/resources/postgres/createTablesAndTestData.sql
```
To get a connection to the data, edit the file src/main/java/edu/kit/informatik/pcc/service/data/DatabaseManager.java to change username, password etc for your own details

####Setup OpenCV library
To setup opencv on your runtime add the native library files to your java.library.path. <br /> 
Therefore download opencv 3.1.0 for your platform from http://opencv.org/downloads.html and
put opencv_java310.* and opencv_ffmpeg310_64.* (for 64-bit systems) or opencv_ffmpeg310_32.*
(for 32 bit systems) into your java.library.path <br />
\* is the extention for your platforms native librarys (.dll for Windows, .so for Linux)

To check if opencv is setup, look into the java.library.path with
```
java -XshowSettings:properties
```
