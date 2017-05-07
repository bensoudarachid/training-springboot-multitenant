cd /d/RP/Tests/SpringBoot_Training
git add .
git commit -m "$*"
git push httpsorigin master
curl --insecure https://localhost:6767/jenkins/buildByToken/build?job=GetRestTrainingAppFromGitlab\&token=d8g347rg375trft3dr34r8zr84t845gc58g5t
sleep 30
curl --insecure https://localhost:6767/jenkins/job/buildDeployTrainingServerNoRestart/build?token=fjgf45f8g458f5gt8tgfui29d234tg45gh