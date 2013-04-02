/*
 * Copyright (c) 2010 The Broad Institute
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.broadinstitute.sting.gatk.refdata.tracks.builders;

import net.sf.samtools.SAMSequenceDictionary;
import net.sf.samtools.SAMSequenceRecord;
import org.apache.log4j.Logger;
import org.broad.tribble.*;
import org.broad.tribble.index.Index;
import org.broad.tribble.index.IndexFactory;
import org.broad.tribble.source.BasicFeatureSource;
import org.broad.tribble.util.LittleEndianOutputStream;
import org.broadinstitute.sting.commandline.Tags;
import org.broadinstitute.sting.gatk.arguments.ValidationExclusion;
import org.broadinstitute.sting.gatk.refdata.ReferenceDependentFeatureCodec;
import org.broadinstitute.sting.gatk.refdata.tracks.RMDTrack;
import org.broadinstitute.sting.gatk.refdata.tracks.RMDTrackCreationException;
import org.broadinstitute.sting.gatk.refdata.utils.RMDTriplet;
import org.broadinstitute.sting.gatk.refdata.utils.RMDTriplet.RMDStorageType;
import org.broadinstitute.sting.utils.GenomeLocParser;
import org.broadinstitute.sting.utils.SequenceDictionaryUtils;
import org.broadinstitute.sting.utils.collections.Pair;
import org.broadinstitute.sting.utils.classloader.PluginManager;
import org.broadinstitute.sting.utils.exceptions.UserException;
import org.broadinstitute.sting.utils.exceptions.ReviewedStingException;
import org.broadinstitute.sting.utils.file.FSLockWithShared;
import org.broadinstitute.sting.utils.file.FileSystemInabilityToLockException;
import org.broadinstitute.sting.utils.instrumentation.Sizeof;

import java.io.*;
import java.util.*;



/**
 * 
 * @author aaron 
 *                                           `
 * Class RMDTrackBuilder
 *
 * This class keeps track of the available codecs, and knows how to put together a track of
 * that gets iterators from the FeatureReader using Tribble.
 *
 */
public class RMDTrackBuilder extends PluginManager<FeatureCodec> {
    /**
     * our log, which we use to capture anything from this class
     */
    private final static Logger logger = Logger.getLogger(RMDTrackBuilder.class);

    // a constant we use for marking sequence dictionary entries in the Tribble index property list
    public static final String SequenceDictionaryPropertyPredicate = "DICT:";

    private Map<String, Class> classes = null;

    // private sequence dictionary we use to set our tracks with
    private SAMSequenceDictionary dict = null;

    /**
     * Private genome loc parser to use when building out new locs.
     */
    private GenomeLocParser genomeLocParser;

    /**
     * Validation exclusions, for validating the sequence dictionary.
     */
    private ValidationExclusion.TYPE validationExclusionType;

    /**
     * Construct an RMDTrackerBuilder, allowing the user to define tracks to build after-the-fact.  This is generally
     * used when walkers want to directly manage the ROD system for whatever reason.  Before using this constructor,
     * please talk through your approach with the SE team.
     * @param dict Sequence dictionary to use.
     * @param genomeLocParser Location parser to use.
     * @param validationExclusionType Types of validations to exclude, for sequence dictionary verification.
     */
    public RMDTrackBuilder(SAMSequenceDictionary dict,
                           GenomeLocParser genomeLocParser,
                           ValidationExclusion.TYPE validationExclusionType) {
        super(FeatureCodec.class, "Codecs", "Codec");
        this.dict = dict;
        this.genomeLocParser = genomeLocParser;
        this.validationExclusionType = validationExclusionType;

        classes = new HashMap<String, Class>();
        for (String name: this.getPluginsByName().keySet()) {
            classes.put(name.toUpperCase(), getPluginsByName().get(name));
        }    }

    /** @return a list of all available track types we currently have access to create */
    public Map<String, Class> getAvailableTrackNamesAndTypes() {
        return Collections.unmodifiableMap(classes);
    }

