/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.util;

/**
 * Created by IntelliJ IDEA.
 * User: nassaud
 * Date: May 13, 2009
 * Time: 10:25:39 AM
 * To change this template use File | Settings | File Templates.
 */
//the Java string tokenizer is not capable of returning null tokens,
// unless you set returnDelims=true, in which case it also returns delimiters as tokens.
// Who wants that?  This version uses Split and returns an empty token as null.
public class StringTokenizerThatCanReturnNull {

    String[] splits;
    int currentIdx;

    public StringTokenizerThatCanReturnNull( String str, String delim ) {
        String[] splitwords = str.split( delim );
        if(str.endsWith( delim )) {
            //split truncates out a trailing delimiter - fix that
            splits = new String[splitwords.length + 1];
            System.arraycopy( splitwords, 0, splits, 0, splitwords.length );
        } else {
            splits = splitwords;
        }
        //replace all "" with null
        for(int i = 0; i < splits.length; i++) {
            if(splits[i] != null && splits[i].length() == 0) {
                splits[i] = null;
            }
        }
        currentIdx = 0;
    }

    public boolean hasMoreTokens() {
        return ( currentIdx < splits.length );
    }

    public String nextToken() {
        if(currentIdx >= splits.length) {
            throw new IllegalStateException( "Attempted read past end of array" );
        }
        return splits[currentIdx++];
    }
}

