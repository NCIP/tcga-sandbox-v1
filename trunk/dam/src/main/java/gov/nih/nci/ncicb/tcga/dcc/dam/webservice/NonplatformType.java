/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.webservice;

/**
 * Created this enum to identify the non platform type such as clinical
 * There is a pseudo platform type associated with this as "-999" for search purposes
 *
 * @author saraswatv
 *         Last updated by: $Author$
 * @version $Rev$
 */
public enum NonplatformType {
     NONPLATFORMTYPE_CLINICAL("c") ;

       private String nonplatformTypeIdentifier;
       private String associatedPseudoPlatformType;

       NonplatformType(final String identifier)
       {
           this.nonplatformTypeIdentifier = identifier;
           this.associatedPseudoPlatformType = "-999";
       }

       public String getNonplatformTypeIdentifier()
       {
           return this.nonplatformTypeIdentifier;
       }

       public String getAssociatedPseudoPlatformType()
       {
            return this.associatedPseudoPlatformType;
       }

}
