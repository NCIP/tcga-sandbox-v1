/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.test.integration;

import gov.nih.nci.ncicb.tcga.dcc.test.integration.Greeter;

import java.lang.invoke.MethodHandles;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.spring.integration.test.annotation.SpringAnnotationConfiguration;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Arquillian.class)
@SpringAnnotationConfiguration(classes = {IntegrationTestConfig.class})
public class GreeterTest {

    static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    @Deployment
    public static JavaArchive createDeployment() {
        JavaArchive deployment = ShrinkWrap.create(JavaArchive.class).addClasses(Greeter.class);
        
        logger.info(deployment.toString(true));
        
        return deployment;
    }

    @Inject
    Greeter greeter;

    @Test
    public void should_create_greeting() {
        Assert.assertEquals("Hello, Earthling!", greeter.createGreeting("Earthling"));
        greeter.greet(System.out, "Earthling");
    }

}
