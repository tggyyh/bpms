/**
 * Created by Dealing076 on 2018/4/9.
 */
//@ sourceURL=edit.js
var bondJson = {};
$(function () {
    initBondInfo();
    initUploadButtons();
});


function initBondInfo() {
    $.ajax({
        url: contextPath + "/upload/findBondInfo/" + $("#hid_upload_companyName").val(),
        type: "get",
        dataType: "json",
        success: function (data) {
            bondJson = data;
            $("#upload_bondName").bsSuggest({
                idField: 'id',
                keyField: 'shortname',
                effectiveFields: ['code','shortname'],            //有效显示于列表中的字段，非有效字段都会过滤，默认全部，对自定义getData方法无效
                effectiveFieldsAlias: {code:'代码', shortname: '简称'},       //有效字段的别名对象，用于 header 的显示
                searchFields: ['code','shortname'],               //有效搜索字段，从前端搜索过滤数据时使用。effectiveFields 配置字段也会用于搜索过滤
                showHeader: false,              //是否显示选择列表的 header，默认有效字段大于一列时显示，否则不显示
                showBtn: false,                  //是否显示下拉按钮
                allowNoKeyword: true,           //是否允许无关键字时请求数据
                data: {
                    value: bondJson
                }
            }).on('onSetSelectValue', function (e, keyword) {
                if (keyword != '') {
                    for (var i = 0; i < bondJson.length; i++) {
                        if (bondJson[i].code == keyword.code) {
                            $("#upload_bondCode").val(data[i].code);
                            break;
                        }
                    }
                }
            });
        }
    });
}

