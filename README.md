#Webservice for Privacy Crash Cam
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

####Setup OpenCV library
To setup opencv on your runtime add the native library files to your java.library.path. \
Therefore download opencv 3.1.0 for your platform from http://opencv.org/downloads.html and
put opencv_java310.* and opencv_ffmpeg310_64.* (for 64-bit systems) or opencv_ffmpeg310_32.*
(for 32 bit systems) into your java.library.path \
\* is the extention for your platforms native librarys (.dll for Windows, .so for Linux)

To check if opencv is setup, look into the java.library.path with
```
java -XshowSettings:properties
```
