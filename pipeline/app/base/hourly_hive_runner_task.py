from generic_luigi.tasks.hive.generic_hql_runner_task import GenericHQLRunnerTask

from enki_etl.base.targetable_hourly_task import TargetableHourlyTask


class HourlyHiveRunnerTask(TargetableHourlyTask, GenericHQLRunnerTask):
    pass
