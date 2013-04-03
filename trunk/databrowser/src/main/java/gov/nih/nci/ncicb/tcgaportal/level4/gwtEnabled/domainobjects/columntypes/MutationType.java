/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes;

/**
 * ColumnType representing a mutation.
 *
 * @author David Nassau
 *         Last updated by: $Author: whitmore $
 * @version $Rev: 9303 $
 */
public class MutationType extends AnomalyType {
    public static float DEFAULT_RATIO_THRESHOLD = 0.01f;

    public enum Category {
        None() {
            public String toString() {
                return "";
            }
            public String getDisplayName() {
                return "";
            }
        }, Missense() {
            public String toString() {
                return "Missense";
            }
            public String getDisplayName() {
                return "Missense";
            }
        }, Nonsense() {
            public String toString() {
                return "Nonsense";
            }
            public String getDisplayName() {
                return "Nonsense";
            }
        }, Frameshift() {
            public String toString() {
                return "Frameshift Indel";
            }
            public String getDisplayName() {
                return "Frameshift Indel";
            }
        }, InFrameIndel() {
            public String toString() {
                return "In-Frame Indel";
            }
            public String getDisplayName() {
                return "In-Frame Indel";
            }
        }, SpliceSite() {
            public String toString() {
                return "SpliceSite";
            }
            public String getDisplayName() {
                return "Splice Site";
            }
        }, AnyNonSilent() {
            public String toString() {
                return "AnyNonSilent";
            }
            public String getDisplayName() {
                return "Any Non-Silent";
            }
        }, Silent() {
            public String toString() {
                return "Silent";
            }
            public String getDisplayName() {
                return "Silent";
            }
        };

        public abstract String getDisplayName();
    }

    protected Category category = Category.None;

    public static Category getCategoryForName(String categoryName) {
        if (categoryName.equalsIgnoreCase(Category.Missense.toString())) {
            return Category.Missense;
        } else if (categoryName.equalsIgnoreCase(Category.Nonsense.toString())) {
            return Category.Nonsense;
        } else if (categoryName.equalsIgnoreCase(Category.Frameshift.toString())) {
            return Category.Frameshift;
        } else if (categoryName.equalsIgnoreCase(Category.SpliceSite.toString())) {
            return Category.SpliceSite;
        } else if (categoryName.equalsIgnoreCase(Category.Silent.toString())) {
            return Category.Silent;
        } else if (categoryName.equalsIgnoreCase(Category.AnyNonSilent.toString())) {
            return Category.AnyNonSilent;
        } else if (categoryName.equalsIgnoreCase(Category.InFrameIndel.toString())) {
            return Category.InFrameIndel;
        } else {
            return Category.None;
        }
    }

    public String getDisplayName() {
        return category.getDisplayName();
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public boolean equals(Object other) {
        if (!(other instanceof MutationType)) {
            return false;
        }
        if (!super.equals(other)) {
            return false;
        }
        MutationType at = (MutationType) other;
        return at.getCategory() == getCategory();
    }

    //can't use Object.clone because GWT doesn't support it
    public Object cloneColumn() {
        MutationType column = (MutationType) super.cloneColumn();
        column.setCategory(getCategory());
        return column;
    }

    protected ColumnType instanceForClone() {
        return new MutationType();
    }

    protected float getDefaultRatioThreshold() {
        return DEFAULT_RATIO_THRESHOLD;
    }

    public String getDisplayCriteria(String formattedFrequency) {
        StringBuilder sCrit = new StringBuilder();
        sCrit.append(category.toString());
        if (formattedFrequency != null && formattedFrequency.length() > 0) {
            sCrit.append(sCrit.length() > 0 ? ", " : "").append("Frequency >= ").append(formattedFrequency);
        }
        return sCrit.toString();
    }
}
