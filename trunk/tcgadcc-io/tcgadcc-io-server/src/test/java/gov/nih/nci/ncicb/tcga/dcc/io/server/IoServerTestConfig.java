package gov.nih.nci.ncicb.tcga.dcc.io.server;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Java-based Spring configuration for testing server I/O components and services.
 * 
 * @author nichollsmc
 */
@Configuration
@Import(IoServerConfig.class)
public class IoServerTestConfig {
    
    /**
     * Default no-arg constructor. A requirement for classes annotated with
     * {@link Configuration}.
     */
    public IoServerTestConfig() {
    }
    
}
