package gov.nih.nci.ncicb.tcgaportal.level4.dao.dbunit.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Description :   Class mainly used to read the test database credentials
 * for executing dbUnit test cases
 *
 * @author Namrata Rane
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class TCGAProperties {

    private Properties properties;
    private static final String propertyFilePath = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private String propertyFileName = propertyFilePath + "databrowserDbunitTest.properties";

    private static TCGAProperties tcgaProperties;

    private void init() throws IOException {
        properties = new Properties();
        try {
            properties.load(new FileInputStream(new File(propertyFileName)));
        } catch (IOException e) {
            throw new IOException("Property file " + propertyFileName + " not found !");
        }
    }

    public static TCGAProperties getInstance() throws IOException {
        if (tcgaProperties == null)
            tcgaProperties = new TCGAProperties();
        tcgaProperties.init();
        return tcgaProperties;
    }


    public String getProperty(String propertyName) throws IOException {
        return properties.getProperty(propertyName, "");
    }

}

