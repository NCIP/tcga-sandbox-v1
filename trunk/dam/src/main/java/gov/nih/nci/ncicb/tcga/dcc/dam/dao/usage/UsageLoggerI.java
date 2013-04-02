package gov.nih.nci.ncicb.tcga.dcc.dam.dao.usage;

import java.util.Map;

/**
 * Usage logger interface.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface UsageLoggerI {
    public void logAction(String sessionKey, String actionName, Object value) throws UsageLoggerException;
    public void logActionGroup(String sessionKey, Map<String, Object> actions) throws UsageLoggerException;
}
