package com.dal.ahmet.yelpdata.processor

import com.dal.ahmet.yelpdata.processor.datasource.CassandraDataSource
import com.dal.ahmet.yelpdata.processor.utils.SparkCassandraITSpecBase
import com.datastax.spark.connector.cql.CassandraConnector
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.cassandra._
import com.datastax.spark.connector._
import org.scalatest.FreeSpec

case class TestSchema(id: String, column1: String, column2: Array[String], override val `type`: String = "test_schema") extends Schema.BaseSchema {
  override def equals(obj: scala.Any): Boolean = {
    this.id == obj.asInstanceOf[TestSchema].id &&
      this.column1 == obj.asInstanceOf[TestSchema].column1 &&
      this.column2.deep == obj.asInstanceOf[TestSchema].column2.deep &&
      this.`type` == obj.asInstanceOf[TestSchema].`type`
  }
}

class CassandraDataSourceITTest extends FreeSpec with SparkCassandraITSpecBase {


  "Cassandra Data Source" - {
    "Persist method of cassandra data source should be well working with test schema." in {

      val sparkSession = useSparkConf(defaultConf)
      import sparkSession.implicits._


      val conn = CassandraConnector(sparkSession.sparkContext.getConf)


      conn.withSessionDo { session =>
        createKeyspace(session)
      }

      conn.withClusterDo { cluster =>
        val session = cluster.connect(getKsName)
        session.execute("DROP TABLE IF EXISTS test_schema")
        session.execute(
          """
            |create table test_schema(
            | id text
            | ,column1 text
            | ,column2 frozen<list<text>>
            | ,type text
            | ,PRIMARY KEY(id)
            |)
          """.stripMargin)

        val dataSource = new CassandraDataSource(getKsName)

        val expected = Array(
          TestSchema("1", "text1", Array("el1", "el3", "el2")),
          TestSchema("2", "text2", Array("el1")),
          TestSchema("3", "text3", Array("el1", "el3"))
        )
        val data: RDD[TestSchema] = sparkSession.sparkContext.parallelize(expected)

        dataSource.persist(sparkSession, sparkSession.createDataFrame(data).as[TestSchema], "test_schema")

        val actual = sparkSession
          .read
          .cassandraFormat(keyspace = getKsName, table = "test_schema")
          .load()
          .as[TestSchema]
          .sort($"id")
          .collect()

        actual should be(expected)
      }


    }

  }

}


