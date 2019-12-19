/**
 * Created by Dealing076 on 2018/3/8.
 */
//@ sourceURL=info.js
$(function () {
    //tab 选择事件
    $("#bondTab li a").click(function(){
        var tabID = $(this).attr("id");
        //加载选则的tab数据
        loadTabData(tabID);
    });

    $("#bondTab li").each(function (index, item) {
        if($(item).hasClass("active")){
            var tabID = $(item).find("a").attr("id");
            loadTabData(tabID);
        }
    });
});

//根据tabID加载数据
function loadTabData(tabID){
    if(tabID=="tab-bond"){
        loadBondInfo();
    }else if(tabID=="tab-mattertemplte"){
        loadMattertemplateInfo();
    }else if(tabID=="tab-attachment"){
        loadAttaachmentInfo();
    }
}

//项目基本信息
function loadBondInfo(){
    $.ajax({
        type: 'GET',
        url: contextPath + "/bond/infoBond/" + $("#hidBondId").val(),
        cache: false,
        success: function (data) {
            console.log("success");
            $("#divInfoBond").html("");
            $("#divInfoBond").append(data);
        }
    });
}

//项目关联事项
function loadMattertemplateInfo(){
    $.ajax({
        type: 'GET',
        url: contextPath + "/bond/infoMattertemplate/1/" + $("#hidBondCode").val(),
        cache: false,
        success: function (data) {
            console.log("success");
            $("#divInfoMattertemplate").html("");
            $("#divInfoMattertemplate").append(data);
        }
    });
}

//事项材料存档
function loadAttaachmentInfo(){
    $.ajax({
        type: 'GET',
        url: contextPath + "/bond/infoAttachment/" + $("#hidBondCode").val(),
        cache: false,
        success: function (data) {
            console.log("success");
            $("#divInfoAttachment").html("");
            $("#divInfoAttachment").append(data);
        }
    });
}