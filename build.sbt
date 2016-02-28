organization := "anev"

name := "NestSlack"

version := "0.1"

scalaVersion := "2.11.7"

resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "jitpack" at "https://jitpack.io"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.13",
  "com.firebase" % "firebase-client" % "1.0.18",
  "com.github.flowctrl" % "slack-api" % "v1.1.0.RELEASE",

  // test
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

enablePlugins(DockerPlugin)

dockerfile in docker := {
  // The assembly task generates a fat JAR file
  val artifact: File = assembly.value
  val artifactTargetPath = s"/app/${artifact.name}"

  new Dockerfile {
    from("java")
    add(artifact, artifactTargetPath)
    entryPoint("java", "-jar", artifactTargetPath)
  }
}

imageNames in docker := Seq(
  // Sets the latest tag
//  ImageName(s"${organization.value}/${name.value}:latest"),

  // Sets a name with a tag that contains the project version
  ImageName(
    namespace = Some(organization.value),
    repository = name.value,
    tag = Some("v" + version.value)
  )
)