package com.aquabasilea.migrosapi.http;

import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.HttpStatusCode;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

public class DummyHttpServerTestCaseBuilder {

   private final int port;
   private final ClientAndServer clientServer;
   private final Map<HttpRequest, HttpResponse> requestHttpResponseMap;
   private String host;

   public DummyHttpServerTestCaseBuilder(int port) {
      this.clientServer = ClientAndServer.startClientAndServer(port);
      this.port = port;
      this.requestHttpResponseMap = new HashMap<>();
   }

   public DummyHttpServerTestCaseBuilder withHost(String host) {
      this.host = host;
      if (!this.host.contains(":")) {
         this.host = this.host + ":" + port;
      }
      return this;
   }

   public HttpRequestResponseBuilder withRequestResponse(String path) {
      return new HttpRequestResponseBuilder(path, this);
   }

   public DummyHttpServerTestCaseBuilder build() {
      requireNonNull(host, "call 'withHost' first");
      for (Map.Entry<HttpRequest, HttpResponse> httpRequestHttpResponseEntry : requestHttpResponseMap.entrySet()) {
         clientServer.when(httpRequestHttpResponseEntry.getKey())
                 .respond(httpRequestHttpResponseEntry.getValue());
      }
      return this;
   }

   private void add(HttpRequestResponseBuilder httpRequestResponseBuilder) {
      this.requestHttpResponseMap.put(httpRequestResponseBuilder.httpRequest, httpRequestResponseBuilder.httpResponse);
   }

   public void showUI() {
      clientServer.openUI(TimeUnit.MINUTES, 60);
   }

   public static final class HttpRequestResponseBuilder {

      private final DummyHttpServerTestCaseBuilder dummyHttpServerTestCaseBuilder;
      private final String path;
      private String responseBody;
      private String method;
      private HttpRequest httpRequest;
      private HttpResponse httpResponse;
      private Header header;
      private String requestBody;

      public HttpRequestResponseBuilder(String path, DummyHttpServerTestCaseBuilder dummyHttpServerTestCaseBuilder) {
         this.path = path;
         this.dummyHttpServerTestCaseBuilder = dummyHttpServerTestCaseBuilder;
      }

      public HttpRequestResponseBuilder withMethod(String method) {
         this.method = method;
         return this;
      }

      public HttpRequestResponseBuilder withResponseBody(String responseBody) {
         this.responseBody = responseBody;
         return this;
      }

      public HttpRequestResponseBuilder withRequestBody(String requestBody) {
         this.requestBody = requestBody;
         return this;
      }

     public HttpRequestResponseBuilder withHeader(Header header) {
         this.header = header;
         return this;
      }

      public DummyHttpServerTestCaseBuilder buildRequestResponse() {
         httpRequest = new HttpRequest()
                 .withMethod(method)
                 .withBody(requestBody)
                 .withPath(path)
                 .withHeader(header);

         httpResponse = new HttpResponse()
                 .withStatusCode(HttpStatusCode.OK_200.code())
                 .withBody(responseBody);
         this.dummyHttpServerTestCaseBuilder.add(this);
         return dummyHttpServerTestCaseBuilder;
      }
   }

}
