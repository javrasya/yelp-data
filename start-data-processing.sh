#!/bin/bash

cd $(dirname $0)

if [ ! "$(docker ps -a | grep yelp_data_platform)" ]
then
    echo "ERROR: Start YELP Data Platform first!"
    echo "Hint: ./start-all.sh"
    exit 1
fi

docker exec -it -e PYTHONPATH="/root/pipeline"  yelp_data_platform bash -c "cd /root/pipeline / && /root/pipeline/venv/bin/luigi --module app.tasks.daily_flow_task DailyFlowTask --local-scheduler"