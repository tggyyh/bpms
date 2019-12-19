/**
 * Created by Dealing076 on 2018/3/26.
 */
//@ sourceURL=bondlinkman.js
$(function () {
    initBondLinkmanButtons();
    initLinkmanInfo();
});

function initLinkmanInfo(){
    if(companyLinkmanList.length>0){
        for(var i=0;i<companyLinkmanList.length;i++){
            var newTR = '<tr>' + $("#tabInfoLinkCompany tr:last").html() + '</tr>';
            $("#tabInfoLinkCompany tr:eq(1)").before(newTR);
            var objTR = $("#tabInfoLinkCompany tr:eq(1)");

            $(objTR).find(".linkman-view").removeClass("hide");
            $(objTR).find(".linkman-edit").addClass("hide");
            $(objTR).find(".linkman-option-view").removeClass("hide");
            $(objTR).find(".linkman-option-save").addClass("hide");
            $(objTR).find(".linkman-option-edit").addClass("hide");

            $(objTR).find('input[name="txtLinkmanName"]').val(companyLinkmanList[i].name);
            $(objTR).find('input[name="txtLinkmanPhone"]').val(companyLinkmanList[i].phone);
            $(objTR).find('input[name="txtLinkmanMobile"]').val(companyLinkmanList[i].mobile);
            $(objTR).find('input[name="txtLinkmanMail"]').val(companyLinkmanList[i].mail);

            $(objTR).find('div[name="divLinkmanName"]').html(companyLinkmanList[i].name);
            $(objTR).find('div[name="divLinkmanPhone"]').html(companyLinkmanList[i].phone);
            $(objTR).find('div[name="divLinkmanMobile"]').html(companyLinkmanList[i].mobile);
            $(objTR).find('div[name="divLinkmanMail"]').html(companyLinkmanList[i].mail);

        }
    }
}

function initBondLinkmanButtons(){
    $("#btnAddLinkman").click(function(){
        var newTR = '<tr>' + $("#tabInfoLinkCompany tr:last").html() + '</tr>';
        $("#tabInfoLinkCompany tr:eq(1)").before(newTR);
    });
}


