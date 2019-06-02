/*
 * Project: lra-order-controller-gatling
 *
 * Last change:
 *
 * by: $Author: bomc $
 *
 * date: $Date: $
 *
 * revision: $Revision: $
 */
package de.bomc.poc.axon.gatling

import io.gatling.core.feeder.RecordSeqFeederBuilder
import io.gatling.commons.stats.Status
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.ExtraInfo

import scala.concurrent.duration._

/*
 * mvn -Pgatling -Dgatling.simulation.Class=de.bomc.poc.axon.gatling.LoadScenarioSimulation gatling:execute
 */
class LoadScenarioSimulation extends Simulation {
  var repeat = 10
  var repeatFail = 10
  var responseStatus = 200
  var updateStatus = 204
  var postBaseUrl = "http://127.0.0.1:8180"
  val isDebug = true

  /*
   * Place for arbitrary Scala code that is to be executed before the simulation begins.
   */
  before {
    println("***** a load scenario simulation for account/authorization is about to begin! *****")
  }

  /*
   * Place for arbitrary Scala code that is to be executed after the simulation has ended.
   */
  after {
    println("***** a load scenario simulation for account/authorization has ended! ******")
  }

  val orderIdFeeder = new Feeder[String] {
    override def hasNext = true

    override def next: Map[String, String] = {
      Map("orderId" -> java.util.UUID.randomUUID.toString());
    }
  }

  /*
   * A HTTP protocol builder is used to specify common properties of request(s) to be sent,
   * for instance the base URL, HTTP headers that are to be enclosed with all requests etc.
   * 
   * NOTE: see in project 'lra-order-controller' by endpoint 'OrderControllerServiceEndpoint' for MediaType 'text/plain'
   */
  val httpProtocol = http
    .baseURL(postBaseUrl)
    .acceptHeader("text/plain,application/json")
    .acceptEncodingHeader("charset=utf-8")
    .shareConnections
    .disableWarmUp
    .extraInfoExtractor { extraInfo => List(getExtraInfo(extraInfo)) }

  object OrderCreate {
    val orderCreateVal = exec(http("orderCreate")
      .get(s"/lra-order-controller/rest/api/order/" + "${orderId}")
      .check(status.is(responseStatus)))
  }

  object FailShipment {
    val failShipmentVal = exec(http("failShipment")
      .get(s"/lra-order-controller/rest/api/order/failShipment")
      .check(status.is(responseStatus)))
  }

  private def getExtraInfo(extraInfo: ExtraInfo): String = {
    // Dump request/response in case of error or in Debug mode
    if (isDebug
      || extraInfo.response.statusCode.get != responseStatus
      || extraInfo.status.eq(Status.apply("KO"))) {
      ", URL: " + extraInfo.request.getUrl +
        ", Request: " + extraInfo.request.getStringData +
        ", Response: " + extraInfo.response.body.string
    } else {
      ""
    }
  }

  /*
   * A scenario consists of one or more requests. For instance logging into a e-commerce
   * website, placing an order and then logging out.
   * One simulation can contain many scenarios.
   */
  val loadScenarioOrderCreate = scenario("Load scenario for create order.")
    .repeat(repeat, "n") {
      feed(orderIdFeeder)
        .exec(OrderCreate.orderCreateVal)
        .exec(session => {
          println("Created order with orderId:")
          println(session("orderId").as[String])
          session
        })
        .pause(25.milliseconds, 50.milliseconds)
    }

  val loadScenariofailShipment = scenario("Load scenario for fail shipment.")
    .repeat(repeatFail, "n") {
      exec(FailShipment.failShipmentVal)
        .pause(25.milliseconds, 50.milliseconds)
    }

  setUp(
      loadScenarioOrderCreate.inject(
          rampUsers(100) over (15 seconds)
      )/*,
      loadScenariofailShipment.inject(
          rampUsers(25) over (15 seconds)
      )*/
  ).protocols(httpProtocol)
    .maxDuration(30 seconds)
    .assertions(global.responseTime.max.lte(500),global.successfulRequests.percent.gte(75))

}
