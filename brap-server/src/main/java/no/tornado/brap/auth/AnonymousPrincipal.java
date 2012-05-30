package no.tornado.brap.auth;

import java.io.Serializable;

/**
 * Default principal that will be returned from the <code>AuthenticationContext</code>
 * if the active <code>AuthenticationProvider</code> does not set another one. 
 */
public class AnonymousPrincipal implements Serializable {
    public String getName() {
        return getClass().getCanonicalName();
    }
}
