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
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Slf4j
public class LoggingRequest implements ClientHttpRequestInterceptor {

    private final List<String> urls;

    private LoggingRequest(String[] urls) {
        this.urls = Arrays.asList(urls);
    }

    public static LoggingRequest of(String... urls) {
        return new LoggingRequest(urls);
    }

    private static final String REQUEST_LOG_TEMPLATE = "\n[Request begin]\n" +
            "Method URI   : {} {}\n" +
            "Status       : {} {}\n" +
            "Request body : {}\n" +
            "Response body: {}\n";

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        ClientHttpResponse response = execution.execute(request, body);
        if (urls.contains(request.getURI().getPath())) {
            logRequest(request, body, response);
        }
        return response;
    }

    private void logRequest(HttpRequest request, byte[] body, ClientHttpResponse response) throws IOException {
        log.info(REQUEST_LOG_TEMPLATE,
                request.getMethod(),
                request.getURI(),
                response.getStatusCode(),
                response.getStatusText(),
                new String(body, StandardCharsets.UTF_8),
                responseAsString(response.getHeaders(), IOUtils.toByteArray(response.getBody())));
    }

    private String responseAsString(HttpHeaders headers, byte[] response) {
        MediaType contentType = headers.getContentType();
        if (response != null && response.length > 0) {
            if (contentType != null && contentType.toString().contains(MediaType.APPLICATION_JSON_VALUE)) {
                return new String(response, StandardCharsets.UTF_8);
            } else {
                return Base64.getEncoder().encodeToString(response);
            }
        }
        return "[EMPTY]";
    }
}