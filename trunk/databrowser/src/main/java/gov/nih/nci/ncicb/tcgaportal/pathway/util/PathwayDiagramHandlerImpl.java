package gov.nih.nci.ncicb.tcgaportal.pathway.util;

import gov.nih.nci.ncicb.tcgaportal.level4.util.Locations;
import gov.nih.nci.ncicb.tcgaportal.pathway.dao.IPathwayQueries;
import gov.nih.nci.ncicb.tcgaportal.pathway.domainobjects.Gene;
import org.apache.batik.ext.awt.image.spi.ImageWriterParams;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.xmlgraphics.image.writer.ImageWriterRegistry;
import org.apache.xmlgraphics.image.writer.internal.JPEGImageWriter;

import java.awt.image.RenderedImage;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * Main implementation of PathwayDiagramHandler.
 *
 * @author David Nassau
 *         Last updated by: $Author: waltonj $
 * @version $Rev: 14750 $
 */
public class PathwayDiagramHandlerImpl implements PathwayDiagramHandler {

    private IPathwaySVGFetcher svgFetcher;
    private String tempFileLocation;
    private Locations locationsUtility;

    public PathwayDiagramHandlerImpl() {
        registerJpegTranscoder();
    }

    /*
     * This is needed for now because the maven distribution is missing a file that auto-registers image writers on init
     */
    private void registerJpegTranscoder() {
        final JPEGImageWriter jpegImageWriter = new JPEGImageWriter();

            org.apache.batik.ext.awt.image.spi.ImageWriter extImageWriter = new org.apache.batik.ext.awt.image.spi.ImageWriter() {

                @Override
                public void writeImage(final RenderedImage renderedImage, final OutputStream outputStream) throws IOException {
                    jpegImageWriter.writeImage(renderedImage, outputStream);
                }

                @Override
                public void writeImage(final RenderedImage renderedImage, final OutputStream outputStream, final ImageWriterParams imageWriterParams) throws IOException {
                    org.apache.xmlgraphics.image.writer.ImageWriterParams myImageWriterParams = new org.apache.xmlgraphics.image.writer.ImageWriterParams();
                    myImageWriterParams.setCompressionMethod(imageWriterParams.getCompressionMethod());
                    myImageWriterParams.setJPEGQuality(imageWriterParams.getJPEGQuality(), false);
                    myImageWriterParams.setResolution(imageWriterParams.getResolution());
                    jpegImageWriter.writeImage(renderedImage, outputStream, myImageWriterParams);
                }

                @Override
                public String getMIMEType() {
                    return jpegImageWriter.getMIMEType();
                }
            };

            org.apache.batik.ext.awt.image.spi.ImageWriterRegistry.getInstance().register(extImageWriter);
            ImageWriterRegistry.getInstance().getWriterFor(jpegImageWriter.getMIMEType());
    }

    public void setTempFileLocation(String tempFileLocation) {
        this.tempFileLocation = tempFileLocation;
    }

    public void setLocationsUtility(Locations locationsUtility) {
        this.locationsUtility = locationsUtility;
    }

    public void setSvgFetcher(IPathwaySVGFetcher svgFetcher) {
        this.svgFetcher = svgFetcher;
    }

    public String fetchPathwayImage(String pathwayName, List<Gene> genes, String anomaliesSelectedByUser, Map<String,Double> thresholds) throws TranscoderException, IOException, IPathwayQueries.PathwayQueriesException {
        String bcGenes = makeBiocartaGeneListString(genes, anomaliesSelectedByUser, thresholds);
        return fetchAndTranslateSVG(pathwayName, bcGenes);
    }

    //this version added for level4 app
    public String fetchPathwayImage(String pathwayName, List<String> bcgenes) throws TranscoderException, IOException, IPathwayQueries.PathwayQueriesException {
        String bcGenes = makeBiocartaGeneListString(bcgenes);
        return fetchAndTranslateSVG(pathwayName, bcGenes);
    }

    private String fetchAndTranslateSVG(String pathwayName, String bcgene) throws TranscoderException, IOException, IPathwayQueries.PathwayQueriesException {
        //random image file name
        String diskfilename = String.valueOf( ( new Random() ).nextInt( 1000 ) + System.currentTimeMillis() );
        diskfilename += ".jpg";
        return fetchAndTranslateSVG(pathwayName, bcgene, diskfilename);
    }

