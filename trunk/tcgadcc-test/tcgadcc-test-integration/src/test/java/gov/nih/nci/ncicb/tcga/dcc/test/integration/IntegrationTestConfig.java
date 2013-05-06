/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.test.integration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Java-based Spring configuration for integration test components and services.
 * 
 * @author nichollsmc
 */
@Configuration
public class IntegrationTestConfig {

    /**
     * Default no-arg constructor. A requirement for classes annotated with {@link Configuration}.
     */
    public IntegrationTestConfig() {
    }
    
    @Bean
    public Greeter greeter() {
        return new Greeter();
    }
    
}
