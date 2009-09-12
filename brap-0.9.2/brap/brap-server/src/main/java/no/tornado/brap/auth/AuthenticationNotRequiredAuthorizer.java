package no.tornado.brap.auth;

import no.tornado.brap.common.InvocationRequest;
import no.tornado.brap.exception.AuthorizationFailedException;

/**
 * AthorizationProvider which authorizes all InvocationRequests.
 */
public class AuthenticationNotRequiredAuthorizer implements AuthorizationProvider {
    public void authorize(InvocationRequest invocationRequest) throws AuthorizationFailedException {
    }
}
