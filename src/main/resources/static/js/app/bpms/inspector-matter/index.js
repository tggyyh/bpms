//@ sourceURL=index.js
$(function () {
    //定义按钮行为
    // initTable();
    initSpan();
});

function initSpan(){
    $.ajax(contextPath + "/inspector-matter/undo-matter").success(function (data) {
        console.log("success");
        $(".ibox-content").html('');
        $(".ibox-content").html(data);
    });
    $("#undoMatter").click(function(){
        $("#undoMatter").addClass("span_color");
        $("#overMatter").removeClass("span_color");
        $.ajax(contextPath + "/inspector-matter/undo-matter").success(function (data) {
            console.log("success");
            $(".ibox-content").html('');
            $(".ibox-content").html(data);
        });
    });
    $("#overMatter").click(function(){
        $("#undoMatter").removeClass("span_color");
        $("#overMatter").addClass("span_color");
        $.ajax(contextPath + "/inspector-matter/over-matter").success(function (data) {
            console.log("success");
            $(".ibox-content").html('');
            $(".ibox-content").html(data);
        });
    });
}

