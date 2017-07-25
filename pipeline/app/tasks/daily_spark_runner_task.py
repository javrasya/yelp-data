from generic_luigi.tasks.base.etl_mixins import TargetableETLTask, GenericDailyTask
from generic_luigi.tasks.spark.generic_spark_task import GenericSparkTask


class DailySparkRunnerTask(TargetableETLTask, GenericDailyTask, GenericSparkTask):
    pass
