#YELP Data Pipeline

This is the pipeline project for yelp data processing project. It includes `Luigi` and `GenericLuigi` which is an open source project to improve luigi functionalities like
 task re-usability and defining luigi pipeline flow as a json. I authored the `GenericLuigi` project.

## Building with Docker:

```bash
cd /path/to/yelp_data_processor/pipeline

pip install -r etc/requirements/dev.txt
```


## Run Tests:

```bash
pip install tox
tox
```

## Run:

```bash
luigi --module enki_etl.base.hourly_flow_task HourlyFlowTask --exec-date 2099-01-01_2300 --local-scheduler
```


### Docker
```bash
docker exec -it enki-etl luigi --module enki_etl.base.hourly_flow_task HourlyFlowTask --exec-date 2099-01-01_2300

#or without luigi daemon

docker exec -it enki-etl luigi --module enki_etl.base.hourly_flow_task HourlyFlowTask --exec-date 2099-01-01_2300 --local-scheduler
```