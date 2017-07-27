#!/bin/bash

cd $(dirname $0)

[  "$(docker ps -a | grep cassandra)" ] && docker rm -f cassandra; echo "Existing Casandra container is removed."
[  "$(docker ps -a | grep yelp_data_platform)" ] && docker rm -f yelp_data_platform; echo "Existing Yelp Data Platform container is removed."
[  "$(docker network ls | grep yelp_data_platform)" ] && docker network rm yelp_data_platform; echo "Existing yelp_data_platform network is removed."
