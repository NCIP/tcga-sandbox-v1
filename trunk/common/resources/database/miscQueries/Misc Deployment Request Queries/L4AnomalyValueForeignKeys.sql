ALTER TABLE L4_Anomaly_Value ADD( 
    CONSTRAINT FK_AnomalyValue_GeneticElemId 
    FOREIGN KEY (genetic_element_id) REFERENCES L4_Genetic_Element(Genetic_Element_Id),
    CONSTRAINT FK_AnomalyValue_SampleId 
    FOREIGN KEY (sample_id) REFERENCES l4_Sample(Sample_Id),
    CONSTRAINT FK_AnomalyValue_AnomDataSetId 
    FOREIGN KEY (anomaly_data_set_id) REFERENCES L4_Anomaly_Data_Set(anomaly_data_set_id));