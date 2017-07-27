#!/bin/bash

cd $(dirname $0)

if [ -z "$1" ]; then
    echo "ERROR: Tar file must be fiven as a paramter..."
    exit 1
fi

echo "Exising Cassandra and Yelp Data Platform containers will be removed. Refusing it will cancel deploying the platform."
read  -n 1 -p "Wanna proceed?(y/n):" mainmenuinput

if [ "$mainmenuinput" = "y" ]; then
    echo ""
    echo "YELP data platform will be using '$1' tar file."
    [  "$(docker ps -a | grep cassandra)" ] && docker rm -f cassandra; echo "Existing Casandra container is removed."
    [  "$(docker ps -a | grep yelp_data_platform)" ] && docker rm -f yelp_data_platform; echo "Existing Yelp Data Platform container is removed."
    [  "$(docker network ls | grep yelp_data_platform)" ] && docker network rm yelp_data_platform; echo "Existing yelp_data_platform network is removed."
    echo "Deployment is started"
    docker network create yelp_data_platform
    echo "yelp_data_platform network is created"

    # Run Cassandra container.
    docker run -d --name cassandra -p 7000:7000 -e CASSANDRA_LISTEN_ADDRESS='cassandra' --volume ${PWD}/processor/src/main/resources/schema/create_scripts.cql:/root/create_scripts.cql --network yelp_data_platform cassandra
    echo "Cassandra container is started"
    
    # Run Yelp Data Platform Container. Make sure you set YELP_DATA_PLATFORM_TAR_FILE environment variable 
    # with the location of the tar file in hosting machine.
    docker run -d --name yelp_data_platform -p 8888:8888 --volume $YELP_DATA_PLATFORM_TAR_FILE:/usr/lib/yelp_data/yelp_dataset.tar --network yelp_data_platform ahmetdal/yelp-data-platform
    echo "Yelp Data Platform container is started"
else
    echo ""
    echo "Deployment is cancelled."
    exit 0
fi
