//@ sourceURL=add.js
$(function(){
    //保存
   addSave();
});

function addSave(){
    $("#addSave").click(function(){
        if(moduleValidate().form()){
            if($("#priority").val()=="")
                $("#priority").val("999");
            if($("#url").val()=="")
                $("#url").val("#");
            var form = new FormData(document.getElementById("moduleForm"));
            $.ajax({
                url: contextPath + "/module/addSave",
                type:"post",
                data:form,
                processData:false,
                contentType:false,
                success:function(data){
                    if(data.code==0){
                        initTree();
                        $('#tab').bootstrapTable('refresh');
                        layer.close(pageIndex);
                    }else{
                        layer.alert(data.message, {icon: 1, time: 3000});
                    }
                },
                error:function(e){
                    layer.alert('保存失败', {icon: 1, time: 3000});
                }
            });
        }
    });
}

function moduleValidate(){
    var icon = "<i class='fa fa-times-circle'></i> ";
    return $("#moduleForm").validate({
        rules: {
            name: "required",
            code: "required"
        },
        messages: {
            name: icon + "请输入模块名称",
            code: icon + "请输入模块code"
        }
    });
}