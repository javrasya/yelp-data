package com.dal.ahmet.yelpdata.processor

import com.dal.ahmet.yelpdata.processor.Schema._
import com.dal.ahmet.yelpdata.processor.datasource.InMemoryDataSource
import com.datastax.spark.connector.embedded.SparkTemplate
import org.scalatest.{BeforeAndAfterAll, FreeSpec, Matchers}

class DataProcessorTest extends FreeSpec with Matchers with SparkTemplate with BeforeAndAfterAll {


  "Data Processor Test" - {
    "Different data types should be read and persisted properly with their schema." in {

      val dataFolderPath = getClass.getClassLoader.getResource("data").getPath
      val sparkSession = useSparkConf(defaultConf.set("spark.yelp.data.folder", s"file://$dataFolderPath"))
      import sparkSession.implicits._


      val expectedBusiness = sparkSession.read.json(this.getClass.getClassLoader.getResource("data/yelp_academic_dataset_business.json").getPath).as[Business]
      val expectedUser = sparkSession.read.json(this.getClass.getClassLoader.getResource("data/yelp_academic_dataset_user.json").getPath).as[User]
      val expectedReview = sparkSession.read.json(this.getClass.getClassLoader.getResource("data/yelp_academic_dataset_review.json").getPath).as[Review]
      val expectedTip = sparkSession.read.json(this.getClass.getClassLoader.getResource("data/yelp_academic_dataset_tip.json").getPath).as[Tip]
      val expectedCheckIn = sparkSession.read.json(this.getClass.getClassLoader.getResource("data/yelp_academic_dataset_checkin.json").getPath).as[CheckIn]


      val dataSource = new InMemoryDataSource()
      val dataProcessor = new DataProcessor(dataSource)
      dataProcessor.process(sparkSession)


      val actualBusiness = dataSource.data.getOrElse("business", sparkSession.emptyDataFrame.as[Business]).as[Business]
      val actualUser = dataSource.data.getOrElse("user", sparkSession.emptyDataFrame.as[User]).as[User]
      val actualReview = dataSource.data.getOrElse("review", sparkSession.emptyDataFrame.as[Review]).as[Review]
      val actualTip = dataSource.data.getOrElse("tip", sparkSession.emptyDataFrame.as[Tip]).as[Tip]
      val actualCheckIn = dataSource.data.getOrElse("checkin", sparkSession.emptyDataFrame.as[CheckIn]).as[CheckIn]


      actualBusiness.collect() should be(expectedBusiness.collect())
      actualUser.collect() should be(expectedUser.collect())
      actualReview.collect() should be(expectedReview.collect())
      actualTip.collect() should be(expectedTip.collect())
      actualCheckIn.collect() should be(expectedCheckIn.collect())

    }

    "An assertion error must be thrown unless yelp data folder is given" in {

      val sparkSession = useSparkConf(defaultConf)
      val dataSource = new InMemoryDataSource()
      val dataProcessor = new DataProcessor(dataSource)
      an[AssertionError] must be thrownBy dataProcessor.process(sparkSession)

    }
  }

}


