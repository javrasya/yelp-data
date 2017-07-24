import luigi
from generic_luigi.tasks.base.generic_task import GenericTask
from luigi import LocalTarget


class BaseTask(GenericTask):
    identifier = luigi.Parameter(default=None)

    @property
    def file_identifier(self):
        return self.identifier or self.name

    def __init__(self, *args, **kwargs):
        from enki_etl.dags.dag_registry import build_dags
        build_dags()
        super(BaseTask, self).__init__(*args, **kwargs)

    def run(self):
        super(BaseTask, self).run()
        self.output()._touchz()

    def output(self):
        return LocalTarget("/tmp/luigi/%s" % self.file_identifier)
