/**
 * Created by Dealing076 on 2018/7/11.
 */
//@ sourceURL=bondlinkinfo.js
$(function () {
    //tab 选择事件
    $("#templateTab li a").click(function(){
        var tabID = $(this).attr("id");
        //加载选则的tab数据
        loadTabData(tabID);
    });

    loadTemplateInfo();
});

//根据tabID加载数据
function loadTabData(tabID){
    if(tabID=="tab-template"){
        //loadTemplateInfo();
    }else if(tabID=="tab-history"){
        loadHistoryInfo();
    }
}

//关联关系
function loadTemplateInfo(){
    $.ajax({
        type: 'GET',
        url: contextPath + "/mattertemplate/bondlink/" + $("#hidBondTemplateId").val(),
        cache: false,
        success: function (data) {
            console.log("success");
            $("#divInfoTemplate").html("");
            $("#divInfoTemplate").append(data);
        }
    });
}

//历史记录
function loadHistoryInfo(){
    $.ajax({
        type: 'GET',
        url: contextPath + "/mattertemplate/bondlinkhistory/" + $("#hidBondTemplateId").val(),
        cache: false,
        success: function (data) {
            console.log("success");
            $("#divInfoHistory").html("");
            $("#divInfoHistory").append(data);
        }
    });
}