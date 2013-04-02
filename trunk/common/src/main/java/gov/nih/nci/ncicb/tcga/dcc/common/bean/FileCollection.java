package gov.nih.nci.ncicb.tcga.dcc.common.bean;

/**
 * Bean representing a file collection, which is a set of files not in an official archive.
 *
 * @author chenjw
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class FileCollection {
    private String name;
    private Long id;

    public void setName(final String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }
}
