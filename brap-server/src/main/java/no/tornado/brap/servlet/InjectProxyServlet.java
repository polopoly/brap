package no.tornado.brap.servlet;

import no.tornado.brap.auth.AuthenticationProvider;
import no.tornado.brap.auth.AuthorizationProvider;
import no.tornado.inject.ApplicationContext;

public class InjectProxyServlet extends ProxyServlet {

    public void createServiceWrapper() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        super.createServiceWrapper();

        String authenticationProviderBean = servletConfig.getInitParameter("authenticationProviderBeanName");
        if (authenticationProviderBean != null)
            serviceWrapper.setAuthenticationProvider((AuthenticationProvider) ApplicationContext.getBean(authenticationProviderBean));

        String authorizationProviderBean = servletConfig.getInitParameter("authorizationProviderBeanName");
        if (authorizationProviderBean != null)
            serviceWrapper.setAuthorizationProvider((AuthorizationProvider) ApplicationContext.getBean(authorizationProviderBean));
    }

    protected Object getService() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String beanName = servletConfig.getInitParameter("beanName");

        if (beanName == null)
            throw new RuntimeException("You must provide the \"beanName\" init-param to the InjectProxyServlet.");

        return ApplicationContext.getBean(beanName);
    }
}
