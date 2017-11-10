c:
cd C:\Programme\TrainingRestApp\
call C:\Programme\apache-maven-3.3.9\bin\mvn.cmd clean package

SET activeworker=2
rem SET deployto=1
for /F "tokens=3 delims=: " %%H in ('sc queryex "tomcat8clusterworker2" ^| findstr "        STATE"') do (
  if /I "%%H" NEQ "RUNNING" (
   REM Put your code you want to execute here
   REM For example, the following line
   SET activeworker=1
rem    SET deployto=2
  )
)
ECHO activeworker = %activeworker%
rem ECHO deployto = %deployto%


for /F "TOKENS=1,2,*" %%a in ('tasklist /FI "IMAGENAME eq tomcat8.exe"') do set awPID=%%b
echo Active worker process %awPID% gets above normal priority
wmic process where processid='%awPID%' CALL setpriority "above normal"

ECHO Copy new war to worker %activeworker%
copy /y C:\Programme\TrainingRestApp\target\*.war C:\Programme\apache-tomcat-8.0.8-x86-cluster\apache-tomcat-8.0.8-x86-%activeworker%\webapps\
