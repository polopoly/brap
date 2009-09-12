package no.tornado.brap.examples.spring;

import no.tornado.brap.client.ServiceProxyFactory;
import no.tornado.brap.examples.service.HelloService;
import no.tornado.brap.examples.service.MyService;
import no.tornado.brap.auth.UsernamePasswordPrincipal;

public class SpringClientExample {
    public static void main(String[] args) {
        UsernamePasswordPrincipal upp = new UsernamePasswordPrincipal("john", "secret");
        HelloService service = ServiceProxyFactory.createProxy(HelloService.class, "http://john:secretx@localhost:8080/remoting/hello", upp);
        System.out.println(service.sayHello("Hello"));
    }
}
