package com.example.soa1rs_2;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class CORSFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext request,
                       ContainerResponseContext response) {
        response.getHeaders().add("Access-Control-Allow-Origin", "*");
        response.getHeaders().add("Access-Control-Allow-Headers", "Authorization, Content-Type," +
                "X-Auth-Token, Access-Control-Allow-Origin, Access-Control-Allow-Credentials");
        response.getHeaders().add("Access-Control-Allow-Credentials", "true");
        response.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PATCH, DELETE, HEAD");

        if (response.getHeaders().get("Access-Control-Allow-Origin").size() > 1) {
            response.getHeaders().remove("Access-Control-Allow-Origin");
            response.getHeaders().add("Access-Control-Allow-Origin", "*");
        }
        if (response.getHeaders().get("Access-Control-Allow-Headers").size() > 1) {
            response.getHeaders().remove("Access-Control-Allow-Headers");
            response.getHeaders().add("Access-Control-Allow-Headers", "Authorization, Content-Type," +
                    "X-Auth-Token, Access-Control-Allow-Origin, Access-Control-Allow-Credentials");
        }
        if (response.getHeaders().get("Access-Control-Allow-Credentials").size() > 1) {
            response.getHeaders().remove("Access-Control-Allow-Credentials");
            response.getHeaders().add("Access-Control-Allow-Credentials", "true");
        }
        if (response.getHeaders().get("Access-Control-Allow-Methods").size() > 1) {
            response.getHeaders().remove("Access-Control-Allow-Methods");
            response.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PATCH, DELETE, HEAD");
        }
    }
}