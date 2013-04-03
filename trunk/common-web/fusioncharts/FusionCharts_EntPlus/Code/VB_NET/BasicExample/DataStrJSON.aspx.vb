Imports InfoSoftGlobal
Imports System.Text
Partial Class BasicExample_DataStrJSON
    Inherits System.Web.UI.Page

    Protected Sub Page_Load(ByVal sender As Object, ByVal e As System.EventArgs) Handles Me.Load

        Dim strJSON As StringBuilder = New StringBuilder()
        strJSON.Append("{")
        strJSON.Append("""chart"":{")
        strJSON.Append("""caption"":""Monthly Unit Sales"", ""xaxisname"":""Month"",")
        strJSON.Append("""yaxisname"":""Units"", ""showvalues"":""0"",""formatnumberscale"":""0"", ""showborder"":""1""  },")
        strJSON.Append("""data"":[")
        strJSON.Append("{ ""label"":""Jan"", ""value"":""462""  },{ ""label"":""Feb"", ""value"":""857""  },{ ""label"":""Mar"", ""value"":""671""  },{ ""label"":""Apr"", ""value"":""494""  },{ ""label"":""May"", ""value"":""761""  },{ ""label"":""Jun"", ""value"":""960""  },{ ""label"":""Jul"", ""value"":""629""  },{ ""label"":""Aug"", ""value"":""622""  },{ ""label"":""Sep"", ""value"":""376""  },{ ""label"":""Oct"", ""value"":""494""  },{ ""label"":""Nov"", ""value"":""761""  },{ ""label"":""Dec"", ""value"":""960""  }")
        strJSON = strJSON.Append("]")
        strJSON = strJSON.Append("}")
        FusionCharts.SetDataFormat("json")
        Literal1.Text = FusionCharts.RenderChart("../FusionCharts/Column3D.swf", "", strJSON.ToString(), "chart1", "600", "300", False, True, False)


    End Sub
End Class
