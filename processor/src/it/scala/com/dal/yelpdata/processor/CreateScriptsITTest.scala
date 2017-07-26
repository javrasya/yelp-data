package com.dal.ahmet.yelpdata.processor.it

import java.io.File

import com.dal.ahmet.yelpdata.processor.DataProcessor
import com.dal.ahmet.yelpdata.processor.Schema._
import com.dal.ahmet.yelpdata.processor.datasource.CassandraDataSource
import com.dal.yelpdata.processor.utils.SparkCassandraITSpecBase
import com.datastax.spark.connector.cql.CassandraConnector
import com.google.common.base.Charsets
import com.google.common.io.Files
import org.apache.spark.sql.cassandra._
import org.scalatest.FreeSpec


class CreateScriptsITTest extends FreeSpec with SparkCassandraITSpecBase {

  override def getKsName = {
    "yelp"
  }

  "Integrated Create Scripts Test" - {
    "Schemas defined in create_scripts.cql must be fit in th e schema of the data" in {

      val dataFolderPath = getClass.getClassLoader.getResource("data").getPath
      val sparkSession = useSparkConf(defaultConf.set("spark.yelp.data.folder", s"file://$dataFolderPath"))
      import sparkSession.implicits._


      val conn = CassandraConnector(sparkSession.sparkContext.getConf)


      conn.withSessionDo { session =>
        Files.toString(new File(this.getClass.getClassLoader.getResource("schema/create_scripts.cql").toURI), Charsets.UTF_8)
          .split(";")
          .map(line => line.trim)
          .foreach {
            line =>
              session.execute(line)
          }
      }


      val expectedBusiness = sparkSession.read.json(this.getClass.getClassLoader.getResource("data/yelp_academic_dataset_business.json").getPath).as[Business]
      val expectedUser = sparkSession.read.json(this.getClass.getClassLoader.getResource("data/yelp_academic_dataset_user.json").getPath).as[User]
      val expectedReview = sparkSession.read.json(this.getClass.getClassLoader.getResource("data/yelp_academic_dataset_review.json").getPath).as[Review]
      val expectedTip = sparkSession.read.json(this.getClass.getClassLoader.getResource("data/yelp_academic_dataset_tip.json").getPath).as[Tip]
      val expectedCheckIn = sparkSession.read.json(this.getClass.getClassLoader.getResource("data/yelp_academic_dataset_checkin.json").getPath).as[CheckIn]


      val dataSource = new CassandraDataSource(getKsName)
      val dataProcessor = new DataProcessor(dataSource)
      dataProcessor.process(sparkSession)

      val actualBusiness = sparkSession
        .read
        .cassandraFormat(keyspace = getKsName, table = "business")
        .load()
        .as[Business]

      val actualReview = sparkSession
        .read
        .cassandraFormat(keyspace = getKsName, table = "review")
        .load()
        .as[Review]

      val actualUser = sparkSession
        .read
        .cassandraFormat(keyspace = getKsName, table = "user")
        .load()
        .as[User]

      val actualCheckIn = sparkSession
        .read
        .cassandraFormat(keyspace = getKsName, table = "checkin")
        .load()
        .as[CheckIn]

      val actualTip = sparkSession
        .read
        .cassandraFormat(keyspace = getKsName, table = "tip")
        .load()
        .as[Tip]

      actualBusiness.collect() should be(expectedBusiness.collect())
      actualUser.collect() should be(expectedUser.collect())
      actualReview.collect() should be(expectedReview.collect())
      actualTip.collect() should be(expectedTip.collect())
      actualCheckIn.collect() should be(expectedCheckIn.collect())
    }
  }

}


