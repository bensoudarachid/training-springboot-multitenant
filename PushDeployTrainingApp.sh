cd /d/RP/Tests/SpringBoot_Training
git add .
git commit -m "$*"
git push httpsorigin master
cd /d/RP/Tests/ReactToDoExp2
npm run build
git add .
git commit -m "$*"
git push httpsorigin master