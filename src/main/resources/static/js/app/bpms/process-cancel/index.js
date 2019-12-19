function cancelProcess () {
    var processId=$("#processId").val();

    $.ajax({
        url: contextPath + "confirm-matter/cancel-process/"+processId,
        type:"post",
        data: JSON.stringify(processId),
        dataType: "json",
        processData:false,
        contentType : 'application/json;charset=utf-8', //设置请求头信息
        success:function(data){
            layer.alert(data.message, {icon: 1, time: 3000});
        },
        error:function(e){
            layer.alert(data.message, {icon: 1, time: 3000});
        }
    });
};