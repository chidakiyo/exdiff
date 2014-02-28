import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName = "helloworld"
  val appVersion = "1.0"

  val appDependencies = Seq(
    // Add your project dependencies here,
    "mysql" % "mysql-connector-java" % "5.1.21",
    "org.apache.poi" % "poi" % "3.9", // Excelファイル操作用
    "org.apache.poi" % "poi-ooxml" % "3.9", // POIのXSSF用のライブラリ
    "com.typesafe" %% "scalalogging-slf4j" % "1.0.1", // ロガー
    "net.arnx" % "jsonic" % "1.3.1", // JSONIC
    jdbc,
    anorm)

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
    testOptions in Test += Tests.Argument("junitxml", "console"))

}