package gov.nih.nci.ncicb.tcga.dcc.common.web;

import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import com.sun.jersey.test.framework.spi.container.TestContainerException;
import com.sun.jersey.test.framework.spi.container.TestContainerFactory;

/**
 * Test classes that use the Jersey test framework can extend this class to
 * obtain Spring resources loaded by the Grizzly container using the
 * {@link org.springframework.beans.factory.annotation.Autowired} annotation.
 * <p>
 * In future versions of the Jersey test framework, a more direct method of
 * obtaining Spring configured resources loaded by the test container may be
 * available. For now, this class and its companion class
 * {@link SpringAwareGrizzlyTestContainerFactory} can be used.
 * 
 * @author nichollsmc
 */
public abstract class AbstractSpringAwareGrizzlyJerseyTest extends JerseyTest {

    /**
     * Constructs a Spring aware Grizzly test container using the provided
     * {@link WebAppDescriptor}.
     * 
     * @param webAppDescriptor
     *            - an instance of {@link WebAppDescriptor} used to configure
     *            the Grizzly container
     */
    public AbstractSpringAwareGrizzlyJerseyTest(WebAppDescriptor webAppDescriptor) {
        super(webAppDescriptor);
    }

    /**
     * Returns a new instance of the
     * {@link SpringAwareGrizzlyTestContainerFactory} that is used by the Jersey
     * test framework to create a Grizzly test container for instances of this
     * class and its subclasses.
     */
    protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
        return new SpringAwareGrizzlyTestContainerFactory(this);
    }

}
