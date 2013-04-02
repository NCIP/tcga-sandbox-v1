/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Robert S. Sfeir
 */
public class ProtocolNameValidator {

    private static String REGEXP_DESCRIPTION = "domain:protocol:platform:version";

    private static final String REGEXP = "([a-zA-Z0-9\\-_.]+)[:]+([a-zA-Z0-9\\-_]+)[:]+([a-zA-Z0-9\\-_]+)[:]+([0-9]+)";
    private static final Pattern PATTERN = Pattern.compile(REGEXP);

    private String domain = null;
    private String protocolType = null;
    private String platform = null;
    private String version = null;
    private int groupCount;
    private boolean valid;

    public ProtocolNameValidator(final String protocolName) {
        final Matcher matcher = PATTERN.matcher(protocolName);
        if (matcher.matches()) {
            setValid(true);

            groupCount = matcher.groupCount();
            domain = matcher.group(1);
            protocolType = matcher.group(2);
            platform = matcher.group(3);
            version = matcher.group(4);
        } else {
            groupCount = 0;
        }
    }

    public static boolean isValid(final String input) {
        
        final Matcher matcher = PATTERN.matcher(input);
        return matcher.matches();
    }

    public int getGroupCount() {
        return groupCount;
    }

    public String getDomain() {
        return domain;
    }

    public String getProtocolType() {
        return protocolType;
    }

    public String getPlatform() {
        return platform;
    }

    public String getVersion() {
        return version;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(final boolean valid) {
        this.valid = valid;
    }

    public static String getDescription() {
        return REGEXP_DESCRIPTION;
    }
}
