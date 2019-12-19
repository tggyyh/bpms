//添加
//@ sourceURL=re-handle-matter.js
$(function () {
    //定义按钮行为
    init();
    initTitle()
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
        $("#tmpFilesR .file-list").remove("");
        $("#spanFileNumR").text(0);
    });

    $("#taskPass").click(function () {
        var taskJson = {};
        var processJson = {};

        taskJson.id=$("#id").val();
        taskJson.processId=$("#processId").val();
        processJson.processId=$("#processId").val();
        processJson.mark=$("#mark").val();
        processJson.content = $("#content").val();
        // processJson.reason = $("#reason").val();
        processJson.remindDate = $("#remindDate").text();
        processJson.completeDate = $("#completeDate").text();
        processJson.mailUser = $("#mailUserHidden").val();
        processJson.type = $("#type").val();
        processJson.name = $("#name").text();
        processJson.companyName = $("#companyName").val();
        processJson.bondCode = $("#bondCode").val();
        processJson.status=16;
        var aryFile = new Array();
        $("#tmpFilesR .file-list").each(function(index, item){
            var attachment = {};
            attachment.type = $(item).find("input[name='type']").val();
            attachment.name = $(item).find("input[name='name']").val();
            attachment.url = $(item).find("input[name='url']").val();
            attachment.processId = $("#processId").val();
            aryFile.push(attachment);
        });
        // if(aryFile.length<1){
        //     layer.alert('请添加事项材料', {icon: 1, time: 3000});
        //     return;
        // }
        processJson.processAttachmentList=aryFile;
        taskJson.processInfo = processJson;

        $.ajax({
            url: contextPath + "/handle-matter/re-handle-right-line-matter",
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
}
