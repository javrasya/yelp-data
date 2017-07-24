from os.path import expanduser

import os

PROJECT_DIR = os.path.dirname(os.path.dirname(__file__))
PROJECT_PARENT_DIR = os.path.dirname(PROJECT_DIR)
USER_HOME = expanduser("~")
#########################################################################################################################
#                                                                                                                       #
#                                               Enki Settings                                                           #
#                                                                                                                       #
#########################################################################################################################
ENKI_EVENTS = [
    'transaction',
    'view',
    'addToBasket',
    'removeFromBasket',
]

ENKI_HDFS_FIRST_EVENT_HOME = "/user/camus/topics"

ENKI_HIVE_DB = 'enki'

ENKI_SCRIPT_WORKSPACE = os.path.join(PROJECT_PARENT_DIR, 'enki-script')
ENKI_HIVE_WORKSPACE = os.path.join(ENKI_SCRIPT_WORKSPACE, 'enki-script-hive/src/main/resources/', )
ENKI_PIG_WORKSPACE = os.path.join(ENKI_SCRIPT_WORKSPACE, 'enki-script-pig/src/main/resources/', )

ENKI_HIVE_EVENT_WORKSPACE = os.path.join(ENKI_HIVE_WORKSPACE, 'events/insert/', )
ENKI_PIG_EVENT_WORKSPACE = os.path.join(ENKI_PIG_WORKSPACE, 'events/insert/', )

#########################################################################################################################
#                                                                                                                       #
#                                               Hadoop Settings                                                         #
#                                                                                                                       #
#########################################################################################################################
HADOOP_BINARY = '/usr/local/hadoop/bin/hadoop'
HDFS_USER = 'hdfs'
HDFS_BASE_PATH = 'hdfs://birdenkimldev'

#########################################################################################################################
#                                                                                                                       #
#                                               PIG Settings                                                            #
#                                                                                                                       #
#########################################################################################################################
PIG_BINARY = '/usr/bin/pig'
PIG_LIBRARY_FOLDER = os.path.join(ENKI_PIG_WORKSPACE, '/piglib/')

#########################################################################################################################
#                                                                                                                       #
#                                               HIVE Settings                                                           #
#                                                                                                                       #
#########################################################################################################################
HIVE_BINARY = '/usr/bin/hive'

#########################################################################################################################
#                                                                                                                       #
#                                               Camus Settings                                                          #
#                                                                                                                       #
#########################################################################################################################

CAMUS_PATH = os.path.join(PROJECT_DIR, 'etc', 'camus', 'HopiCamus-1.0-SNAPSHOT-jar-with-dependencies.jar')
CAMUS_PROPERTIES_FILE = os.path.join(PROJECT_DIR, 'etc', 'camus', 'camus.properties')
CAMUS_DUMP_HDFS_PATH_FORMAT = 'yyyyMMddHH'
CAMUS_LOG4J_PATH = os.path.join(PROJECT_DIR, 'etc', 'camus', 'log4j.xml')
CAMUS_KAFKA_TOPICS = ','.join(
    ENKI_EVENTS +
    [
        'user',
        'product',
        'problematicEvents',
        'problematicEntities'
    ])
CAMUS_KAFKA_BROKERS = 'enki-ml-kafka:9092'
