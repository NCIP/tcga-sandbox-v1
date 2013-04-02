package org.broadinstitute.sting.gatk.refdata;

import org.testng.Assert;
import org.broadinstitute.sting.BaseTest;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @author aaron 
 * 
 * Class ReferenceOrderedDataUnitTest
 *
 * some functionality to test parts of the reference ordered data system that I've added.  This is by NO MEANS
 * a complete test suite, but additions would be extremely welcome
 */
public class ReferenceOrderedDataUnitTest extends BaseTest {
    @Test
    public void extractRodsFromFileTest() {
        String file = validationDataLocation + "testRODFileImpl.csv";
        List<String> lst = new ArrayList<String>();
        ReferenceOrderedData.extractRodsFromFile(lst,file);
        Assert.assertEquals(lst.size(), 6);
        int index = 0;
        for (String entry: lst) {
            String first = entry.subSequence(0,entry.indexOf(",")).toString();            
            Assert.assertTrue(first.equals("rod" + String.valueOf(++index)));
        }
    }
    @Test
    public void extractRodsFromMultiFileTest() {
        String file = validationDataLocation + "testRODFileImpl.csv";
        String file2 = validationDataLocation + "testRODFileImpl2.csv";
        List<String> lst = new ArrayList<String>();
        ReferenceOrderedData.extractRodsFromFile(lst,file);
        ReferenceOrderedData.extractRodsFromFile(lst,file2);
        Assert.assertEquals(lst.size(), 12);
        int index = 0;
        for (String entry: lst) {
            String first = entry.subSequence(0,entry.indexOf(",")).toString();
            Assert.assertTrue(first.equals("rod" + String.valueOf(++index)));
        }
    }
}
