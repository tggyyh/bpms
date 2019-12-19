/**
 * Created by Dealing076 on 2018/7/9.
 */
//@ sourceURL=info.js
$(function () {
    //tab 选择事件
    $("#templateTab li a").click(function(){
        var tabID = $(this).attr("id");
        //加载选则的tab数据
        loadTabData(tabID);
    });

    // $("#templateTab li").each(function (index, item) {
    //     if($(item).hasClass("active")){
    //         var tabID = $(item).find("a").attr("id");
    //         loadTabData(tabID);
    //     }
    // });

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

//事项基本信息
function loadTemplateInfo(){
    $.ajax({
        type: 'GET',
        url: contextPath + "/mattertemplate/edit/" + $("#hidTemplateId").val(),
        cache: false,
        success : function (data) {
            console.log("success");
            $("#divInfoTemplate").html("");
            $("#divInfoTemplate").append(data);
        }
    });
}

//事项历史记录
function loadHistoryInfo(){
    $.ajax({
        type: 'GET',
        url: contextPath + "/mattertemplate/history/" + $("#hidTemplateId").val(),
        cache: false,
        success: function (data) {
            console.log("success");
            $("#divInfoHistory").html("");
            $("#divInfoHistory").append(data);
        }
    });
}