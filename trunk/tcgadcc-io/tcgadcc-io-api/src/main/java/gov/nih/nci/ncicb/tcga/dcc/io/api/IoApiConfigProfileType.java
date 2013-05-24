/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.api;

/**
 * Enumeration of I/O application configuration environments.
 * 
 * @author nichollsmc
 */
public enum IoApiConfigProfileType {

    // -- Enumeration Types -- //

    /**
     * Development profile enumeration type.
     */
    DEVELOPMENT_PROFILE_TYPE(IoApiConfigProfileType.DEVELOPMENT_PROFILE_NAME),

    /**
     * Production profile enumeration type.
     */
    PRODUCTION_PROFILE_TYPE(IoApiConfigProfileType.PRODUCTION_PROFILE_NAME),

    /**
     * QA profile enumeration type.
     */
    QA_PROFILE_TYPE(IoApiConfigProfileType.QA_PROFILE_NAME),

    /**
     * Test profile enumeration type.
     */
    TEST_PROFILE_TYPE(IoApiConfigProfileType.TEST_PROFILE_NAME),

    /**
     * TLS enabled profile enumeration type.
     */
    TLS_ENABLED_PROFILE_TYPE(IoApiConfigProfileType.TLS_ENABLED_PROFILE_NAME),

    /**
     * TLS disabled profile enumeration type.
     */
    TLS_DISABLED_PROFILE_TYPE(IoApiConfigProfileType.TLS_DISABLED_PROFILE_NAME);

    // -- Constants -- //

    /**
     * Development profile name constant.
     */
    public static final String DEVELOPMENT_PROFILE_NAME  = "dev";

    /**
     * Production profile name constant.
     */
    public static final String PRODUCTION_PROFILE_NAME   = "prod";

    /**
     * QA profile name constant.
     */
    public static final String QA_PROFILE_NAME           = "qa";

    /**
     * Test profile name constant.
     */
    public static final String TEST_PROFILE_NAME         = "test";

    /**
     * TLS enabled profile name constant.
     */
    public static final String TLS_ENABLED_PROFILE_NAME  = "tls-enabled";

    /**
     * TLS disabled name constant.
     */
    public static final String TLS_DISABLED_PROFILE_NAME = "tls-disabled";

    private String profileName;

    private IoApiConfigProfileType(String profileName) {
        this.profileName = profileName;
    }

    /**
     * Returns the profile name of a {@link IoApiConfigProfileType}.
     * 
     * @return the profile name of a {@link IoApiConfigProfileType}
     */
    public String profileName() {
        return profileName;
    }

}
