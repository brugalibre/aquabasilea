package com.aquabasilea.migrosapi.http.model.response;

import com.aquabasilea.migrosapi.http.service.HttpService;

/**
 * Common interface for all responses from the {@link HttpService}
 *
 * @author Dominic
 */
public interface HttpResponse {

   /**
    * @return <code>true</code> if the request was posted successfully or <code>false</code> if not
    */
   boolean isSuccessful();

   /**
    * sets the http-status of the response
    *
    * @param successful @return <code>true</code> if the request was posted successfully or <code>false</code> if not
    */
   void setIsSuccessful(boolean successful);
}
