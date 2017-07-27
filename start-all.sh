#!/bin/bash

cd $(dirname $0)

TAR_FILE="$1"
DEFAULT_TAR_FILE="${PWD}/processor/src/main/resources/sample_data.tar"
if [ -z $tarFile ]; then
    echo "WARN: Tar file is not give. Sample data which is $DEFAULT_TAR_FILE will be used."
    TAR_FILE=$DEFAULT_TAR_FILE
fi


echo "Exising Cassandra and Yelp Data Platform containers will be removed. Refusing it will cancel deploying the platform."
read  -n 1 -p "Wanna proceed?(y/n):" mainmenuinput

if [ "$mainmenuinput" = "y" ]; then
    echo ""
    echo "YELP data platform will be using '$TAR_FILE' tar file."
    source stop-all.sh
    echo "* Deployment is started"
    r="$(docker network create yelp_data_platform)"

    # Run Cassandra container.
    r="$(docker run -d --name cassandra -p 7000:7000 -e CASSANDRA_LISTEN_ADDRESS='cassandra' --volume ${PWD}/processor/src/main/resources/schema/create_scripts.cql:/root/create_scripts.cql --network yelp_data_platform cassandra)"
    
    echo "Waiting for Cassandra to start..."
    expectedConent="Created default superuser role"
    tailContent="$(docker logs --tail 50 cassandra)"
    while [[ $tailContent != *"$expectedConent"* ]] 
    do
        tailContent="$(docker logs --tail 50 cassandra)"
        sleep 1        
    done
    echo "* Cassandra container is started"

    r="$(docker exec -it cassandra cqlsh cassandra -f /root/create_scripts.cql --request-timeout=3600)"
    echo "* Tables are created in Cassandra."
    
    
    r="$(docker run -d --name yelp_data_platform -p 8888:8888 --volume $TAR_FILE:/usr/lib/yelp_data/yelp_dataset.tar --network yelp_data_platform ahmetdal/yelp-data-platform)"
    echo "Waiting for Yelp Data Platform to start..."
    echo "* Yelp Data Platform container is started"

    echo "Deployment is finalized."

    read  -n 1 -p "Wanna trigger data processing?(y/n):" mainmenuinput2
    if [ "$mainmenuinput2" = "y" ]; then
        source start-data-processing.sh
    fi
else
    echo ""
    echo "! Deployment is cancelled."
    exit 0
fi
