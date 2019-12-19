//添加
//@ sourceURL=add.js
$(function () {

    //定义按钮行为
    deployProcess();

});

function deployProcess() {
    var form = new FormData(document.getElementById("resumeForm"));
    $("#addSave").click(function () {
        var detailJson = {};
        detailJson.applicantName = $("#applicantName").val();
        detailJson.description = $("#description").val();
        $.ajax({
            url: contextPath + "/qj-process/addSave",
            type:"post",
            data:JSON.stringify(detailJson),
            processData:false,
            contentType : 'application/json;charset=utf-8',
            dataType: "json",
            success:function(data){

                layer.alert('申请成功', {icon: 1, time: 3000});
            },
            error:function(e){
                layer.alert('保存失败', {icon: 1, time: 3000});
            }
        });
    });
}

