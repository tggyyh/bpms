//添加
//@ sourceURL=add.js
$(function () {

    //定义按钮行为
    hrTask();
});

function hrTask() {
    $("#hrTaskPass").click(function () {
        $.ajax({
            url: contextPath + "/qj-process/hrTaskPass/"+$("#taskId").val(),
            type:"post",
            processData:false,
            contentType :false,
            success:function(data){
                layer.alert(data.message, {icon: 1, time: 3000});
                layer.close(pageTask);
                $('#tab').bootstrapTable('refresh');
            },
            error:function(e){
                layer.alert('处理失败', {icon: 1, time: 3000});
            }
        });
    });
    $("#hrTaskReject").click(function () {
        $.ajax({
            url: contextPath + "/qj-process/hrTaskReject/"+$("#taskId").val(),
            type:"post",
            processData:false,
            contentType :false,
            success:function(data){
                layer.close(pageTask);
                $('#tab').bootstrapTable('refresh');
                layer.alert('处理成功', {icon: 1, time: 3000});
            },
            error:function(e){
                layer.alert('处理失败', {icon: 1, time: 3000});
            }
        });
    });
}