function initUploadButtons() {
    //模板材料个数
    $("#upload_spanFileNum").text($("#upload_tmpFiles .tmp-file").size());

    $("#upload_bondName").blur(function () {
        var isFlag = false;
        for(var i=0;i<bondJson.length;i++){
            if(bondJson[i].shortname==$("#upload_bondName").val()){
                isFlag = true;
                $("#upload_bondCode").val(bondJson[i].code);
                break;
            }
        }
        if(!isFlag){
            $("#upload_bondName").val("");
            $("#upload_bondCode").val("");
        }
    });

    //模板材料添加
    $("#upload_addTmpFile").click(function () {
        $("#upload_tmpFile").click();
    });

    //全部下载
    $("#upload_fileDownloadAll").click(function () {
        var ary =  new Array();
        $("#upload_tmpFiles .tmp-file").each(function(index, item){
            var attachment = {};
            attachment.type = $(item).find("input[name='templateAttachment.type']").val();
            attachment.name = $(item).find("input[name='templateAttachment.name']").val();
            attachment.url = $(item).find("input[name='templateAttachment.url']").val();
            ary.push(attachment);
        });
        if(ary.length<1){
            layer.alert('没有文件可以下载', {icon: 1, time: 3000});
            return;
        }
        var form = document.createElement('form');
        form.setAttribute('method', 'post');
        form.setAttribute('action', contextPath + "/file/downloadZip");
        for(var i=0;i<ary.length;i++){
            var nameValue = ary[i].name;
            var nameField = document.createElement("input");
            nameField.setAttribute("type", "hidden");
            nameField.setAttribute("name", "attachments["+i+"].name");
            nameField.setAttribute("value",nameValue);

            var urlValue = ary[i].url;
            var urlField = document.createElement("input");
            urlField.setAttribute("type", "hidden");
            urlField.setAttribute("name", "attachments["+i+"].url");
            urlField.setAttribute("value",urlValue);
            form.appendChild(nameField);
            form.appendChild(urlField);
        }
        $(".ibox-content").append(form);
        form.submit();
        form.remove();
    });

    //全部删除
    $("#upload_fileRemoveAll").click(function () {

        layer.confirm(
            "是否要删除所有事项材料？",
            {icon: 3, title:'事项材料删除'},
            function(index) {
                $("#upload_tmpFiles").html("");
                $("#upload_spanFileNum").text(0);
                layer.close(index);
            }
        );
    });

    //保存
    $("#uploadSave").click(function(){
        var upload = {};
        upload.id = $("#hid_upload_id").val();
        upload.companyName = $("#hid_upload_companyName").val();
        upload.name = $("#upload_name").val();
        upload.bondCode = $("#upload_bondCode").val();

        if($("#upload_description").val()==""){
            layer.alert("材料说明不能为空", {icon: 1, time: 3000});
            return false;
        }
        upload.description = $("#upload_description").val();

        if($("#upload_bondCode").val()!=""){
            upload.type = 1;
        }else{
            upload.type = 0;
        }

        var aryFile = new Array();
        if($("#upload_tmpFiles .tmp-file").size()<=0){
            layer.alert("保存失败：请添加模板材料", {icon: 1, time: 3000});
            isFlag = false;
            return false;
        }else{
            $("#upload_tmpFiles .tmp-file").each(function(index, item){
                var fileJson = {};
                fileJson.id = 0;
                fileJson.uploadId = $("#hid_upload_id").val();
                fileJson.type = $(item).find("input[name='templateAttachment.type']").val();
                fileJson.name = $(item).find("input[name='templateAttachment.name']").val();
                fileJson.url = $(item).find("input[name='templateAttachment.url']").val();
                aryFile.push(fileJson);
            });
        }
        upload.uploadAttachmentList = aryFile;

        var loadIndex = layer.load(2, {
            shade: [0.3,'#cacaca'],
            area: ['50px', '50px'],
            offset:['50%','50%'],
        });

        var form = new FormData(document.getElementById("uploadForm"));
        $.ajax({
            url: contextPath + "/upload/editSave",
            type:"post",
            data: JSON.stringify(upload),//将对象序列化成JSON字符串
            dataType:"json",
            contentType : 'application/json;charset=utf-8', //设置请求头信息
            success:function(data){
                layer.close(loadIndex);
                if(data.code==0){
                    layer.alert("修改成功", {icon: 1, time: 3000});
                    layer.close(uploadPage);
                    findInfoAttachmentData(0);
                }else{
                    layer.alert(data.message, {icon: 1, time: 3000});
                }
            },
            error:function(e){
                layer.close(loadIndex);
                layer.alert('修改失败', {icon: 1, time: 3000});
            }
        });

    });

    $("#uploadCancel").click(function () {
        layer.close(uploadPage);
    });
}

