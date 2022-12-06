package com.aquabasilea.migrosapi.http.service.response;

import com.aquabasilea.migrosapi.http.model.response.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Response;

import java.io.IOException;

import static java.util.Objects.nonNull;

/**
 * Base implementation of a {@link ResponseReader}. Takes the body and maps it into any sub type of {@link HttpResponse}
 * Only this very subtype has to be provided by any subclass of this {@link AbstractHttpResponseReader}
 *
 * @param <T>
 * @author Dominic
 */
public abstract class AbstractHttpResponseReader<T extends HttpResponse> implements ResponseReader<T> {

   @Override
   public T readResponse(Response response) throws IOException {
      String bodyAsString = getResponseBody(response);
      T httpResponse = new ObjectMapper().readValue(bodyAsString, getResponseResultClass());
      setResponseValues(response, httpResponse);
      return httpResponse;
   }

   private static String getResponseBody(Response response) throws IOException {
      return response.body() == null ? "" : response.body().string();
   }

   private static <T extends HttpResponse> void setResponseValues(Response okResponse, T httpResponse) {
      httpResponse.setIsSuccessful(okResponse.isSuccessful());
   }

   protected abstract Class<T> getResponseResultClass();
}
