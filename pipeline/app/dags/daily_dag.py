from dynaconf import settings

default_args = {'elements': ['exec_date']}

NAMESPACE = 'daily_dag'
CONFIG = {
    # Default Configuration
    'tasks': [
        {
            # Parsing JSON data and converting them into tabular format in Cassandra
            'name': 'Processor',
            'args_from_parent': default_args,
            'cls': 'app.tasks.daily_spark_runner_task.DailySparkRunnerTask',
            'app': settings.SPARK_YELP_DATA_PROCESSOR_FAT_JAR_PATH,
            'entry_class': settings.SPARK_YELP_DATA_PROCESSOR_CLASS,
            'extra_conf': {
                'spark.yelp.data.folder': settings.YELP_EXTRACTED_FOLDER_PATH,
            },
            'requires':
                {
                    'tasks': [
                        {
                            # Extracting tar file contains yelp json data
                            'name': 'Extractor',
                            # 'args_from_parent': default_args,
                            'cls': 'generic_luigi.tasks.extractor.tar_extractor_task.TarExtractorTask',
                            'tar_file': settings.YELP_TAR_FILE_PATH,
                            'create_sub_folder': True,
                            'requires': {
                            }
                        }
                    ]
                }

        },
    ]
}
