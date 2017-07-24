package com.dal.ahmet.yelpdata.processor.datasource

import com.dal.ahmet.yelpdata.processor.Schema.BaseSchema
import org.apache.spark.sql.cassandra._
import org.apache.spark.sql.{Dataset, SparkSession}


class CassandraDataSource(keyspace: String, cluster: Option[String] = Option.empty) extends DataSource {


  def persist(sparkSession: SparkSession, dataSet: Dataset[_ <: BaseSchema], dataType: String): Unit = {
    dataSet
      .write
      .cassandraFormat(table = dataType, keyspace = keyspace, cluster = cluster.getOrElse(CassandraSourceRelation.defaultClusterName))
      .save()

  }
}
