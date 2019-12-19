//添加
//@ sourceURL=add.js
$(function () {

    //定义按钮行为
    init();
    initTitle();
});
function initTitle(){
    var title="";
    var type= $("#type").val();
    var companyName= $("#companyName").val();
    var bondShortname= $("#bondShortname").val();
    var bondCode= $("#bondCode").val();
    if(type==0){
        title=companyName;
    }else{
        title='<div>'+bondShortname+'&nbsp;&nbsp;'+bondCode+'</div>'
    }
    $(".layui-layer-title").html(title);
}
function init() {
    $("#spanFileNum").text($("#tmpFiles .file-list").size());
    $("#spanFileNumR").text($("#tmpFilesR .file-list").size());
    var mailUser=$("#mailUserHidden").val();
    var mailUserText="";
    if((mailUser&1)==1){
        mailUserText+="发行人对接人；";
    }
    if((mailUser&2)==2){
        mailUserText+="项目组负责人；";
    }
    if((mailUser&4)==4){
        mailUserText+="督导人员；";
    }
    $("#mailUser").text(mailUserText);
    $("#taskPass").click(function () {
        var checkResult = $("#checkResult option:selected").val();
        var reason = $.trim($("#reason").val());
        if(checkResult==-1){
            layer.alert("请选择事项审核", {icon: 1, time: 3000});
            return;
        }else if(checkResult==0 && reason==""){
            layer.alert("请填写退回理由", {icon: 1, time: 3000});
            return;
        }
        var taskJson = {};
        var processJson = {};

        taskJson.id=$("#id").val();
        taskJson.checkResult=checkResult;
        taskJson.processId=$("#processId").val();
        processJson.processId=$("#processId").val();
        // processJson.content =  $('#summernote').code();
        processJson.reason = reason;
        processJson.remindDate = $("#remindDate").text();
        processJson.completeDate = $("#completeDate").text();
        processJson.mailUser = $("#mailUserHidden").val();
        processJson.type = $("#type").val();
        processJson.name = $("#name").text();
        processJson.companyName = $("#companyName").val();
        processJson.bondCode = $("#bondCode").val();
        if(0==checkResult){
            processJson.status=8;
        }else {
            processJson.status=32;
        }
        var aryFile = new Array();
        $("#tmpFiles .file-list").each(function(index, item){
            var attachment = {};
            attachment.type = $(item).find("input[name='type']").val();
            attachment.name = $(item).find("input[name='name']").val();
            attachment.url = $(item).find("input[name='url']").val();
            attachment.processId = $("#processId").val();
            aryFile.push(attachment);
        });

        processJson.processTemplateAttachmentList=aryFile;
        taskJson.processInfo = processJson;

        $.ajax({
            url: contextPath + "/check-matter/check-matter",
            type:"post",
            data: JSON.stringify(taskJson),
            dataType: "json",
            processData:false,
            contentType : 'application/json;charset=utf-8', //设置请求头信息
            success:function(data){
                layer.alert(data.data, {icon: 1, time: 3000});
                layer.close(pageTask);
                refreshTable();
            },
            error:function(e){
                layer.alert(data.data, {icon: 1, time: 3000});
            }
        });
    });

    $("#addFile").click(function () {
        $("#uploadFile").click();
    });
    // 全部下载
    $("#fileDownloadAll").click(function () {
        downAllFile();
    });
    $("#fileDownloadAllR").click(function () {
        downAllFileR();
    });
    //全部删除
    $("#fileRemoveAll").click(function () {
        $("#tmpFiles .file-list").remove("");
        $("#spanFileNum").text(0);
    });

    $("#reasonDiv").hide();
    $("#checkResult").change(function() {
        var checkResult = $("#checkResult option:selected").val();
        if(checkResult==0){
            $("#reasonDiv").show();
        }else{
            $("#reasonDiv").hide();
        }
    });
}

