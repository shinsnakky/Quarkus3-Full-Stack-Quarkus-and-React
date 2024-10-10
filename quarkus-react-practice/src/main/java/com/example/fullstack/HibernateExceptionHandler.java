package com.example.fullstack;

import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.exception.ConstraintViolationException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class HibernateExceptionHandler
    implements ExceptionMapper<HibernateException> {

    @Override
    public Response toResponse(HibernateException exception) {
        if (exception instanceof ObjectNotFoundException) {
            return Response
                .status(Response.Status.NOT_FOUND)
                .entity(exception.getMessage())
                .build();
        }

        if (exception instanceof ConstraintViolationException) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(exception.getMessage())
                    .build();
        }

        return Response
            .status(Response.Status.BAD_REQUEST)
            .entity("\"" + exception.getMessage() + "\"")
            .build();
    }

}
