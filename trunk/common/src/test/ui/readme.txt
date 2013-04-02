UI Testing with Selenium for TCGA

Introduction
------------
The UI tests here are all created using the Selenium IDE for Firefox.  You can read about the IDE at:

    http://seleniumhq.org/projects/ide/

The IDE is a plugin for Firefox and can be used to create and run test cases.  It will also record
your actions and turn them into a set of tests.  The tests are then stored as HTML and can later be
loaded back into the IDE and run.  The tests can also be saved in various programming languages,
but those require some tweaking to work.

For the current tests, all testing is done in Firefox as it is common across the Mac and PC
platforms.  The tests also run in IE, Safari, and Chrome.  Changing to an alternate browser
requires modifying the *firefox entry in the .sh or .bat file to the desired alternate browser.  
Here are some of the options:

*firefox3 - Firefox, whichever version 3 you have
*googlechrome - the Chrome browser (confusingly *chrome starts firefox)
*iexplore - IE
*safari - Safari
*opera - yeah, if you really want to go for it, it's there

The tests assume that you are running on localhost:8080.  If you are not, please modify the .bat
or .sh file accordingly.

The tests are stored in the Common project in the src/test/ui directory.

Testing a UI
------------
For the purposes of regular testing, tests will be run from the command line.  There are bat 
files for running in standard PC command mode.  There are also shell files for running in CygWin 
and on the Mac.  

To run a test, start up the target application and then head to the command line.  Go to the 
directory the tests are in.  There, just run the .bat or .sh file you are interested in.  For 
instance, to test Annotations on the Mac or in Cygwin:

./testAnnotations.sh

Two browser windows will then be brought up.  One will contain the controls for the test and 
the other will contain the pages being tested.

Note that, as we have invalid certificates for the UUID Manager and Annotations on our localhosts
the test will initially fail.  If you confirm an exception for the certificate and restart the
test in the control window, the tests should run.

If some of the tests fail, it may be that they are running too quickly.  You can go into the 
browser with the Selenium controls, move the speed slider to the right and slow things down.  
Then hit the green arrow on the control screen to run the tests again.

On an unfortunate note, the test from the command line does not exit cleanly except for one flavor
of the IE specifier (*iexploreproxy).  You will need to Ctrl-C to close out the test after it has
finished.

All of the tests work this way, however, testDataReports.sh reports 404 errors on opening the 
pages.  This is still being investigated and the rest of the tests in the suite work.

Alternately...

If you would like to run the tests in the Selenium IDE, first, install the Selenium IDE plugin 
in Firefox.  Next, start up Firefox and open up the IDE from the Tools menu.  In the IDE's File 
menu, select Open Test Suite.  Navigate to the appropriate directory and select one of the ts* 
files, such as tsAnnotations.  The tests are stored in the tests directory where you found the 
.sh and .bat files.

Developing new tests
--------------------
If you would like to develop new tests, use the Selenium IDE.  There are two modes for creating 
new tests, manual and recorded, and they can be intermixed.  For either mode, first open a new 
test suite from the File menu in the IDE.  Next, enter the base URL, such as 
http://tcga-data-dev.nci.nih.gov/ or http://localhost:8080/ in the Base URL test field.

For the recorded mode, just make sure the red dot in the upper right corner has a white box 
around it.  If it's not, then click on the red dot.  Then, start doing things in the browser 
and tests will magically start appearing.  To end record, click on the red dot again.  The 
recorded mode is a nice guideline, but I usually end up rewriting anything recorded as it does 
not always make the most sustainable selections for picking out elements of the page.

For the manual mode, you can enter tests in the Command box at the bottom of the IDE, which 
has a nice auto-complete.  Then you can enter Targets and Values in the named fields.  A test 
might be something like:

Command - verifyElementPresent
Target - cancelButton
Value - empty (not needed, we're just verifying presence)

There are lots of potential ways of defining targets.  There don't seem to be any good 
explanations of targets, but there is documentation here:

http://release.seleniumhq.org/selenium-core/1.0.1/reference.html 

Once you have finished your tests, you will have a test case which is run from the test suite.  
You can save with a Ctrl-S or with a save of the test suite from from the File menu of the IDE.  
Saving, by default, stores in HTML mode, which is what we are using.  

For examples of test cases and test suites, look in the tests directory below with .bat and 
.sh files.

Notes on Java versions of the tests
-----------------------------------
There are some Java versions of the Data Portal tests in the archiveTestsInJava directory in 
the tests directory.  The java tests will work, but when saved from the Selenium IDE some of 
the naming in the generated java code has to be corrected.  So, we are not using this route 
for now.

References
---------------------------------
Selenium code and documentation is available at:

http://seleniumhq.org/

 -- Maven and Selenium
http://mavenium.sourceforge.net/parameters.html
http://www.vineetmanohar.com/2007/06/maven-selenium/
http://shale.apache.org/shale-apps/selenium.html



