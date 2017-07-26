#YELP Data Pipeline

This is the pipeline project for yelp data processing project. It includes `Luigi` and `GenericLuigi` which is an open source project to improve luigi functionalities like
 task re-usability and defining luigi pipeline flow as a json. I authored the `GenericLuigi` project.

## Building Dev Environment:

```bash
cd /path/to/yelp_data/pipeline
pip install -r etc/requirements/dev.txt
```
## Run Tests:

```bash
pip install tox
tox
```

## Run:

```bash
luigi --module app.tasks.daily_flow_task DailyFlowTask --local-scheduler
```