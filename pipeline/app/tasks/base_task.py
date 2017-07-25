from generic_luigi.tasks.base.generic_task import GenericTask


class BaseTask(GenericTask):
    def __init__(self, *args, **kwargs):
        from app.dags.dag_registry import build_dags
        build_dags()
        super(BaseTask, self).__init__(*args, **kwargs)
