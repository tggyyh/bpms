//@ sourceURL=index.js

$(function () {
    //定义按钮行为
    initSpan();
});

function initSpan(){
    $.ajax(contextPath + "/manager-matter/undo-matter").success(function (data) {
        console.log("success");
        $(".ibox-content").html('');
        $(".ibox-content").html(data);
    });
    $("#undoMatter").click(function(){
        $("#undoMatter").addClass("span_color");
        $("#overMatter").removeClass("span_color");
        $.ajax(contextPath + "/manager-matter/undo-matter").success(function (data) {
            console.log("success");
            $(".ibox-content").html('');
            $(".ibox-content").html(data);
        });
    });
    $("#overMatter").click(function(){
        $("#undoMatter").removeClass("span_color");
        $("#overMatter").addClass("span_color");
        $.ajax(contextPath + "/manager-matter/over-matter").success(function (data) {
            console.log("success");
            $(".ibox-content").html('');
            $(".ibox-content").html(data);
        });
    });
}

function loadFile() {
    var loadIndex = layer.load(2, {
        shade: [0.3,'#cacaca'],
        area: ['50px', '50px'],
        offset:['50%','50%'],
    });
    var form = new FormData(document.getElementById("taskForm"));
    $.ajax({
        url: contextPath + "/file/parse",
        type: "post",
        data: form,
        processData: false,
        contentType: false,
        success: function (data) {
            layer.close(loadIndex);
        },
        error: function (e) {
            layer.close(loadIndex);
            layer.alert('上传失败', {icon: 1, time: 3000});
        }
    });
}