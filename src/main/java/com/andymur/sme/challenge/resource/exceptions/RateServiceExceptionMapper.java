package com.andymur.sme.challenge.resource.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RateServiceExceptionMapper implements ExceptionMapper<RateServiceException> {

    @Override
    public Response toResponse(RateServiceException exception) {
        return Response.status(exception.getCode())
                .entity(exception.getMessage())
                .type(MediaType.TEXT_PLAIN)
                .build();
    }
}
