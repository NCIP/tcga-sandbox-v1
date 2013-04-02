/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.view;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.DataAccessMatrixJSPUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * author: David Nassau
 * <p/>
 * model that supports the DAD page, in particular the file tree
 */
public class DataAccessDownloadModel {
    public boolean isFlatten() {
        return flatten;
    }

    public void setFlatten(final boolean flatten) {
        this.flatten = flatten;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getEmail2() {
        return email2;
    }

    public void setEmail2(final String email2) {
        this.email2 = email2;
    }

    public Collection<Archive> getOriginalArchives() {
        return originalArchives;
    }

    public void setOriginalArchives(final Collection<Archive> originalArchives) {
        this.originalArchives = originalArchives;
    }

    enum NodeCategory {

        Root, PlatformType, Center, Level, File
    }

    /**
     * Forms a hierarchical tree of folders and files which we keep in memory.
     * From this, we write out XML that goes to the client and is rendered as a tree
     * by the DHTMLX javascript component.
     */
    public class TreeNode {

        private String name, treeNodeId, fileOrFolderName, fileId, sample;
        private List<TreeNode> children;
        private long fileSize;
        private boolean isProtected, isFolder;
        private NodeCategory cat;
        private DataFile dataFile;

        TreeNode(final String name, final NodeCategory cat) {
            this.name = name;
            this.cat = cat;
        }

        //"tree" or "item" for tree xml
        String getName() {
            return name;   //todo: use an enum instead?
        }

        TreeNode addChild(final String name, final NodeCategory cat) {
            if (children == null) {
                children = new ArrayList<TreeNode>();
            }
            final TreeNode ret = new TreeNode(name, cat);
            children.add(ret);
            return ret;
        }

        boolean hasChildren() {
            return (children != null && children.size() > 0);
        }

        List<TreeNode> getChildren() {
            return children;
        }

        void setFileSize(final long value) {
            fileSize = value;
        }

        long getFileSize() {
            return fileSize;
        }

        void setProtected(final boolean value) {
            isProtected = value;
        }

        boolean isProtected() {
            return isProtected;
        }

        //file name that is displayed in the tree
        void setFileOrFolderName(final String value) {
            fileOrFolderName = value;
        }

        String getFileOrFolderName() {
            return fileOrFolderName;
        }

        //internal file identifier - not displayed but used after selection to grab the actual file
        void setFileId(final String value) {
            fileId = value;
        }

        String getFileId() {
            return fileId;
        }

        //numeric treeNodeId used by the tree component (it has to be a number even though they don't say that)
        void setTreeNodeId(final String value) {
            treeNodeId = value;
        }

        String getTreeNodeId() {
            return treeNodeId;
        }

        void setFolder(final boolean value) {
            isFolder = value;
        }

        boolean isFolder() {
            return isFolder;
        }

        NodeCategory getCategory() {
            return cat;
        }

        public String getSample() {
            return sample;
        }

        public void setSample(final String sample) {
            this.sample = sample;
        }

        public void setDataFile(final DataFile fileInfo) {
            this.dataFile = fileInfo;
        }

        public DataFile getDataFile() {
            return dataFile;
        }
    }

    public static final String COLOR_PROTECTED = "red";
    private String diseaseType;
    private TreeNode rootNode;
    private String email;
    private String email2;
    private int nextId = 0;
    private int sizeLimitGigs = -1;
    private boolean fromExternalFilter;
    private boolean consolidateFiles;
    private boolean flatten;
    private Collection<Archive> originalArchives;

    /**
     * @param diseaseType e.g., "GBM"
     */
    public DataAccessDownloadModel(final String diseaseType) {
        this(diseaseType, -1); //no size limit
    }

    public DataAccessDownloadModel(final String diseaseType, final int sizeLimitGigs) {
        this.diseaseType = diseaseType;
        this.sizeLimitGigs = sizeLimitGigs;
        rootNode = new TreeNode("tree", NodeCategory.Root);
        rootNode.setFolder(true);
        rootNode.setTreeNodeId("" + nextId++);
    }

    public String getDiseaseType() {
        return diseaseType;
    }

    public TreeNode getRootNode() {
        return rootNode;
    }

    //for use with external filtering - doesn't make sense to go back to matrix
    public void setFromExternalFilter(final boolean fromExternalFilter) {
        this.fromExternalFilter = fromExternalFilter;
    }

    public boolean isFromExternalFilter() {
        return fromExternalFilter;
    }

    public int getSizeLimitGigs() {
        return sizeLimitGigs;
    }

    public void setConsolidateFiles(final boolean consolidateFiles) {
        this.consolidateFiles = consolidateFiles;
    }

    public boolean isConsolidateFiles() {
        return consolidateFiles;
    }

    /**
     * Receives two lists: one of data sets, the other of files. This class is then responsible for
     * merging these two lists by collating on barcode.
     *
     * @param fileList List of FileInfo objects representing files corresponding to the DataSets in the first list
     */
    public void addFileInfo(final List<DataFile> fileList) {
        for (final DataFile fileInfo : fileList) {
            addFileInfo(fileInfo);
        }
        setProtectedFlagsOnFolders(rootNode);
    }

    private void addFileInfo(final DataFile fileInfo) {
        final String[] folders = new String[3];
        folders[0] = fileInfo.getPlatformTypeId();
        folders[1] = fileInfo.getCenterId() + (fileInfo.getPlatformId() == null ? "" : ("." + fileInfo.getPlatformId()));
        folders[2] = fileInfo.getLevel();
        final TreeNode folderNode = findOrMakeFolderNode(folders);
        final TreeNode fileNode = folderNode.addChild("item", NodeCategory.File);
        fileNode.setDataFile(fileInfo); // ToDo: added because we cannot be recreating DataFile outside of the DataAccessDownloadQueries
        fileNode.setFolder(false);
        fileNode.setTreeNodeId("" + nextId++);
        fileNode.setFileOrFolderName(fileInfo.getFileName());
        fileNode.setFileSize(fileInfo.getSize());
        fileNode.setProtected(fileInfo.isProtected());
        fileNode.setFileId(fileInfo.getFileId());
        fileNode.setSample(fileInfo.getDisplaySample());
    }

    //if ANY of the children are flagged as protected, flag the parent too
    private boolean setProtectedFlagsOnFolders(final TreeNode node) {
        boolean setProtected;
        if (node.hasChildren()) {
            boolean anyProtected = false;
            for (final TreeNode child : node.getChildren()) {
                anyProtected = (anyProtected | setProtectedFlagsOnFolders(child));
            }
            node.setProtected(anyProtected);
            setProtected = anyProtected;
        } else {
            setProtected = node.isProtected();
        }
        return setProtected;
    }

    private TreeNode findOrMakeFolderNode(final String[] folders) {
        TreeNode parent = rootNode;
        for (int i = 0; i < folders.length; i++) {
            final String folderName = folders[i];
            NodeCategory cat = null;
            switch (i) {
                case 0:
                    cat = NodeCategory.PlatformType;
                    break;
                case 1:
                    cat = NodeCategory.Center;
                    break;
                case 2:
                    cat = NodeCategory.Level;
                    break;
            }
            if (folderName != null) {
                TreeNode folderNode = findFolderNode(parent, folderName);
                if (folderNode == null) {
                    folderNode = parent.addChild("item", cat);
                    folderNode.setFolder(true);
                    folderNode.setTreeNodeId("" + nextId++);
                    folderNode.setFileOrFolderName(folderName);
                }
                parent = folderNode;
            } //else throw exception for null folder name?
        }
        return parent;
    }

    private TreeNode findFolderNode(final TreeNode parent, final String folderName) {
        TreeNode ret = null;
        if (parent.hasChildren()) {
            for (final TreeNode child : parent.getChildren()) {
                if (child.isFolder() && folderName.equals(child.getFileOrFolderName())) { //folderName.equals(child.getAttribute("text"))) {
                    ret = child;
                    break;
                }
            }
        }
        return ret;
    }

    /**
     * outputs XML specifically formatted for dhtmlx tree component on web page
     *
     * @return XML formatted for dhtmlx tree
     */
    public String getXmlForTree() {
        final StringBuffer buf = new StringBuffer();
        writeXml(buf, rootNode, -1);
        return buf.toString();
    }

    private void writeXml(final StringBuffer buf, final TreeNode node, int levels) {
        //todo: have a "skipLevel" property in DataFile, rather than using special case?
        final boolean clinicalLevelFolder = (node.isFolder() && DataAccessMatrixQueries.LEVEL_CLINICAL.equals(node.getFileOrFolderName()));
        final boolean metadataLevelFolder = (node.isFolder() && DataAccessMatrixQueries.LEVEL_METADATA.equals(node.getFileOrFolderName()));
        if (clinicalLevelFolder || metadataLevelFolder) {
            levels--;
        } else {
            writeXMLNodeText(buf, node); //skip node for nonexistent clinical level
        }
        if (levels != 0 && node.hasChildren()) {
            for (final TreeNode child : node.getChildren()) {
                writeXml(buf, child, levels - 1); //if we start with negative level, we'll get ALL levels, otherwise just as specified
            }
        }
        if (!(clinicalLevelFolder || metadataLevelFolder)) {
            buf.append("</").append(node.getName()).append(">");
        }
    }

    private void writeXMLNodeText(final StringBuffer buf, final TreeNode node) {
        buf.append("<").append(node.getName()).append(" ");
        buf.append("id='").append(node.getTreeNodeId()).append("' ");
        if (node.isFolder()) {
            buf.append("child='1' ");
            buf.append("im1='folderOpen.gif' ");
            buf.append("im2='folderClosed.gif' ");
            buf.append("text='").append(getFileOrFolderName(node));
            if (node.isProtected()) {
                buf.append(" (contains protected)");
            }
            buf.append("' ");
        } else {
            buf.append("child='0' ");
            buf.append("im1='").append("leaf.gif").append("' ");
            buf.append("text='").append(node.getSample()).append("::").append(node.getFileOrFolderName());
            if (node.isProtected()) {
                buf.append(" (protected)");
            }
            buf.append(" (").append(FileUtil.getFormattedFileSize(node.getFileSize())).append(")").append("' ");
        }
        if (node.isProtected()) {
            buf.append("aCol='").append(COLOR_PROTECTED).append("' ");
        }
        buf.append(">");
    }

    private String getFileOrFolderName(final TreeNode node) {
        String ret = node.getFileOrFolderName();
        if (node.getCategory() == NodeCategory.Center || node.getCategory() == NodeCategory.PlatformType) {
            final Header.HeaderCategory cat = (node.getCategory() == NodeCategory.Center ? Header.HeaderCategory.Center : Header.HeaderCategory.PlatformType);
            ret = DataAccessMatrixJSPUtil.lookupHeaderText(cat, node.getFileOrFolderName());
        } else if (node.getCategory() == NodeCategory.Level && !"".equals(ret)) {
            //level, not clinical. Add "Level" to return
            ret = "Level " + ret;
        }
        return ret;
    }

    /**
     * Converts numeric "treenode ids" into file Ids that can be handed to the database.
     * We assume that the list of IDs is already in the order of a tree recursion - this speeds things greatly
     *
     * @param treeNodeIds comma-separated list of treenode ids
     * @return a list of DataFiles representing the given treenodes
     */
    public List<DataFile> getFileInfoForSelectedTreeNodes(final String treeNodeIds) {
        final List<DataFile> ret = new ArrayList<DataFile>();
        final List<String> idList = new ArrayList<String>();
        final StringTokenizer st = new StringTokenizer(treeNodeIds, ",");
        while (st.hasMoreTokens()) {
            idList.add(st.nextToken());
        }
        final int[] idIndex = new int[1];
        final String[] folders = new String[3];
        idIndex[0] = 0;
        findFileInfoForId(rootNode, idList, idIndex, ret, folders, 0);
        return ret;
    }

    private void findFileInfoForId(final TreeNode thisNode, final List<String> idList, final int[] idIndex,
                                   final List<DataFile> ret, final String[] folders, final int currentLevel) {
        if (idList.get(idIndex[0]).equals(thisNode.getTreeNodeId())) {
            idIndex[0]++;
            if (!thisNode.hasChildren()) {   //only write the Id if it's a leaf node
                final DataFile fileInfo = thisNode.getDataFile();
                ret.add(fileInfo);
            }
            if (idIndex[0] >= idList.size()) {
                return;
            }
        }
        if (thisNode.hasChildren()) {
            for (final TreeNode child : thisNode.getChildren()) {
                if (currentLevel < folders.length) {
                    folders[currentLevel] = child.getFileOrFolderName();
                }
                findFileInfoForId(child, idList, idIndex, ret, folders, currentLevel + 1);
                if (idIndex[0] >= idList.size()) {
                    return;
                }
            }
        }
    }

    /**
     * Used in JavaScript to calculate download size on the fly.
     *
     * @return a map of tree node Ids and file sizes.
     */
    public Map<String, Long> getFileSizes() {
        final Map<String, Long> ret = new HashMap<String, Long>();
        getFileSizesFromTree(rootNode, ret);
        return ret;
    }

    private void getFileSizesFromTree(final TreeNode node, final Map<String, Long> ret) {
        if (node.hasChildren()) {
            for (final TreeNode child : node.getChildren()) {
                getFileSizesFromTree(child, ret);
            }
        } else {
            ret.put(node.getTreeNodeId(), node.getFileSize());
        }
    }

    /**
     * Used to dynamically display a warning if the user has protected files selected,
     * in case the user does not have an account.
     *
     * @return a list of tree node Ids representing protected files.
     */
    public List<String> getProtectedFileTreeNodeIds() {
        final List<String> ret = new ArrayList<String>();
        gatherProtectedFileTreeNodeIds(rootNode, ret);
        return ret;
    }

    private void gatherProtectedFileTreeNodeIds(final TreeNode node, final List<String> ret) {
        if (node.hasChildren()) {
            for (final TreeNode child : node.getChildren()) {
                gatherProtectedFileTreeNodeIds(child, ret);
            }
        } else {
            if (node.isProtected()) {
                ret.add(node.getTreeNodeId());
            }
        }
    }
}
