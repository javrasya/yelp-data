
name := "yelp-data-processor"

organization := "com.dal.ahmet"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.6"

val sparkVersion = "2.2.0"

val sparkCassandraVersion = "2.0.3"
val cassandraVersion = "3.0.14"


resolvers += DefaultMavenRepository
resolvers += "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository"
resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)


updateOptions := updateOptions.value.withLatestSnapshots(false)


libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
  "com.datastax.spark" %% "spark-cassandra-connector" % sparkCassandraVersion % "provided",
  //Test Dependencies
  "com.datastax.spark" %% "spark-cassandra-connector-embedded" % sparkCassandraVersion % "test"
    exclude("com.datastax.cassandra", "cassandra-driver-core"),
  "org.apache.cassandra" % "cassandra-all" % cassandraVersion % "test",


  "org.scalactic" %% "scalactic" % "3.0.1" % "test",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
).map(_.exclude("org.slf4j", "log4j-over-slf4j"))


mainClass in assembly := Some("com.dal.ahmet.yelpdata.processor.DataProcessor")



assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
