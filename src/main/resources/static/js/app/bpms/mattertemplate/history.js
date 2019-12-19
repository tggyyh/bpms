/**
 * Created by Dealing076 on 2018/7/10.
 */
//@ sourceURL=history.js
$(function () {
    $("#divInfoHistory").css({"height": $(".layui-layer-page").height()-150 + "px", "overflow-x":"hidden","overflow-y":"auto"});
    $(".layui-layer-content").css({"overflow-x":"hidden","overflow-y":"hidden"});
});

function showItemContent(obj) {
    var id = $(obj).attr("id");
    if($(obj).hasClass("fa-plus-square-o")){
        $("div[name='" + id + "']").removeClass("hide");
        $(obj).removeClass("fa-plus-square-o");
        $(obj).addClass("fa-minus-square-o");
    }else{
        $("div[name='" + id + "']").addClass("hide");
        $(obj).addClass("fa-plus-square-o");
        $(obj).removeClass("fa-minus-square-o");
    }
}