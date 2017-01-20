#### pcc-imp-webservice
###Setup a Test-Database
```
psql
CREATE DATABASE PrivacyCrashCam;
\q
psql -d PrivacyCrashCam -a -f src/main/resources/postgres/createTablesAndTestData.sql
```
