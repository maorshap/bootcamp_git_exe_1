package jersey.rest;

import boundaries.ShipLogResponse;
import javax.inject.Inject;
import javax.inject.Singleton;
import entities.ServerConfigData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static java.util.Objects.requireNonNull;

@Singleton
@Path("bootcamp")
public class LogShipResource {

    private static int messageCounter = 1;
    private final ServerConfigData serverConfigData;
    private static Logger LOGGER = LogManager.getLogger(LogShipResource.class);

    @Inject
    public LogShipResource(ServerConfigData serverConfigData){
        this.serverConfigData = requireNonNull(serverConfigData);
    }

    /**
     * "/ship" entry point.
     *
     * @return Response
     */
    @GET
    @Path("ship")
    @Produces(MediaType.APPLICATION_JSON)
    public Response shipLog() {
        ShipLogResponse response = new ShipLogResponse();
        response.setMessage(serverConfigData.getLogMessage());
        response.setCounter(messageCounter++);


        LOGGER.info(response);

        return Response
                .status(Response.Status.OK)
                .entity(response)
                .build();
    }
}
