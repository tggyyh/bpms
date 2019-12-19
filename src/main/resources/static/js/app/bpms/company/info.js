/**
 * Created by Dealing076 on 2018/3/8.
 */
//@ sourceURL=info.js
$(function () {
    //tab 选择事件
    $("#companyTab li a").click(function(){
        var tabID = $(this).attr("id");
        //加载选则的tab数据
        loadTabData(tabID);
    });

    $("#companyTab li").each(function (index, item) {
        if($(item).hasClass("active")){
            var tabID = $(item).find("a").attr("id");
            loadTabData(tabID);
        }
    });
});

//根据tabID加载数据
function loadTabData(tabID){
    if(tabID=="tab-company"){
        loadCompanyInfo();
    }else if(tabID=="tab-mattertemplte"){
        loadMattertemplateInfo();
    }else if(tabID=="tab-attachment"){
        loadAttaachmentInfo();
    }
}

//发行人基本信息
function loadCompanyInfo(){
    $.ajax({
        type: 'GET',
        url: contextPath + "/company/infoCompany/" + $("#hidCompanyId").val(),
        cache: false,
        success: function (data) {
            console.log("success");
            $("#divInfoCompany").html("");
            $("#divInfoCompany").append(data);
        }
    });
}

//发行人关联事项
function loadMattertemplateInfo(){
    $.ajax(contextPath + "/company/infoMattertemplate/" + $("#hidCompanyId").val() + "/0/" + $("#hidCompanyName").val()).success(function (data) {
        console.log("success");
        $("#divInfoMattertemplate").html("");
        $("#divInfoMattertemplate").append(data);
    });
}

//事项材料存档
function loadAttaachmentInfo(){
    $.ajax(contextPath + "/company/infoAttachment/" + $("#hidCompanyId").val()).success(function (data) {
        console.log("success");
        $("#divInfoAttachment").html("");
        $("#divInfoAttachment").append(data);
    });
}