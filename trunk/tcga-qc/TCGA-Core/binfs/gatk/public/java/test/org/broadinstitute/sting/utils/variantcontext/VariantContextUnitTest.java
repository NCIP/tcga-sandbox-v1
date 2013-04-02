// our package
package org.broadinstitute.sting.utils.variantcontext;


// the imports for unit testing.


import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;


/**
 * Basic unit test for RecalData
 */
public class VariantContextUnitTest {
    Allele A, Aref, T, Tref;
    Allele del, delRef, ATC, ATCref;

    // A [ref] / T at 10
    String snpLoc = "chr1";
    int snpLocStart = 10;
    int snpLocStop = 10;

    // - / ATC [ref] from 20-23
    String delLoc = "chr1";
    int delLocStart = 20;
    int delLocStop = 23;

    // - [ref] / ATC from 20-20
    String insLoc = "chr1";
    int insLocStart = 20;
    int insLocStop = 20;

    // - / A / T / ATC [ref] from 20-23
    String mixedLoc = "chr1";
    int mixedLocStart = 20;
    int mixedLocStop = 23;

    @BeforeSuite
    public void before() {
        del = Allele.create("-");
        delRef = Allele.create("-", true);

        A = Allele.create("A");
        Aref = Allele.create("A", true);
        T = Allele.create("T");
        Tref = Allele.create("T", true);

        ATC = Allele.create("ATC");
        ATCref = Allele.create("ATC", true);
    }

    @Test
    public void testDetermineTypes() {
        Allele ACref = Allele.create("AC", true);
        Allele AC = Allele.create("AC");
        Allele AT = Allele.create("AT");
        Allele C = Allele.create("C");
        Allele CAT = Allele.create("CAT");
        Allele TAref = Allele.create("TA", true);
        Allele TA = Allele.create("TA");
        Allele TC = Allele.create("TC");
        Allele symbolic = Allele.create("<FOO>");

        // test REF
        List<Allele> alleles = Arrays.asList(Tref);
        VariantContext vc = new VariantContext("test", snpLoc,snpLocStart, snpLocStop, alleles);
        Assert.assertEquals(vc.getType(), VariantContext.Type.NO_VARIATION);

        // test SNPs
        alleles = Arrays.asList(Tref, A);
        vc = new VariantContext("test", snpLoc,snpLocStart, snpLocStop, alleles);
        Assert.assertEquals(vc.getType(), VariantContext.Type.SNP);

        alleles = Arrays.asList(Tref, A, C);
        vc = new VariantContext("test", snpLoc,snpLocStart, snpLocStop, alleles);
        Assert.assertEquals(vc.getType(), VariantContext.Type.SNP);

        // test MNPs
        alleles = Arrays.asList(ACref, TA);
        vc = new VariantContext("test", snpLoc,snpLocStart, snpLocStop+1, alleles);
        Assert.assertEquals(vc.getType(), VariantContext.Type.MNP);

        alleles = Arrays.asList(ATCref, CAT, Allele.create("GGG"));
        vc = new VariantContext("test", snpLoc,snpLocStart, snpLocStop+2, alleles);
        Assert.assertEquals(vc.getType(), VariantContext.Type.MNP);

        // test INDELs
        alleles = Arrays.asList(Aref, ATC);
        vc = new VariantContext("test", snpLoc,snpLocStart, snpLocStop, alleles);
        Assert.assertEquals(vc.getType(), VariantContext.Type.INDEL);

        alleles = Arrays.asList(ATCref, A);
        vc = new VariantContext("test", snpLoc,snpLocStart, snpLocStop+2, alleles);
        Assert.assertEquals(vc.getType(), VariantContext.Type.INDEL);

        alleles = Arrays.asList(Tref, TA, TC);
        vc = new VariantContext("test", snpLoc,snpLocStart, snpLocStop, alleles);
        Assert.assertEquals(vc.getType(), VariantContext.Type.INDEL);

        alleles = Arrays.asList(ATCref, A, AC);
        vc = new VariantContext("test", snpLoc,snpLocStart, snpLocStop+2, alleles);
        Assert.assertEquals(vc.getType(), VariantContext.Type.INDEL);

        alleles = Arrays.asList(ATCref, A, Allele.create("ATCTC"));
        vc = new VariantContext("test", snpLoc,snpLocStart, snpLocStop+2, alleles);
        Assert.assertEquals(vc.getType(), VariantContext.Type.INDEL);

        // test MIXED
        alleles = Arrays.asList(TAref, T, TC);
        vc = new VariantContext("test", snpLoc,snpLocStart, snpLocStop+1, alleles);
        Assert.assertEquals(vc.getType(), VariantContext.Type.MIXED);

        alleles = Arrays.asList(TAref, T, AC);
        vc = new VariantContext("test", snpLoc,snpLocStart, snpLocStop+1, alleles);
        Assert.assertEquals(vc.getType(), VariantContext.Type.MIXED);

        alleles = Arrays.asList(ACref, ATC, AT);
        vc = new VariantContext("test", snpLoc,snpLocStart, snpLocStop+1, alleles);
        Assert.assertEquals(vc.getType(), VariantContext.Type.MIXED);

        alleles = Arrays.asList(Aref, T, symbolic);
        vc = new VariantContext("test", snpLoc,snpLocStart, snpLocStop, alleles);
        Assert.assertEquals(vc.getType(), VariantContext.Type.MIXED);

        // test SYMBOLIC
        alleles = Arrays.asList(Tref, symbolic);
        vc = new VariantContext("test", snpLoc,snpLocStart, snpLocStop, alleles);
        Assert.assertEquals(vc.getType(), VariantContext.Type.SYMBOLIC);
    }

