/**
 * Created by Dealing076 on 2018/3/13.
 */
//@ sourceURL=infoCompany.js
$(function () {

    //tab 选择事件
    $("#sysuserTab li a").click(function(){
        var tabID = $(this).attr("id");
        //加载选则的tab数据
        loadSysuserTabData(tabID);
    });

    $("#sysuserTab li").each(function (index, item) {
        if($(item).hasClass("active")){
            var tabID = $(item).find("a").attr("id");
            loadSysuserTabData(tabID);
        }
    });

});


//根据tabID加载数据
function loadSysuserTabData(tabID){
    if(tabID=="tab-linkBond"){
        initBondTable();
    }else if(tabID=="tab-linkCompany"){
        initCompanyTable();
    }
}


//初始化项目负责人Table
function initBondTable() {
    $.ajax({
        type: 'GET',
        url: contextPath + "/companylinkman/bondmanagerlist/" + $("#hidCompanyId").val(),
        cache: false,
        success: function (data) {
            console.log("success");
            $("#divInfoLinkBond").html("");
            $("#divInfoLinkBond").append(data);
        }
    });
}

//发行人对接人Table
function initCompanyTable() {
    $.ajax({
        type: 'GET',
        url: contextPath + "/companylinkman/list/" + $("#hidCompanyId").val(),
        cache: false,
        success: function (data) {
            console.log("success");
            $("#divInfoLinkCompany").html("");
            $("#divInfoLinkCompany").append(data);
        }
    });
}