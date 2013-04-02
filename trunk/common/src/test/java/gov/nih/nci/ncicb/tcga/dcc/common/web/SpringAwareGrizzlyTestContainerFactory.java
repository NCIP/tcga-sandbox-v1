package gov.nih.nci.ncicb.tcga.dcc.common.web;

import java.io.IOException;
import java.net.URI;
import java.util.EventListener;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import javax.ws.rs.core.UriBuilder;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.sun.grizzly.http.embed.GrizzlyWebServer;
import com.sun.grizzly.http.servlet.ServletAdapter;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.apache.config.ApacheHttpClientConfig;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;
import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.WebAppDescriptor;
import com.sun.jersey.test.framework.WebAppDescriptor.FilterDescriptor;
import com.sun.jersey.test.framework.spi.container.TestContainer;
import com.sun.jersey.test.framework.spi.container.TestContainerException;
import com.sun.jersey.test.framework.spi.container.TestContainerFactory;

/**
 * Custom Spring aware Jersey {@link TestContainerFactory} that exposes the
 * resources loaded into the Spring context by the Grizzly container.
 * 
 * @author nichollsmc
 */
public class SpringAwareGrizzlyTestContainerFactory implements TestContainerFactory {

    private Object springTarget;

    public <T> SpringAwareGrizzlyTestContainerFactory(final T springTarget) {
        this.springTarget = springTarget;
    }

    @Override
    public Class<WebAppDescriptor> supports() {
        return WebAppDescriptor.class;
    }

    @Override
    public TestContainer create(final URI baseUri, final AppDescriptor appDescriptor) {
        assertWebAppDescriptor(appDescriptor);
        return new SpringAwareGrizzlyWebTestContainer(baseUri, (WebAppDescriptor) appDescriptor, springTarget);
    }

    private void assertWebAppDescriptor(final AppDescriptor appDescriptor) {
        if (!(appDescriptor instanceof WebAppDescriptor)) {
            throw new IllegalArgumentException("The application descriptor must be an instance of WebAppDescriptor");
        }
    }

    private static class SpringAwareGrizzlyWebTestContainer implements TestContainer {

        private static final Logger log = Logger.getLogger(SpringAwareGrizzlyWebTestContainer.class.getName());

        private URI baseUri;
        private GrizzlyWebServer grizzlyWebServer;
        private Object springTarget;
        private Servlet servletInstance;

        private <T> SpringAwareGrizzlyWebTestContainer(
                final URI baseUri, 
                final WebAppDescriptor webAppDescriptor, 
                final T springTarget) {
            
            this.springTarget = springTarget;
            this.baseUri = 
                    UriBuilder
                    .fromUri(baseUri)
                    .path(webAppDescriptor.getContextPath())
                    .path(webAppDescriptor.getServletPath())
                    .build();

            log.info("Creating Grizzly Web Container configured for base URI '" + this.baseUri + "'");

            instantiateGrizzlyWebServer(webAppDescriptor, springTarget);
        }

        @Override
        public Client getClient() {
            final DefaultApacheHttpClientConfig config = new DefaultApacheHttpClientConfig();
            config.getProperties().put(ApacheHttpClientConfig.PROPERTY_HANDLE_COOKIES, true);
            final ApacheHttpClient client = ApacheHttpClient.create(config);
            return client;
        }

        @Override
        public URI getBaseUri() {
            return baseUri;
        }

        @Override
        public void start() {

            log.info("Starting the Grizzly Web Container...");

            try {
                grizzlyWebServer.start();
                autoWireSpringTarget();
            }
            catch (IOException ex) {
                throw new TestContainerException(ex);
            }

        }

        @Override
        public void stop() {

            log.info("Stopping the Grizzly Web Container...");

            grizzlyWebServer.stop();
            grizzlyWebServer.getSelectorThread().stopEndpoint();
        }

        private <T> void instantiateGrizzlyWebServer(final WebAppDescriptor webAppDescriptor, final T springTarget) {
            grizzlyWebServer = new GrizzlyWebServer(baseUri.getPort());
            final ServletAdapter servletAdapter = new ServletAdapter();
            servletAdapter.setProperty("load-on-startup", 1);
            servletInstance = createrServletInstance(webAppDescriptor.getServletClass());
            servletAdapter.setServletInstance(servletInstance);

            populateEventListeners(servletAdapter, webAppDescriptor.getListeners());
            populateFilterDescriptors(servletAdapter, webAppDescriptor.getFilters());
            populateContextParams(servletAdapter, webAppDescriptor.getContextParams());
            populateInitParams(servletAdapter, webAppDescriptor.getInitParams());
            setContextPath(servletAdapter, webAppDescriptor.getContextPath());
            setServletPath(servletAdapter, webAppDescriptor.getServletPath());

            String[] mapping = null;
            grizzlyWebServer.addGrizzlyAdapter(servletAdapter, mapping);

        }

        private void setServletPath(final ServletAdapter servletAdapter, final String servletPath) {
            if (notEmpty(servletPath)) {
                servletAdapter.setServletPath(servletPath);
            }
        }

        private void setContextPath(final ServletAdapter servletAdpater, final String contextPath) {
            if (notEmpty(contextPath)) {
                servletAdpater.setContextPath(ensureLeadingSlash(contextPath));
            }
        }

        private boolean notEmpty(final String string) {
            return string != null && string.length() > 0;
        }

        private void populateInitParams(final ServletAdapter servletAdapter,
                final Map<String, String> initParams) {
            for (String initParamName : initParams.keySet()) {
                servletAdapter.addInitParameter(initParamName, initParams.get(initParamName));
            }

        }

        private void populateContextParams(final ServletAdapter servletAdapter, final Map<String, String> contextParams) {
            for (String contextParamName : contextParams.keySet()) {
                servletAdapter.addContextParameter(contextParamName, contextParams.get(contextParamName));
            }
        }

        private void populateFilterDescriptors(final ServletAdapter servletAdapter, final List<FilterDescriptor> filters) {
            if (filters != null) {
                for (WebAppDescriptor.FilterDescriptor d : filters) {
                    servletAdapter.addFilter(instantiate(d.getFilterClass()), d.getFilterName(), d.getInitParams());
                }
            }
        }

        private void populateEventListeners(final ServletAdapter servletAdapter, final List<Class<? extends EventListener>> listeners) {
            for (Class<? extends EventListener> eventListener : listeners) {
                servletAdapter.addServletListener(eventListener.getName());
            }
        }

        private String ensureLeadingSlash(final String string) {
            return (string.startsWith("/") ? string : "/".concat(string));
        }

        private Servlet createrServletInstance(final Class<? extends HttpServlet> servletClass) {
            return (servletClass == null ? new SpringServlet() : instantiate(servletClass));
        }

        private <I> I instantiate(final Class<? extends I> clazz) {
            I instance = null;
            try {
                instance = clazz.newInstance();
            }
            catch (InstantiationException e) {
                throw new TestContainerException(e);
            }
            catch (IllegalAccessException e) {
                throw new TestContainerException(e);
            }

            return instance;
        }

        private void autoWireSpringTarget() {
            WebApplicationContext webApplicationContext = WebApplicationContextUtils
                    .getRequiredWebApplicationContext(servletInstance.getServletConfig()
                            .getServletContext());
            AutowireCapableBeanFactory beanFactory = webApplicationContext.getAutowireCapableBeanFactory();
            beanFactory.autowireBean(springTarget);
        }

    }

}
