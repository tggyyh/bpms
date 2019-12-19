//添加
//@ sourceURL=add.js
$(function () {

    //定义按钮行为
    init();
    initTitle();
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
    // 全部下载
    $("#fileDownloadAll").click(function () {
        downAllFile();
    });
    $("#fileDownloadAllR").click(function () {
        downAllFileR();
    });

}
