package com.dal.yelpdata.processor.utils

import com.datastax.driver.core.{ProtocolVersion, Session}
import com.datastax.spark.connector.cql.CassandraConnector
import com.datastax.spark.connector.embedded.{EmbeddedCassandra, SparkTemplate, YamlTransformations}
import org.apache.commons.lang3.StringUtils
import org.scalatest.{BeforeAndAfterAll, Matchers, Suite}

/* Copyright (C) Boyner Holding A.S- All Rights Reserved
* Unauthorized copying of this file, via any medium is strictly prohibited
* Proprietary and confidential
* @2017
*/

trait SparkCassandraITSpecBase extends Suite with Matchers with EmbeddedCassandra with SparkTemplate with BeforeAndAfterAll {

  val originalProps = sys.props.clone()

  override def clearCache(): Unit = CassandraConnector.evictCache()

  def getKsName = {
    val className = this.getClass.getSimpleName
    val suffix = StringUtils.splitByCharacterTypeCamelCase(className.filter(_.isLetterOrDigit)).mkString("_")
    s"test_$suffix".toLowerCase()
  }

  def conn: CassandraConnector = ???

  def pv = conn.withClusterDo(_.getConfiguration.getProtocolOptions.getProtocolVersion)

  def report(message: String): Unit = {}

  val ks = getKsName

  def skipIfProtocolVersionGTE(protocolVersion: ProtocolVersion)(f: => Unit): Unit = {
    if (!(pv.toInt >= protocolVersion.toInt)) f
    else report(s"Skipped Because ProtcolVersion $pv >= $protocolVersion")
  }

  def skipIfProtocolVersionLT(protocolVersion: ProtocolVersion)(f: => Unit): Unit = {
    if (!(pv.toInt < protocolVersion.toInt)) f
    else report(s"Skipped Because ProtocolVersion $pv < $protocolVersion")
  }


  def createKeyspace(session: Session, name: String = ks): Unit = {
    session.execute(s"DROP KEYSPACE IF EXISTS $name")
    session.execute(
      s"""
         |CREATE KEYSPACE IF NOT EXISTS $name
         |WITH REPLICATION = { 'class': 'SimpleStrategy', 'replication_factor': 1 }
         |AND durable_writes = false
         |""".stripMargin
    )
  }


  def restoreSystemProps(): Unit = {
    sys.props ++= originalProps
    sys.props --= (sys.props.keySet -- originalProps.keySet)
  }


  override protected def beforeAll(): Unit = {
    super.beforeAll()
    useCassandraConfig(Seq(YamlTransformations.Default))
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    clearCache()
    restoreSystemProps()
  }
}
