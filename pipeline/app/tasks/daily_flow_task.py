from datetime import datetime
from luigi import Parameter

from app.tasks.base_task import BaseTask
from generic_luigi.tasks.base.etl_mixins import TargetableETLTask, GenericDailyTask
from generic_luigi.tasks.base.generic_wrapper_task import GenericWrapperTask


class DailyFlowTask(BaseTask, TargetableETLTask, GenericDailyTask, GenericWrapperTask):
    exec_date = Parameter(default=datetime.now().strftime('%Y-%m-%d_%H%M'))
    dag_names = ['daily_dag']
