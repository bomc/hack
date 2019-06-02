/**
 * Project: bomc-upload
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: 2016-12-23 14:20:47 +0100 (Fr, 23 Dez 2016) $
 *
 *  revision: $Revision: 9598 $
 *
 * </pre>
 */
package ch.bs.zid.egov.gatling

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._

/**
 * mvn -Dgatling.simulation.name=UploadSimulation gatling:execute
 *
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 07.03.2016
 */
class UploadSimulation extends Simulation {

  var repeat = 5
  var responseStatus = 200
  var uploadBaseUrl = "http://192.168.4.1:8180"
  var username = "bomc-username"
  var password = "bomc-password"

  /* 
	 * Place for arbitrary Scala code that is to be executed before the simulation begins.
	 */
  before {
    println("***** bomc simulation is about to begin! *****")
  }

  /* 
	 * Place for arbitrary Scala code that is to be executed after the simulation has ended.
	 */
  after {
    println("***** bomc simulation has ended! ******")
  }

  val bomc_dataCrit = csv("upload_data.csv").circular

  /*
	 * A HTTP protocol builder is used to specify common properties of request(s) to be sent,
	 * for instance the base URL, HTTP headers that are to be enclosed with all requests etc.
	 */
  val httpProtocol = http
    .baseURL(uploadBaseUrl)
    .acceptHeader("application/vnd.bomc-v1+json")
    .acceptEncodingHeader("gzip, deflate")
    .shareConnections

  /*
	 * A scenario consists of one or more requests. e.g. for instance logging into a e-commerce
	 * website, placing an order and then logging out.
	 * One simulation can contain many scenarios.
	 */
  val scn = scenario("Upload a file to bomc-upload")
    .repeat(repeat, "n") {
      feed(bomc_dataCrit)
        .exec(http("upload_request_${n}")
          .post("/bomc-upload/rest/api/upload")
          .bodyPart(RawFileBodyPart("attachment", "bomc_file_${filePostfix}.csv"))
          .basicAuth(username, password)
          .queryParam("requestId", "${requestId}")
          .check(status.is(responseStatus)))
    }

  /*
	 * Define the load simulation.
	 * Here is specified how many users will be to simulated, if the number of users is to increase
	 * gradually or if all the simulated users are to start sending requests at once etc.
	 * It is also specified the HTTP protocol builder to be used by the load simulation.
	 *
	 */
  //    scn.inject(atOnceUsers(1), rampUsers(1).over(5 seconds), rampUsers(1).over(1 minutes)))

  // the arrival rate at the start of the test
  val initUPS = 0.1
  // the final arrival rate
  val upsRate = 0.5
  // the time it takes to gradually increase from start to max load
  val rampDuration = 60
  // the duration of the "steady state"
  val testDuration = 1

  setUp(
    scn.inject(rampUsersPerSec(initUPS) to (upsRate) during (rampDuration seconds), constantUsersPerSec(upsRate) during (testDuration minutes)))
    .protocols(httpProtocol)
    .assertions(global.responseTime.max.lessThan(10000))
    .assertions(global.failedRequests.count.is(0))
    .assertions(global.successfulRequests.percent.is(100))
}

