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