    @Test
    public void testCreatingSNPVariantContext() {

        List<Allele> alleles = Arrays.asList(Aref, T);
        VariantContext vc = new VariantContext("test", snpLoc,snpLocStart, snpLocStop, alleles);

        Assert.assertEquals(vc.getChr(), snpLoc);
        Assert.assertEquals(vc.getStart(), snpLocStart);
        Assert.assertEquals(vc.getEnd(), snpLocStop);
        Assert.assertEquals(vc.getType(), VariantContext.Type.SNP);
        Assert.assertTrue(vc.isSNP());
        Assert.assertFalse(vc.isIndel());
        Assert.assertFalse(vc.isInsertion());
        Assert.assertFalse(vc.isDeletion());
        Assert.assertFalse(vc.isMixed());
        Assert.assertTrue(vc.isBiallelic());
        Assert.assertEquals(vc.getNAlleles(), 2);

        Assert.assertEquals(vc.getReference(), Aref);
        Assert.assertEquals(vc.getAlleles().size(), 2);
        Assert.assertEquals(vc.getAlternateAlleles().size(), 1);
        Assert.assertEquals(vc.getAlternateAllele(0), T);

        Assert.assertFalse(vc.hasGenotypes());

        Assert.assertEquals(vc.getSampleNames().size(), 0);
    }

    @Test
    public void testCreatingRefVariantContext() {
         List<Allele> alleles = Arrays.asList(Aref);
        VariantContext vc = new VariantContext("test", snpLoc,snpLocStart, snpLocStop, alleles);

        Assert.assertEquals(vc.getChr(), snpLoc);
        Assert.assertEquals(vc.getStart(), snpLocStart);
        Assert.assertEquals(vc.getEnd(), snpLocStop);
        Assert.assertEquals(VariantContext.Type.NO_VARIATION, vc.getType());
        Assert.assertFalse(vc.isSNP());
        Assert.assertFalse(vc.isIndel());
        Assert.assertFalse(vc.isInsertion());
        Assert.assertFalse(vc.isDeletion());
        Assert.assertFalse(vc.isMixed());
        Assert.assertFalse(vc.isBiallelic());
        Assert.assertEquals(vc.getNAlleles(), 1);

        Assert.assertEquals(vc.getReference(), Aref);
        Assert.assertEquals(vc.getAlleles().size(), 1);
        Assert.assertEquals(vc.getAlternateAlleles().size(), 0);
        //Assert.assertEquals(vc.getAlternateAllele(0), T);

        Assert.assertFalse(vc.hasGenotypes());
        Assert.assertEquals(vc.getSampleNames().size(), 0);
    }

