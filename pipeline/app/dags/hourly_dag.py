import os
from dynaconf import settings
from .base_dag import *

NAMESPACE = 'hourly_dag'

CONFIG = {
    # Default Configuration
    'tasks': list(
        map(
            lambda event: {
                # Moving TMP tables to FACT tables
                'name': '%sTmpToFactHiveTask' % event.title(),
                'args_from_parent': default_args,
                'cls': 'enki_etl.base.hourly_hive_runner_task.HourlyHiveRunnerTask',
                'file': os.path.join(settings.ENKI_HIVE_EVENT_WORKSPACE, "tmp_to_fact_%s.hql" % event, ),
                'hdfs_user': settings.HDFS_USER,
                'task_params': {
                    'etl_date': '{{this.relevant_date_as_str}}',
                    'hive_db': settings.ENKI_HIVE_DB,
                    'macros_path': os.path.join(settings.ENKI_HIVE_WORKSPACE, 'macros.hql')
                },
                'requires':
                    {
                        'tasks': [
                            {
                                # Moving raw json event data from HDFS to TMP hive tables.
                                'name': '%sHdfsToHiveTmpTask' % event.title(),
                                'args_from_parent': default_args,
                                'cls': 'enki_etl.base.hourly_pig_runner_task.HourlyPigRunnerTask',
                                'file': os.path.join(settings.ENKI_PIG_EVENT_WORKSPACE, "to_hive_%s.pig" % event, ),
                                'hdfs_user': settings.HDFS_USER,
                                'task_params': {
                                    'pig_library_folder': settings.PIG_LIBRARY_FOLDER,
                                    'etl_date': '{{this.relevant_date_as_str}}',
                                    'hive_db': settings.ENKI_HIVE_DB,
                                    'input': os.path.join(
                                        settings.HDFS_BASE_PATH,
                                        settings.ENKI_HDFS_FIRST_EVENT_HOME,
                                        event,
                                        'hourly',
                                        "{{this.year}}", "{{this.month}}", "{{this.day}}", "{{this.hour}}"
                                    )
                                },
                                'requires': {
                                    'tasks': [
                                        {
                                            # Moving raw json event data from KAFKA to HDFS.
                                            'name': 'KafkaToHdfsTask',
                                            'args_from_parent': default_args,
                                            'cls': 'enki_etl.base.hourly_shell_task.HourlyShellTask',
                                            'env': {'HADOOP_USER_NAME': settings.HDFS_USER, 'HDFS_USER': settings.HDFS_USER},
                                            'cmd': [
                                                (
                                                    "%(hadoop_bin)s jar %(jar_file)s com.hopi.HopiCamusJob "
                                                    "-P %(camus_properties_file)s "
                                                    "-Dcamus.dump.hdfs.path={{this.relevant_date_as_str}} "
                                                    "-Dcamus.dump.hdfs.path.format=%(path_date_format)s "
                                                    "-Dlog4j.configuration=%(log4j_file)s "
                                                    "-Dkafka.whitelist.topics=%(kafka_topics)s "
                                                    "-Dkafka.brokers=%(kafka_brokers)s"
                                                ) % {
                                                    "hadoop_bin": settings.HADOOP_BINARY,
                                                    "jar_file": settings.CAMUS_PATH,
                                                    "camus_properties_file": settings.CAMUS_PROPERTIES_FILE,
                                                    "path_date_format": settings.CAMUS_DUMP_HDFS_PATH_FORMAT,
                                                    "log4j_file": settings.CAMUS_LOG4J_PATH,
                                                    "kafka_topics": settings.CAMUS_KAFKA_TOPICS,
                                                    "kafka_brokers": settings.CAMUS_KAFKA_BROKERS,

                                                }
                                            ],
                                        }

                                    ]
                                }
                            }
                        ]
                    }

            },
            settings.ENKI_EVENTS

        )
    )
}
