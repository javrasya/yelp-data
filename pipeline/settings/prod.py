from .base import *

ENKI_HIVE_WORKSPACE = os.path.join(ENKI_SCRIPT_WORKSPACE, 'enki-script-hive/', )
ENKI_PIG_WORKSPACE = os.path.join(ENKI_SCRIPT_WORKSPACE, 'enki-script-pig/', )

ENKI_HIVE_EVENT_WORKSPACE = os.path.join(ENKI_HIVE_WORKSPACE, 'events/insert/', )
ENKI_PIG_EVENT_WORKSPACE = os.path.join(ENKI_PIG_WORKSPACE, 'events/insert/', )

#########################################################################################################################
#                                                                                                                       #
#                                               Hadoop Settings                                                         #
#                                                                                                                       #
#########################################################################################################################
HADOOP_BINARY = '/usr/bin/hadoop'
HDFS_USER = 'hdfs'
HDFS_BASE_PATH = 'hdfs://birdenkimldev'

#########################################################################################################################
#                                                                                                                       #
#                                               PIG Settings                                                            #
#                                                                                                                       #
#########################################################################################################################
PIG_LIBRARY_FOLDER = os.path.join(ENKI_PIG_WORKSPACE, 'piglib')

#########################################################################################################################
#                                                                                                                       #
#                                               Camus Settings                                                          #
#                                                                                                                       #
#########################################################################################################################
CAMUS_KAFKA_BROKERS = 'enki-bigdata-data01:6667,enki-bigdata-data02:6667,enki-bigdata-data03:6667,enki-bigdata-data04:6667'