    //this one added for testing purpose - supply an image file name
    String fetchAndTranslateSVG(String pathwayName, String bcgene, String diskfilename) throws TranscoderException, IOException, IPathwayQueries.PathwayQueriesException {
        if (locationsUtility != null) {
            locationsUtility.makeSureLocationExists(tempFileLocation);
        }
        diskfilename = tempFileLocation + "/" + diskfilename;

        JPEGTranscoder t = new JPEGTranscoder();
        t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(1.)); //no compression

        String anomalycolor = (bcgene != null && bcgene.length()>0 ? ANOMALY_COLOR : null);
        String svg = svgFetcher.fetchPathwaySVG(pathwayName, bcgene, anomalycolor);

        OutputStream ostream = null;
        try {
            TranscoderInput input = new TranscoderInput(new StringReader(svg));
            ostream = new FileOutputStream(diskfilename);
            TranscoderOutput output = new TranscoderOutput(ostream);
            t.transcode(input, output);
        } finally {
            if (ostream != null) {
                ostream.flush();
                ostream.close();
            }
        }

        //assume the last folder in the path can be used as a relative path from the browser.
        //(we have to configure the web site to make sure this is true)
        int secondToLastSlash = diskfilename.lastIndexOf('/', diskfilename.lastIndexOf('/')-1);
        return diskfilename.substring(secondToLastSlash);
    }

    private String makeBiocartaGeneListString(List<Gene> genes, String anomaliesSelectedByUser, Map<String,Double> thresholds) throws IPathwayQueries.PathwayQueriesException {
        String ret = null;
        if (anomaliesSelectedByUser != null && anomaliesSelectedByUser.length() > 0) {
            boolean checkMutation = anomaliesSelectedByUser.contains(Gene.ANOMALYTYPE_MUTATION);
            boolean checkAmplification = anomaliesSelectedByUser.contains(Gene.ANOMALYTYPE_AMPLIFICATION);
            boolean checkDeletion = anomaliesSelectedByUser.contains(Gene.ANOMALYTYPE_DELETION);
            boolean checkAgent = anomaliesSelectedByUser.contains(Gene.ANOMALYTYPE_AGENT);

            int igenes = genes.size();
            if (igenes > 0) {
                StringBuffer bcgene = new StringBuffer();
                boolean first = true;
                for (int i=0; i<igenes; i++) {
                    Gene gene = genes.get(i);
                    boolean anomalous = false;
                    if (checkMutation) {
                        anomalous = (gene.getMutationRatio() >= thresholds.get(Gene.ANOMALYTYPE_MUTATION));
                    }
                    if (!anomalous && checkAmplification) {
                        anomalous = (gene.getAmplificationRatio() >= thresholds.get(Gene.ANOMALYTYPE_AMPLIFICATION));
                    }
                    if (!anomalous && checkDeletion) {
                        anomalous = (gene.getDeletionRatio() >= thresholds.get(Gene.ANOMALYTYPE_DELETION));
                    }
                    if (!anomalous && checkAgent) {
                        anomalous = gene.isAffectedByAgent();
                    }
                    if (anomalous) {
                        if (!first) bcgene.append(',');
                        first = false;
                        bcgene.append(gene.getBioCartaId());
                    }
                }
                ret = bcgene.toString();
            }
        }
        return ret;
    }

    //this version added for level4 app
    private String makeBiocartaGeneListString(List<String> genes) throws IPathwayQueries.PathwayQueriesException {
        String ret = null;
        int igenes = genes.size();
        if (igenes > 0) {
            StringBuilder bcgeneList = new StringBuilder();
            boolean first = true;
            for (int i=0; i<igenes; i++) {
                if (!first) bcgeneList.append(',');
                first = false;
                bcgeneList.append(genes.get(i));
            }
            ret = bcgeneList.toString();
        }
        return ret;
    }

    //thread that waits a relatively long time then deletes the image file
    class DeleterThread extends Thread {
        String filename;
        public DeleterThread(String filename) {
            this.filename = filename;
        }
        public void run() {
            try {
                Thread.sleep(WAITTODELETE_MILLIS);
            } catch (InterruptedException e) {
                // do nothing
            }
            deleteImageFile(filename);
        }
    }

    public void planDeletionOfImage(String filename) {
        (new DeleterThread(filename)).start();
    }

    private void deleteImageFile(String filename) {
        int lastslash = filename.lastIndexOf('/');
        if (lastslash >= 0) {
            filename = filename.substring(lastslash + 1);
        }
        File f = new File(tempFileLocation + "/" + filename);
        if (f.exists()) {
            boolean deleted = f.delete();
            if (! deleted) {
                // todo log this somehow?
            }
        }
    }
}
