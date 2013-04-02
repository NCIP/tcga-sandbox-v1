/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for performing text comparisons between text files.
 * 
 * <p>
 * The inner classes {@link TextComparisonEvent} and {@link TextComparisonResult} are used
 * by the methods of this class to record each text comparison event and the final results
 * of the comparison, respectively.
 *
 * @author Matt Nicholls
 *         Last updated by: nichollsmc
 * @version 
 */
public class TextFileComparer {
	
	private static final String DEFAULT_CHARSET = "UTF-8"; 
	
	/**
     * Compares each character in a line of text from a source file to a target file.
     * 
     * <p>
     * For each set of non-matching lines, a new {@link TextComparisonEvent} will be added to the 
     * {@link TextComparisonResult} instance returned by this method. Each instance of 
     * <code>TextComparisonEvent</code> added to <code>TextComparisonResult</code> will indicate the 
     * index (column) of the first occurrence of non-matching characters between a set of lines.
     * 
     * @param source - path of the source text file for comparison
     * @param target - path of the target text file for comparison
     * @return an instance of {@link TextComparisonResult}
     */
    public static TextComparisonResult compareFiles(final String source, final String target) {

    	if(source == null || target == null) {
    		return new TextComparisonResult(false, "Cannot perform line comparison because one of the file paths is null", null);
    	}
    	
    	final File sourceFile = new File(source);
    	final File targetFile = new File(target);
    	
    	if(!sourceFile.exists() || !targetFile.exists()) {
    		return new TextComparisonResult(false, "Cannot perform line comparison because one of the files does not exist", null);
    	}
    	
    	List<String> sourceLines;
    	List<String> targetLines;
    	try {
	    	// Get the lines of text for the source and target files
	    	sourceLines = getLinesFromFile(sourceFile);
	    	targetLines = getLinesFromFile(targetFile);
    	}
    	catch(IOException ioe) {
    		return new TextComparisonResult(false, ioe.getMessage(), null);
    	}
    	
    	if(sourceLines.isEmpty() || targetLines.isEmpty()) {
    		return new TextComparisonResult(false, "Cannot perform line comparison because one of the files contains no text", null);
    	}
    	else if(sourceLines.size() != targetLines.size()) {
    		return new TextComparisonResult(false, "Files do not contain the same number of lines " +
    				"(source file contains " + sourceLines.size() + " line(s), target file contains " + targetLines.size() + " line(s))", null);
    	}
    	
    	// Compare target lines to source lines and record diffs (if any) along the way
    	final List<TextComparisonEvent> lineComparisonEvents = new ArrayList<TextComparisonEvent>();
    	TextComparisonEvent textComparisonEvent = null;
    	final int numLines = sourceLines.size();
    	for(int i = 0; i < numLines; i++) {
    		textComparisonEvent = compareText(sourceLines.get(i), targetLines.get(i), i);
    		if(textComparisonEvent != null) {
    			lineComparisonEvents.add(textComparisonEvent);
    		}
    	}
    	
    	// Return the comparison results
    	if(!lineComparisonEvents.isEmpty()) {
    		final int numComparisonEvents = lineComparisonEvents.size();
    		return new TextComparisonResult(
    				false, 
    				"Found [" + numComparisonEvents + "] non-matching line" + (numComparisonEvents > 1 ? "s" : ""), 
    				lineComparisonEvents);
    	}
    	else {
    		return new TextComparisonResult(true, "", null);
    	}
    }
    
    private static TextComparisonEvent compareText(final String sourceText, final String targetText, final int lineNumber) {
    	
    	if(sourceText.length() != targetText.length()) {
    		return new TextComparisonEvent(sourceText, targetText, lineNumber + 1, 0);
    	}
    	
    	final StringCharacterIterator sourceTextIterator = new StringCharacterIterator(sourceText);
    	final StringCharacterIterator targetTextIterator = new StringCharacterIterator(targetText);
    	char sourceChar = sourceTextIterator.first();
		char targetChar = targetTextIterator.first();
		while(sourceChar != StringCharacterIterator.DONE) {
			if(sourceChar != targetChar) {
				return new TextComparisonEvent(sourceText, targetText, lineNumber + 1, (sourceTextIterator.getIndex() + 1));
			}
			else {
				sourceChar = sourceTextIterator.next();
	    		targetChar = targetTextIterator.next();
			}
		}
		
		return null;
    }
    
