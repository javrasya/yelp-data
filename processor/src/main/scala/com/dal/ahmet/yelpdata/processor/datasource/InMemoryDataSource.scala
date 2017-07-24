package com.dal.ahmet.yelpdata.processor.datasource

import com.dal.ahmet.yelpdata.processor.Schema.BaseSchema
import org.apache.spark.sql.{Dataset, SparkSession}

import scala.collection.mutable


class InMemoryDataSource() extends DataSource {

  val data: mutable.Map[String, Dataset[_ <: BaseSchema]] = mutable.Map()


  def persist(sparkSession: SparkSession, dataSet: Dataset[_ <: BaseSchema], dataType: String): Unit = {
    data.put(dataType, dataSet)

  }
}
