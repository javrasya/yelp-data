from generic_luigi.tasks.base.generic_wrapper_task import GenericWrapperTask
from luigi import Parameter

from enki_etl.base.base_task import BaseTask
from enki_etl.base.targetable_hourly_task import TargetableHourlyTask


class HourlyFlowTask(TargetableHourlyTask, GenericWrapperTask):
    exec_date = Parameter()
    dag_names = ['hourly_dag']
