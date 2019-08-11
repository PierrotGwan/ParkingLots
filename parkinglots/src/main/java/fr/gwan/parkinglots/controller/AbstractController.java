package fr.gwan.parkinglots.controller;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractController {

    protected final ObjectMapper objectMapper;

    protected final HttpServletRequest request;

    public AbstractController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }
    protected Optional<ObjectMapper> getObjectMapper() {
        return Optional.ofNullable(objectMapper);
    }

    protected Optional<HttpServletRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    protected Optional<String> getAcceptHeader() {
        return getRequest().map(r -> r.getHeader("Accept"));
    }
}