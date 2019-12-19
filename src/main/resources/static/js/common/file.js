
function setFile(objFile) {
    // var tmpFile = $("#uploadFile").val();
    if(objFile.files[0]==null){
        return;
    }
    var existFileName =[];
    $("#tmpFiles .file-list").each(function(index, item){
        existFileName.push($(item).find("input[name='name']").val());
    });
    var existFileSize = existFileName.length;
    if(existFileSize>0){
        var size = objFile.files.length;
        for(var i=0;i<size;i++){
            var fileName = objFile.files[i].name;
            for(var j=0;j<existFileSize;j++){
                if(fileName == existFileName[j]){
                    layer.alert('上传失败，上传材料的文件名已存在！', {icon: 1, time: 3000});
                    $("#uploadFile").val("");
                    return;
                }
            }
        }
    }

    var loadIndex = layer.load(2, {
        shade: [0.3,'#cacaca'],
        area: ['50px', '50px'],
        offset:['50%','50%'],
    });
    var form = new FormData(document.getElementById("taskForm"));
    $.ajax({
        url: contextPath + "/file/upload",
        type: "post",
        data: form,
        processData: false,
        contentType: false,
        success: function (data) {
            layer.close(loadIndex);
            if(data.code==-1){
                layer.alert(data.message, {icon: 1, time: 3000});
                return;
            }

            var files =[];
            files = data.data;
            for(var i=0; i<files.length;i++){
                var fileIcon = getTmpFileIcon(files[i].type);
                var tmpFileHtml = '<div class="file-list">';
                tmpFileHtml += '<input type="hidden" name="type" value="' + fileIcon + '"/>';
                tmpFileHtml += '<input type="hidden" name="name" value="' + files[i].name + '"/>';
                tmpFileHtml += '<input type="hidden" name="url" value="' + files[i].url + '"/>';

                tmpFileHtml += '<span class="file-title" title="' + files[i].name + '">' +files[i].name+'</span>';
                tmpFileHtml += '<a class="file-down" onclick="downFile(this)">下载</a>';
                tmpFileHtml += '<a class="file-remove" onclick="fileRemove(this)">删除</a>';
                tmpFileHtml += '</div>';
                $("#tmpFiles").append(tmpFileHtml);
            }
            layer.alert(data.message, {icon: 1, time: 3000});
            $("#spanFileNum").text($("#tmpFiles .file-list").size());
            $("#uploadFile").val("");
        },
        error: function (e) {
            $("#uploadFile").val("");
            layer.close(loadIndex);
            layer.alert('上传失败', {icon: 1, time: 3000});
        }
    });
}

//文件删除
function fileRemove(fileObj) {
    $(fileObj).parent().remove();
    $("#spanFileNum").text($("#tmpFiles .file-list").size());
}
//文件删除
function fileRemoveR(fileObj) {
    $(fileObj).parent().remove();
    $("#spanFileNumR").text($("#tmpFilesR .file-list").size());
}
function downFile(ele) {
    var form = document.createElement('form');
    form.setAttribute('method', 'post');
    form.setAttribute('action', contextPath + "/file/download");
    var nameValue = $(ele).parent().find("input[name='name']").val();
    var nameField = document.createElement("input");
    nameField.setAttribute("type", "hidden");
    nameField.setAttribute("name", "name");
    nameField.setAttribute("value",nameValue);

    var urlValue = $(ele).parent().find("input[name='url']").val();
    var urlField = document.createElement("input");
    urlField.setAttribute("type", "hidden");
    urlField.setAttribute("name", "url");
    urlField.setAttribute("value",urlValue);
    form.appendChild(nameField);
    form.appendChild(urlField);
    ele.appendChild(form);
    form.submit();
    if (form.remove) {
        form.remove();
    } else {
        form.removeNode();
    }
}


function downAllFile() {
    var ary =  new Array();
    $("#tmpFiles .file-list").each(function(index, item){
        var attachment = {};
        attachment.type = $(item).find("input[name='type']").val();
        attachment.name = $(item).find("input[name='name']").val();
        attachment.url = $(item).find("input[name='url']").val();
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
    if (form.remove) {
        form.remove();
    } else {
        form.removeNode();
    }
}

function downAllFileR() {
    var ary =  new Array();
    $("#tmpFilesR .file-list").each(function(index, item){
        var attachment = {};
        attachment.type = $(item).find("input[name='type']").val();
        attachment.name = $(item).find("input[name='name']").val();
        attachment.url = $(item).find("input[name='url']").val();
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
    if (form.remove) {
        form.remove();
    } else {
        form.removeNode();
    }
}

function setFileR(objFile) {
    // var tmpFile = $("#uploadFile").val();
    if(objFile.files[0]==null){
        return;
    }

    var existFileName =[];
    $("#tmpFilesR .file-list").each(function(index, item){
        existFileName.push($(item).find("input[name='name']").val());
    });
    var existFileSize = existFileName.length;
    if(existFileSize>0){
        var size = objFile.files.length;
        for(var i=0;i<size;i++){
            var fileName = objFile.files[i].name;
            for(var j=0;j<existFileSize;j++){
                if(fileName == existFileName[j]){
                    layer.alert('上传失败，上传材料的文件名已存在！', {icon: 1, time: 3000});
                    $("#uploadFile").val("");
                    return;
                }
            }
        }
    }

    var loadIndex = layer.load(2, {
        shade: [0.3,'#cacaca'],
        area: ['50px', '50px'],
        offset:['50%','50%'],
    });
    var form = new FormData(document.getElementById("taskForm"));
    $.ajax({
        url: contextPath + "/file/upload",
        type: "post",
        data: form,
        processData: false,
        contentType: false,
        success: function (data) {
            layer.close(loadIndex);
            if(data.code==-1){
                layer.alert(data.message, {icon: 1, time: 3000});
                return;
            }
            var files =[];
            files = data.data;
            for(var i=0; i<files.length;i++){
                var fileIcon = getTmpFileIcon(files[i].type);
                var tmpFileHtml = '<div class="file-list">';
                tmpFileHtml += '<input type="hidden" name="type" value="' + fileIcon + '"/>';
                tmpFileHtml += '<input type="hidden" name="name" value="' + files[i].name + '"/>';
                tmpFileHtml += '<input type="hidden" name="url" value="' + files[i].url + '"/>';

                tmpFileHtml += '<span class="file-title" title="' + files[i].name + '">' +files[i].name+'</span>';
                tmpFileHtml += '<a class="file-down" onclick="downFile(this)">下载</a>';
                tmpFileHtml += '<a class="file-remove" onclick="fileRemoveR(this)">删除</a>';
                tmpFileHtml += '</div>';
                $("#tmpFilesR").append(tmpFileHtml);
            }
            layer.alert(data.message, {icon: 1, time: 3000});
            $("#spanFileNumR").text($("#tmpFilesR .file-list").size());
            $("#uploadFile").val("");
        },
        error: function (e) {
            $("#uploadFile").val("");
            layer.close(loadIndex);
            layer.alert('上传失败', {icon: 1, time: 3000});
        }
    });
}