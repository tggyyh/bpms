/**
 * Created by Dealing076 on 2018/3/20.
 */
//@ sourceURL=infoBond.js
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

    if($("#divListedDate").html()==""){
        $("#divListedDate").html("无");
    }

});


//根据tabID加载数据
function loadSysuserTabData(tabID){
    if(tabID=="tab-linkBond"){
        initBondTable();
    }else if(tabID=="tab-linkBank"){
        initBankTable();
    }
}


//初始化项目负责人Table
function initBondTable() {
    $.ajax({
        type: 'GET',
        url: contextPath + "/bondmanager/bondList/" + $("#hidBondCode").val(),
        cache: false,
        success: function (data) {
            console.log("success");
            $("#divInfoLinkBond").html("");
            $("#divInfoLinkBond").append(data);
        }
    });
}

//银行管理负责人Table
function initBankTable() {
    $.ajax({
        type: 'GET',
        url: contextPath + "/bankmanager/list/" + $("#hidBondCode").val(),
        cache: false,
        success: function (data) {
            console.log("success");
            $("#divInfoLinkBank").html("");
            $("#divInfoLinkBank").append(data);
        }
    });
}