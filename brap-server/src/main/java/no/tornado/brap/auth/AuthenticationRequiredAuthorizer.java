package no.tornado.brap.auth;

import no.tornado.brap.common.InvocationRequest;
import no.tornado.brap.exception.AuthorizationFailedException;

/**
 * This authenticator authorizes all authenticated requests.
 *
 */
public class AuthenticationRequiredAuthorizer implements AuthorizationProvider {
    public void authorize(InvocationRequest invocationRequest) throws AuthorizationFailedException {
        if (AuthenticationContext.getPrincipal() == null)
            throw new AuthorizationFailedException("Please supply valid credentials and try again.");
    }
}
