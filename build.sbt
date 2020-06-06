name := """server"""
organization := "com.gabenpls"
maintainer := "mcsim1993@gmail.com"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, DebianPlugin, SystemdPlugin)

scalaVersion := "2.13.1"
javacOptions ++= Seq("-source", "11", "-target", "11")

libraryDependencies ++= Seq(
  guice,
  openId,
  javaWs,
  caffeine
)

maintainer in Linux := "Maxim Gribov <mcsim1993@gmail.com>"
packageSummary in Linux := "My custom package summary"
packageDescription := "My longer package description"
