from os.path import expanduser

import os

PROJECT_DIR = os.path.dirname(os.path.dirname(__file__))
PROJECT_PARENT_DIR = os.path.dirname(PROJECT_DIR)
USER_HOME = expanduser("~")

YELP_EXTRACTED_FOLDER_PATH = "/usr/lib/yelp_data/yelp_dataset_challenge"
YELP_TAR_FILE_PATH = YELP_EXTRACTED_FOLDER_PATH + ".tar"

SPARK_YELP_DATA_PROCESSOR_FAT_JAR_PATH = "/usr/lib/yelp_data/yelp-data-processor-assembly-0.0.1-SNAPSHOT.jar"
SPARK_YELP_DATA_PROCESSOR_CLASS = "com.dal.ahmet.yelpdata.processor.DataProcessor"
