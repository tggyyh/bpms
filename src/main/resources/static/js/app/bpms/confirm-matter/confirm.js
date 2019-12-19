//添加
//@ sourceURL=confirm-matter.js
$(function () {
    //定义按钮行为
    initConfirm();
    initTitle();
    initSummernote();
    var markupStr = $("#hidContent").val();
    $('#summernote').code(markupStr);
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
function initConfirm() {
    //模板材料个数

    $("#spanFileNum").text($("#tmpFiles .file-list").size());
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
        var taskJson = {};
        var processJson = {};

        taskJson.id=$("#id").val();
        taskJson.processId=$("#processId").val();
        processJson.processId=$("#processId").val();
        processJson.content =  $("#summernote").code();
        processJson.remindDate = $("#remindDate").text();
        processJson.completeDate = $("#completeDate").text();
        processJson.mailUser = $("#mailUserHidden").val();
        processJson.type = $("#type").val();
        processJson.confirm = $("#confirm").val();
        processJson.name = $("#name").text();
        processJson.companyName = $("#companyName").val();
        processJson.bondShortname = $("#bondShortname").val();
        processJson.bondCode = $("#bondCode").val();
        processJson.status=2;
        var aryFile = new Array();
        $("#tmpFiles .file-list").each(function(index, item){
            var attachment = {};
            attachment.type = $(item).find("input[name='type']").val();
            attachment.name = $(item).find("input[name='name']").val();
            attachment.url = $(item).find("input[name='url']").val();
            attachment.processId = $("#processId").val();
            aryFile.push(attachment);
        });
        if(trim($("#summernote").code())=="" || trim($("#summernote").code())=="<p><br></p>") {
            layer.alert('请填写事项内容', {icon: 1, time: 3000});
            return;
        }
        processJson.processTemplateAttachmentList=aryFile;
        taskJson.processInfo = processJson;

        $.ajax({
            url: contextPath + "confirm-matter/confirm",
            type:"post",
            data: JSON.stringify(taskJson),
            dataType: "json",
            processData:false,
            contentType : 'application/json;charset=utf-8', //设置请求头信息
            success:function(data){
                refreshTable();
                layer.alert(data.data, {icon: 1, time: 3000});
                layer.close(pageTask);
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

    //全部删除
    $("#fileRemoveAll").click(function () {
        $("#tmpFiles .file-list").remove("");
        $("#spanFileNum").text(0);
    });
}

