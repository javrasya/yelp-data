import sbt.ExclusionRule

name := "yelp-data-processor"

organization := "com.dal.ahmet"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.6"

val sparkVersion = "2.2.0"

val sparkCassandraVersion = "2.0.3"
val cassandraVersion = "3.2"

javacOptions in compile ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint:deprecation", "-Xlint:unchecked")

resolvers += DefaultMavenRepository
resolvers += "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository"
resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"

updateOptions := updateOptions.value.withLatestSnapshots(false)


libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
  "com.datastax.spark" %% "spark-cassandra-connector" % sparkCassandraVersion % "provided",
  //Test Dependencies
  "com.datastax.spark" %% "spark-cassandra-connector-embedded" % sparkCassandraVersion % "test"
    exclude("com.datastax.cassandra", "cassandra-driver-core"),
  "org.apache.cassandra" % "cassandra-all" % cassandraVersion % "test",


  "org.scalactic" %% "scalactic" % "3.0.1",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
).map(_.exclude("org.slf4j", "log4j-over-slf4j"))


// This is important for embedded cassandra.
fork in Test := true

logBuffered in Test := false
//parallelExecution in Test := false

mainClass in assembly := Some("com.dal.ahmet.yelpdata.processor.DataProcessor")

unmanagedSourceDirectories in Test += baseDirectory.value / "src/it/scala"

unmanagedResourceDirectories in Test += baseDirectory.value / "src/it/resources"

excludeFilter in Compile := "*.tar" || "*.json" || "*.txt"


//import ScalaxbKeys._

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-W", "120", "60")