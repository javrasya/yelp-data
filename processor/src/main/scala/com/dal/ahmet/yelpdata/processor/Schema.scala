package com.dal.ahmet.yelpdata.processor

import org.apache.spark.sql.{Dataset, Row, SparkSession}

object Schema extends Serializable {


  trait BaseSchema extends Serializable {
    val `type`: String
  }

  case class Business(
                       business_id: String
                       , name: String
                       , neighborhood: String
                       , address: String
                       , city: String
                       , state: String
                       , latitude: Double
                       , longitude: Double
                       , stars: Double
                       , review_count: Long
                       , is_open: Boolean
                       , attributes: Array[String]
                       , categories: Array[String]
                       , hours: Array[String]
                       , `type`: String = "business"
                     ) extends BaseSchema


  case class User(
                   user_id: String
                   , name: String
                   , review_count: Long
                   , yelping_since: String
                   , friends: Array[String]
                   , useful: Long
                   , funny: Long
                   , cool: Long
                   , fans: Long
                   , elite: Array[String]
                   , average_stars: Double
                   , compliment_hot: Long
                   , compliment_more: Long
                   , compliment_profile: Long
                   , compliment_cute: Long
                   , compliment_list: Long
                   , compliment_note: Long
                   , compliment_plain: Long
                   , compliment_cool: Long
                   , compliment_funny: Long
                   , compliment_writer: Long
                   , compliment_photos: Long
                   , override val `type`: String = "user"

                 ) extends BaseSchema

  case class Review(
                     review_id: String,
                     user_id: String,
                     business_id: String,
                     stars: Double,
                     date: String,
                     text: String,
                     useful: Long,
                     funny: Long,
                     cool: Long,
                     override val `type`: String = "review"
                   ) extends BaseSchema

  case class CheckIn(
                      time: Array[String],
                      business_id: String,
                      override val `type`: String = "checkin"
                    ) extends BaseSchema


  case class Tip(
                  text: String
                  , date: String
                  , likes: Long
                  , business_id: String
                  , user_id: String
                  , override val `type`: String = "tip"
                ) extends BaseSchema


  def encodeDataSet(sparkSession: SparkSession, dataType: String, dataSet: Dataset[Row]): Dataset[_ <: BaseSchema] = {
    import sparkSession.implicits._
    dataType match {
      case "business" => dataSet.as[Business]
      case "user" => dataSet.as[User]
      case "review" => dataSet.as[Review]
      case "checkin" => dataSet.as[CheckIn]
      case "tip" => dataSet.as[Tip]
      case _ => null
    }
  }

  val schemas = Array(
    "business"
    , "user"
    , "review"
    , "checkin"
    , "tip"
  )


}