function editLinkMan(obj){
    var index = $(obj).parent().parent().parent().index();  //当前行索引
    var objTR = $("#tabInfoLinkCompany tr:eq(" + index + ")");
    $(objTR).find(".linkman-view").addClass("hide");
    $(objTR).find(".linkman-edit").removeClass("hide");
    $(objTR).find(".linkman-option-view").addClass("hide");
    $(objTR).find(".linkman-option-save").addClass("hide");
    $(objTR).find(".linkman-option-edit").removeClass("hide");

    var name = $(objTR).find('div[name="divLinkmanName"]').html();
    var phone = $(objTR).find('div[name="divLinkmanPhone"]').html();
    var mobile = $(objTR).find('div[name="divLinkmanMobile"]').html();
    var mail = $(objTR).find('div[name="divLinkmanMail"]').html();
    $(objTR).find('input[name="txtLinkmanName"]').val(name);
    $(objTR).find('input[name="txtLinkmanPhone"]').val(phone);
    $(objTR).find('input[name="txtLinkmanMobile"]').val(mobile);
    $(objTR).find('input[name="txtLinkmanMail"]').val(mail);

}
function deleteLinkMan(obj){
    var index = $(obj).parent().parent().parent().index();  //当前行索引
    var objTR = $("#tabInfoLinkCompany tr:eq(" + index + ")");
    $(objTR).remove();
}
function updateLinkMan(obj){
    var index = $(obj).parent().parent().parent().index();  //当前行索引
    var objTR = $("#tabInfoLinkCompany tr:eq(" + index + ")");
    var id = $(objTR).find('input[name="hidLinkmanId"]').val();
    var name = trim($(objTR).find('input[name="txtLinkmanName"]').val());
    var phone = trim($(objTR).find('input[name="txtLinkmanPhone"]').val());
    var mobile = trim($(objTR).find('input[name="txtLinkmanMobile"]').val());
    var mail = trim($(objTR).find('input[name="txtLinkmanMail"]').val());
    var companyLinkman = {};
    companyLinkman.id = id;
    companyLinkman.companyName = $("#divCompanyName").html();
    companyLinkman.name = name;
    companyLinkman.phone = phone;
    companyLinkman.mobile = mobile;
    companyLinkman.mail = mail;
    if(name==""){
        layer.alert("更新失败：姓名不能为空", {icon: 1, time: 3000});
        return;
    }
    if(phone==""){
        layer.alert("更新失败：电话不能为空", {icon: 1, time: 3000});
        return;
    }
    if(mobile!="" && !isPositiveInteger(mobile)){
        layer.alert("更新失败：手机格式有误", {icon: 1, time: 3000});
        return;
    }
    if(!isEmail(mail)){
        layer.alert("更新失败：邮箱格式有误", {icon: 1, time: 3000});
        return;
    }

    $(objTR).find('div[name="divLinkmanName"]').html(name);
    $(objTR).find('div[name="divLinkmanPhone"]').html(phone);
    $(objTR).find('div[name="divLinkmanMobile"]').html(mobile);
    $(objTR).find('div[name="divLinkmanMail"]').html(mail);
    $(objTR).find(".linkman-view").removeClass("hide");
    $(objTR).find(".linkman-edit").addClass("hide");
    $(objTR).find(".linkman-option-view").removeClass("hide");
    $(objTR).find(".linkman-option-save").addClass("hide");
    $(objTR).find(".linkman-option-edit").addClass("hide");
}
function cancelLinkMan(obj){
    var index = $(obj).parent().parent().parent().index();  //当前行索引
    var objTR = $("#tabInfoLinkCompany tr:eq(" + index + ")");
    $(objTR).find(".linkman-view").removeClass("hide");
    $(objTR).find(".linkman-edit").addClass("hide");
    $(objTR).find(".linkman-option-view").removeClass("hide");
    $(objTR).find(".linkman-option-save").addClass("hide");
    $(objTR).find(".linkman-option-edit").addClass("hide");
}
function insertLinkMan(obj){
    var index = $(obj).parent().parent().parent().index();  //当前行索引
    var objTR = $("#tabInfoLinkCompany tr:eq(" + index + ")");

    var name = $(objTR).find('input[name="txtLinkmanName"]').val();
    var phone = $(objTR).find('input[name="txtLinkmanPhone"]').val();
    var mobile = $(objTR).find('input[name="txtLinkmanMobile"]').val();
    var mail = $(objTR).find('input[name="txtLinkmanMail"]').val();
    var companyLinkman = {};
    companyLinkman.id = 0;
    companyLinkman.companyName = $("#divCompanyName").html();
    companyLinkman.name = name;
    companyLinkman.phone = phone;
    companyLinkman.mobile = mobile;
    companyLinkman.mail = mail;

    if(name==""){
        layer.alert("更新失败：姓名不能为空", {icon: 1, time: 3000});
        return;
    }
    if(phone==""){
        layer.alert("更新失败：电话不能为空", {icon: 1, time: 3000});
        return;
    }
    if(mobile!="" && !isPositiveInteger(mobile)){
        layer.alert("更新失败：手机格式有误", {icon: 1, time: 3000});
        return;
    }
    if(!isEmail(mail)){
        layer.alert("更新失败：邮箱格式有误", {icon: 1, time: 3000});
        return;
    }

    $(objTR).find('input[name="hidLinkmanId"]').val(0);
    $(objTR).find('div[name="divLinkmanName"]').html(name);
    $(objTR).find('div[name="divLinkmanPhone"]').html(phone);
    $(objTR).find('div[name="divLinkmanMobile"]').html(mobile);
    $(objTR).find('div[name="divLinkmanMail"]').html(mail);
    $(objTR).find(".linkman-view").removeClass("hide");
    $(objTR).find(".linkman-edit").addClass("hide");
    $(objTR).find(".linkman-option-view").removeClass("hide");
    $(objTR).find(".linkman-option-save").addClass("hide");
    $(objTR).find(".linkman-option-edit").addClass("hide");
}
function removeLinkMan(obj){
    var index = $(obj).parent().parent().parent().index();  //当前行索引
    var objTR = $("#tabInfoLinkCompany tr:eq(" + index + ")");
    $(objTR).remove();
}