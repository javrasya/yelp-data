# YELP Data Processing Project

This is the academic yelp data processing project consist of `pipeline` and `processor` parts based on SMACK concept. While processor part is 
developed as `Spark` project runs on `Cassandra`,  pipeline part is developed with `python` `Luigi` and `GenericLuigi` which is an open source project to improve 
luigi functionalities like task re-usability and defining luigi pipeline flow as a json. I authored the `GenericLuigi` project. There is also data serving layer
with `Jupyter` and some prepared `Jupyter` notebooks contains interesting queries.

 This project provide ease of processing YELP academic dataset by minimum code changes required on a schema update or new one comes up.

<div style="text-align:center">
<img src="https://user-images.githubusercontent.com/1279644/28627645-152a9054-722b-11e7-9edc-ad512ed02b36.png" width="200"></div> 


## Running
### Important Note:
**With my local environment, it wasn't enough to process real large yelp academic data. I sampled the data for my unit and integration test cases. if you face some issues like slowness(writing into cassandra running in docker container may be slow and may cause timeout for `LOCAL_QUORUM`) or casandra exits suspiciously without an error (which I also faced and couldn't figure out whats happening), you can use that sampled data to check wheter entire platform is working or not.**



With pipeline execution ; it will extract tar file and then trigger `Spark` job to process the data to convert them into tabular format in `Cassandra`. In Here, **`<path_to_your_yelp_tar_file>` must be replaced  with the location of the yelp tar file on hosting machine as first parameter for the `start-all.sh` script.**. I may suggest to use sampled data for local environment.

```bash
cd /path/to/yelp_data/
./start-all.sh <path_to_your_yelp_tar_file>
#-------------------------------------------------------------------------------------
# # Here is the example for sample data.
#
# ./start-all.sh $PWD/processor/src/main/resources/sample_data.tar
#-------------------------------------------------------------------------------------
```

### Create Cassandra Tables

After make sure `cassandra` container is started properly, execute the command below;

```bash
# This will be executing the create scripts /root/create_scripts.cql given as volume on cassandra container initialization.
docker exec -it cassandra cqlsh cassandra -f /root/create_scripts.cql --request-timeout=3600
```

### Trigger Data Processing

```bash
# This will be executing the luigi wrapper task to trigger extracting the tar file 
# and processing the data from the extracted folder and converting json data into tabular format
# in Cassandra 
docker exec -it -e PYTHONPATH="/root/pipeline"  yelp_data_platform bash -c "cd /root/pipeline / && /root/pipeline/venv/bin/luigi --module app.tasks.daily_flow_task DailyFlowTask --local-scheduler"
```

If you even trigger the flow more than once, tar extractor won't be executed more than once under the favor of the idempotency feature of `Luigi`.

## Executing Example Queries

This is provided by introducing `Jupyter` here. Check [Jupyter Web Interface (http://localhost:8088)](http://localhost:888/) and to execute an example just click an example and then run it. By the way, it alread has the output initially as pre calculated. `Jupyter` will ask a key from you. To obtain that key, you should look at yelp data platform container logs;

```bash
docker logs -f yelp_data_platform
```

![screen shot 2017-07-27 at 03 17 58](https://user-images.githubusercontent.com/1279644/28649013-81ccf6ee-727a-11e7-8fbd-0e51edd57d4b.png)


### Examples
#### Positive Words by Business:

This is the example of finding positive words by ignoring stop words(in English) and ordering them with level of how much they are positive.

![screen shot 2017-07-27 at 02 16 35](https://user-images.githubusercontent.com/1279644/28647817-2bf0959e-7272-11e7-8d76-fe126044e4a4.png)



 ## Building(OPTIONAL)

 ### Building Processor Fat Jar

  **Note:** There is no need to build fat jar. Because it exists in the repository as pre-built.

 This phase requires `sbt` installed ideally, but fat jar is included in this git repository to test easily without `sbt`. Just in case;

```bash
sbt clean assembly
```

(by the way all test cases are supposed to be passed); If some test cases are broken, skip them;

 ```
 sbt 'set test in assembly := {}' clean assembly
 ```


 ### Building Docker Image

 **Note:** There is no need to build docker image. Because it exists in `DockerHub` and all git commits will trigger automated docker build.
 
Because it is based on the `Jupyter-AllSpark` image, it's size is **a bit large** (2 GB on `DockerHub`). This image will contain `Jupyter`,`Apache Toree`,`Java`,`Spark`, `PySpark` and `the platform code`(which consist of `processor` and `pipeline`).

 ```bash
 docker build -t yelp-data-platform
 docker tag yelp-data-platform ahmetdal/yelp-data-platform
 docker push
 ```