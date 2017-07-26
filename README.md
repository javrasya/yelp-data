# YELP Data Processing Project

This is the academic yelp data processing project consist of `pipeline` and `processor` parts based on SMACK concept. While processor part is 
developed as `Spark` project runs on `Cassandra`,  pipeline part is developed with `python` `Luigi` and `GenericLuigi` which is an open source project to improve 
luigi functionalities like task re-usability and defining luigi pipeline flow as a json. I authored the `GenericLuigi` project. There is also data serving layer
with `Jupyter` and some prepared `Jupyter` notebooks contains interesting queries.

 This project provide ease of processing YELP academic dataset by minimum code changes required on a schema update or new one comes up.

<div style="text-align:center">
<img src="https://user-images.githubusercontent.com/1279644/28627645-152a9054-722b-11e7-9edc-ad512ed02b36.png" width="200"></div> 


 ## Building

 ### Building Processor Fat Jar (Optional)

 This phase requires `sbt` installed ideally, but fat jar is included in this git repository to test easily without `sbt`. Just in case;

```bash
sbt clean assembly
```

(by the way all test cases are supposed to be passed); If some test cases are broken, skip them;

 ```
 sbt 'set test in assembly := {}' clean assembly
 ```


 ### Building Docker Image (OPTIONAL)

 **Note:** There is no need to build docker image. Because it exists in `DockerHub`.
 
Because it is based on the `Jupyter-AllSpark` image, it's size is **a bit large** (2 GB on `DockerHub`). This image will contain `Jupyter`,`Apache Toree`,`Java`,`Spark`, `PySpark` and `the platform code`(which consist of `processor` and `pipeline`).

 ```bash
 docker build -t yelp_data_platform:latest
 docker tag yelp_data_platform ahmetdal/yelp_data_platform:latest
 ```



 ## Running

 Tar file will be extracted and the json files will be converted into tabular form in Cassandra. **`<path_to_your_yelp_tar_file>` must be filled with the location of
 the yelp tar file on hosting machine.**

```bash
cd /path/to/yelp_data/
# Create network to provide communication between cassandra and spark containers
docker network create yelp_data_platform

# Run Cassandra container.
docker run --name cassandra -p 7000:7000 --volume ${PWD}/processor/src/main/resources/schema/create_scripts.cql:/root/create_scripts.cql --network yelp_data_platform cassandra:3.0

# Run Yelp Data Platform Container. Make sure you change <path_to_your_yelp_tar_file> part 
# with the location of the tar file in hosting machine.
docker run --name yelp_data_platform -p 8888:8888 --volume <path_to_your_yelp_tar_file>:/usr/lib/yelp_data/yelp_dataset.tar --network yelp_data_platform ahmetdal/yelp_data_platform
```

### Create Cassandra Tables

```bash
# This will be executing the create scripts under /root/create_scripts.cql given as volumne above
docker exec -it cassandra cqlsh cassandra -f /root/create_scripts.cql --request-timeout=3600
```

### Trigger Data Processing

```bash
# This will be executing the luigi wrapper task to trigger extracting the tar file 
# and processing the data from the extracted folder and converting json data into tabular format
# in Cassandra 
docker exec -it -e PYTHONPATH="/root/pipeline"  yelp_data_platform /root/pipeline/venv/bin/luigi --module app.tasks.daily_flow_task DailyFlowTask --local-scheduler
```



## Executing Example Queries

This is provided by introducing `Jupyter` here


