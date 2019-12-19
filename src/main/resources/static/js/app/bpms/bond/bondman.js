/**
 * Created by Dealing076 on 2018/5/2.
 */
//@ sourceURL=bondman.js
$(function () {
    $("#divBondManContent").css("height", $(".layui-layer-page").height()-250 + "px");

    var bondManId = $("#hidBondManId").val().split(",");
    for(var i=0;i<bondManId.length;i++){
        $("#cbx-" + bondManId[i]).prop("checked","checked");
    }

    $("#btnBondManSave").click(function(){
        var bondMan = [];
        var bondManId = [];
        $("input[name='cbxBondMan']:checkbox:checked").each(function(index, item){
            bondManId.push($(item).attr("id").replace("cbx-",""));
            bondMan.push($(item).val());
        });
        $("#txtBondMan").val(bondMan.join(","));
        $("#hidBondManId").val(bondManId.join(","));

        layer.close(pageBondman);
    });

    $("#btnBondManCancel").click(function(){
        layer.close(pageBondman);
    });
});