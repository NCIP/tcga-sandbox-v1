package gov.nih.nci.ncicb.tcga.dcc.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses protocol names as they currently appear in SDRF/IDF files.
 * Moved from QCLive, where it was called ProtocolNameValidator.
 *
 * @author Robert S. Sfeir
 */
public class ProtocolNameParser {

    private static final String thePattern = "([a-zA-Z0-9\\-_.]+)[:]+([a-zA-Z0-9\\-_]+)[:]+([a-zA-Z0-9\\-_]+)[:]+([0-9]+)";
    private String domain = null;
    private String protocolType = null;
    private String platform = null;
    private String version = null;
    private final int groupCount;

    public ProtocolNameParser(final String protocolName) {
        final Pattern pattern = Pattern.compile( thePattern );
        final Matcher matcher = pattern.matcher( protocolName );
        if(matcher.matches()) {
            groupCount = matcher.groupCount();
            domain = matcher.group( 1 );
            protocolType = matcher.group( 2 );
            platform = matcher.group( 3 );
            version = matcher.group( 4 );
        } else {
            groupCount = 0;
        }
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
}

