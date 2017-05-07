rem TITLE bla test
rem ping 127.0.0.1 -n 10 -w 1000 > nul
rem net stop training-node-app


rem robocopy "./src" C:\Programme\TrainingRestApp\src /e /zb /xo /copyall /dcopy:t /purge
rem robocopy "." C:\Programme\TrainingRestApp\ /e /zb /xo /copyall /dcopy:t /xd .git src target sql old .git .settings

net stop training-node-app
c:
cd C:\Programme\TrainingRestApp\
call C:\Programme\apache-maven-3.3.9\bin\mvn.cmd clean package
del C:\Programme\apache-tomcat-8.0.8-x86\webapps\ROOT.war
rem ping 127.0.0.1 -n 15 -w 1000 > nul
rem copy /y C:\Programme\TrainingRestApp\target\SpringBoot_Part_1-0.0.1-SNAPSHOT.war C:\Programme\apache-tomcat-8.0.8-x86\webapps\ROOT.war
rem cd D:\ProgFiles\jenkins\workspace\GetNodeTrainingAppFromGitlab
robocopy D:\ProgFiles\jenkins\workspace\GetNodeTrainingAppFromGitlab\build C:\Programme\TrainingNodeApp\build /e /zb /xo /copyall /dcopy:t /purge
robocopy D:\ProgFiles\jenkins\workspace\GetNodeTrainingAppFromGitlab C:\Programme\TrainingNodeApp\ /e /zb /xo /copyall /dcopy:t /xd .git src build
c:
cd C:\Programme\TrainingNodeApp\
call npm install
rem call npm run build
rem Program below will be run as an MS 'at' process
rem net stop tomcat8
rem copy /y C:\Programme\TrainingRestApp\target\SpringBoot_Part_1-0.0.1-SNAPSHOT.war C:\Programme\apache-tomcat-8.0.8-x86\webapps\ROOT.war
rem net start tomcat8
rem ping 127.0.0.1 -n 70 -w 1000 > nul
rem net start training-node-app
