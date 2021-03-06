import os
import sys

__author__ = 'ahmetdal'

from setuptools import setup, find_packages

readme_file = os.path.join(os.path.dirname(__file__), 'README.md')
try:
    long_description = open(readme_file).read()
except IOError as err:
    sys.stderr.write("[ERROR] Cannot find file specified as "
                     "``long_description`` (%s)\n" % readme_file)
    sys.exit(1)

setup(
    name='yelp-data-pipeline',
    version='0.1.0',
    author='Ahmet DAL',
    author_email='ceahmetdal@gmail.com',
    packages=find_packages(),
    url='https://github.com/javrasya/yelp-data.git',
    description='',
    long_description=long_description,
    install_requires=[
    ],
    include_package_data=True,
    zip_safe=False,
    license='Apache License 2.0',
    platforms=['any'],
)
