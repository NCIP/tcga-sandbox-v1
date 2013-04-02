$(document).ready(
        function() {
            Ext.Ajax.request({
                url: '/web/news/dataTypesInclude.html',
                success: function(response, opts) {
                    var contentDiv = document.getElementById("dataTypeContent");
                    contentDiv.innerHTML = response.responseText;
                }
            });
        }
);
