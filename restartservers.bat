net stop tomcat8
copy /y C:\Programme\TrainingRestApp\target\SpringBoot_Part_1-0.0.1-SNAPSHOT.war C:\Programme\apache-tomcat-8.0.8-x86\webapps\ROOT.war
net start tomcat8
ping 127.0.0.1 -n 80 -w 1000 > nul
net start training-node-app