    /** @return a list of all available track record types we currently have access to create */
    public Map<String, Class> getAvailableTrackNamesAndRecordTypes() {
        HashMap classToRecord = new HashMap<String, Class>();
        for (String name: this.getPluginsByName().keySet()) {
            FeatureCodec codec = this.createByName(name);
            classToRecord.put(name, codec.getFeatureType());
        }
        return classToRecord;
    }

    /**
     * create a RMDTrack of the specified type
     *
     * @param fileDescriptor a description of the type of track to build.
     *
     * @return an instance of the track
     * @throws RMDTrackCreationException
     *          if we don't know of the target class or we couldn't create it
     */
    public RMDTrack createInstanceOfTrack(RMDTriplet fileDescriptor) throws RMDTrackCreationException {
        String name = fileDescriptor.getName();
        File inputFile = new File(fileDescriptor.getFile());

        Class featureCodecClass = getAvailableTrackNamesAndTypes().get(fileDescriptor.getType().toUpperCase());
        if (featureCodecClass == null)
            throw new UserException.BadArgumentValue("-B",fileDescriptor.getType());

        // return a feature reader track
        Pair<FeatureSource, SAMSequenceDictionary> pair;
        if (inputFile.getAbsolutePath().endsWith(".gz"))
            pair = createTabixIndexedFeatureSource(featureCodecClass, name, inputFile);
        else
            pair = getFeatureSource(featureCodecClass, name, inputFile, fileDescriptor.getStorageType());
        if (pair == null) throw new UserException.CouldNotReadInputFile(inputFile, "Unable to make the feature reader for input file");
        return new RMDTrack(featureCodecClass, name, inputFile, pair.first, pair.second, genomeLocParser, createCodec(featureCodecClass,name));
    }

    /**
     * Convenience method simplifying track creation.  Assume unnamed track based on a file rather than a stream.
     * @param targetClass Type of Tribble class to build.
     * @param inputFile Input file type to use.
     * @return An RMDTrack, suitable for accessing reference metadata.
     */
    public RMDTrack createInstanceOfTrack(Class targetClass, File inputFile) {
        // TODO: Update RMDTriplet to contain an actual class object rather than a name to avoid these gymnastics.
        String typeName = null;
        for(Map.Entry<String,Class> trackType: getAvailableTrackNamesAndTypes().entrySet()) {
            if(trackType.getValue().equals(targetClass))
                typeName = trackType.getKey();
        }

        if(typeName == null)
            throw new ReviewedStingException("Unable to find type name for class " + targetClass.getName());

        return createInstanceOfTrack(new RMDTriplet("anonymous",typeName,inputFile.getAbsolutePath(),RMDStorageType.FILE,new Tags()));
    }

    /**
     * create a feature reader, without assuming there exists an index.  This code assumes the feature
     * reader of the appropriate type will figure out what the right index type is, and determine if it
     * exists.
     *
     * @param targetClass the codec class type
     * @param name the name of the track
     * @param inputFile the file to load
     * @return a feature reader implementation
     */
    private Pair<FeatureSource, SAMSequenceDictionary> createTabixIndexedFeatureSource(Class targetClass, String name, File inputFile) {
        // we might not know the index type, try loading with the default reader constructor
        logger.info("Attempting to blindly load " + inputFile + " as a tabix indexed file");
        try {
            return new Pair<FeatureSource, SAMSequenceDictionary>(BasicFeatureSource.getFeatureSource(inputFile.getAbsolutePath(), createCodec(targetClass, name)),null);
        } catch (TribbleException e) {
            throw new UserException(e.getMessage(), e);
        }
    }

    /**
     * add a name to the codec, if it takes one
     * @param targetClass the class to create a codec for
     * @param name the name to assign this codec
     * @return the feature codec itself
     */
    public FeatureCodec createCodec(Class targetClass, String name) {
        FeatureCodec codex = this.createByType(targetClass);
        if ( codex instanceof NameAwareCodec )
            ((NameAwareCodec)codex).setName(name);
        if(codex instanceof ReferenceDependentFeatureCodec)
            ((ReferenceDependentFeatureCodec)codex).setGenomeLocParser(genomeLocParser);        
        return codex;
    }

