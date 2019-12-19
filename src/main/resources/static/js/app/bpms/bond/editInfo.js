/**
 * Created by Dealing076 on 2018/7/11.
 */
//@ sourceURL=editInfo.js
$(function () {
    //tab 选择事件
    $("#bondTab li a").click(function(){
        var tabID = $(this).attr("id");
        //加载选则的tab数据
        loadBondTabData(tabID);
    });

    loadBondInfo();
});

//根据tabID加载数据
function loadBondTabData(tabID){
    if(tabID=="tab-bond"){
        //loadTemplateInfo();
    }else if(tabID=="tab-history"){
        loadHistoryInfo();
    }
}

//修改项目
function loadBondInfo(){
    $.ajax({
        type: 'GET',
        url: contextPath + "/bond/edit/" + $("#hidBondId").val(),
        cache: false,
        success: function (data) {
            console.log("success");
            $("#divInfoBond").html("");
            $("#divInfoBond").append(data);
        }
    });
}

//历史记录
function loadHistoryInfo(){
    $.ajax({
        type: 'GET',
        url: contextPath + "/bond/history/" + $("#hidBondCode").val(),
        cache: false,
        success: function (data) {
            console.log("success");
            $("#divInfoHistory").html("");
            $("#divInfoHistory").append(data);
        }
    });
}