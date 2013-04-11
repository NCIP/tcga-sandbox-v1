=============================================
Building TCGA DCC Source
=============================================


=============================================
Prerequisites
=============================================

    =============================================
    Install the latest 1.7 JDK
    =============================================

    	Follow the instructions provided in the following wiki page to install and configure the JDK:
    
        	https://wiki.nci.nih.gov/x/Ggo9Ag
    
    =============================================
    Install Apache Maven 3
    =============================================

        Follow the instructions provided in the following wiki page to install and configure Maven 3: 
    
            https://wiki.nci.nih.gov/x/-wc9Ag
    
    =============================================
    Install Tomcat 7.0.3x
    =============================================

        Download Tomcat 7.0.3x:
    
            https://tomcat.apache.org/download-70.cgi
        
        and follow these instructions for installation: 
    
            https://tomcat.apache.org/tomcat-7.0-doc/RUNNING.txt
    
    =============================================
    Install Subversion
    =============================================

        Install a Subversion client that is compatible with your host operating system.
    
    =============================================
    Checkout TCGA DCC Source from Subversion
    =============================================

        The TCGA DCC source URL is TBD


=============================================
Maven Build Commands
=============================================

    *NOTE: A 'full build' in the context of the build commands detailed below includes database updates and the execution of unit tests.*

    =============================================
    Full Build, Including Environment Setup
    =============================================

        To perform a full build and environment setup run the following command:
    
            mvn -Dsetup clean install
        
        Providing the '-Dsetup' parameter will in affect do the following:
        
            - Update database schemas
            - Configure JBoss
            - Copy application specific configuration files to target machine file system. The location used for
              copying configuration files can be specified by the '-Dprops.loc=/alternate/path' parameter. The 
              default is '/local/content/tcga/conf'.
	
    !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    !!!!																												!!!!
    !!!!            WARNING: Using the '-Dsetup' parameter will overwrite existing JBoss and application specific       !!!!
    !!!!                     configuration files, and drop ALL database objects for the schemas configured in           !!!!
    !!!!                     the settings.xml file. It should only be used when running a build for the first time      !!!!
    !!!!                     or refreshing an existing environment with default settings.                               !!!!
    !!!!																												!!!!
    !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    
    =============================================
    Full Build without JBoss Configuration
    =============================================

        To perform a full build with an already configured JBoss installation, simply remove the '-Dsetup' parameter:

            mvn clean install
    
    =============================================
    Building a Single Module or Set of Modules
    =============================================

        Most of the time, performing a full build of all the modules is not necessary, and only a certain sub-set of modules need 
        to be built. To build a single module, use the following command:
    
            mvn -pl :<module-name> clean install
    
        where <module-name> is replaced with the name of the module being built.
    
        Multiple modules can be specified using commas:
    
            mvn -pl :<module-name-1>,:<module-name-2>,... clean install
    
        The full list of module names are specified in the <modules>...</modules> section of the parent POM
    
    =============================================
    Resuming a Build from a Specific Module
    =============================================

        There will more than likely be build failures within one or two modules when doing a full build. Rather than re-building all the
        modules after the cause of the failure has been fixed, you can resume the build from the module that failed by using the '-rf' 
        (resume from) switch:
        
            mvn -rf <module-name> clean install
            
        The '-rf' parameter is also useful for 'skipping' upstream modules that take a long time to build and are not necessary for building
        certain downstream modules. For example, you can can skip the tcgadcc-database module that performs database updates and just build 
        the remaining modules like so:
        
            mvn -rf tcgadcc-dependencies clean install

    =============================================
    Skipping Unit Tests During a Build
    =============================================

        To skip the unit tests during a build, simply provide the '-DskipTests=true' parameter:
        
            mvn -rf my-module -DskipTests=true clean install
        
    =============================================
    Stopping and Starting Tomcat
    =============================================

        To start the Tomcat container for build artifacts (WAR, EAR, etc.) that are deployed to Tomcat:
        
            TBD
        
        To stop the Tomcat container:
        
            TBD
    
    =============================================
    Full Build, Configure and Start Tomcat
    =============================================

        TBD
        
