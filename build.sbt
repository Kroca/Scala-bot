name := "Scala-bot"

version := "0.1"

scalaVersion := "2.12.5"

libraryDependencies ++= Seq(
  "info.mukel" %% "telegrambot4s" % "3.0.14",
  "org.json4s"   %% "json4s-jackson" % "3.5.2",
  "com.typesafe.scala-logging"  %% "scala-logging" % "3.7.2",
  "org.slf4j" % "slf4j-simple" % "1.6.4",
  "com.github.wookietreiber" %% "scala-chart" % "latest.integration",
  "com.itextpdf" % "itextpdf" % "5.5.6",
  "org.jfree" % "jfreesvg" % "3.0"
)