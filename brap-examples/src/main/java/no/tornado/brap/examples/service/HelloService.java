package no.tornado.brap.examples.service;

import java.io.IOException;
import java.io.InputStream;

public interface HelloService {
    public String sayHello(String name);
    public void sendLargeStream(InputStream in) throws IOException;
}
