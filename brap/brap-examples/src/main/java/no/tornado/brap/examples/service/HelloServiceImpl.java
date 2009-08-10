package no.tornado.brap.examples.service;

import no.tornado.brap.auth.AnonymousPrincipal;
import no.tornado.brap.auth.AuthenticationContext;
import no.tornado.brap.auth.UsernamePasswordPrincipal;

import java.io.Serializable;

public class HelloServiceImpl implements HelloService {
    public String sayHello(String name) {
        Serializable principal = AuthenticationContext.getPrincipal();
        if (principal instanceof UsernamePasswordPrincipal) {
            UsernamePasswordPrincipal upp = (UsernamePasswordPrincipal) AuthenticationContext.getPrincipal();
            return "Hello there, " + name + ", your username is " + upp.getUsername() + " and your password is " + upp.getPassword();
        } else if (principal instanceof AnonymousPrincipal) {
            return "Hello there, " + name + ", you are authenticated anonymously.";
        } else {
            return "Hello there, " + name;
        }


    }
}