    /**
     * create a feature source object given:
     * @param targetClass the target class
     * @param name the name of the codec
     * @param inputFile the tribble file to parse
     * @param storageType How the RMD is streamed into the input file.
     * @return the input file as a FeatureReader
     */
    private Pair<FeatureSource, SAMSequenceDictionary> getFeatureSource(Class targetClass, String name, File inputFile, RMDStorageType storageType) {
        // Feature source and sequence dictionary to use as the ultimate reference
        FeatureSource featureSource = null;
        SAMSequenceDictionary sequenceDictionary = null;

        // Detect whether or not this source should be indexed.
        boolean canBeIndexed = (storageType == RMDStorageType.FILE);

        if(canBeIndexed) {
            try {
                Index index = loadIndex(inputFile, createCodec(targetClass, name));
                try { logger.info(String.format("  Index for %s has size in bytes %d", inputFile, Sizeof.getObjectGraphSize(index))); }
                catch (ReviewedStingException e) { }

                sequenceDictionary = getSequenceDictionaryFromProperties(index);

                // if we don't have a dictionary in the Tribble file, and we've set a dictionary for this builder, set it in the file if they match
                if (sequenceDictionary.size() == 0 && dict != null) {
                    File indexFile = Tribble.indexFile(inputFile);
                    setIndexSequenceDictionary(inputFile,index,dict,indexFile,true);
                    sequenceDictionary = getSequenceDictionaryFromProperties(index);
                }

                featureSource = new BasicFeatureSource(inputFile.getAbsolutePath(), index, createCodec(targetClass, name));
            }
            catch (TribbleException e) {
                throw new UserException(e.getMessage());
            }
            catch (IOException e) {
                throw new UserException.CouldNotCreateOutputFile(inputFile, "unable to write Tribble index", e);
            }
        }
        else {
            featureSource = BasicFeatureSource.getFeatureSource(inputFile.getAbsolutePath(),createCodec(targetClass, name),false);
        }

        return new Pair<FeatureSource,SAMSequenceDictionary>(featureSource,sequenceDictionary);
    }

    /**
     * create an index for the input file
     * @param inputFile the input file
     * @param codec the codec to use
     * @return a linear index for the specified type
     * @throws IOException if we cannot write the index file
     */
    public synchronized Index loadIndex(File inputFile, FeatureCodec codec) throws IOException {
        // create the index file name, locking on the index file name
        File indexFile = Tribble.indexFile(inputFile);
        FSLockWithShared lock = new FSLockWithShared(indexFile);

        // acquire a lock on the file
        Index idx = null;
        if (indexFile.canRead())
            idx = attemptIndexFromDisk(inputFile, codec, indexFile, lock);

        // if we managed to make an index, return
        if (idx != null) return idx;

        // we couldn't read the file, or we fell out of the conditions above, continue on to making a new index
        return writeIndexToDisk(createIndexInMemory(inputFile, codec), indexFile, lock);
    }

    /**
     * attempt to read the index from disk
     * @param inputFile the input file
     * @param codec the codec to read from
     * @param indexFile the index file itself
     * @param lock the lock file
     * @return an index, or null if we couldn't load one
     * @throws IOException if we fail for FS issues
     */
    protected Index attemptIndexFromDisk(File inputFile, FeatureCodec codec, File indexFile, FSLockWithShared lock) throws IOException {
        boolean locked;
        try {
            locked = lock.sharedLock();
        }
        catch(FileSystemInabilityToLockException ex) {
            throw new UserException.MissortedFile(inputFile, "Unexpected inability to lock exception", ex);
        }
        Index idx;
        try {
            if (!locked) // can't lock file
                idx = createIndexInMemory(inputFile, codec);
            else
                idx = loadFromDisk(inputFile, indexFile);
        } finally {
            if (locked) lock.unlock();
        }
        return idx;
    }

