/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.web.editor;

import java.beans.Encoder;
import java.beans.Expression;
import java.beans.PersistenceDelegate;

/**
 * UUID Persistence Delegate to allow for proper xml encode of UUID when serialization occurs.
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class UUIDPersistenceDelegate extends PersistenceDelegate {
    
    public UUIDPersistenceDelegate() {
    }

    protected boolean mutatesTo(Object oldObject, Object newObject) {
        return (oldObject.equals(newObject));
    }

    @Override
    protected Expression instantiate(Object oldInstance, Encoder out) {
        return new Expression(oldInstance, oldInstance.getClass(),
                "fromString", new Object[]{oldInstance.toString()});
    }

}//End of Class
