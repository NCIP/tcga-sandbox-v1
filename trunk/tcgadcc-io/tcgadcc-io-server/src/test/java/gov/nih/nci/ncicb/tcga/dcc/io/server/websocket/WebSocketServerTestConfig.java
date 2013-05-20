/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.server.websocket;

import gov.nih.nci.ncicb.tcga.dcc.io.server.ServerIOConfig;
import gov.nih.nci.ncicb.tcga.dcc.io.server.support.ConnectClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Java-based Spring configuration for testing server I/O components and services.
 * 
 * @author nichollsmc
 */
@Configuration
@Import(ServerIOConfig.class)
public class WebSocketServerTestConfig {

    /**
     * Default no-arg constructor. A requirement for classes annotated with
     * {@link Configuration}.
     */
    public WebSocketServerTestConfig() {
    }
    
    @Bean
    public ConnectClient connectClient() {
        return new ConnectClient();
    }
    
}