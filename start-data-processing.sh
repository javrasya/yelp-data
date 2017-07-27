#!/bin/bash

cd $(dirname $0)

if [ ! "$(docker ps -a | grep yelp_data_platform)" ]
then
    echo "Start platform first!"
    read  -n 1 -p "Wanna start the platform?(y/n):" mainmenuinput
    if [ "$mainmenuinput" = "y" ]; then
        source start-all.sh
    else
        exit 1
    fi
    
fi

docker exec -it -e PYTHONPATH="/root/pipeline"  yelp_data_platform bash -c "cd /root/pipeline / && /root/pipeline/venv/bin/luigi --module app.tasks.daily_flow_task DailyFlowTask --local-scheduler"