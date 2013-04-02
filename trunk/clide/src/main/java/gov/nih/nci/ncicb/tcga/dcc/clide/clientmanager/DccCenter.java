/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.clide.clientmanager;

import org.apache.commons.lang.StringUtils;
/**
 * Enum is used to list centers for client manager
 * @author girshiks
 */

public enum DccCenter {


	
	UNC("UNC"), BROAD("BROAD"), 
	JHU("JHU"),MDA("MDA"),
	WUSM("WUSM"),NCH("NCH"),
	BI("BI"),BCM("BCM"),
	HAIB("HAIB"),MSKCC("MSKCC"),
	LBL("LBL"),HMS("HMS"),
	JHU_USC("JHU-USC");

	private String centerName;	
	
	DccCenter(String newCenterName) {
		this.centerName = newCenterName;
	}

	public static DccCenter fromString(String centerToConvert) {
		if (StringUtils.isNotEmpty(centerToConvert)) {
			for (DccCenter center : DccCenter.values()) {
				if (center.centerName.equalsIgnoreCase(centerToConvert)) {
					return center;
				} 
			}
		}
		return null;

	}

}