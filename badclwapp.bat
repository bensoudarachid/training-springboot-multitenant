
rem Build server war file
c:
cd C:\Programme\TrainingRestApp\
call C:\Programme\apache-maven-3.3.9\bin\mvn.cmd clean package

rem Now deploy to Cluster
copy /y C:\Programme\TrainingRestApp\target\*.war C:\Programme\apache-tomcat-8.0.8-x86-cluster\apache-tomcat-8.0.8-x86-2\webapps\

ping 127.0.0.1 -n 80 -w 1000 > nul


rem Get Node and Webapp files into web app program folder /NFL /NDL /NJH /NJS /NC /NS /NP
robocopy D:\ProgFiles\jenkins\workspace\GetNodeTrainingAppFromGitlab\build C:\Programme\TrainingNodeApp\build /e /zb /xo /copyall /dcopy:t /purge 
robocopy D:\ProgFiles\jenkins\workspace\GetNodeTrainingAppFromGitlab C:\Programme\TrainingNodeApp\ /e /zb /xo /copyall /dcopy:t /xd .git src build
c:
cd C:\Programme\TrainingNodeApp\
call npm install

rem call npm run build

pm2 reload all


rem c:
rem cd C:\Programme\TrainingNodeApp\
rem pm2 reload pm2apps.json