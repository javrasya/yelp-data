package com.dal.ahmet.yelpdata.processor.datasource

import com.dal.ahmet.yelpdata.processor.Schema.BaseSchema
import org.apache.spark.sql.{Dataset, SparkSession}

trait DataSource extends Serializable {


  def persist(sparkSession: SparkSession, dataSet: Dataset[_ <: BaseSchema], dataType: String): Unit
}
