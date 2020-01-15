package jersey.rest;

import javax.ws.rs.core.Response;

public class RestUtils {

    private RestUtils(){}

    public static Response buildResponse(int status, String message){
        return Response.status(status)
                .entity(message)
                .build();
    }
}
