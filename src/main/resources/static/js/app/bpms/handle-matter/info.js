/**
 * Created by Dealing076 on 2018/3/8.
 */
//@ sourceURL=info.js
$(function () {
    //tab 选择事件
    $("#viewTab li a").click(function(){
        var tabID = $(this).attr("id");
        //加载选则的tab数据
        loadTabData(tabID);
    });

    $("#viewTab li").each(function (index, item) {
        if($(item).hasClass("active")){
            var tabID = $(item).find("a").attr("id");
            loadTabData(tabID);
        }
    });
});

//根据tabID加载数据
function loadTabData(tabID){
    if(tabID=="tab-view"){
        loadViewInfo();
    }else if(tabID=="tab-history"){
        loadHistoryInfo();
    }
}

function loadViewInfo(){
    var orderIndex = $("#hidOrderIndex").val();
    var status = $("#hidStatus").val();
    if(orderIndex==0) {
        if(status==2){
            $.ajax(contextPath + "handle-matter/handle-matter/" + $("#hidTaskId").val()).success(function (data) {
                console.log("success");
                $("#divView").html("");
                $("#divView").append(data);
            });
        }else{
            $.ajax(contextPath + "handle-matter/re-handle-matter/" + $("#hidTaskId").val()).success(function (data) {
                console.log("success");
                $("#divView").html("");
                $("#divView").append(data);
            });
        }
    }else{
        if(status==2){
            $.ajax(contextPath + "handle-matter/handle-right-line-matter/" + $("#hidTaskId").val()).success(function (data) {
                console.log("success");
                $("#divView").html("");
                $("#divView").append(data);
            });
        }else{
            $.ajax(contextPath + "handle-matter/re-handle-right-line-matter/" + $("#hidTaskId").val()).success(function (data) {
                console.log("success");
                $("#divView").html("");
                $("#divView").append(data);
            });
        }
    }
}

function loadHistoryInfo(){
    $.ajax(contextPath + "/process-history/" + $("#hidPId").val()).success(function (data) {
        console.log("success");
        $("#divHistory").html("");
        $("#divHistory").append(data);
    });
}
