name := """mongodb"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.reactivemongo" %% "play2-reactivemongo" % "0.11.14",
  "com.typesafe.play" % "play-json_2.11" % "2.6.0-M1",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)


unmanagedResourceDirectories in Assets += baseDirectory.value / "node_modules"

routesGenerator := InjectedRoutesGenerator