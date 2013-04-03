/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.util;

/**
 *
 * @author Jessica Chen
 *         Last updated by: $Author: whitmore $
 * @version $Rev: 9303 $
 */

public interface FishersExact {
    double calculateFisherTwoTail(int totalChanged, int changedInNode, int total,
                                  int inNode);

    double getFisherLeftTail();

    double calculateFisherLeftTail(int totalChanged, int changedInNode, int total,
                                   int inNode);

    double getFisherRightTail();

    double calculateFisherRightTail(int totalChanged, int changedInNode, int total,
                                    int inNode);

    double calculateFisherTwoTail();
}
