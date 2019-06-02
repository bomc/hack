package de.bomc.registrator.rest.endpoints;

import java.net.URI;
import java.util.UUID;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;

import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.registrator.events.command.EventCommandService;
import de.bomc.registrator.events.entity.EventInfo;
import de.bomc.registrator.events.entity.EventType;
import de.bomc.registrator.rest.json.JsonResponseObject;
import de.bomc.registrator.rest.json.RegisterUserData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Rest endpoint to expose kafka messages.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.07.2018
 */
@Path("/event")
@Api(value = "/event")
public class EventEndpoint {

	private static final String LOG_PREFIX = "EventEndpoint#";

	@Inject
	@LoggerQualifier
	private Logger logger;
	@Inject
	private EventCommandService eventCommandService;
    @Context
    private UriInfo uriInfo;

	@GET
	@Path("/register-user")
	@Produces("application/json")
	@Consumes("application/json")
	@ApiOperation(notes = "Register a user by the given payload.", value = "Register a new user.", nickname = "registerUser", httpMethod = "POST")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully finished.", response = String.class) })
	public String registerUser(final RegisterUserData registerUserData) {
		this.logger.debug(LOG_PREFIX + "registerUser " + registerUserData);
		
		final EventType eventType = EventType.fromString(EventType.BOMC_EVENT_A.toString());
		final UUID eventId = UUID.randomUUID();
		final String payload = "bomc_send_event_payload";
		
		this.eventCommandService.doEvent(new EventInfo(eventId, eventType, payload));
		
		// @TODO method -> 'sendEvent', if hateos is used the next invocation has to be entered.
        final URI uri = uriInfo.getRequestUriBuilder().path(EventEndpoint.class, "sendEvent").build(eventId);
//        return Response.accepted().header(HttpHeaders.LOCATION, uri).build();
        
        this.logger.debug(LOG_PREFIX + "sendEvent - [uri=" + uri.toString() + "]");
        
        return new JsonResponseObject("status=OK").toJson();

	}

//	@GET
//	@Path("/test-broker")
//	@Produces("application/json")
//	@ApiOperation(notes = "bomc - add here some notes.", value = "Test connection to broker.", nickname = "testSocketToBroker", httpMethod = "GET")
//	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully finished.", response = Boolean.class) })
//	public Boolean testSocketToBroker() {
//		
//		try {
//			final Socket socket = new Socket("bootstrap", 9092);
//			socket.close();
//		} catch(IOException ioex) {
//			return false;
//		}
//		
//		return true;
//	}
	
	// @GET
	// @Path("/hello-chaining")
	// @Produces("application/json")
	// @ApiOperation("Returns the greeting plus the next service in the chain")
	// public List<String> helloChaining() {
	// List<String> greetings = new ArrayList<>();
	// greetings.add(hello());
	// // greetings.addAll(getNextService().namaste());
	// return greetings;
	// }

//	@GET
//	@Path("/health")
//	@Produces("text/plain")
//	@ApiOperation("Used to verify the health of the service")
//	public String health() {
//		return "... the whole enchilada was done ...";
//	}

	/**
	 * This is were the "magic" happens: it creates a Feign, which is a proxy
	 * interface for remote calling a REST endpoint with Hystrix fallback
	 * support.
	 *
	 * @return The feign pointing to the service URL and with Hystrix fallback.
	 */
	// private NamasteService getNextService() {
	// final String serviceName = "namaste";
	// // This stores the Original/Parent ServerSpan from ZiPkin.
	// final ServerSpan serverSpan =
	// brave.serverSpanThreadBinder().getCurrentServerSpan();
	// final CloseableHttpClient httpclient =
	// HttpClients.custom()
	// .addInterceptorFirst(new
	// BraveHttpRequestInterceptor(brave.clientRequestInterceptor(), new
	// DefaultSpanNameProvider()))
	// .addInterceptorFirst(new
	// BraveHttpResponseInterceptor(brave.clientResponseInterceptor()))
	// .build();
	// String url = String.format("http://%s:8080/", serviceName);
	// return HystrixFeign.builder()
	// // Use apache HttpClient which contains the ZipKin Interceptors
	// .client(new ApacheHttpClient(httpclient))
	// // Bind Zipkin Server Span to Feign Thread
	// .requestInterceptor((t) ->
	// brave.serverSpanThreadBinder().setCurrentSpan(serverSpan))
	// .logger(new Logger.ErrorLogger()).logLevel(Level.BASIC)
	// .decoder(new JacksonDecoder())
	// .target(NamasteService.class, url,
	// () -> Collections.singletonList("Namaste response (fallback)"));
	// }

}
