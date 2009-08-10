package no.tornado.brap.examples.spring;

import no.tornado.brap.client.ServiceProxyFactory;
import no.tornado.brap.examples.service.HelloService;

public class SpringClientExample {
    public static void main(String[] args) {
        HelloService service = (HelloService) ServiceProxyFactory.
                createProxy(HelloService.class, "http://john:secretx@localhost:8080/remoting/hello");
    }
}