    private static List<String> getLinesFromFile(final File file) throws IOException {
    	final List<String> linesFromFile = new ArrayList<String>();
    	BufferedReader bufferedReader = null;
    	try {
    		
    		bufferedReader = new BufferedReader(new FileReader(file));
    		
			String line = null;
	    	while((line = bufferedReader.readLine()) != null) {
	    		linesFromFile.add(new String(line.getBytes(DEFAULT_CHARSET)));
	    	}
    	}
    	finally {
    		if(bufferedReader != null) {
    			bufferedReader.close();
    		}
    	}
    	return linesFromFile;
    }
    
    /**
     * Bean class used to record text comparison results.
     * 
     * <p>
     * Use {@link TextComparisonResult#filesMatch} on instances of this class to determine
     * whether or not file comparison passed. For failed comparisons, the cause of the failure
     * can be retrieved by calling {@link TextComparisonResult#getMessage()} and the individual
     * comparison events (if any) can be retrieved by calling 
     * {@link TextComparisonResult#getTextComparisonEvents()}.
     */
    public static class TextComparisonResult {
    	
    	private boolean filesMatch;
    	private String message;
    	private List<TextComparisonEvent> lineComparisonEvents;
    	
    	@SuppressWarnings("unused")
		private TextComparisonResult() {
    		
    	}
    	
    	public TextComparisonResult(final boolean filesMatch, final String message, final List<TextComparisonEvent> lineComparisonEvents) {
    		this.filesMatch = filesMatch;
    		this.message = message;
    		this.lineComparisonEvents = lineComparisonEvents;
    	}
    	
    	/**
		 * @return the filesMatch
		 */
		public boolean filesMatch() {
			return filesMatch;
		}
		
		/**
		 * @return the message
		 */
		public String getMessage() {
			return message;
		}
		
		/**
		 * @return the lineComparisonEvents
		 */
		public List<TextComparisonEvent> getTextComparisonEvents() {
			return lineComparisonEvents;
		}
		
		@Override
		public String toString() {
			final StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("\nFiles match: " + filesMatch + "\n");
			stringBuilder.append("Message: " + message + "\n");
			stringBuilder.append("Number of line comparison events: " + (lineComparisonEvents == null ? 0 : lineComparisonEvents.size()));
			stringBuilder.append("\n\n");
			return stringBuilder.toString();
		}
    }
    
    /**
     * Bean class used to record text comparison events.
     */
    public static class TextComparisonEvent {
    	
    	private String sourceLine;
    	private String targetLine;
    	private int line = 0;
    	private int column = 0;
    	
    	@SuppressWarnings("unused")
		private TextComparisonEvent() {
    		
    	}
    	
    	public TextComparisonEvent(final String sourceLine, final String targetLine, final int line, final int column) {
    		this.sourceLine = sourceLine;
    		this.targetLine = targetLine;
    		this.line = line;
    		this.column = column;
    	}
    	
    	/**
		 * @return the sourceLine
		 */
		public String getSourceLine() {
			return sourceLine;
		}
		
		/**
		 * @return the targetLine
		 */
		public String getTargetLine() {
			return targetLine;
		}
		
		/**
		 * @return the line
		 */
		public int getLine() {
			return line;
		}
		
		/**
		 * @return the column
		 */
		public int getColumn() {
			return column;
		}
		
		@Override
		public String toString() {
			final StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("\nSource line: " + sourceLine + "\n");
			stringBuilder.append("Target line: " + targetLine + "\n");
			stringBuilder.append("Non-matching location: line " + line + ", column " + column);
			stringBuilder.append("\n\n");
			return stringBuilder.toString();
		}
    }
}
