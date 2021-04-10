#!/bin/bash

# check if docker-compose exists
if [ -f ./docker-compose.yml ]
then
    echo "Found existing docker-compose.yml file in this directory."
    read -p "Do you want to overwrite it? [y/n]" OVRWRT
    echo
    if [[ $OVRWRT =~ ^[Yy]$ ]]
    then
        # get docker-compose
        wget -O docker-compose.yml https://raw.githubusercontent.com/p-Heinze/Qanary-question-answering-components/python-component-deployment/qanary_docker-compose-writer/docker-compose.yml
    fi
else
wget -O docker-compose.yml https://raw.githubusercontent.com/p-Heinze/Qanary-question-answering-components/python-component-deployment/qanary_docker-compose-writer/docker-compose.yml
fi

printf "WARNING: You are about to start ALL available Qanary components without resource constraints!\n"
read -p "Do you want to start ALL services now? [y/n]" REPLY
echo
if [[ $REPLY =~ ^[Yy]$ ]]
then
    docker-compose up 
else
    echo "Omitting automatic start"
fi

