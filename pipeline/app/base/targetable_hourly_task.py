from generic_luigi.tasks.base.generic_hourly_task import GenericHourlyTask

from enki_etl.base.base_task import BaseTask


class TargetableHourlyTask(BaseTask, GenericHourlyTask):
    @property
    def file_identifier(self):
        return "%s_%s" % (super(TargetableHourlyTask, self).file_identifier, self.relevant_date_as_str)