    /**
     * load the index from disk, checking for out of date indexes and old versions (both of which are deleted)
     * @param inputFile the input file
     * @param indexFile the input file, plus the index extension
     * @return an Index, or null if we're unable to load
     */
    public static Index loadFromDisk(File inputFile, File indexFile) {
        logger.info("Loading Tribble index from disk for file " + inputFile);
        Index index = IndexFactory.loadIndex(indexFile.getAbsolutePath());

        // check if the file is up-to date (filestamp and version check)
        if (index.isCurrentVersion() && indexFile.lastModified() >= inputFile.lastModified())
            return index;
        else if (indexFile.lastModified() < inputFile.lastModified())
            logger.warn("Index file " + indexFile + " is out of date (index older than input file), deleting and updating the index file");
        else // we've loaded an old version of the index, we want to remove it <-- currently not used, but may re-enable
            logger.warn("Index file " + indexFile + " is out of date (old version), deleting and updating the index file");

        // however we got here, remove the index and return null
        boolean deleted = indexFile.delete();

        if (!deleted) logger.warn("Index file " + indexFile + " is out of date, but could not be removed; it will not be trusted (we'll try to rebuild an in-memory copy)");
        return null;
    }


    /**
     * attempt to write the index to disk
     * @param index the index to write to disk
     * @param indexFile the index file location
     * @param lock the locking object
     * @return the index object
     * @throws IOException when unable to create the new index
     */
    private static Index writeIndexToDisk(Index index, File indexFile, FSLockWithShared lock) throws IOException {
        boolean locked = false; // could we exclusive lock the file?
        try {
            locked = lock.exclusiveLock(); // handle the case where we aren't locking anything
            if (locked) {
                logger.info("Writing Tribble index to disk for file " + indexFile);
                LittleEndianOutputStream stream = new LittleEndianOutputStream(new FileOutputStream(indexFile));
                index.write(stream);
                stream.close();
            }
            else // we can't write it to disk, just store it in memory, tell them this
                logger.warn("Unable to write to " + indexFile + " for the index file, creating index in memory only");

            try { logger.info(String.format("  Index for %s has size in bytes %d", indexFile, Sizeof.getObjectGraphSize(index))); }
            catch ( ReviewedStingException e) { }

            return index;
        }
        catch(FileSystemInabilityToLockException ex) {
            throw new UserException.CouldNotCreateOutputFile(indexFile,"Unexpected inability to lock exception", ex);
        }
        finally {
            if (locked) lock.unlock();
        }

    }

    /**
     * create the index in memory, given the input file and feature codec
     * @param inputFile the input file
     * @param codec the codec
     * @return a LinearIndex, given the file location
     * @throws IOException when unable to create the index in memory
     */
    private Index createIndexInMemory(File inputFile, FeatureCodec codec) {
        // this can take a while, let them know what we're doing
        logger.info("Creating Tribble index in memory for file " + inputFile);
        Index idx = IndexFactory.createIndex(inputFile, codec, IndexFactory.IndexBalanceApproach.FOR_SEEK_TIME);
        setIndexSequenceDictionary(inputFile, idx, dict, null, false);
        return idx;
    }

    /**
     * Returns a collection of track names that match the record type.
     * @param trackRecordType the record type specified in the @RMD annotation
     * @return a collection of available track record type names that match the record type
     */
    public Collection<String> getTrackRecordTypeNames(Class trackRecordType) {
        Set<String> names = new TreeSet<String>();
        if (trackRecordType == null)
            throw new IllegalArgumentException("trackRecordType value is null, please pass in an actual class object");

        for (Map.Entry<String, Class> availableTrackRecordType: getAvailableTrackNamesAndRecordTypes().entrySet()) {
            if (availableTrackRecordType.getValue() != null && trackRecordType.isAssignableFrom(availableTrackRecordType.getValue()))
                names.add(availableTrackRecordType.getKey());
        }
        return names;
    }

