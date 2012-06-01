package no.tornado.brap.examples.service;

import no.tornado.brap.auth.AnonymousPrincipal;
import no.tornado.brap.auth.AuthenticationContext;
import no.tornado.brap.common.UsernamePasswordPrincipal;

import java.io.*;

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

    public void sendLargeStream(InputStream in) throws IOException {
        FileOutputStream out = new FileOutputStream(System.getProperty("user.home") + File.separator + "in.stream");
        byte[] buf = new byte[16384];
        int len;
        while ((len = in.read(buf)) != -1) {
            out.write(buf, 0, len);
        }
        out.close();
        in.close();
    }
}
