/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.api;

import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * Java-based Spring configuration for shared I/O security components and
 * services.
 * 
 * @author nichollsmc
 */
@Configuration
@Profile(IoApiConfigProfileType.TLS_ENABLED_PROFILE_NAME)
@PropertySource("classpath:properties/io-config-security.properties")
public class IoApiSecurityConfig {

    private final static Logger log = LoggerFactory.getLogger(IoApiSecurityConfig.class);

    @Value("${security.ssl.protocol}")
    private String sslProtocol;

    @Value("${security.ssl.KeyManagerFactory.algorithm}")
    private String sslKeyManagerFactoryAlgorithm;
    
    @Value("#{systemProperties['keystore.file.path']}")
    private String keyStoreFilePath;

    @Value("#{systemProperties['keystore.file.password']}")
    private String keyStoreFilePassword;
    
    /**
     * Default no-arg constructor. A requirement for classes annotated with
     * {@link Configuration}.
     */
    public IoApiSecurityConfig() {
    }

    @Bean
    public SSLContext sslContext() {
        SSLContext sslContext = null;

        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            log.info("Using keystore file: " + keyStoreFilePath);
            log.info("Using keystore password: " + keyStoreFilePassword);
            
            keyStore.load(new FileInputStream(keyStoreFilePath), keyStoreFilePassword.toCharArray());

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(sslKeyManagerFactoryAlgorithm);
            keyManagerFactory.init(keyStore, keyStoreFilePassword.toCharArray());

            sslContext = SSLContext.getInstance(sslProtocol);
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);
        }
        catch (Exception e) {
            log.error("Error initializing SslContextManager.", e);
        }

        return sslContext;
    }

}
