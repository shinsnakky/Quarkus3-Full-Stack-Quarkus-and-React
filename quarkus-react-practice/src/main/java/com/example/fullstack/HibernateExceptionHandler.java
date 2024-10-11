package com.example.fullstack;

import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.StaleObjectStateException;
import org.hibernate.exception.ConstraintViolationException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Optional;

@Provider
public class HibernateExceptionHandler
    implements ExceptionMapper<HibernateException> {

    @Override
    public Response toResponse(HibernateException exception) {
        if (hasExceptionInChain(exception, ObjectNotFoundException.class)) {
            return Response
                .status(Response.Status.NOT_FOUND)
                .entity(exception.getMessage())
                .build();
        }
        if (hasExceptionInChain(exception, StaleObjectStateException.class)) {
            return Response
                .status(Response.Status.CONFLICT)
                .entity(exception.getMessage())
                .build();
        }
        if (hasExceptionInChain(
            exception,
            ConstraintViolationException.class)
        ) {
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

    private static boolean hasExceptionInChain(
        Throwable throwable,
        Class<? extends Throwable> exceptionClass
    ) {
        return getExceptionInChain(throwable, exceptionClass).isPresent();
    }

    private static <T extends Throwable> Optional<Throwable> getExceptionInChain(
        Throwable throwable,
        Class<T> exceptionClass
    ) {
        while (throwable != null) {
            if (exceptionClass.isInstance(throwable)) {
                return Optional.of(throwable);
            }
            throwable = throwable.getCause();
        }
        return Optional.empty();
    }
}
