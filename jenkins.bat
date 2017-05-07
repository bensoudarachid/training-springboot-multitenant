rem TITLE bla test
rem ping 127.0.0.1 -n 10 -w 1000 > nul
rem net stop training-node-app

rem robocopy "./src" C:\Programme\TrainingRestApp\src /e /zb /xo /copyall /dcopy:t /purge
rem robocopy "." C:\Programme\TrainingRestApp\ /e /zb /xo /copyall /dcopy:t /xd .git src target sql old .git .settings

rem c:
rem cd C:\Programme\TrainingRestApp\

rem call npm install --only=dev
rem call C:\Programme\apache-maven-3.3.9\bin\mvn.cmd install

rem call C:\Programme\apache-maven-3.3.9\bin\mvn.cmd clean package
rem del C:\Programme\apache-tomcat-8.0.8-x86\webapps\ROOT.war
rem ping 127.0.0.1 -n 15 -w 1000 > nul
rem copy /y .\target\SpringBoot_Part_1-0.0.1-SNAPSHOT.war C:\Programme\apache-tomcat-8.0.8-x86\webapps\ROOT.war

robocopy "./src" C:\Programme\TrainingRestApp\src /e /zb /xo /copyall /dcopy:t /purge
robocopy "." C:\Programme\TrainingRestApp\ /e /zb /xo /copyall /dcopy:t /xd .git src target sql old .git .settings

exit 0