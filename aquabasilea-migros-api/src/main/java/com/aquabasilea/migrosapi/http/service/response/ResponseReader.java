package com.aquabasilea.migrosapi.http.service.response;

import com.aquabasilea.migrosapi.http.model.response.HttpResponse;
import com.aquabasilea.migrosapi.http.service.HttpService;
import okhttp3.Response;

import java.io.IOException;

/**
 * The {@link ResponseReader} reads the json response the {@link HttpService} has received and maps
 * it in any subtype of {@link HttpResponse}
 *
 * @author Dominic
 */
public interface ResponseReader<T extends HttpResponse> {

   /**
    * Reads the given {@link ResponseReader} and maps it into a {@link HttpResponse}
    *
    * @param response the Response to read
    * @return the {@link HttpResponse} as a parsed result
    * @throws IOException an {@link IOException} thrown by {@link okhttp3.OkHttp}
    */
   T readResponse(Response response) throws IOException;

   /**
    * Creates an error response which contains further information about the failed call
    *
    * @param e   the exception which occurred
    * @param url the url which was posted in order to get a result
    * @return an error response which contains further information about the failed call
    */
   T createErrorResponse(Exception e, String url);
}
