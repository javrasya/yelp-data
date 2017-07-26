package com.dal.ahmet.yelpdata.processor.it

import com.dal.ahmet.yelpdata.processor.Schema
import com.dal.ahmet.yelpdata.processor.datasource.CassandraDataSource
import com.dal.yelpdata.processor.utils.SparkCassandraITSpecBase
import com.datastax.spark.connector.cql.CassandraConnector
import com.datastax.spark.connector.embedded.YamlTransformations
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.cassandra._
import org.scalatest.FreeSpec

case class TestSchema(id: String, column1: String, column2: Array[String], override val `type`: String = "test_schema") extends Schema.BaseSchema {
  override def equals(obj: scala.Any): Boolean = {
    this.deepEquals[TestSchema](obj.asInstanceOf[TestSchema])
  }
}

class CassandraDataSourceITTest extends FreeSpec with SparkCassandraITSpecBase {

  "Integrated Cassandra Data Source Test" - {
    "Persist method of cassandra data source should be well working with test schema." in {

      useCassandraConfig(Seq(YamlTransformations.Default))

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


