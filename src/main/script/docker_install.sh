#!/bin/bash
cd $PWD/target
echo $PWD
sudo docker ps
sudo docker rm -f $(sudo docker ps -aq -f "label=com.weshaka.label=ole_svc_1.0.0") 2>/dev/null || echo "No more containers to remove."
sudo docker rmi -f $(sudo docker images -f "label=com.weshaka.label=ole_svc_1.0.0" -q) 2>/dev/null || echo "No more images to remove."
sudo docker build -t shakahub/ole .
sudo docker run -d -p 8080:8080 -p 9089:9089 -e "SPRING_PROFILES_ACTIVE=prod" --link shaka-mongo:mongo $(sudo docker images -f "label=com.weshaka.label=ole_svc_1.0.0" -q)
