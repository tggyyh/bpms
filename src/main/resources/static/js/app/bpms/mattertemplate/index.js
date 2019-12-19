/**
 * Created by Dealing076 on 2018/2/23.
 */
var pageIndex;
$(function () {

    //初始化Buttons
    initButtons();
});

function initButtons() {
    //添加
    $("#btnAdd").click(function(){
        $.ajax(contextPath + "/mattertemplate/add").success(function (data) {
            pageIndex = layer.open({
                id: 'addPage',
                type: 1,
                title: '新增提醒事项',
                maxmin: false,
                resize: false,
                shade: 0.3,
                area: ['90%', '95%'],
                content: data
            });
        });
    });

    //展开收缩
    $("#spanCompanyTemp").click(function(){
        if($("#spanCompanyTemp").hasClass("glyphicon-triangle-bottom")){
            $(".tr-comp").addClass("hide");
            $("#spanCompanyTemp").removeClass("glyphicon-triangle-bottom");
            $("#spanCompanyTemp").addClass("glyphicon-triangle-top");
        }else if($("#spanCompanyTemp").hasClass("glyphicon-triangle-top")){
            $(".tr-comp").removeClass("hide");
            $("#spanCompanyTemp").addClass("glyphicon-triangle-bottom");
            $("#spanCompanyTemp").removeClass("glyphicon-triangle-top");
        }
    });
    $("#spanBondTemp").click(function(){
        if($("#spanBondTemp").hasClass("glyphicon-triangle-bottom")){
            $(".tr-bond").addClass("hide");
            $("#spanBondTemp").removeClass("glyphicon-triangle-bottom");
            $("#spanBondTemp").addClass("glyphicon-triangle-top");
        }else if($("#spanBondTemp").hasClass("glyphicon-triangle-top")){
            $(".tr-bond").removeClass("hide");
            $("#spanBondTemp").addClass("glyphicon-triangle-bottom");
            $("#spanBondTemp").removeClass("glyphicon-triangle-top");
        }
    });
}

function editMattertemplate(obj){
    var id = $(obj).parent().find("input[name='hidTemplateId']").val();
    if(null==id || ""==id){
        layer.alert('编辑失败，该数据已丢失', {icon: 1, time: 3000});
        return false;
    }
    $.ajax(contextPath + "/mattertemplate/info/"+id).success(function (data) {
        pageIndex = layer.open({
            id: 'editPage',
            type: 1,
            title: '编辑提醒事项',
            maxmin: false,
            resize: false,
            shade: 0.3,
            area: ['90%', '95%'],
            content: data
        });
    });
}

function linkCompMattertemplate(obj) {
    var id = $(obj).parent().find("input[name='hidTemplateId']").val();
    if(null==id || ""==id){
        layer.alert('编辑失败，该数据已丢失', {icon: 1, time: 3000});
        return false;
    }
    var name = $(obj).parent().find("input[name='hidTemplateName']").val();
    $.ajax(contextPath + "/mattertemplate/companylinkinfo/"+id).success(function (data) {
        pageIndex = layer.open({
            id: 'companyLinkPage',
            type: 1,
            title: name,
            maxmin: false,
            resize: false,
            shade: 0.3,
            area: ['90%', '95%'],
            content: data
        });
    });
}

function linkBondMattertemplate(obj) {
    var id = $(obj).parent().find("input[name='hidTemplateId']").val();
    if(null==id || ""==id){
        layer.alert('编辑失败，该数据已丢失', {icon: 1, time: 3000});
        return false;
    }
    var name = $(obj).parent().find("input[name='hidTemplateName']").val();
    $.ajax(contextPath + "/mattertemplate/bondlinkinfo/"+id).success(function (data) {
        pageIndex = layer.open({
            id: 'bondLinkPage',
            type: 1,
            title: name,
            maxmin: false,
            resize: false,
            shade: 0.3,
            area: ['90%', '95%'],
            content: data
        });
    });
}

function refreshMattertemplate(){
    window.location.reload();
}

function removeMattertemplate(objID){
    var id = $(objID).parent().find("input[name='hidTemplateId']").val();
    if(null==id || ""==id){
        layer.alert('编辑失败，该数据已丢失', {icon: 1, time: 3000});
        return false;
    }

    var linkCount = $(objID).parent().find("input[name='hidLinkCount']").val();
    var delMsg = '您是否确认要删除该模板事项？';
    if(parseInt(linkCount)>0){
        delMsg = '该模板事项已经处在被应用中，删除后将导致已应用该模板事项的项目或主体中彻底删除了该模板事项，您是否确认要删除该模板事项？';
    }

    layer.confirm(
        delMsg,
        {icon: 3, title:'模板事项删除'},
        function(index) {
            $.ajax({
                url: contextPath + "/mattertemplate/deleteSave",
                type: 'post',
                data: JSON.stringify(id),//将对象序列化成JSON字符串
                contentType : 'application/json;charset=utf-8', //设置请求头信息
                success: function (data) {
                    if (data.code == 0) {
                        refreshMattertemplate();
                    } else {
                        layer.alert(data.message, {icon: 1, time: 3000});
                    }
                },
                error: function (e) {
                    layer.alert('删除失败', {icon: 1, time: 3000});
                }
            });

            layer.close(index);
        });
}

function loadRuleMore(objRule){
    var addHTML = "";
    $(objRule).prev().children().each(function(index, item){
        addHTML += "<div>" + $(item).html() + "</div>";
    });
    $(objRule).next().children().eq(1).html("");
    $(objRule).next().children().eq(1).append(addHTML);
    $(objRule).next().removeClass("hide");
}

function closeRuleMore(objRule){
    $(objRule).next().html("");
    $(objRule).parent().addClass("hide");
}
