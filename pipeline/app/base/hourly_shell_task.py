from generic_luigi.tasks.base.generic_hourly_task import GenericHourlyTask
from generic_luigi.tasks.shell.generic_shell_task import GenericShellTask
from enki_etl.base.base_task import BaseTask
from enki_etl.base.targetable_hourly_task import TargetableHourlyTask


class HourlyShellTask(TargetableHourlyTask, GenericShellTask):
    pass
