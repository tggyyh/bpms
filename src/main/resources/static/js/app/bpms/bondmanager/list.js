/**
 * Created by Dealing076 on 2018/3/15.
 */
//@ sourceURL=list.js
var pageBondman=0;
$(function () {
    $('.selectpicker').selectpicker();
});

function editBondMan(obj) {
    var index = $(obj).parent().parent().parent().index();  //当前行索引
    var objTR = $("#tabInfoLinkBond tr:eq(" + index + ")");
    var aryId = [];
    $(objTR).find("span[name='manId']").each(function(index, item){
        aryId.push($(item).attr("id").replace("manId-",""));
    });
    $("#hidBondManId").val(aryId.join(","));
    var aryName = [];
    $(objTR).find(".linkman-viewtext span").each(function (index, item) {
        aryName.push($(item).text().replace("、",""));
    });
    $("#hidBondManName").val(aryName.join(","));

    $.ajax(contextPath + "/bondmanager/bondman").success(function (data) {
        console.log("success");
        pageBondman = layer.open({
            type: 1,
            title: '设置项目负责人',
            maxmin: false,
            resize: false,
            shade: 0.3,
            area: ['80%', '85%'],
            scrollbar: false,
            content: data,
            end: function(){
                var aryManId = $("#hidBondManId").val().split(",");
                var aryManName = $("#hidBondManName").val().split(",");
                var idHTML = '';
                var nameHTML = '';
                for(var i=0;i<aryManId.length;i++){
                    idHTML += '<span id="manId-' + aryManId[i] + '" name="manId"></span>';
                }
                for(var i=0;i<aryManName.length;i++){
                    var vname = '';
                    if(i==aryManName.length-1){
                        vname = aryManName[i];
                    }else{
                        vname = aryManName[i] + "、";
                    }
                    nameHTML += '<span>' + vname + '</span>';
                }
                $(objTR).find("div[name='divBondManId']").html(idHTML);
                $(objTR).find(".linkman-viewtext").html(nameHTML);
            }

        });
    });
}
function editBondMans(obj) {
    var index = $(obj).parent().parent().parent().index();  //当前行索引
    var objTR = $("#tabInfoLinkBond tr:eq(" + index + ")");
    $(objTR).find(".linkman-viewtext").addClass("hide");
    $(objTR).find(".linkman-edittext").removeClass("hide");

    var aryUserId = [];
    var objSelect = $(objTR).find("select[class='selectpicker']");

    $(objTR).find(".linkman-viewtext span").each(function (index, item) {
        var selectValue = $(objSelect).find("option:contains(" + $(item).text().replace("、","") + ")").val();
        aryUserId.push(selectValue);
    });
    $(objTR).find(".selectpicker").selectpicker('val', aryUserId);
    $(objTR).find(".dropdown-toggle").removeClass("btn-default");
    $(objTR).find(".dropdown-toggle").addClass("btn-white");
    $(objTR).find(".linkman-option-e").addClass("hide");
    $(objTR).find(".linkman-option-s").removeClass("hide");
}
function viewBondMan(obj) {
    var index = $(obj).parent().parent().parent().index();  //当前行索引
    var objTR = $("#tabInfoLinkBond tr:eq(" + index + ")");

    $.ajax(contextPath + "/bondmanager/bondView/" + $("#divBondCode").html()).success(function (data) {
        console.log("success");
        layer.open({
            type: 1,
            title: '项目负责人',
            maxmin: false,
            shade: 0.3,
            area: ['70%', '60%'],
            scrollbar: false,
            content: data
        });
    });
}
function saveBondMan(obj) {
    var index = $(obj).parent().parent().parent().index();  //当前行索引
    var objTR = $("#tabInfoLinkBond tr:eq(" + index + ")");
    var aryUserId = $(objTR).find(".selectpicker").selectpicker('val');
    if(null==aryUserId || aryUserId.length<=0){
        layer.alert('保存失败:项目负责人不能为空', {icon: 1, time: 3000});
        return;
    }
    var bondManagerList = new Array();
    var accountHTML = '';
    var objSelect = $(objTR).find("select[class='selectpicker']");
    if(null!=aryUserId){
        for(var i=0;i<aryUserId.length;i++){
            var bondManager = {};
            bondManager.bondCode = $("#divBondCode").html();
            bondManager.userId = aryUserId[i];
            bondManagerList.push(bondManager);

            var selectText = $(objSelect).find("option[value='" + aryUserId[i] + "']").text();
            accountHTML += '<span>' + selectText + (i==aryUserId.length-1?'</span>':'、</span>');
        }
    }
    var reqData = {};
    reqData.bondCode = $("#divBondCode").html();
    reqData.bondManagerList = bondManagerList;

    var loadIndex = layer.load(2, {
        shade: [0.3,'#cacaca'],
        area: ['50px', '50px'],
        offset:['50%','50%'],
    });

    $.ajax({
        url: contextPath + "/bondmanager/bondManagerSave",
        type:"post",
        data: JSON.stringify(reqData),//将对象序列化成JSON字符串
        dataType:"json",
        contentType : 'application/json;charset=utf-8', //设置请求头信息
        success:function(data){
            layer.close(loadIndex);
            if(data.code==0){
                $(objTR).find(".linkman-viewtext").removeClass("hide");
                $(objTR).find(".linkman-edittext").addClass("hide");
                $(objTR).find(".linkman-option-e").removeClass("hide");
                $(objTR).find(".linkman-option-s").addClass("hide");
                $(objTR).find(".linkman-viewtext").html(accountHTML);
            }else{
                layer.alert(data.message, {icon: 1, time: 3000});
            }
        },
        error:function(e){
            layer.close(loadIndex);
            layer.alert('保存失败', {icon: 1, time: 3000});
        }
    });
}
function cancelBondMan(obj) {
    var index = $(obj).parent().parent().parent().index();  //当前行索引
    var objTR = $("#tabInfoLinkBond tr:eq(" + index + ")");
    $(objTR).find(".linkman-viewtext").removeClass("hide");
    $(objTR).find(".linkman-edittext").addClass("hide");
    $(objTR).find(".linkman-option-e").removeClass("hide");
    $(objTR).find(".linkman-option-s").addClass("hide");
}