    @Test
    public void testCreatingDeletionVariantContext() {
        List<Allele> alleles = Arrays.asList(ATCref, del);
        VariantContext vc = new VariantContext("test", delLoc, delLocStart, delLocStop, alleles);

        Assert.assertEquals(vc.getChr(), delLoc);
        Assert.assertEquals(vc.getStart(), delLocStart);
        Assert.assertEquals(vc.getEnd(), delLocStop);
        Assert.assertEquals(vc.getType(), VariantContext.Type.INDEL);
        Assert.assertFalse(vc.isSNP());
        Assert.assertTrue(vc.isIndel());
        Assert.assertFalse(vc.isInsertion());
        Assert.assertTrue(vc.isDeletion());
        Assert.assertFalse(vc.isMixed());
        Assert.assertTrue(vc.isBiallelic());
        Assert.assertEquals(vc.getNAlleles(), 2);

        Assert.assertEquals(vc.getReference(), ATCref);
        Assert.assertEquals(vc.getAlleles().size(), 2);
        Assert.assertEquals(vc.getAlternateAlleles().size(), 1);
        Assert.assertEquals(vc.getAlternateAllele(0), del);

        Assert.assertFalse(vc.hasGenotypes());

        Assert.assertEquals(vc.getSampleNames().size(), 0);
    }

