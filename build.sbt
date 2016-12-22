val maintainer = "xofspades"
name := "scala-server"

version := "0.3.0"

scalaVersion := "2.11.8"

// config for serial execution of db related tests
lazy val Serial = config("serial") extend(Test)

def serialFilter(name: String): Boolean = {
  (name contains "Repository") ||
  (name contains "Postgres")
}
def parallelFilter(name: String): Boolean = !serialFilter(name)

lazy val rest = Project(
  id = "rest",
  base = file("rest"))
  .configs(Serial)
  .settings(inConfig(Serial)(Defaults.testTasks) : _*)
  .settings(
    testOptions in Test := Seq(Tests.Filter(parallelFilter)),
    testOptions in Serial := Seq(Tests.Filter(serialFilter))
  )
  .settings(parallelExecution in Serial := false : _*)

parallelExecution in Test := false

resolvers ++= Seq()

scalacOptions ++= Seq(
  "-Xlint",
  "-deprecation",
  "-feature",
  "-unchecked"
)

libraryDependencies ++= {
  val akkaVersion = "2.4.12"
  val akkaHttpVersion = "10.0.0"
  Seq(
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion
  )
}


enablePlugins(DockerPlugin)

docker <<= docker.dependsOn(assembly)

imageNames in docker := Seq(
  ImageName(s"${maintainer}/${name.value}:${version.value}"),
  ImageName(s"${maintainer}/${name.value}:latest")
)

buildOptions in docker := BuildOptions(
  cache = true,
  removeIntermediateContainers = BuildOptions.Remove.OnSuccess,
  pullBaseImage = BuildOptions.Pull.Always
)

dockerfile in docker := {
  val exposedPort = 8080
  val jarFile = (assemblyOutputPath in assembly).value
  val jarName = name.value + ".jar"

  new Dockerfile {
    from("java:8-jre-alpine")
    workDir("/app")
    run("mkdir", "-p", "/config")
    volume("/config")
    expose(exposedPort)
    entryPointRaw(s"java -Dconfig.file=/config/application.conf -jar ${jarName}")
    copy(jarFile, s"/app/${jarName}")
  }
}
