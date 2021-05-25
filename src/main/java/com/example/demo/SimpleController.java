package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class SimpleController {

    @RequestMapping
    //Http method didn't specified to receive all requests
    public ResponseEntity<String> handleRequest(HttpServletRequest request) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("Requested uri: ").append(request.getRequestURI()).append('\n');
        appendHeaders(request, builder);
        appendMultiParts(request, builder);
        appendParameters(request, builder);
        appendRequestBody(request, builder);
        return ResponseEntity.ok(builder.toString());
    }

    private void appendRequestBody(HttpServletRequest request, StringBuilder builder) {
        builder.append("===========body===============\n");
        try (
                ServletInputStream in = request.getInputStream();
                BufferedReader buffered = new BufferedReader(new InputStreamReader(in))
        ) {
            String line;
            while ((line = buffered.readLine()) != null) {
                builder.append(line).append('\n');
            }
        } catch (IOException e) {
            builder.append("failed reading body... ").append(e.getMessage());
        }
        builder.append("===========body===============\n");
    }

    private void appendParameters(HttpServletRequest request, StringBuilder builder) {
        Map<String, String[]> map = request.getParameterMap();
        builder.append("===========parameters===============\n");
        map.entrySet().stream().map(e -> e.getKey() + '=' + Arrays.toString(e.getValue()))
                .forEach(e -> builder.append(e).append('\n'));
        builder.append("===========parameters===============\n");
    }

    private void appendHeaders(HttpServletRequest request, StringBuilder builder) {
        Enumeration<String> headerNames = request.getHeaderNames();
        builder.append("===========headers===============\n");
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            builder.append(headerName).append('=').append(request.getHeader(headerName)).append('\n');
        }
        builder.append("===========headers============\n");
    }

    private void appendMultiParts(HttpServletRequest request, StringBuilder builder) throws IOException {
        builder.append("===========multi parts============\n");
        try {
            request.getParts().forEach(part -> builder
                    .append(part.getName()).append(',').append(part.getContentType()).append('\n'));
        } catch (ServletException e) {
            builder.append("failed getting parts: ").append(e.getMessage()).append('\n');
        }
        builder.append("===========multi parts============\n");
    }
}
