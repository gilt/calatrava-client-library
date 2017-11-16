import scoverage.ScoverageSbtPlugin.ScoverageKeys

lazy val commonSettings = Seq(
  organization := "com.gilt",
  name := "calatrava-client-library",
  scalaVersion := "2.11.7",
  crossScalaVersions := Seq("2.11.7", "2.10.5")
)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(coverageSettings: _*)
  .settings(releaseSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "com.google.inject" % "guice" % "4.0",
      "com.gilt" %% "gfc-logging" % "0.0.3",
      "com.gilt" %% "gfc-util" % "0.1.1",
      "com.amazonaws" % "aws-java-sdk" % "1.10.20",
      "com.amazonaws" % "amazon-kinesis-client" % "1.6.1",
      "com.fasterxml.jackson.datatype" % "jackson-datatype-joda" % "2.6.1",
      "com.gilt" %% "jerkson" % "0.6.8",
      "org.slf4j" % "slf4j-simple" % "1.7.12" % "test",
      "org.scalatest" %% "scalatest" % "2.2.4" % "test",
      "org.mockito" % "mockito-core" % "1.8.5" % "test"
    )
  )

lazy val coverageSettings = Seq(
  ScoverageKeys.coverageExcludedPackages := "com.gilt.calatrava.v0.models"
)

lazy val releaseSettings = Seq(
  releaseCrossBuild := true,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  publishMavenStyle := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  licenses := Seq("Apache-style" -> url("https://raw.githubusercontent.com/gilt/calatrava-client-library/master/LICENSE")),
  homepage := Some(url("https://github.com/gilt/calatrava-client-library")),
  pomExtra := <scm>
                <url>https://github.com/gilt/calatrava-client-library.git</url>
                <connection>scm:git:git@github.com:gilt/calatrava-client-library.git</connection>
              </scm>
              <developers>
                <developer>
                  <id>vdumitrescu</id>
                  <name>Val Dumitrescu</name>
                  <url>https://github.com/vdumitrescu</url>
                </developer>
              </developers>
)
