from generic_luigi.service.generic_task_service import GenericTaskService
from .daily_dag import NAMESPACE as daily_dag_ns, CONFIG as daily_dag_cfg

DAGs = {
    daily_dag_ns: daily_dag_cfg
}


def build_dags():
    GenericTaskService.build_dags(DAGs)