//文件上传
function setUploadTmpFile(objFile) {
    // var tmpFiles = $("#upload_tmpFile")[0].files;
    // if(tmpFiles.length<=0){
    //     layer.alert("上传失败：请选择要上传的文件", {icon: 1, time: 3000});
    //     return;
    // }

    if(objFile.files[0]==null){
        return;
    }

    var loadIndex = layer.load(2, {
        shade: [0.3,'#cacaca'],
        area: ['50px', '50px'],
        offset:['50%','50%'],
    });

    var isFlag = false;
    $("#upload_tmpFiles .tmp-file").each(function(index, item){
        if(isFlag===true){
            return false;
        }
        var attachment = {};
        attachment.type = $(item).find("input[name='templateAttachment.type']").val();
        attachment.name = $(item).find("input[name='templateAttachment.name']").val();
        for(var i=0;i<objFile.files.length;i++){
            if(objFile.files[i].name===attachment.name){
                isFlag = true;
                break;
            }
        }
    });
    if(isFlag===true){
        layer.alert("上传失败，上传材料的文件名已存在！", {icon: 1, time: 3000});
        $("#upload_tmpFile").val("");
        layer.close(loadIndex);
        return false;
    }

    var form = new FormData(document.getElementById("uploadForm"));

    $.ajax({
        url: contextPath + "/file/upload",
        type: "post",
        data: form,
        processData: false,
        contentType: false,
        success: function (data) {

            if (data.code == 0) {
                var files = data.data;
                for(var i=0;i<files.length;i++){
                    var fileIcon = getTmpFileIcon(files[i].type);
                    var tmpFileHtml = '<div class="tmp-file" style="width:100%;text-align:left;">';
                    tmpFileHtml += '<input type="hidden" name="templateAttachment.type" value="' + fileIcon + '"/>';
                    tmpFileHtml += '<input type="hidden" name="templateAttachment.name" value="' + files[i].name + '"/>';
                    tmpFileHtml += '<input type="hidden" name="templateAttachment.url" value="' + files[i].url + '"/>';

                    // if(fileIcon=="img"){
                    //     tmpFileHtml += '<div><img src="' + files[i].url + '" class="tmp-file-img" /></div>';
                    // }else{
                    //     tmpFileHtml += '<div class="tmp-file-' + fileIcon + '"></div>';
                    // }
                    // tmpFileHtml += '<div class="tmp-file-' + fileIcon + '" title="' + files[i].name + '"></div>';
                    // tmpFileHtml += '<div class="tmp-file-content tmp-font-12" title="' + files[i].name + '">' + files[i].name + '</div>';
                    // tmpFileHtml += '<div class="tmp-file-down" onclick="downUploadFile(this)">下载</div>';
                    // tmpFileHtml += '<div class="tmp-file-remove" onclick="removeUploadFile(this)">删除</div>';
                    // tmpFileHtml += '</div>';

                    tmpFileHtml += '<div title="' + files[i].name + '" style="float:left;max-width:85%;padding:5px 0;display:block;text-overflow:ellipsis;white-space:nowrap;overflow:hidden;">' + files[i].name +'</div>';
                    tmpFileHtml += '<div class="tmp-file-down" style="padding:5px;" onclick="downUploadFile(this)">下载</div>';
                    tmpFileHtml += '<div class="tmp-file-remove" style="padding:5px;" onclick="removeUploadFile(this)">删除</div>';
                    tmpFileHtml += '</div>';

                    $("#upload_tmpFiles").append(tmpFileHtml);
                }
                $("#upload_spanFileNum").text($("#upload_tmpFiles .tmp-file").size());
                layer.alert("上传成功", {icon: 1, time: 3000});
            } else {
                layer.alert(data.message, {icon: 1, time: 3000});
            }
            $("#upload_tmpFile").val("");
            layer.close(loadIndex);
        },
        error: function (e) {
            $("#upload_tmpFile").val("");
            layer.close(loadIndex);
            layer.alert('上传失败', {icon: 1, time: 3000});
        }
    });
}

//文件下载
function downUploadFile(objFile) {
    var form = document.createElement('form');
    form.setAttribute('method', 'post');
    form.setAttribute('action', contextPath + "/file/download");

    var nameValue = $(objFile).parent().find("input[name='templateAttachment.name']").val();
    var nameField = document.createElement("input");
    nameField.setAttribute("type", "hidden");
    nameField.setAttribute("name", "name");
    nameField.setAttribute("value",nameValue);

    var urlValue = $(objFile).parent().find("input[name='templateAttachment.url']").val();
    var urlField = document.createElement("input");
    urlField.setAttribute("type", "hidden");
    urlField.setAttribute("name", "url");
    urlField.setAttribute("value",urlValue);
    form.appendChild(nameField);
    form.appendChild(urlField);
    objFile.appendChild(form);
    form.submit();
    if (form.remove) {
        form.remove();
    } else {
        form.removeNode();
    }
}

//文件删除
function removeUploadFile(fileObj) {

    layer.confirm(
        "是否要删除该事项材料？",
        {icon: 3, title:'事项材料删除'},
        function(index) {
            $(fileObj).parent().remove();
            $("#upload_spanFileNum").text($("#upload_tmpFiles .tmp-file").size());
            layer.close(index);
        }
    );
}