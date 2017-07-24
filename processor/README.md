# YELP Data Processor

This is the processor part of yelp data project


## Developer Guide

### Run Tests
```bash
sbt clean package test
```


### Build Fat Jar
```bash
sbt 'set test in assembly := {}' clean assembly
```