    // ---------------------------------------------------------------------------------------------------------
    // static functions to work with the sequence dictionaries of indexes
    // ---------------------------------------------------------------------------------------------------------
    
    /**
     * get the sequence dictionary from the track, if available.  If not, make it from the contig list that is always in the index
     * @param index the index file to use
     * @return a SAMSequenceDictionary if available, null if unavailable
     */
    public static SAMSequenceDictionary getSequenceDictionaryFromProperties(Index index) {
        SAMSequenceDictionary dict = new SAMSequenceDictionary();
        for (Map.Entry<String,String> entry : index.getProperties().entrySet()) {
            if (entry.getKey().startsWith(SequenceDictionaryPropertyPredicate))
                dict.addSequence(new SAMSequenceRecord(entry.getKey().substring(SequenceDictionaryPropertyPredicate.length() , entry.getKey().length()),
                                 Integer.valueOf(entry.getValue())));
        }
        return dict;
    }

    /**
     * create the sequence dictionary with the contig list; a backup approach
     * @param index the index file to use
     * @param dict the sequence dictionary to add contigs to
     * @return the filled-in sequence dictionary
     */
    private static SAMSequenceDictionary createSequenceDictionaryFromContigList(Index index, SAMSequenceDictionary dict) {
        LinkedHashSet<String> seqNames = index.getSequenceNames();
        if (seqNames == null) {
            return dict;
        }
        for (String name : seqNames) {
            SAMSequenceRecord seq = new SAMSequenceRecord(name, 0);
            dict.addSequence(seq);
        }
        return dict;
    }

    /**
     * set the sequence dictionary of the track.  This function checks that the contig listing of the underlying file is compatible.
     * (that each contig in the index is in the sequence dictionary).
     * @param inputFile for proper error message formatting.
     * @param dict the sequence dictionary
     * @param index the index file
     * @param indexFile the index file
     * @param rewriteIndex should we rewrite the index when we're done?
     *
     */
    public void setIndexSequenceDictionary(File inputFile, Index index, SAMSequenceDictionary dict, File indexFile, boolean rewriteIndex) {
        if (dict == null) return;

        SAMSequenceDictionary currentDict = createSequenceDictionaryFromContigList(index, new SAMSequenceDictionary());
        validateTrackSequenceDictionary(inputFile.getAbsolutePath(),currentDict,dict);

        // check that every contig in the RMD contig list is at least in the sequence dictionary we're being asked to set
        for (SAMSequenceRecord seq : currentDict.getSequences()) {
            if (dict.getSequence(seq.getSequenceName()) == null)
                continue;
            index.addProperty(SequenceDictionaryPropertyPredicate + dict.getSequence(seq.getSequenceName()).getSequenceName(), String.valueOf(dict.getSequence(seq.getSequenceName()).getSequenceLength()));
        }
        // re-write the index
        if (rewriteIndex) try {
            writeIndexToDisk(index,indexFile,new FSLockWithShared(indexFile));
        } catch (IOException e) {
            logger.warn("Unable to update index with the sequence dictionary for file " + indexFile + "; this will not effect your run of the GATK");
        }
    }


    public void validateTrackSequenceDictionary(String trackName, SAMSequenceDictionary trackDict, SAMSequenceDictionary referenceDict) {
        // if the sequence dictionary is empty (as well as null which means it doesn't have a dictionary), skip validation
        if (trackDict == null || trackDict.size() == 0)
            logger.info("Track " + trackName + " doesn't have a sequence dictionary built in, skipping dictionary validation");
        else {
            Set<String> trackSequences = new TreeSet<String>();
            for (SAMSequenceRecord dictionaryEntry : trackDict.getSequences())
                trackSequences.add(dictionaryEntry.getSequenceName());
            SequenceDictionaryUtils.validateDictionaries(logger, validationExclusionType, trackName, trackDict, "reference", referenceDict);
        }
    }
}
