from unittest import TestCase

from dynaconf import settings
from generic_luigi.tasks.extractor.tar_extractor_task import TarExtractorTask

from app.tasks.daily_flow_task import DailyFlowTask
from app.tasks.daily_spark_runner_task import DailySparkRunnerTask


class TestHourlyFlowTask(TestCase):
    def setUp(self):
        self.task = DailyFlowTask()

    def test_requires(self):
        self.assertEqual('DailyFlowTask', self.task.name)
        requires = self.task.requires()

        self.assertEqual(1, len(requires))

        (
            processor_task,
        ) = requires

        self.assertEqual("Processor", processor_task.name)
        self.assertIsInstance(processor_task, DailySparkRunnerTask)
        self.assertEqual(settings.SPARK_YELP_DATA_PROCESSOR_FAT_JAR_PATH, processor_task.app)
        self.assertEqual(settings.SPARK_YELP_DATA_PROCESSOR_CLASS, processor_task.entry_class)
        self.assertDictEqual({'spark.yelp.data.folder': settings.YELP_EXTRACTED_FOLDER_PATH}, processor_task.extra_conf)

        requires = processor_task.requires()
        self.assertEqual(1, len(requires))
        (
            extractor_task,
        ) = requires

        self.assertEqual("Extractor", extractor_task.name)
        self.assertIsInstance(extractor_task, TarExtractorTask)
        self.assertEqual(settings.YELP_TAR_FILE_PATH, extractor_task.tar_file)
