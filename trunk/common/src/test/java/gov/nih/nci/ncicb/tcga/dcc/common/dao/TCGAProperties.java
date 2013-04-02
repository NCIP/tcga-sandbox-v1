/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Description :   Class mainly used to read the test database credentials for executing dbUnit test cases
 *
 * @author Namrata Rane Last updated by: $Author$
 * @version $Rev$
 */
public class TCGAProperties {

    private Properties properties;
    private String propertyFilePath;
    private String propertyFileName;

    private TCGAProperties() {

    }

    private TCGAProperties(String filePath, String fileName) {
        propertyFilePath = filePath;
        propertyFileName = fileName;

    }

    private static TCGAProperties tcgaProperties;

    private void init() throws IOException {

        FileInputStream fileInputStream = null;
        try {
            properties = new Properties();
            //noinspection IOResourceOpenedButNotSafelyClosed
            fileInputStream = new FileInputStream(new File(propertyFilePath, propertyFileName));
            properties.load(fileInputStream);
        } finally {
            IOUtils.closeQuietly(fileInputStream);
        }
    }

    public static TCGAProperties getInstance(String filePath, String fileName) throws IOException {
        if (tcgaProperties == null) {
            tcgaProperties = new TCGAProperties(filePath, fileName);
            tcgaProperties.init();
        }
        return tcgaProperties;
    }


    public String getProperty(String propertyName) throws IOException {
        return properties.getProperty(propertyName, "");
    }

}

