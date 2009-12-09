package no.tornado.brap.examples.spring;

import no.tornado.brap.client.ServiceProxyFactory;
import no.tornado.brap.common.UsernamePasswordPrincipal;
import no.tornado.brap.examples.service.HelloService;

import java.io.FileInputStream;
import java.io.IOException;

public class SpringClientExample {
    public static void main(String[] args) throws IOException {
        UsernamePasswordPrincipal upp = new UsernamePasswordPrincipal("john", "secret");
        HelloService service = ServiceProxyFactory.createProxy(HelloService.class, "http://localhost:8080/remoting/hello", upp);
        System.out.println(service.sayHello("Hello"));

        FileInputStream in = new FileInputStream("/Users/edvin/Movies/Video0001.3gp");
        service.sendLargeStream(in);
    }
}
