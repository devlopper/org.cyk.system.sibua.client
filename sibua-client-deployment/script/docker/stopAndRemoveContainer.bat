docker rm $(docker stop $(docker ps -a -q --filter ancestor=sibua-client))