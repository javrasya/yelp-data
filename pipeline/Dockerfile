FROM jupyter/all-spark-notebook

MAINTAINER ahmetdal

Label "system"="yelp-data-processor-pipeline"

RUN mkdir -p /root/pipeline/

COPY ./* /root/pipeline/
RUN mkdir -p /root/pipeline/etc/logs

RUN pip install -r /root/pipeline/etc/requirements/requirements.txt

ENV DYNACONF_SETTINGS settings.prod

ENV PYTHONPATH /root/pipeline/