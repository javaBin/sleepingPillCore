package no.java.sleepingpill.core.exceptions;

import no.java.sleepingpill.core.ServiceResult;

public class ServiceResultException extends RuntimeException {
    private final ServiceResult serviceResult;
    public ServiceResultException(ServiceResult serviceResult) {
        super();
        this.serviceResult = serviceResult;
    }

    public ServiceResult getServiceResult() {
        return serviceResult;
    }
}
