package gov.nih.nci.ncicb.tcgaportal.level4.dao.dbunit.util;

import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterChromRegion;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterSpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.AggregateMutationType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.CopyNumberType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ExpressionType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.MutationType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.UpperAndLowerLimits;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Description :   Used to parse the input XML files and set the values in FilterSpecifier object
 *
 * @author Namrata Rane
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class FilterSpecifierXmlInputParser {

    FilterSpecifier filter;

    public void parseInputFile(String inputFileName, FilterSpecifier filter) throws TransformerException, XPathExpressionException, IOException, SAXException, ParserConfigurationException {
        this.filter = filter;
        File fileName = new File(inputFileName);
        XmlParser parser = new XPathXmlParser();
        //Get the main node list.
        NodeList nodes = parser.getNodes(parser.parseXmlFile(fileName.getCanonicalPath(), false), "//filter");

        String disease = "";
        String listBy = "";
        String patientList = "";
        String geneList = "";
        List<ColumnType> columnsList = new ArrayList<ColumnType>();

        for (int i = 0; i < nodes.getLength(); i++) {

            Element elem = (Element) nodes.item(i);
            NodeList childNodes = elem.getChildNodes();

            for (int cNode = 0; cNode < childNodes.getLength(); cNode++) {

                Node tempNode = childNodes.item(cNode);
                if (tempNode.getNodeName().equals("disease")) {
                    disease = tempNode.getTextContent().trim();
                    filter.setDisease(disease);
                } else if (tempNode.getNodeName().equals("listBy")) {
                    listBy = tempNode.getTextContent().trim();
                    filter.setListBy(getListBy(listBy));
                } else if (tempNode.getNodeName().equals("patientListOptions")) {
                    if (getAttributeValue(tempNode, "value").equals("List")) {
                        filter.setPatientListOptions(FilterSpecifier.PatientListOptions.List);
                        for (int pNode = 0; pNode < tempNode.getChildNodes().getLength(); pNode++) {
                            Node patientListNode = tempNode.getChildNodes().item(pNode);
                            if (!patientList.equals(""))
                                patientList += ",";
                            patientList += patientListNode.getTextContent().trim();

                        }
                        filter.setPatientList(patientList);
                    } else if (getAttributeValue(tempNode, "value").equals("All")) {
                        filter.setPatientListOptions(FilterSpecifier.PatientListOptions.All);

                    }
                } else if (tempNode.getNodeName().equals("geneListOptions")) {
                    NamedNodeMap nodeMap = tempNode.getAttributes();
                    if (getAttributeValue(tempNode, "value").equals("List")) {
                        filter.setGeneListOptions(FilterSpecifier.GeneListOptions.List);
                        for (int gNode = 0; gNode < tempNode.getChildNodes().getLength(); gNode++) {
                            Node geneListNode = tempNode.getChildNodes().item(gNode);
                            if (!geneList.equals(""))
                                geneList += ",";
                            geneList += geneListNode.getTextContent().trim();
                        }
                        filter.setGeneList(geneList);
                    } else if (getAttributeValue(tempNode, "value").equals("All")) {
                        filter.setGeneListOptions(FilterSpecifier.GeneListOptions.All);
                    }
                } else if (tempNode.getNodeName().equals("chromList")) {
                    //set the crom start and stop in filter
                    for (int columnNodeNo = 0; columnNodeNo < tempNode.getChildNodes().getLength(); columnNodeNo++) {
                        Node columnNode = tempNode.getChildNodes().item(columnNodeNo);
                        // get column type
                        // and create a column of that type
                        // set all the values accordingly
                        FilterChromRegion chromRegion = getChromRegion(columnNode);
                        //added null check for chromosome, what good is chromRegion without chromosome
                        if (chromRegion != null && chromRegion.getChromosome() != null) {
                            filter.setGeneListOptions(FilterSpecifier.GeneListOptions.Region);
                            filter.addChromRegion(chromRegion);
                        }
                    }
                } else if (tempNode.getNodeName().equals("pickedColumns")) {

                    int pickedColSize = Integer.parseInt(getAttributeValue(tempNode, "pickedColumnSize"));
                    if (pickedColSize > 0) {
                        for (int columnNodeNo = 0; columnNodeNo < tempNode.getChildNodes().getLength(); columnNodeNo++) {
                            Node columnNode = tempNode.getChildNodes().item(columnNodeNo);
                            // get column type
                            // and create a column of that type
                            // set all the values accordingly
                            ColumnType column = getColumn(columnNode);
                            if (column != null) {
                                columnsList.add(column);
                            }

                        }
                    }
                    filter.setColumnTypes(columnsList);
                }
            }
        }
    }


    private String getAttributeValue(Node node, String attribName) {
        String returnVal = "";
        NamedNodeMap nodeMap = node.getAttributes();
        //added null check for cases where node could have value such as [#text: \n] basically a newline character
        if (nodeMap != null && nodeMap.getNamedItem(attribName) != null)
            returnVal = nodeMap.getNamedItem(attribName).getNodeValue();
        return returnVal;
    }


    private ColumnType getColumn(Node columnNode) {
        ColumnType returnColumn = null;
        if (getAttributeValue(columnNode, "type").equalsIgnoreCase("copyNumber")) {
            CopyNumberType copyNumberCol = new CopyNumberType();
            copyNumberCol.setPicked(true);


            for (int columnNodeNo = 0; columnNodeNo < columnNode.getChildNodes().getLength(); columnNodeNo++) {
                Node columnChild = columnNode.getChildNodes().item(columnNodeNo);

                if (columnChild.getNodeName().equals("calculationType")) {

                    if ((columnChild.getTextContent().trim()).equalsIgnoreCase("gistic")) {
                        copyNumberCol.setCalculationType(CopyNumberType.CalculationType.GISTIC);
                    } else {
                        copyNumberCol.setCalculationType(CopyNumberType.CalculationType.Regular);
                    }
                } else if (columnChild.getNodeName().equals("limits")) {

                    for (int operatorNodeNo = 0; operatorNodeNo < columnChild.getChildNodes().getLength(); operatorNodeNo++) {
                        Node operatorChild = columnChild.getChildNodes().item(operatorNodeNo);
                        if (operatorChild.getNodeName().equals("uLimitOperator")) {
                            copyNumberCol.setUpperOperator(
                                    UpperAndLowerLimits.Operator.fromString(operatorChild.getTextContent().trim()));
                        } else if (operatorChild.getNodeName().equals("lLimitOperator")) {
                            copyNumberCol.setLowerOperator(
                                    UpperAndLowerLimits.Operator.fromString(operatorChild.getTextContent().trim()));
                        } else if (operatorChild.getNodeName().equals("uLimit")) {
                            copyNumberCol.setUpperLimit(Float.valueOf(operatorChild.getTextContent().trim()));
                        } else if (operatorChild.getNodeName().equals("lLimit")) {
                            copyNumberCol.setLowerLimit(Float.valueOf(operatorChild.getTextContent().trim()));
                        }
                    }
                } else if (columnChild.getNodeName().equals("platformTypeId")) {
                    copyNumberCol.setPlatformType(Integer.parseInt(columnChild.getTextContent().trim()));
                } else if (columnChild.getNodeName().equals("anomalyTypeId")) {
                    copyNumberCol.setAnomalyTypeId(Integer.parseInt(columnChild.getTextContent().trim()));
                } else if (columnChild.getNodeName().equals("datasetId")) {
                    copyNumberCol.setDataSetId(Integer.parseInt(columnChild.getTextContent().trim()));
                } else if (columnChild.getNodeName().equals("displayPlatformType")) {
                    copyNumberCol.setDisplayPlatformType(columnChild.getTextContent().trim());
                } else if (columnChild.getNodeName().equals("displayCenter")) {
                    copyNumberCol.setDisplayCenter(columnChild.getTextContent().trim());
                } else if (columnChild.getNodeName().equals("displayPlatform")) {
                    copyNumberCol.setDisplayPlatform(columnChild.getTextContent().trim());
                } else if (columnChild.getNodeName().equals("frequency")) {
                    copyNumberCol.setFrequency(Float.valueOf(columnChild.getTextContent().trim()));
                } else if (columnChild.getNodeName().equals("colTypeId")) {
                    // ignore for not
                    // todo : check if this is needed
                }
            }
            returnColumn = copyNumberCol;
        } else if (getAttributeValue(columnNode, "type").equalsIgnoreCase("geneExpression")) {
            ExpressionType geneExpCol = new ExpressionType();
            geneExpCol.setPicked(true);

            for (int columnNodeNo = 0; columnNodeNo < columnNode.getChildNodes().getLength(); columnNodeNo++) {
                Node columnChild = columnNode.getChildNodes().item(columnNodeNo);

                if (columnChild.getNodeName().equals("limits")) {

                    for (int operatorNodeNo = 0; operatorNodeNo < columnChild.getChildNodes().getLength(); operatorNodeNo++) {
                        Node operatorChild = columnNode.getChildNodes().item(operatorNodeNo);
                        if (operatorChild.getNodeName().equals("uLimitOperator")) {
                            geneExpCol.setUpperOperator(
                                    UpperAndLowerLimits.Operator.fromString(operatorChild.getTextContent().trim()));
                        } else if (operatorChild.getNodeName().equals("lLimitOperator")) {
                            geneExpCol.setLowerOperator(
                                    UpperAndLowerLimits.Operator.fromString(operatorChild.getTextContent().trim()));
                        }
                        if (operatorChild.getNodeName().equals("uLimit")) {
                            geneExpCol.setUpperLimit(Float.valueOf(operatorChild.getTextContent().trim()));
                        } else if (operatorChild.getNodeName().equals("lLimit")) {
                            geneExpCol.setLowerLimit(Float.valueOf(operatorChild.getTextContent().trim()));
                        }
                    }
                } else if (columnChild.getNodeName().equals("platformTypeId")) {
                    geneExpCol.setPlatformType(Integer.parseInt(columnChild.getTextContent().trim()));
                } else if (columnChild.getNodeName().equals("anomalyTypeId")) {
                    geneExpCol.setAnomalyTypeId(Integer.parseInt(columnChild.getTextContent().trim()));
                } else if (columnChild.getNodeName().equals("datasetId")) {
                    geneExpCol.setDataSetId(Integer.parseInt(columnChild.getTextContent().trim()));
                } else if (columnChild.getNodeName().equals("displayPlatformType")) {
                    geneExpCol.setDisplayPlatformType(columnChild.getTextContent().trim());
                } else if (columnChild.getNodeName().equals("displayCenter")) {
                    geneExpCol.setDisplayCenter(columnChild.getTextContent().trim());
                } else if (columnChild.getNodeName().equals("displayPlatform")) {
                    geneExpCol.setDisplayPlatform(columnChild.getTextContent().trim());
                } else if (columnChild.getNodeName().equals("frequency")) {
                    geneExpCol.setFrequency(Float.valueOf(columnChild.getTextContent().trim()));
                } else if (columnChild.getNodeName().equals("colTypeId")) {
                    // ignore for not
                    // todo : check if this is needed
                }
            }
            returnColumn = geneExpCol;

        } else if (getAttributeValue(columnNode, "type").equalsIgnoreCase("somaticMutation")) {
            AggregateMutationType aggregateMutationCol = new AggregateMutationType();
            aggregateMutationCol.setPicked(true);

            for (int columnNodeNo = 0; columnNodeNo < columnNode.getChildNodes().getLength(); columnNodeNo++) {
                Node columnChild = columnNode.getChildNodes().item(columnNodeNo);

                if (columnChild.getNodeName().equals("category")) {
                    MutationType.Category category = MutationType.getCategoryForName(columnChild.getTextContent().trim());
                    aggregateMutationCol.setCategory(category);
                } else if (columnChild.getNodeName().equals("frequency")) {
                    aggregateMutationCol.setFrequency(Float.valueOf(columnChild.getTextContent().trim()));
                }

                //todo : set the category
                /*
                List<MutationType> types = mutationTypes.get(category);
                mutationType.setMutationTypes(types);
                */

            }
            returnColumn = aggregateMutationCol;
        }

        return returnColumn;
    }

    private FilterChromRegion getChromRegion(Node columnNode) {
        FilterChromRegion chromRegion = new FilterChromRegion();
        for (int columnNodeNo = 0; columnNodeNo < columnNode.getChildNodes().getLength(); columnNodeNo++) {
            Node columnChild = columnNode.getChildNodes().item(columnNodeNo);

            if (columnChild.getNodeName().equals("chromName")) {
                chromRegion.setChromosome(columnChild.getTextContent().trim());
            } else if (columnChild.getNodeName().equals("start")) {
                chromRegion.setStart(Long.parseLong(columnChild.getTextContent().trim()));
            } else if (columnChild.getNodeName().equals("stop")) {
                chromRegion.setStop(Long.parseLong(columnChild.getTextContent().trim()));
            }
        }
        return chromRegion;
    }


    private FilterSpecifier.ListBy getListBy(String property) {
        if (property.equalsIgnoreCase("gene"))
            return FilterSpecifier.ListBy.Genes;
        else if (property.equalsIgnoreCase("patient"))
            return FilterSpecifier.ListBy.Patients;
        else if (property.equalsIgnoreCase("pathway"))
            return FilterSpecifier.ListBy.Pathways;
        else return null; // todo handle null value ?
    }

    public static void main(String[] args) {
        String portalFolder = Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
        FilterSpecifierXmlInputParser parser = new FilterSpecifierXmlInputParser();
        FilterSpecifier filter = null;
        try {

            filter = new FilterSpecifier();
            parser.parseInputFile(portalFolder + "input_testNoColumnsWithChromosome.xml", filter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}