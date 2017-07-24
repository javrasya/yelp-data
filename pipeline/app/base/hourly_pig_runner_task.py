from generic_luigi.tasks.pig.generic_pig_runner_task import GenericPigRunnerTask

from enki_etl.base.targetable_hourly_task import TargetableHourlyTask


class HourlyPigRunnerTask(TargetableHourlyTask, GenericPigRunnerTask):
    pass
