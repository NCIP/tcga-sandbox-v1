/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common;

import java.io.Serializable;

/**
 * Holds common context across QCLive steps . This class can be used to pass
 * state between upload, experiment checkers as well as the loaders 
 *
 * @author Stanley Girshik
 *         Last updated by: 
 * @version $Rev: 3419 $
 */
public class QcLiveStateBean implements Serializable{

	private Long transactionId;
	
	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}
}
