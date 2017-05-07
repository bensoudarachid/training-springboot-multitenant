c:
cd C:\Programme\TrainingRestApp\
call C:\Programme\apache-maven-3.3.9\bin\mvn.cmd clean package
del C:\Programme\apache-tomcat-8.0.8-x86\webapps\ROOT.war
copy /y C:\Programme\TrainingRestApp\target\SpringBoot_Part_1-0.0.1-SNAPSHOT.war C:\Programme\apache-tomcat-8.0.8-x86\webapps\ROOT.war
