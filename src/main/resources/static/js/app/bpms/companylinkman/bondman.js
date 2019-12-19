/**
 * Created by Dealing076 on 2018/5/2.
 */
//@ sourceURL=bondman.js
$(function () {
    $("#divBondManContent").css("height", $(".layui-layer-page").height()-250 + "px");

    var bondManId = $("#hidBondManId").val().split(",");
    for(var i=0;i<bondManId.length;i++){
        $("#cbx-" + bondManId[i]).prop("checked","checked");
    }

    $("#btnBondManSave").click(function(){
        var bondCode = $("#hidBondManCode").val();
        var bondMan = [];
        var bondManId = [];
        var bondManagerList = new Array();
        $("input[name='cbxBondMan']:checkbox:checked").each(function(index, item){
            bondManId.push($(item).attr("id").replace("cbx-",""));
            bondMan.push($(item).val());

            var bondManager = {};
            bondManager.bondCode = bondCode;
            bondManager.userId = $(item).attr("id").replace("cbx-","");
            bondManagerList.push(bondManager);
        });

        if(bondManId.length<=0){
            layer.alert('保存失败:项目负责人不能为空', {icon: 1, time: 3000});
            return;
        }

        var reqData = {};
        reqData.bondCode = bondCode;
        reqData.bondManagerList = bondManagerList;

        var loadIndex = layer.load(2, {
            shade: [0.3,'#cacaca'],
            area: ['50px', '50px'],
            offset:['50%','50%'],
        });

        $.ajax({
            url: contextPath + "/bondmanager/bondManagerSave",
            type:"post",
            data: JSON.stringify(reqData),//将对象序列化成JSON字符串
            dataType:"json",
            contentType : 'application/json;charset=utf-8', //设置请求头信息
            success:function(data){
                layer.close(loadIndex);
                if(data.code==0){
                    $("#hidBondManCode").val(bondCode);
                    $("#hidBondManName").val(bondMan.join(","));
                    $("#hidBondManId").val(bondManId.join(","));

                    layer.close(pageBondman);
                }else{
                    layer.alert(data.message, {icon: 1, time: 3000});
                }
            },
            error:function(e){
                layer.close(loadIndex);
                layer.alert('保存失败', {icon: 1, time: 3000});
            }
        });
    });

    $("#btnBondManCancel").click(function(){
        layer.close(pageBondman);
    });
});