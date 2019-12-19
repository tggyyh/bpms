//添加
//@ sourceURL=confirm-right-line.js
$(function () {
    //定义按钮行为
    initConfirm();
    initTitle();
    initSummernoteS();
});
function initSummernoteS() {
    $(".sub-matter-color").each(function (index,item) {
        var curSum = $(item).find("div[class='summernote']");
        curSum.summernote({
            lang: 'zh-CN',
            height: 300,
            minHeight: 300,
            maxHeight:null,
            disableDragAndDrop: false,   //禁止拖放
            dialogsInBody: true,
            placeholder: '这里填写事项内容',
            // toolbar工具栏默认
            toolbar: [
                ['style', ['style', 'bold', 'italic', 'underline', 'clear']],
                ['fontname', ['fontname']],
                ['fontsize', ['fontsize']],
                ['color', ['color']],
                ['para', ['ul', 'ol', 'paragraph']],
                ['table', ['table']],
                ['insert', ['link']],
                ['history', ['undo', 'redo']]
            ],
            onImageUpload: function (files) {
                //sendFile(files);
            }
        });

        $(".note-editor>.btn-toolbar>div").each(function(index, item){
            $(item).find(":button").each(function(vindex,vitem){
                var title = $(vitem).attr("data-original-title");
                $(vitem).attr("title", title);
            });
        });

        var markupStr = $(item).find("input[name='hidContent']").val();
        curSum.code(markupStr);
    });
    $("#rightLineContent").summernote({
        lang: 'zh-CN',
        height: 300,
        minHeight: 300,
        maxHeight:null,
        disableDragAndDrop: false,   //禁止拖放
        dialogsInBody: true,
        placeholder: '这里填写事项内容',
        // toolbar工具栏默认
        toolbar: [
            ['style', ['style', 'bold', 'italic', 'underline', 'clear']],
            ['fontname', ['fontname']],
            ['fontsize', ['fontsize']],
            ['color', ['color']],
            ['para', ['ul', 'ol', 'paragraph']],
            ['table', ['table']],
            ['insert', ['link']],
            ['history', ['undo', 'redo']]
        ],
        onImageUpload: function (files) {
            //sendFile(files);
        }
    });

    $(".note-editor>.btn-toolbar>div").each(function(index, item){
        $(item).find(":button").each(function(vindex,vitem){
            var title = $(vitem).attr("data-original-title");
            $(vitem).attr("title", title);
        });
    });

    var lineContent = $("#hidRightLineContent").val();
    $("#rightLineContent").code(lineContent);
}

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
        var mts = new Array();
        var flag = true;
        $(".sub-matter-color").each(function (index,item) {
            var taskJson = {};
            var processJson = {};

            taskJson.id=$(item).find("input[name='taskId']").val();
            taskJson.processId=$(item).find("input[name='pId']").val();
            processJson.processId=$(item).find("input[name='pId']").val();
            processJson.content = $(item).find("div[class='summernote']").code();
            processJson.rightLineContent=$("#rightLineContent").code();
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
            $("#tmpFiles .file-list").each(function(index, fileItem){
                var attachment = {};
                attachment.type = $(fileItem).find("input[name='type']").val();
                attachment.name = $(fileItem).find("input[name='name']").val();
                attachment.url = $(fileItem).find("input[name='url']").val();
                attachment.processId = $(item).find("input[name='pId']").val();
                aryFile.push(attachment);
            });

            if($.trim(processJson.content )==""){
                layer.alert('请填写事项内容', {icon: 1, time: 3000});
                flag = false;
            }
            processJson.processTemplateAttachmentList=aryFile;
            taskJson.processInfo = processJson;
            mts.push(taskJson);
        });
        if(!flag){
            return;
        }
        $.ajax({
            url: contextPath + "confirm-matter/confirm-right-line",
            type:"post",
            data: JSON.stringify(mts),
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


