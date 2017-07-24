package com.dal.ahmet.yelpdata.processor

import java.io.File

import com.dal.ahmet.yelpdata.processor.datasource.{CassandraDataSource, DataSource}
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object DataProcessor {


  def getSparkSession: SparkSession = {
    SparkSession.builder().config(new SparkConf()).getOrCreate()
  }

  def getDataSource: DataSource = {
    new CassandraDataSource("yelp")
  }


  def main(args: Array[String]): Unit = {
    val sparkSession = getSparkSession
    val dataSource = getDataSource
    val processor = new DataProcessor(dataSource)
    processor.process(sparkSession)

  }

}


class DataProcessor(dataSource: DataSource) extends Serializable {


  def process(sparkSession: SparkSession): Unit = {

    val dataFolder = sparkSession.conf.getOption("spark.yelp.data.folder")

    assert(dataFolder.nonEmpty, message = "'spark.yelp.data.folder' conf should be set. This is the path the processor will read the json files from.")

    Schema.schemas.foreach {
      schemaType =>
        val df = sparkSession.read.json(new File(dataFolder.get, s"*_$schemaType.json").getPath)
        if (df.cache().count > 0) {
          dataSource.persist(sparkSession, Schema.encodeDataSet(sparkSession, schemaType, df), schemaType)
        }
    }
  }
}