import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "monitor"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
 
    javaCore,

	// Twitter4j Library
  	"org.twitter4j" % "twitter4j-core" % "3.0.3",
    "org.twitter4j" % "twitter4j-async" % "3.0.3",
    "org.twitter4j" % "twitter4j-stream" % "3.0.3",

 	// Morphia
    "com.google.code.morphia" % "morphia" % "0.99.1-SNAPSHOT",
    "com.google.code.morphia" % "morphia-logging-slf4j" % "0.99",
    "com.google.code.morphia" % "morphia-validation" % "0.99",

  // Guice Dependence Injection
    "com.google.inject" % "guice" % "3.0"


    //javaJdbc,
    //javaEbean
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(

	resolvers += "Morphia Repository" at "http://morphia.googlecode.com/svn/mavenrepo/"
	
  )

}
