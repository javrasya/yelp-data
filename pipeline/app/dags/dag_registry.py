
from generic_luigi.service.generic_task_service import GenericTaskService
from .hourly_dag import NAMESPACE as hourly_dag_ns, CONFIG as hourly_dag_cfg


DAGs = {
    hourly_dag_ns: hourly_dag_cfg
}


def build_dags():
    GenericTaskService.build_dags(DAGs)
