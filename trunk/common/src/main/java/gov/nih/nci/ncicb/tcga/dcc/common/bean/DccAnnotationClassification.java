package gov.nih.nci.ncicb.tcga.dcc.common.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Bean representing the classification of an annotation.  This is a more general grouping than AnnotationCategory.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class DccAnnotationClassification implements Comparable<DccAnnotationClassification> {

    @XmlElement(name = "annotationClassificationId")
    private Long annotationClassificationId;

    @XmlElement(name = "annotationClassificationName")
    private String annotationClassificationName;

    @XmlElement(name = "annotationClassificationDescription")
    private String annotationClassificationDescription;

    public Long getAnnotationClassificationId() {
        return annotationClassificationId;
    }

    public void setAnnotationClassificationId(final Long annotationClassificationId) {
        this.annotationClassificationId = annotationClassificationId;
    }

    public String getAnnotationClassificationName() {
        return annotationClassificationName;
    }

    public void setAnnotationClassificationName(final String annotationClassificationName) {
        this.annotationClassificationName = annotationClassificationName;
    }

    public String getAnnotationClassificationDescription() {
        return annotationClassificationDescription;
    }

    public void setAnnotationClassificationDescription(final String annotationClassificationDescription) {
        this.annotationClassificationDescription = annotationClassificationDescription;
    }

    @Override
    public String toString() {
        return annotationClassificationName;
    }

    @Override
    public int compareTo(final DccAnnotationClassification o) {
        return annotationClassificationName.compareTo(o.getAnnotationClassificationName());
    }

}
