//@ sourceURL=process.js
//文本编辑器
function initSummernote(){
    $('#summernote').summernote({
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
}
function companyInfo(ob){
    var companyName = $(ob).text().trim();
    $.ajax(contextPath + "/company/info/"+companyName).success(function (data) {
        console.log("success");
        layer.open({
            type: 1,
            title: '发行人详情',
            maxmin: false,
            resize: false,
            shade: 0.3,
            area: ['95%', '98%'],
            scrollbar: false,
            content: data
        });
    });
}

function bondInfo(ob) {
    var bondCode = $(ob).text().trim();
    $.ajax(contextPath + "/bond/info/" + bondCode ).success(function (data) {
        console.log("success");
        layer.open({
            type: 1,
            title: '项目详情',
            maxmin: false,
            resize: false,
            shade: 0.3,
            area: ['95%', '98%'],
            scrollbar: false,
            content: data
        });
    });
}
function bondInfo1(ob) {
    var bondCode = $(ob).next().text().trim();
    $.ajax(contextPath + "/bond/info/" + bondCode ).success(function (data) {
        console.log("success");
        layer.open({
            type: 1,
            title: '项目详情',
            maxmin: false,
            resize: false,
            shade: 0.3,
            area: ['95%', '98%'],
            scrollbar: false,
            content: data
        });
    });
}
function managerInfo(ob){
    var bondCode = $(ob).prev().prev().text().trim();
    $.ajax(contextPath + "/bondmanager/bondView/" + bondCode).success(function (data) {
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

function linkmanInfo(ob){
    var companyName = $(ob).prev().prev().text().trim();
    $.ajax(contextPath + "/companylinkman/listLinkman/" + companyName).success(function (data) {
        console.log("success");
        layer.open({
            type: 1,
            title: '发行人对接人',
            maxmin: false,
            shade: 0.3,
            area: ['70%', '60%'],
            scrollbar: false,
            content: data
        });
    });
}


function  confirmMatter(id) {
    $.ajax(contextPath + "confirm-matter/" + id).success(function (data) {
        console.log("success");
        if(data.indexOf("errorDiv")>0){
            layer.alert(data,{icon: 1, time: 2000});
            refreshTable();
        }else {
            pageTask = layer.open({
                type: 1,
                title: id,
                maxmin: false,
                shade: 0,
                area: ['90%', '95%'],
                content: data
            });
        }
    });
}

function  confirmRightLineMatter(id) {
    $.ajax(contextPath + "confirm-matter/right-line/" + id).success(function (data) {
        console.log("success");
        if(data.indexOf("errorDiv")>0){
            layer.alert(data,{icon: 1, time: 2000});
            refreshTable();
        }else {
            pageTask = layer.open({
                type: 1,
                title: id,
                maxmin: false,
                shade: 0,
                area: ['90%', '95%'],
                content: data
            });
        }
    });
}


function  checkMatter(id) {
    $.ajax(contextPath + "check-matter/check-matter/" + id).success(function (data) {
        console.log("success");
        if(data.indexOf("errorDiv")>0){
            layer.alert(data,{icon: 1, time: 2000});
            refreshTable();
        }else {
            pageTask = layer.open({
                type: 1,
                title: id,
                maxmin: false,
                shade: 0,
                area: ['90%', '95%'],
                content: data
            });
        }
    });
}

function  reCheckMatter(id) {
    $.ajax(contextPath + "check-matter/re-check-matter/" + id).success(function (data) {
        console.log("success");
        if(data.indexOf("errorDiv")>0){
            layer.alert(data,{icon: 1, time: 2000});
            refreshTable();
        }else {
            pageTask = layer.open({
                type: 1,
                title: id,
                maxmin: false,
                shade: 0,
                area: ['90%', '95%'],
                content: data
            });
        }
    });
}


function  checkRightLineMatter(id) {
    $.ajax(contextPath + "check-matter/check-right-line-matter/" + id).success(function (data) {
        console.log("success");
        if(data.indexOf("errorDiv")>0){
            layer.alert(data,{icon: 1, time: 2000});
            refreshTable();
        }else {
            pageTask = layer.open({
                type: 1,
                title: id,
                maxmin: false,
                shade: 0,
                area: ['90%', '95%'],
                content: data
            });
        }
    });
}

function  reCheckRightLineMatter(id) {
    $.ajax(contextPath + "check-matter/re-check-right-line-matter/" + id).success(function (data) {
        console.log("success");
        if(data.indexOf("errorDiv")>0){
            layer.alert(data,{icon: 1, time: 2000});
            refreshTable();
        }else {
            pageTask = layer.open({
                type: 1,
                title: id,
                maxmin: false,
                shade: 0,
                area: ['90%', '95%'],
                content: data
            });
        }
    });
}

function  viewMatterInfo(id,orderIndex,pId) {
    $.ajax(contextPath + "inspector-trace-matter/info/" + id+"/"+pId+"/"+orderIndex).success(function (data) {
        console.log("success");
        pageTask = layer.open({
            type: 1,
            title: id,
            maxmin: false,
            shade: 0,
            area: ['90%', '95%'],
            content: data
        });
    });
}
function  checkMatterInfo(id,orderIndex,pId,status) {
    $.ajax(contextPath + "check-matter/info/" + id+"/"+pId+"/"+orderIndex+"/"+status).success(function (data) {
        console.log("success");
        pageTask = layer.open({
            type: 1,
            title: id,
            maxmin: false,
            shade: 0,
            area: ['90%', '95%'],
            content: data
        });
    });
}
function  viewMatter(id) {
    $.ajax(contextPath + "inspector-trace-matter/" + id).success(function (data) {
        console.log("success");
        pageTask = layer.open({
            type: 1,
            title: id,
            maxmin: false,
            shade: 0,
            area: ['90%', '95%'],
            content: data
        });
    });
}

function  viewRightLineMatter(id) {
    $.ajax(contextPath + "inspector-trace-matter/right-line/" + id).success(function (data) {
        console.log("success");
        pageTask = layer.open({
            type: 1,
            title: id,
            maxmin: false,
            shade: 0,
            area: ['90%', '95%'],
            content: data
        });
    });
}
function  handleMatterInfo(id,orderIndex,pId,status) {
    $.ajax(contextPath + "handle-matter/info/" + id+"/"+pId+"/"+orderIndex+"/"+status).success(function (data) {
        console.log("success");
        pageTask = layer.open({
            type: 1,
            title: id,
            maxmin: false,
            shade: 0,
            area: ['90%', '95%'],
            content: data
        });
    });
}

function  handleMatter(id) {
    $.ajax(contextPath + "handle-matter/handle-matter/" + id).success(function (data) {
        console.log("success");
        if(data.indexOf("errorDiv")>0){
            layer.alert(data,{icon: 1, time: 2000});
            refreshTable();
        }else {
            pageTask = layer.open({
                type: 1,
                title: id,
                maxmin: false,
                shade: 0,
                area: ['90%', '95%'],
                content: data
            });
        }
    });
}

function  reHandleMatter(id) {
    $.ajax(contextPath + "handle-matter/re-handle-matter/" + id).success(function (data) {
        console.log("success");
        if(data.indexOf("errorDiv")>0){
            layer.alert(data,{icon: 1, time: 2000});
            refreshTable();
        }else {
            pageTask = layer.open({
                type: 1,
                title: id,
                maxmin: false,
                shade: 0,
                area: ['90%', '95%'],
                content: data
            });
        }
    });
}

function  handleRightLineMatter(id) {
    $.ajax(contextPath + "handle-matter/handle-right-line-matter/" + id).success(function (data) {
        console.log("success");
        if(data.indexOf("errorDiv")>0){
            layer.alert(data,{icon: 1, time: 2000});
            refreshTable();
        }else {
            pageTask = layer.open({
                type: 1,
                title: id,
                maxmin: false,
                shade: 0,
                area: ['90%', '95%'],
                content: data
            });
        }
    });
}

function  reHandleRightLineMatter(id) {
    $.ajax(contextPath + "handle-matter/re-handle-right-line-matter/" + id).success(function (data) {
        console.log("success");
        if(data.indexOf("errorDiv")>0){
            layer.alert(data,{icon: 1, time: 2000});
            refreshTable();
        }else {
            pageTask = layer.open({
                type: 1,
                title: id,
                maxmin: false,
                shade: 0,
                area: ['90%', '95%'],
                content: data
            });
        }
    });
}

function  cancelMatter(id) {
    $.ajax({
        url: contextPath + "/confirm-matter/cancel-process/"+id,
        type:"post",
        dataType: "json",
        processData:false,
        contentType : 'application/json;charset=utf-8', //设置请求头信息
        success:function(data){
            layer.alert("事项作废成功！", {icon: 1, time: 3000});
            refreshTable();
        },
        error:function(e){
            layer.alert("事项作废失败", {icon: 1, time: 3000});
        }
    });
}

function  cancelRightLineMatter(id) {
    $.ajax({
        url: contextPath + "/confirm-matter/cancel-right-line-process/"+id,
        type:"post",
        dataType: "json",
        processData:false,
        contentType : 'application/json;charset=utf-8', //设置请求头信息
        success:function(data){
            layer.alert("事项作废成功！", {icon: 1, time: 3000});
            refreshTable();
        },
        error:function(e){
            layer.alert("事项作废失败", {icon: 1, time: 3000});
        }
    });
}

function  viewOverMatterInfo(id,orderIndex) {
    $.ajax(contextPath + "inspector-matter/view-over-matter/info/" + id+"/"+orderIndex).success(function (data) {
        console.log("success");
        pageTask = layer.open({
            type: 1,
            title: id,
            maxmin: false,
            shade: 0,
            area: ['90%', '95%'],
            content: data
        });
    });
}
