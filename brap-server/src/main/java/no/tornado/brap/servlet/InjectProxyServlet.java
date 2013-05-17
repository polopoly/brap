package no.tornado.brap.servlet;

import no.tornado.inject.ApplicationContext;

public class InjectProxyServlet extends ProxyServlet {

    protected Object getService() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String beanName = servletConfig.getInitParameter("beanName");

        if (beanName == null)
            throw new RuntimeException("You must provide the \"beanName\" init-param to the InjectProxyServlet.");

        return ApplicationContext.getBean(beanName);
    }
}
