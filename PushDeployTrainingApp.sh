cd /d/RP/Tests/SpringBoot_Training
/C/Programm/apache-ant-1.10.1/bin/ant.bat -f version.xml
git add .
git commit -m "$*"
git push httpsorigin master
curl --insecure https://localhost:6767/jenkins/buildByToken/build?job=GetRestTrainingAppFromGitlab\&token=d8g347rg375trft3dr34r8zr84t845gc58g5t
cd /d/RP/Tests/ReactToDoExp2
npm run build
git add .
git commit -m "$*"
git push httpsorigin master
curl --insecure https://localhost:6767/jenkins/buildByToken/build?job=GetNodeTrainingAppFromGitlab\&token=5t8r28gg0f8h47gfgjhgf8762gfd34rv28fg
sleep 40
curl --insecure https://localhost:6767/jenkins/buildByToken/build?job=BuildDeployTrainingApp\&token=dfgufgug348rt4784f934hr9hr3489sdf
C:\Programm\apache-ant-1.10.1\bin\ant.bat -f
 version.xml