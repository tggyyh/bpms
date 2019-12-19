/**
 * Created by Dealing076 on 2018/3/15.
 */
//@ sourceURL=list.js
$(function () {
    initLinkmanBankButtons();
});

function initLinkmanBankButtons(){
    $("#btnAddLinkman").click(function(){
        var newTR = '<tr>' + $("#tabInfoLinkBank tr:last").html() + '</tr>';
        $("#tabInfoLinkBank tr:eq(1)").before(newTR);
    });
}


function editLinkMan(obj){
    var index = $(obj).parent().parent().parent().index();  //当前行索引
    var objTR = $("#tabInfoLinkBank tr:eq(" + index + ")");
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
    var objTR = $("#tabInfoLinkBank tr:eq(" + index + ")");
    var id = $(objTR).find('input[name="hidLinkmanId"]').val();
    $.ajax({
        url: contextPath + "/bankmanager/deleteSave/" +id,
        type: "get",
        dataType: "json",
        success: function (data) {
            if (data.code == 0) {
                $(objTR).remove();
            } else {
                layer.alert(data.message, {icon: 1, time: 3000});
            }
        },
        error: function (e) {
            layer.alert('删除失败', {icon: 1, time: 3000});
        }
    });
}
function updateLinkMan(obj){
    var index = $(obj).parent().parent().parent().index();  //当前行索引
    var objTR = $("#tabInfoLinkBank tr:eq(" + index + ")");
    var id = $(objTR).find('input[name="hidLinkmanId"]').val();
    var name = $(objTR).find('input[name="txtLinkmanName"]').val();
    var phone = $(objTR).find('input[name="txtLinkmanPhone"]').val();
    var mobile = $(objTR).find('input[name="txtLinkmanMobile"]').val();
    var mail = $(objTR).find('input[name="txtLinkmanMail"]').val();
    if(name=="" && phone=="" && mobile=="" && mail==""){
        deleteLinkMan(obj);
    }else{
        var companyLinkman = {};
        companyLinkman.id = id;
        companyLinkman.bondCode = $("#hidBondCode").val();
        companyLinkman.name = name;
        companyLinkman.phone = phone;
        companyLinkman.mobile = mobile;
        companyLinkman.email = mail;
        if(id==""){
            layer.alert("更新失败：此数据已失效", {icon: 1, time: 3000});
            return;
        }
        // if(name==""){
        //     layer.alert("更新失败：姓名不能为空", {icon: 1, time: 3000});
        //     return;
        // }
        // if(phone==""){
        //     layer.alert("更新失败：电话不能为空", {icon: 1, time: 3000});
        //     return;
        // }
        if(mobile!="" && !isPositiveInteger(mobile)){
            layer.alert("更新失败：手机格式有误", {icon: 1, time: 3000});
            return;
        }
        if(mail!="" && !isEmail(mail)){
            layer.alert("更新失败：邮箱格式有误", {icon: 1, time: 3000});
            return;
        }

        $.ajax({
            url: contextPath + "/bankmanager/editSave",
            type: 'post',
            data: JSON.stringify(companyLinkman),//将对象序列化成JSON字符串
            contentType : 'application/json;charset=utf-8', //设置请求头信息
            success: function (data) {
                if (data.code == 0) {
                    $(objTR).find('div[name="divLinkmanName"]').html(name);
                    $(objTR).find('div[name="divLinkmanPhone"]').html(phone);
                    $(objTR).find('div[name="divLinkmanMobile"]').html(mobile);
                    $(objTR).find('div[name="divLinkmanMail"]').html(mail);
                    $(objTR).find(".linkman-view").removeClass("hide");
                    $(objTR).find(".linkman-edit").addClass("hide");
                    $(objTR).find(".linkman-option-view").removeClass("hide");
                    $(objTR).find(".linkman-option-save").addClass("hide");
                    $(objTR).find(".linkman-option-edit").addClass("hide");
                } else {
                    layer.alert(data.message, {icon: 1, time: 3000});
                }
            },
            error: function (e) {
                layer.alert('更新失败', {icon: 1, time: 3000});
            }
        });
    }
    
}
function cancelLinkMan(obj){
    var index = $(obj).parent().parent().parent().index();  //当前行索引
    var objTR = $("#tabInfoLinkBank tr:eq(" + index + ")");
    $(objTR).find(".linkman-view").removeClass("hide");
    $(objTR).find(".linkman-edit").addClass("hide");
    $(objTR).find(".linkman-option-view").removeClass("hide");
    $(objTR).find(".linkman-option-save").addClass("hide");
    $(objTR).find(".linkman-option-edit").addClass("hide");
}
function insertLinkMan(obj){
    var index = $(obj).parent().parent().parent().index();  //当前行索引
    var objTR = $("#tabInfoLinkBank tr:eq(" + index + ")");

    var name = $(objTR).find('input[name="txtLinkmanName"]').val();
    var phone = $(objTR).find('input[name="txtLinkmanPhone"]').val();
    var mobile = $(objTR).find('input[name="txtLinkmanMobile"]').val();
    var mail = $(objTR).find('input[name="txtLinkmanMail"]').val();
    if(name=="" && phone=="" && mobile=="" && mail==""){
        layer.alert("无信息可保存", {icon: 1, time: 3000});
        return false;
    }
    var companyLinkman = {};
    companyLinkman.id = 0;
    companyLinkman.bondCode = $("#hidBondCode").val();
    companyLinkman.name = name;
    companyLinkman.phone = phone;
    companyLinkman.mobile = mobile;
    companyLinkman.email = mail;

    // if(name==""){
    //     layer.alert("新增失败：姓名不能为空", {icon: 1, time: 3000});
    //     return;
    // }
    // if(phone==""){
    //     layer.alert("新增失败：电话不能为空", {icon: 1, time: 3000});
    //     return;
    // }
    if(mobile!="" && !isPositiveInteger(mobile)){
        layer.alert("新增失败：手机格式有误", {icon: 1, time: 3000});
        return;
    }
    if(mail!="" && !isEmail(mail)){
        layer.alert("新增失败：邮箱格式有误", {icon: 1, time: 3000});
        return;
    }

    $.ajax({
        url: contextPath + "/bankmanager/addSave",
        type: 'post',
        data: JSON.stringify(companyLinkman),//将对象序列化成JSON字符串
        contentType : 'application/json;charset=utf-8', //设置请求头信息
        success: function (data) {
            if (data.code == 0) {
                $(objTR).find('input[name="hidLinkmanId"]').val(data.data);
                $(objTR).find('div[name="divLinkmanName"]').html(name);
                $(objTR).find('div[name="divLinkmanPhone"]').html(phone);
                $(objTR).find('div[name="divLinkmanMobile"]').html(mobile);
                $(objTR).find('div[name="divLinkmanMail"]').html(mail);
                $(objTR).find(".linkman-view").removeClass("hide");
                $(objTR).find(".linkman-edit").addClass("hide");
                $(objTR).find(".linkman-option-view").removeClass("hide");
                $(objTR).find(".linkman-option-save").addClass("hide");
                $(objTR).find(".linkman-option-edit").addClass("hide");
            } else {
                layer.alert(data.message, {icon: 1, time: 3000});
            }
        },
        error: function (e) {
            layer.alert('添加失败', {icon: 1, time: 3000});
        }
    });
}
function removeLinkMan(obj){
    var index = $(obj).parent().parent().parent().index();  //当前行索引
    var objTR = $("#tabInfoLinkBank tr:eq(" + index + ")");
    $(objTR).remove();
}