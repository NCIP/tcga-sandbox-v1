-- exit if there is an error
WHENEVER SQLERROR EXIT SQL.SQLCODE
/
ALTER SESSION FORCE PARALLEL DML
/
/*
** delete level 3 data first; use the following select to generate delete statements
*/
delete from expgene_value where data_set_id = 215
/
commit
/
delete from expgene_value where data_set_id = 216
/
commit
/
delete from expgene_value where data_set_id = 217
/
commit
/
delete from expgene_value where data_set_id = 218
/
commit
/
delete from expgene_value where data_set_id = 219
/
commit
/
delete from expgene_value where data_set_id = 220
/
commit
/
delete from expgene_value where data_set_id = 221
/
commit
/
delete from expgene_value where data_set_id = 222
/
commit
/
delete from expgene_value where data_set_id = 277
/
commit
/
delete from expgene_value where data_set_id = 241
/
commit
/
delete from expgene_value where data_set_id = 244
/
commit
/
delete from cna_value where data_set_id = 122
/
commit
/
delete from cna_value where data_set_id = 123
/
commit
/
delete from cna_value where data_set_id = 124
/
commit
/
delete from cna_value where data_set_id = 125
/
commit
/
delete from cna_value where data_set_id = 126
/
commit
/
delete from cna_value where data_set_id = 127
/
commit
/
delete from cna_value where data_set_id = 144
/
commit
/
delete from cna_value where data_set_id = 178
/
commit
/
delete from cna_value where data_set_id = 290
/
commit
/
delete from cna_value where data_set_id = 117
/
commit
/
delete from cna_value where data_set_id = 252
/
commit
/
/*
** delete hybridzation_value records. 
*/
delete from hybridization_value where hybridization_data_group_id = 9 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 17)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 6 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 15)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 7 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 15)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 57 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 55)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 56 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 54)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 97 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 91)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 98 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 91)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 53 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 52)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 54 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 52)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 11 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 19)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 18 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 25)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 19 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 25)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 17 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 24)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 124 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 108)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 107 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 98)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 108 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 98)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 82 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 80)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 91 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 88)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 92 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 88)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 125 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 109)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 83 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 81)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 93 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 89)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 94 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 89)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 109 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 99)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 110 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 99)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 95 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 90)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 96 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 90)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 113 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 102)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 114 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 102)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 126 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 110)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 86 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 83)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 128 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 112)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 115 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 103)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 116 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 103)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 120 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 106)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 121 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 106)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 88 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 85)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 119 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 105)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 118 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 105)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 122 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 107)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 123 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 107)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 130 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 114)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 89 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 86)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 58 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 56)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 207 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 203)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 70 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 68)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 208 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 204)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 71 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 69)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 209 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 205)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 72 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 70)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 210 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 206)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 73 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 71)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 211 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 207)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 74 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 72)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 212 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 208)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 75 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 73)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 213 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 209)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 214 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 210)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 198 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 194)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 59 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 57)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 139 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 143)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 76 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 74)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 77 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 75)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 79 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 77)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 80 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 78)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 172 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 171)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 62 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 60)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 145 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 149)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 144 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 149)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 141 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 146)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 142 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 147)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 143 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 148)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 146 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 150)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 147 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 151)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 148 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 152)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 149 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 153)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 150 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 153)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 63 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 61)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 151 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 154)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 152 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 155)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 153 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 156)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 154 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 157)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 155 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 157)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 111 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 100)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 186 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 184)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 187 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 184)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 294 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 346)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 295 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 347)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 188 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 185)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 189 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 185)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 293 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 345)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 156 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 158)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 181 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 179)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 182 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 180)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 183 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 181)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 184 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 182)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 185 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 183)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 312 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 376)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 313 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 376)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 164 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 165)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 165 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 166)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 166 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 166)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 167 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 167)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 171 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 170)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 168 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 168)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 169 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 169)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 170 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 169)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 60 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 58)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 197 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 193)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 190 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 186)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 129 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 113)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 191 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 187)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 192 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 188)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 193 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 189)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 194 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 190)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 195 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 191)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 196 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 192)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 65 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 63)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 217 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 213)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 215 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 211)
/
commit
/
delete from hybridization_value where hybridization_data_group_id = 201 and composite_element_id in (select composite_element_id from composite_element where data_set_id = 197)
/
commit
/
/*
** now delete composite_element. 
*/
delete from composite_element where data_set_id = 17
/
commit
/
delete from composite_element where data_set_id = 15
/
commit
/
delete from composite_element where data_set_id = 55
/
commit
/
delete from composite_element where data_set_id = 54
/
commit
/
delete from composite_element where data_set_id = 91
/
commit
/
delete from composite_element where data_set_id = 122
/
commit
/
delete from composite_element where data_set_id = 52
/
commit
/
delete from composite_element where data_set_id = 19
/
commit
/
delete from composite_element where data_set_id = 25
/
commit
/
delete from composite_element where data_set_id = 24
/
commit
/
delete from composite_element where data_set_id = 108
/
commit
/
delete from composite_element where data_set_id = 98
/
commit
/
delete from composite_element where data_set_id = 80
/
commit
/
delete from composite_element where data_set_id = 123
/
commit
/
delete from composite_element where data_set_id = 88
/
commit
/
delete from composite_element where data_set_id = 109
/
commit
/
delete from composite_element where data_set_id = 81
/
commit
/
delete from composite_element where data_set_id = 89
/
commit
/
delete from composite_element where data_set_id = 99
/
commit
/
delete from composite_element where data_set_id = 124
/
commit
/
delete from composite_element where data_set_id = 90
/
commit
/
delete from composite_element where data_set_id = 102
/
commit
/
delete from composite_element where data_set_id = 110
/
commit
/
delete from composite_element where data_set_id = 83
/
commit
/
delete from composite_element where data_set_id = 125
/
commit
/
delete from composite_element where data_set_id = 112
/
commit
/
delete from composite_element where data_set_id = 103
/
commit
/
delete from composite_element where data_set_id = 106
/
commit
/
delete from composite_element where data_set_id = 85
/
commit
/
delete from composite_element where data_set_id = 126
/
commit
/
delete from composite_element where data_set_id = 105
/
commit
/
delete from composite_element where data_set_id = 107
/
commit
/
delete from composite_element where data_set_id = 114
/
commit
/
delete from composite_element where data_set_id = 86
/
commit
/
delete from composite_element where data_set_id = 127
/
commit
/
delete from composite_element where data_set_id = 56
/
commit
/
delete from composite_element where data_set_id = 215
/
commit
/
delete from composite_element where data_set_id = 203
/
commit
/
delete from composite_element where data_set_id = 68
/
commit
/
delete from composite_element where data_set_id = 216
/
commit
/
delete from composite_element where data_set_id = 204
/
commit
/
delete from composite_element where data_set_id = 69
/
commit
/
delete from composite_element where data_set_id = 217
/
commit
/
delete from composite_element where data_set_id = 205
/
commit
/
delete from composite_element where data_set_id = 70
/
commit
/
delete from composite_element where data_set_id = 218
/
commit
/
delete from composite_element where data_set_id = 206
/
commit
/
delete from composite_element where data_set_id = 71
/
commit
/
delete from composite_element where data_set_id = 219
/
commit
/
delete from composite_element where data_set_id = 207
/
commit
/
delete from composite_element where data_set_id = 72
/
commit
/
delete from composite_element where data_set_id = 220
/
commit
/
delete from composite_element where data_set_id = 208
/
commit
/
delete from composite_element where data_set_id = 73
/
commit
/
delete from composite_element where data_set_id = 221
/
commit
/
delete from composite_element where data_set_id = 209
/
commit
/
delete from composite_element where data_set_id = 210
/
commit
/
delete from composite_element where data_set_id = 222
/
commit
/
delete from composite_element where data_set_id = 194
/
commit
/
delete from composite_element where data_set_id = 277
/
commit
/
delete from composite_element where data_set_id = 57
/
commit
/
delete from composite_element where data_set_id = 144
/
commit
/
delete from composite_element where data_set_id = 143
/
commit
/
delete from composite_element where data_set_id = 74
/
commit
/
delete from composite_element where data_set_id = 75
/
commit
/
delete from composite_element where data_set_id = 77
/
commit
/
delete from composite_element where data_set_id = 78
/
commit
/
delete from composite_element where data_set_id = 178
/
commit
/
delete from composite_element where data_set_id = 171
/
commit
/
delete from composite_element where data_set_id = 60
/
commit
/
delete from composite_element where data_set_id = 149
/
commit
/
delete from composite_element where data_set_id = 146
/
commit
/
delete from composite_element where data_set_id = 147
/
commit
/
delete from composite_element where data_set_id = 148
/
commit
/
delete from composite_element where data_set_id = 150
/
commit
/
delete from composite_element where data_set_id = 151
/
commit
/
delete from composite_element where data_set_id = 152
/
commit
/
delete from composite_element where data_set_id = 153
/
commit
/
delete from composite_element where data_set_id = 61
/
commit
/
delete from composite_element where data_set_id = 154
/
commit
/
delete from composite_element where data_set_id = 155
/
commit
/
delete from composite_element where data_set_id = 156
/
commit
/
delete from composite_element where data_set_id = 157
/
commit
/
delete from composite_element where data_set_id = 100
/
commit
/
delete from composite_element where data_set_id = 184
/
commit
/
delete from composite_element where data_set_id = 290
/
commit
/
delete from composite_element where data_set_id = 346
/
commit
/
delete from composite_element where data_set_id = 347
/
commit
/
delete from composite_element where data_set_id = 185
/
commit
/
delete from composite_element where data_set_id = 345
/
commit
/
delete from composite_element where data_set_id = 158
/
commit
/
delete from composite_element where data_set_id = 179
/
commit
/
delete from composite_element where data_set_id = 180
/
commit
/
delete from composite_element where data_set_id = 181
/
commit
/
delete from composite_element where data_set_id = 182
/
commit
/
delete from composite_element where data_set_id = 183
/
commit
/
delete from composite_element where data_set_id = 376
/
commit
/
delete from composite_element where data_set_id = 165
/
commit
/
delete from composite_element where data_set_id = 166
/
commit
/
delete from composite_element where data_set_id = 167
/
commit
/
delete from composite_element where data_set_id = 170
/
commit
/
delete from composite_element where data_set_id = 168
/
commit
/
delete from composite_element where data_set_id = 169
/
commit
/
delete from composite_element where data_set_id = 58
/
commit
/
delete from composite_element where data_set_id = 193
/
commit
/
delete from composite_element where data_set_id = 186
/
commit
/
delete from composite_element where data_set_id = 113
/
commit
/
delete from composite_element where data_set_id = 187
/
commit
/
delete from composite_element where data_set_id = 188
/
commit
/
delete from composite_element where data_set_id = 189
/
commit
/
delete from composite_element where data_set_id = 190
/
commit
/
delete from composite_element where data_set_id = 191
/
commit
/
delete from composite_element where data_set_id = 192
/
commit
/
delete from composite_element where data_set_id = 63
/
commit
/
delete from composite_element where data_set_id = 117
/
commit
/
delete from composite_element where data_set_id = 213
/
commit
/
delete from composite_element where data_set_id = 252
/
commit
/
delete from composite_element where data_set_id = 211
/
commit
/
delete from composite_element where data_set_id = 241
/
commit
/
delete from composite_element where data_set_id = 244
/
commit
/
delete from composite_element where data_set_id = 197
/
commit
/
/*
** now delete hybridzation_data_group ; since there are only few, do it with a subselect
** on the delete_old_data table
*/
delete from hybridization_data_group where data_set_id in (select data_set_id from delete_old_data)
/
commit
/
/*
** now delete the data_sets; since there are only few, do it with a subselect
** on the delete_old_data table
*/
delete from data_set where data_set_id in (select data_set_id from delete_old_data)
/
commit
/
/*
** delete the hyb ref records associated with experiments that no longer have data_sets
*/
delete from hybridization_ref where experiment_id not in (select experiment_id from data_Set)
/
commit
/
/*
** delete the experiments that no longer have data_sets
*/
delete from experiment where experiment_id not in (select experiment_id from data_Set)
/
commit
/
