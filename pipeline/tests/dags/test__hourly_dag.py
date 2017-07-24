from unittest import TestCase

import os
from dynaconf import settings

from enki_etl.base.hourly_flow_task import HourlyFlowTask
from enki_etl.base.hourly_hive_runner_task import HourlyHiveRunnerTask
from enki_etl.base.hourly_pig_runner_task import HourlyPigRunnerTask


class TestHourlyFlowTask(TestCase):
    def setUp(self):
        self.task = HourlyFlowTask(exec_date='2099-01-01_2359')

    def test_requires(self):
        self.assertEqual('HourlyFlowTask', self.task.name)
        requires = self.task.requires()

        self.assertEqual(len(settings.ENKI_EVENTS), len(requires))

        for i, task in enumerate(requires):
            event = settings.ENKI_EVENTS[i]
            self.assertEqual("%sTmpToFactHiveTask" % event.title(), task.name)
            self.assertIsInstance(task, HourlyHiveRunnerTask)
            self.assertEqual(task.file, os.path.join(settings.ENKI_HIVE_EVENT_WORKSPACE, "tmp_to_fact_%s.hql" % event))
            self.assertEqual(task.hdfs_user, settings.HDFS_USER)
            self.assertDictEqual(task.task_params, {'hive_db': settings.ENKI_HIVE_DB, 'etl_date': "2099010123",
                                                    'macros_path': unicode(os.path.join(settings.ENKI_HIVE_WORKSPACE, 'macros.hql'))})

            self.assertEqual("%s_%s" % (task.name, "2099010123"), task.file_identifier)

            requires2 = task.requires()
            for i, task2 in enumerate(requires2):
                self.assertEqual("%sHdfsToHiveTmpTask" % event.title(), task2.name)
                self.assertIsInstance(task2, HourlyPigRunnerTask)
                self.assertEqual(task2.file, os.path.join(settings.ENKI_PIG_EVENT_WORKSPACE, "to_hive_%s.pig" % event))
                self.assertDictEqual(task2.task_params, {
                    'pig_library_folder': settings.PIG_LIBRARY_FOLDER,
                    'etl_date': "2099010123",
                    'hive_db': settings.ENKI_HIVE_DB,
                    'input': os.path.join(
                        settings.HDFS_BASE_PATH,
                        settings.ENKI_HDFS_FIRST_EVENT_HOME,
                        event,
                        'hourly',
                        "2099", "01", "01", "23"
                    )
                })

                self.assertEqual("%s_%s" % (task2.name, "2099010123"), task2.file_identifier)

                (
                    kafka_to_hdfs_task,
                ) = tuple(task2.requires())

                self.assertEqual('KafkaToHdfsTask', kafka_to_hdfs_task.name)
                self.assertDictEqual({'HADOOP_USER_NAME': settings.HDFS_USER, 'HDFS_USER': settings.HDFS_USER}, kafka_to_hdfs_task.env)
                self.assertListEqual([
                    (
                        "%(hadoop_bin)s jar %(jar_file)s com.hopi.HopiCamusJob "
                        "-P %(camus_properties_file)s "
                        "-Dcamus.dump.hdfs.path=2099010123 "
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
                ], kafka_to_hdfs_task.cmd)

                self.assertEqual("%s_%s" % (kafka_to_hdfs_task.name, "2099010123"), kafka_to_hdfs_task.file_identifier)
