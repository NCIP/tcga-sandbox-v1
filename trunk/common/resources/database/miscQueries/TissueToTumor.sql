select tt.tissue_id,t.tissue,tt.tumor_id,tu.tumor_abbreviation, tu.tumor_name
 from tissue_to_tumor tt, tissue_info t, tumor_info tu
 where tt.tissue_id=t.id
 and tt.tumor_id=tu.id
 order by tt.tumor_id;
