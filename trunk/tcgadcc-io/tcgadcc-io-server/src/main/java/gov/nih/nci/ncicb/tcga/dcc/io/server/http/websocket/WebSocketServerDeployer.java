/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket;

import gov.nih.nci.ncicb.tcga.dcc.io.api.IoApiConfig;
import gov.nih.nci.ncicb.tcga.dcc.io.api.IoApiConfigProfileType;
import gov.nih.nci.ncicb.tcga.dcc.io.api.IoApiSecurityConfig;
import gov.nih.nci.ncicb.tcga.dcc.io.server.IoServerConfig;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Configures and starts a WebSocket server instance.
 * 
 * @author nichollsmc
 */
public class WebSocketServerDeployer {

    private AnnotationConfigApplicationContext applicationContext;

    private Class<?>[] configClasses = { IoApiConfig.class, 
                                         IoApiSecurityConfig.class,
                                         IoServerConfig.class };

    public WebSocketServerDeployer() {
        applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.registerShutdownHook();
        applicationContext.getEnvironment().addActiveProfile(IoApiConfigProfileType.TLS_DISABLED_PROFILE_NAME);
        applicationContext.register(configClasses);
        applicationContext.refresh();
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void main(String... args) throws Exception {
        assertPreconditions();
        WebSocketServerDeployer deployer = new WebSocketServerDeployer();
    }

    public static void assertPreconditions() {
        String keyStoreFilePath = System.getProperty("keystore.file.path");
        if (keyStoreFilePath == null || keyStoreFilePath.isEmpty()) {
            System.out.println("ERROR: System property keystore.file.path not set. Exiting now!");
            System.exit(1);
        }

        String keyStoreFilePassword = System.getProperty("keystore.file.password");
        if (keyStoreFilePassword == null || keyStoreFilePassword.isEmpty()) {
            System.out.println("ERROR: System property keystore.file.password not set. Exiting now!");
            System.exit(1);
        }
    }

}
