package ru.aliascage.movie_service.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
public class LoggingRequest implements ClientHttpRequestInterceptor {

    private static final String RESPONSE_LOG_TEMPLATE = "\n=======================response begin===============================================\n" +
            "Status       : {} {}\n" +
            "Headers      : {}\n" +
            "Response body: {}\n" +
            "=======================response end=================================================";

    private static final String REQUEST_LOG_TEMPLATE = "\n==========================request begin==============================================\n" +
            "Method URI  : {} {}\n" +
            "Headers     : {}\n" +
            "Request body: {}\n" +
            "==========================request end================================================";

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        logRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        logResponse(response);
        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) {
        log.info(REQUEST_LOG_TEMPLATE,
                request.getMethod(),
                request.getURI(),
                request.getHeaders(),
                new String(body, StandardCharsets.UTF_8));
    }

    private void logResponse(ClientHttpResponse response) throws IOException {
        log.info(RESPONSE_LOG_TEMPLATE,
                response.getStatusCode(),
                response.getStatusText(),
                response.getHeaders(),
                responseAsString(response.getHeaders(), IOUtils.toByteArray(response.getBody())));
    }

    private String responseAsString(HttpHeaders headers, byte[] response) {
        MediaType contentType = headers.getContentType();
        if (response != null && response.length > 0) {
            if (contentType != null && contentType.toString().contains("json")) {
                return new String(response, Charset.forName("UTF-8"));
            } else {
                return Base64.getEncoder().encodeToString(response);
            }
        }
        return "[EMPTY]";
    }
}