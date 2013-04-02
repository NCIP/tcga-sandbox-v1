package gov.nih.nci.ncicb.tcga.dcc.qclive.standalone;


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;


/**
 * Standalone application to schedule level2 jobs
 * <p/>
 * Command to run this application
 * Step1: generate xml file
 * java -jar  cache-generator.jar XML
 * or
 * java -jar  cache-generator.jar XML TCGAGBM ( to run this in production )
 * Step2: schedule level2 job
 * java -jar  cache-generator.jar JOB
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class CacheGeneratorApp {
    public static final String DATA_FILE = "./resources/job_data.xml";
    public static final String APP_FILE = "./resources/cache-generator-application-context.xml";

    public CacheGeneratorApp() {
    }

    public static void main(String[] args) {
        try {
            ApplicationContext applicationContext = new FileSystemXmlApplicationContext(APP_FILE);
            if (args.length < 1 ||
                    ((!args[0].equals("XML")) &&
                            (!args[0].equals("JOB")))) {
                System.out.println("Invalid arguments. \n java -jar  cache-generator.jar XML \n java -jar  cache-generator.jar JOB");
                System.exit(0);
            }
            String diseaseDBName = "";
            if (args.length == 2) {
                diseaseDBName = ((args[1] != null) && args[1].length() > 0) ? args[1] : "";
            }
            if (args[0].equals("XML")) {
                System.out.println(" Generating XML File started ...");
                XMLGenerator xmlGenerator = (XMLGenerator) applicationContext.getBean("xmlGenerator");
                xmlGenerator.generateDataXML(diseaseDBName);
                System.out.println(" Generating XML File completed.");
            } else if (args[0].equals("JOB")) {
                System.out.println(" Scheduling jobs started.");
                JOBGenerator jobGenerator = (JOBGenerator) applicationContext.getBean("jobGenerator");
                jobGenerator.scheduleCacheGeneratorJobs();
                System.out.println(" Scheduling jobs completed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }
}
