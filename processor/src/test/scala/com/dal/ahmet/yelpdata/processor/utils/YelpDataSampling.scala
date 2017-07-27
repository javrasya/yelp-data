package com.dal.ahmet.yelpdata.processor.utils

import java.io.File

import org.apache.spark.sql.SparkSession

import sys.process._


object YelpDataSampling {


  def doSampling(sparkSession: SparkSession, fullDataPath: String): Unit = {
    import sparkSession.sqlContext.implicits._
    val businessDF = sparkSession
      .read
      .json(s"file://${new File(fullDataPath, "yelp_academic_dataset_business.json").getPath}")
      .filter($"business_id".isNotNull)
      .limit(1000)

    businessDF.createTempView("business")

    sparkSession
      .read
      .json(s"file://${new File(fullDataPath, "yelp_academic_dataset_review.json").getPath}")
      .filter($"business_id".isNotNull && $"user_id".isNotNull)
      .createTempView("all_review")

    sparkSession
      .read
      .json(s"file://${new File(fullDataPath, "yelp_academic_dataset_user.json").getPath}")
      .filter($"user_id".isNotNull)
      .createTempView("all_user")

    sparkSession
      .read
      .json(s"file://${new File(fullDataPath, "yelp_academic_dataset_checkin.json").getPath}")
      .filter($"business_id".isNotNull)
      .createTempView("all_checkin")


    sparkSession
      .read
      .json(s"file://${new File(fullDataPath, "yelp_academic_dataset_tip.json").getPath}")
      .filter($"business_id".isNotNull && $"user_id".isNotNull)
      .createTempView("all_tips")


    val reviewDF = sparkSession.sql(
      s"""
         |select
         |  r.*,
         |  rank() OVER (PARTITION BY r.business_id  ORDER BY r.business_id) as rank
         |from all_review r
         | inner join business b on b.business_id = r.business_id
        """.stripMargin).filter($"rank" < 1000).drop("rank")

    reviewDF.createTempView("review")


    val userDF = sparkSession.sql(
      """
        |select
        | distinct
        | u.*,
        | rank() OVER (PARTITION BY r.review_id  ORDER BY r.review_id) as rank
        |from review r
        |inner join all_user u on u.user_id = r.user_id
      """.stripMargin).drop("rank")

    userDF.createTempView("user")

    val tipsDF = sparkSession.sql(
      """
        |select
        | t.*,
        | rank() OVER (PARTITION BY t.business_id,t.user_id  ORDER BY t.business_id) as rank
        |from all_tips t
        |inner join user u on t.user_id = u.user_id
        |inner join business b on t.business_id = b.business_id
      """.stripMargin).filter($"rank" < 10000).drop("rank")

    val checkinDF = sparkSession.sql(
      s"""
         |select
         |  r.*,
         |  rank() OVER (PARTITION BY r.business_id  ORDER BY r.business_id) as rank
         |from all_checkin r
         | inner join business b on b.business_id = r.business_id
        """.stripMargin).filter($"rank" < 1000).drop("rank")

    val resourceDataPath = ClassLoader.getSystemResource("data").getPath


    businessDF.write
      .json(s"file://${new File(resourceDataPath, "yelp_academic_dataset_business").getPath}")

    reviewDF.write
      .json(s"file://${new File(resourceDataPath, "yelp_academic_dataset_review").getPath}")

    userDF.write
      .json(s"file://${new File(resourceDataPath, "yelp_academic_dataset_user").getPath}")

    tipsDF.write
      .json(s"file://${new File(resourceDataPath, "yelp_academic_dataset_tip").getPath}")

    checkinDF.write
      .json(s"file://${new File(resourceDataPath, "yelp_academic_dataset_checkin").getPath}")


//    s"cat ${new File(resourceDataPath, "yelp_academic_dataset_business/part*.json").getPath} >> ${new File(resourceDataPath, "yelp_academic_dataset_business.json").getPath}" !
//
//    s"cat ${new File(resourceDataPath, "yelp_academic_dataset_review/part*.json").getPath} >> ${new File(resourceDataPath, "yelp_academic_dataset_review.json").getPath}" !
//
//    s"cat ${new File(resourceDataPath, "yelp_academic_dataset_user/part*.json").getPath} >> ${new File(resourceDataPath, "yelp_academic_dataset_user.json").getPath}" !
//
//    s"cat ${new File(resourceDataPath, "yelp_academic_dataset_tip/part*.json").getPath} >> ${new File(resourceDataPath, "yelp_academic_dataset_tip.json").getPath}" !
//
//    s"cat ${new File(resourceDataPath, "yelp_academic_dataset_checkin/part*.json").getPath} >> ${new File(resourceDataPath, "yelp_academic_dataset_checkin.json").getPath}" !
//
//
//    s"rm -rf ${new File(resourceDataPath, "yelp_academic_dataset_business").getPath}"
//
//    s"rm -rf ${new File(resourceDataPath, "yelp_academic_dataset_review").getPath}"
//
//    s"rm -rf ${new File(resourceDataPath, "yelp_academic_dataset_user").getPath}"
//
//    s"rm -rf ${new File(resourceDataPath, "yelp_academic_dataset_tip").getPath}"
//
//    s"rm -rf ${new File(resourceDataPath, "yelp_academic_dataset_checkin").getPath}"
  }


}
