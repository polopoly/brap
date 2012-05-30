package no.tornado.brap.auth;

import no.tornado.brap.common.InvocationRequest;

/**
 * AuthenticationProvider for not requiring authentication.
 */
public class AuthenticationNotRequiredAuthenticator implements AuthenticationProvider {
    public void authenticate(InvocationRequest invocationRequest) {
    }
}
