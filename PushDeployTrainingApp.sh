cd /d/RP/Tests/SpringBoot_Training
git add .
git commit -m "$*"
git push httpsorigin master
cd /d/RP/Tests/ReactToDoExp2
npm run build
git add .
git commit -m "$*"
git push httpsorigin master
sleep 60
curl --insecure https://localhost:6767/jenkins/buildByToken/build?job=BuildDeployTrainingApp&token=dfgufgug348rt4784f934hr9hr3489sdf
