package gov.nih.nci.ncicb.tcga.dcc.common.util;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Class taken from http://www.ibm.com/developerworks/java/library/x-nmspccontext/index.html (Listing 10)
 * But then I fixed several bugs in it...
 *
 * Acts as a cache/lookup class for XML namespace prefixes/URIs.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class UniversalNamespaceCache implements NamespaceContext {
    private static final String DEFAULT_NS = "DEFAULT";
    private final Map<String, String> prefix2Uri = new HashMap<String, String>();
    private final Map<String, String> uri2Prefix = new HashMap<String, String>();

    /**
     * This constructor parses the document and stores all namespaces it can
     * find. If toplevelOnly is true, only namespaces in the root are used.
     *
     * @param document source document
     * @param toplevelOnly if true will only look in the root node (not recommended)
     */
    public UniversalNamespaceCache(final Document document, final boolean toplevelOnly) {
        examineNode(document.getDocumentElement(), toplevelOnly);
    }

    private void examineNode(final Node node, final boolean doNotRecurse) {
        final NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                final Node attribute = attributes.item(i);
                    storeAttribute((Attr) attribute);
            }
        }

        if (!doNotRecurse) {
            final NodeList childNodes = node.getChildNodes();
            if (childNodes != null) {
                for (int i = 0; i < childNodes.getLength(); i++) {
                    final Node child = childNodes.item(i);
                    if (child.getNodeType() == Node.ELEMENT_NODE)
                        examineNode(child, false);
                }
            }
        }
    }

    private void storeAttribute(final Attr attribute) {
        // examine the attributes in namespace xmlns
        if (attribute.getNamespaceURI() != null
                && attribute.getNamespaceURI().equals(
                XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) {
            // Default namespace xmlns="uri goes here"
            if (attribute.getNodeName().equals(XMLConstants.XMLNS_ATTRIBUTE)) {
                putInCache(DEFAULT_NS, attribute.getNodeValue());
            } else {
                // The defined prefixes are stored here
                putInCache(attribute.getLocalName(), attribute.getNodeValue());
            }
        }
    }

    private void putInCache(final String prefix, final String uri) {
        prefix2Uri.put(prefix, uri);
        uri2Prefix.put(uri, prefix);
    }

    /**
     * This method is called by XPath. It returns the default namespace, if the
     * prefix is null or "".
     *
     * @param prefix to search for
     * @return uri that corresponds, or null if not found
     */
    public String getNamespaceURI(final String prefix) {
        if (prefix == null || prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
            return prefix2Uri.get(DEFAULT_NS);
        } else {
            return prefix2Uri.get(prefix);
        }
    }

    public String getPrefix(final String namespaceURI) {
        return uri2Prefix.get(namespaceURI);
    }

    public Iterator getPrefixes(final String namespaceURI) {
        return prefix2Uri.keySet().iterator();
    }
}
