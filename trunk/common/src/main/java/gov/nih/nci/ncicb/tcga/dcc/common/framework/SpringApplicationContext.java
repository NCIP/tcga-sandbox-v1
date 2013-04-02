package gov.nih.nci.ncicb.tcga.dcc.common.framework;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Class which provides access to applicationContext.
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class SpringApplicationContext implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(final ApplicationContext ctx) throws BeansException {
        applicationContext = ctx;

    }

    public static Object getObject(final String name) {
        return applicationContext.getBean(name);
    }
}