    @Test
    public void testCreatingInsertionVariantContext() {
        List<Allele> alleles = Arrays.asList(delRef, ATC);
        VariantContext vc = new VariantContext("test", insLoc, insLocStart, insLocStop, alleles);

        Assert.assertEquals(vc.getChr(), insLoc);
        Assert.assertEquals(vc.getStart(), insLocStart);
        Assert.assertEquals(vc.getEnd(), insLocStop);
        Assert.assertEquals(vc.getType(), VariantContext.Type.INDEL);
        Assert.assertFalse(vc.isSNP());
        Assert.assertTrue(vc.isIndel());
        Assert.assertTrue(vc.isInsertion());
        Assert.assertFalse(vc.isDeletion());
        Assert.assertFalse(vc.isMixed());
        Assert.assertTrue(vc.isBiallelic());
        Assert.assertEquals(vc.getNAlleles(), 2);

        Assert.assertEquals(vc.getReference(), delRef);
        Assert.assertEquals(vc.getAlleles().size(), 2);
        Assert.assertEquals(vc.getAlternateAlleles().size(), 1);
        Assert.assertEquals(vc.getAlternateAllele(0), ATC);
        Assert.assertFalse(vc.hasGenotypes());

        Assert.assertEquals(vc.getSampleNames().size(), 0);
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void testBadConstructorArgs1() {
        new VariantContext("test", insLoc, insLocStart, insLocStop, Arrays.asList(delRef, ATCref));
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void testBadConstructorArgs2() {
        new VariantContext("test", insLoc, insLocStart, insLocStop, Arrays.asList(delRef, del));
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void testBadConstructorArgs3() {
        new VariantContext("test", insLoc, insLocStart, insLocStop, Arrays.asList(del));
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void testBadConstructorArgsDuplicateAlleles1() {
        new VariantContext("test", insLoc, insLocStart, insLocStop, Arrays.asList(Aref, T, T));
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void testBadConstructorArgsDuplicateAlleles2() {
        new VariantContext("test", insLoc, insLocStart, insLocStop, Arrays.asList(Aref, A));
    }

    @Test (expectedExceptions = IllegalStateException.class)
    public void testBadLoc1() {
        List<Allele> alleles = Arrays.asList(Aref, T, del);
        new VariantContext("test", delLoc, delLocStart, delLocStop, alleles);
    }

    @Test
    public void testAccessingSimpleSNPGenotypes() {
        List<Allele> alleles = Arrays.asList(Aref, T);

        Genotype g1 = new Genotype("AA", Arrays.asList(Aref, Aref), 10);
        Genotype g2 = new Genotype("AT", Arrays.asList(Aref, T), 10);
        Genotype g3 = new Genotype("TT", Arrays.asList(T, T), 10);

        VariantContext vc = new VariantContext("test", snpLoc,snpLocStart, snpLocStop, alleles, Arrays.asList(g1, g2, g3));

        Assert.assertTrue(vc.hasGenotypes());
        Assert.assertFalse(vc.isMonomorphic());
        Assert.assertTrue(vc.isPolymorphic());
        Assert.assertEquals(vc.getSampleNames().size(), 3);

        Assert.assertEquals(vc.getGenotypes().size(), 3);
        Assert.assertEquals(vc.getGenotypes().get("AA"), g1);
        Assert.assertEquals(vc.getGenotype("AA"), g1);
        Assert.assertEquals(vc.getGenotypes().get("AT"), g2);
        Assert.assertEquals(vc.getGenotype("AT"), g2);
        Assert.assertEquals(vc.getGenotypes().get("TT"), g3);
        Assert.assertEquals(vc.getGenotype("TT"), g3);

        Assert.assertTrue(vc.hasGenotype("AA"));
        Assert.assertTrue(vc.hasGenotype("AT"));
        Assert.assertTrue(vc.hasGenotype("TT"));
        Assert.assertFalse(vc.hasGenotype("foo"));
        Assert.assertFalse(vc.hasGenotype("TTT"));
        Assert.assertFalse(vc.hasGenotype("at"));
        Assert.assertFalse(vc.hasGenotype("tt"));

        Assert.assertEquals(vc.getChromosomeCount(), 6);
        Assert.assertEquals(vc.getChromosomeCount(Aref), 3);
        Assert.assertEquals(vc.getChromosomeCount(T), 3);
    }

    @Test
    public void testAccessingCompleteGenotypes() {
        List<Allele> alleles = Arrays.asList(Aref, T, del);

        Genotype g1 = new Genotype("AA", Arrays.asList(Aref, Aref), 10);
        Genotype g2 = new Genotype("AT", Arrays.asList(Aref, T), 10);
        Genotype g3 = new Genotype("TT", Arrays.asList(T, T), 10);
        Genotype g4 = new Genotype("Td", Arrays.asList(T, del), 10);
        Genotype g5 = new Genotype("dd", Arrays.asList(del, del), 10);
        Genotype g6 = new Genotype("..", Arrays.asList(Allele.NO_CALL, Allele.NO_CALL), 10);

        VariantContext vc = new VariantContext("test", snpLoc,snpLocStart, snpLocStop, alleles, Arrays.asList(g1, g2, g3, g4, g5, g6));

        Assert.assertTrue(vc.hasGenotypes());
        Assert.assertFalse(vc.isMonomorphic());
        Assert.assertTrue(vc.isPolymorphic());
        Assert.assertEquals(vc.getGenotypes().size(), 6);

        Assert.assertEquals(3, vc.getGenotypes(Arrays.asList("AA", "Td", "dd")).size());

        Assert.assertEquals(10, vc.getChromosomeCount());
        Assert.assertEquals(3, vc.getChromosomeCount(Aref));
        Assert.assertEquals(4, vc.getChromosomeCount(T));
        Assert.assertEquals(3, vc.getChromosomeCount(del));
        Assert.assertEquals(2, vc.getChromosomeCount(Allele.NO_CALL));
    }

    @Test
    public void testAccessingRefGenotypes() {
        List<Allele> alleles1 = Arrays.asList(Aref, T);
        List<Allele> alleles2 = Arrays.asList(Aref);
        List<Allele> alleles3 = Arrays.asList(Aref, T, del);
        for ( List<Allele> alleles : Arrays.asList(alleles1, alleles2, alleles3)) {
            Genotype g1 = new Genotype("AA1", Arrays.asList(Aref, Aref), 10);
            Genotype g2 = new Genotype("AA2", Arrays.asList(Aref, Aref), 10);
            Genotype g3 = new Genotype("..", Arrays.asList(Allele.NO_CALL, Allele.NO_CALL), 10);
            VariantContext vc = new VariantContext("test", snpLoc,snpLocStart, snpLocStop, alleles, Arrays.asList(g1, g2, g3));

            Assert.assertTrue(vc.hasGenotypes());
            Assert.assertTrue(vc.isMonomorphic());
            Assert.assertFalse(vc.isPolymorphic());
            Assert.assertEquals(vc.getGenotypes().size(), 3);

            Assert.assertEquals(4, vc.getChromosomeCount());
            Assert.assertEquals(4, vc.getChromosomeCount(Aref));
            Assert.assertEquals(0, vc.getChromosomeCount(T));
            Assert.assertEquals(2, vc.getChromosomeCount(Allele.NO_CALL));
        }
    }

    @Test
    public void testFilters() {
        List<Allele> alleles = Arrays.asList(Aref, T, del);
        Genotype g1 = new Genotype("AA", Arrays.asList(Aref, Aref), 10);
        Genotype g2 = new Genotype("AT", Arrays.asList(Aref, T), 10);
        MutableVariantContext vc = new MutableVariantContext("test", snpLoc,snpLocStart, snpLocStop, alleles, Arrays.asList(g1,g2));

        Assert.assertTrue(vc.isNotFiltered());
        Assert.assertFalse(vc.isFiltered());
        Assert.assertEquals(0, vc.getFilters().size());

        vc.addFilter("BAD_SNP_BAD!");

        Assert.assertFalse(vc.isNotFiltered());
        Assert.assertTrue(vc.isFiltered());
        Assert.assertEquals(1, vc.getFilters().size());

        vc.addFilters(Arrays.asList("REALLY_BAD_SNP", "CHRIST_THIS_IS_TERRIBLE"));

        Assert.assertFalse(vc.isNotFiltered());
        Assert.assertTrue(vc.isFiltered());
        Assert.assertEquals(3, vc.getFilters().size());

        vc.clearFilters();

        Assert.assertTrue(vc.isNotFiltered());
        Assert.assertFalse(vc.isFiltered());
        Assert.assertEquals(0, vc.getFilters().size());
    }

    @Test
    public void testVCromGenotypes() {
        List<Allele> alleles = Arrays.asList(Aref, T, del);
        Genotype g1 = new Genotype("AA", Arrays.asList(Aref, Aref), 10);
        Genotype g2 = new Genotype("AT", Arrays.asList(Aref, T), 10);
        Genotype g3 = new Genotype("TT", Arrays.asList(T, T), 10);
        Genotype g4 = new Genotype("..", Arrays.asList(Allele.NO_CALL, Allele.NO_CALL), 10);
        Genotype g5 = new Genotype("--", Arrays.asList(del, del), 10);
        VariantContext vc = new VariantContext("test", snpLoc,snpLocStart, snpLocStop , alleles, Arrays.asList(g1,g2,g3,g4,g5));

        VariantContext vc12 = vc.subContextFromGenotypes(Arrays.asList(g1,g2));
        VariantContext vc1 = vc.subContextFromGenotypes(Arrays.asList(g1));
        VariantContext vc23 = vc.subContextFromGenotypes(Arrays.asList(g2, g3));
        VariantContext vc4 = vc.subContextFromGenotypes(Arrays.asList(g4));
        VariantContext vc14 = vc.subContextFromGenotypes(Arrays.asList(g1, g4));
        VariantContext vc5 = vc.subContextFromGenotypes(Arrays.asList(g5));

        Assert.assertTrue(vc12.isPolymorphic());
        Assert.assertTrue(vc23.isPolymorphic());
        Assert.assertTrue(vc1.isMonomorphic());
        Assert.assertTrue(vc4.isMonomorphic());
        Assert.assertTrue(vc14.isMonomorphic());
        Assert.assertTrue(vc5.isPolymorphic());

        Assert.assertTrue(vc12.isSNP());
        Assert.assertTrue(vc12.isVariant());
        Assert.assertTrue(vc12.isBiallelic());

        Assert.assertFalse(vc1.isSNP());
        Assert.assertFalse(vc1.isVariant());
        Assert.assertFalse(vc1.isBiallelic());

        Assert.assertTrue(vc23.isSNP());
        Assert.assertTrue(vc23.isVariant());
        Assert.assertTrue(vc23.isBiallelic());

        Assert.assertFalse(vc4.isSNP());
        Assert.assertFalse(vc4.isVariant());
        Assert.assertFalse(vc4.isBiallelic());

        Assert.assertFalse(vc14.isSNP());
        Assert.assertFalse(vc14.isVariant());
        Assert.assertFalse(vc14.isBiallelic());

        Assert.assertTrue(vc5.isIndel());
        Assert.assertTrue(vc5.isDeletion());
        Assert.assertTrue(vc5.isVariant());
        Assert.assertTrue(vc5.isBiallelic());

        Assert.assertEquals(3, vc12.getChromosomeCount(Aref));
        Assert.assertEquals(1, vc23.getChromosomeCount(Aref));
        Assert.assertEquals(2, vc1.getChromosomeCount(Aref));
        Assert.assertEquals(0, vc4.getChromosomeCount(Aref));
        Assert.assertEquals(2, vc14.getChromosomeCount(Aref));
        Assert.assertEquals(0, vc5.getChromosomeCount(Aref));
    }


    @Test
    public void testManipulatingAlleles() {
        // todo -- add tests that call add/set/remove
    }

    @Test
    public void testManipulatingGenotypes() {
        // todo -- add tests that call add/set/remove
    }
}
