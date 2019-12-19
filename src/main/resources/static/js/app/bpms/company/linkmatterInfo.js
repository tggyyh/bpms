/**
 * Created by Dealing076 on 2018/7/16.
 */
//@ sourceURL=linkmatterInfo.js
$(function () {
    //tab 选择事件
    $("#linkmatterTab li a").click(function(){
        var tabID = $(this).attr("id");
        //加载选则的tab数据
        loadLinkmatterTabData(tabID);
    });

    loadLinkmatterInfo();
});

//根据tabID加载数据
function loadLinkmatterTabData(tabID){
    if(tabID=="tab-linkmatter"){
        //loadLinkmatterInfo();
    }else if(tabID=="tab-history"){
        loadHistoryInfo();
    }
}

//关联
function loadLinkmatterInfo(){
    $.ajax({
        type: 'GET',
        url: contextPath + "/company/linkmatter/" + $("#hidMatterType").val() + "/" + $("#hidCompanyName").val(),
        cache: false,
        success: function (data) {
            console.log("success");
            $("#divInfoLinkmatter").html("");
            $("#divInfoLinkmatter").append(data);
        }
    });
}

//历史记录
function loadHistoryInfo(){
    $.ajax({
        type: 'GET',
        url: contextPath + "/company/linkmatterHistory/" + $("#hidCompanyName").val(),
        cache: false,
        success: function (data) {
            console.log("success");
            $("#divInfoHistory").html("");
            $("#divInfoHistory").append(data);
        }
    });
}