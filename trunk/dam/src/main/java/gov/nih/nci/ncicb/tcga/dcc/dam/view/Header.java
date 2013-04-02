/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.view;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a row or column header in the matrix.
 * Headers have a hierarchical structure to represent multi-level headers.
 */
public class Header implements Comparable {

    public enum HeaderType {

        ROW_HEADER, COL_HEADER
    }

    public enum HeaderCategory {

        PlatformType, Center, Level, Batch, Sample, Platform
    }  //Platform is not actually a type of header

    private int id;
    private Header parentHeader;
    private String name;  //a platform type id, or center id, or center + "." platform, or level
    private String tag;   //extra text, such as platform id
    private HeaderCategory category;
    private boolean isProtected;
    private List<Header> childHeaders = new ArrayList<Header>(); //for upper-level headers
    private HeaderType headerType;
    private int rowOrColSpan;
    private int categoryIndex; //functions to sort headers; also as index to 2d array for cells

    public Header() {
    }

    // constructor for testing, since setters aren't public
    public Header( Header.HeaderCategory category, String name, Header parent, Header.HeaderType headerType ) {
        setCategory( category );
        setName( name );
        setParentHeader( parent );
        setHeaderType( headerType );
    }
    //friend access, called from DAMStaticModel

    void setId( final int id ) {
        this.id = id;
    }

    void setParentHeader( final Header parentHeader ) {
        this.parentHeader = parentHeader;
    }

    void setName( final String name ) {
        this.name = name;
    }

    void setCategory( final HeaderCategory category ) {
        this.category = category;
    }

    void setProtected( final boolean aProtected ) {
        isProtected = aProtected;
    }

    void setChildHeaders( final List<Header> childHeaders ) {
        this.childHeaders = childHeaders;
    }
//    void setChildCells(List<Cell> childCells) {
//        this.childCells = childCells;
//    }

    void setHeaderType( final HeaderType headerType ) {
        this.headerType = headerType;
    }

    void setRowOrColSpan( final int rowOrColSpan ) {
        this.rowOrColSpan = rowOrColSpan;
    }

    //public for testing (hack)
    public int getCategoryIndex() {
        return categoryIndex;
    }

    void setCategoryIndex( final int categoryIndex ) {
        this.categoryIndex = categoryIndex;
    }

    /**
     * This is just an internal ID assigned to the header - doesn't correspond to any external data-driven ID
     * It's used by the JSP and JavaScript to communicate with the model.
     * Not to be confused with Center ID, PlatformType ID etc. - those are passed in getName()
     *
     * @return
     */
    public String getId() {
        return "header" + id;
    }

    /**
     * The parent header or null if top-level header.
     *
     * @return
     */
    public Header getParentHeader() {
        return parentHeader;
    }

    public List<Header> getChildHeaders() {
        return childHeaders;
    }

    /**
     * getName actually returns an Id, whether it be a center Id, or platform Type.
     * For level, it returns the actual level "1" or "2".
     *
     * @return
     */
    public String getName() {
        return name;
    }

    public HeaderCategory getCategory() {
        return category;
    }

    public HeaderType getHeaderType() {
        return headerType;
    }

    public int getRowOrColSpan() {
        return rowOrColSpan;
    }

    public boolean isProtected() {
        return isProtected;
    }

    public String getTag() {
        return tag;
    }

    public void setTag( final String tag ) {
        this.tag = tag;
    }

    //Comparable interface
    public int compareTo( final Object o ) {
        if(o instanceof Header) {
            final Header other = (Header) o;
            final int otherSortNo = other.getCategoryIndex();
            int ret = 0;
            if(this.categoryIndex > otherSortNo) {
                ret = 1;
            } else if(this.categoryIndex < otherSortNo) {
                ret = -1;
            }
            return ret;
        } else {
            return -1;
        }
    }
}
