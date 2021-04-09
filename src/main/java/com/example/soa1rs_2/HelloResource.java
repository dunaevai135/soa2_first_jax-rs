package com.example.soa1rs_2;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

@Path("/hello-world")
public class HelloResource {
    @GET
    @Produces("text/plain")
    public String hello(@Context HttpServletRequest request) {
        return String.valueOf(request.getParameterMap());
    }
}