/**
 * Created by Dealing076 on 2017/6/16.
 */
//@ sourceURL=index.js
$(function () {
    getMatterCount();
});
var pwdPageIndex;
function editPWD(){
    $.ajax(contextPath + "/editPWD").success(function (data) {
        pwdPageIndex = layer.open({
            id: 'editPWD',
            type: 1,
            title: '修改密码',
            zIndex :1000,
            maxmin: false,
            shade: 0.3,
            area: ['40%', '50%'],
            content: data
        });
    });
}
function manualTask(){
    var count = $("#matter_count").text();
    if(count==0){
        return;
    }
    $.ajax({
        url: contextPath + "/manual-task/",
        type:"post",
        dataType: "json",
        processData:false,
        contentType : 'application/json;charset=utf-8', //设置请求头信息
        success:function(data){
            if(data.data == -1){
                layer.alert("其他流程正在处理,请稍后重试！", {icon: 1, time: 3000});
            }else {
                layer.alert("当天可触发的事项已触发，请到待办事项版块进行处理！", {icon: 1, time: 3000});
                $("#bellDiv").removeClass("menu-bell-color-red");
                $("#bellDiv").addClass("menu-bell-color-grey");
                $("#matter_count").text(0);
            }
        },
        error:function(e){
            layer.alert("触发失败", {icon: 1, time: 3000});
        }
    });
}

function getMatterCount(){
    $.ajax({
        url: contextPath + "/manual-task/count",
        type:"POST",
        dataType: "json",
        processData:false,
        contentType : 'application/json;charset=utf-8', //设置请求头信息
        success:function(data){
            if(data.data>0){
                $("#bellDiv").removeClass("menu-bell-color-grey");
                $("#bellDiv").addClass("menu-bell-color-red");
                $("#matter_count").text(data.data);
            }else{
                $("#bellDiv").removeClass("menu-bell-color-red");
                $("#bellDiv").addClass("menu-bell-color-grey");
                $("#matter_count").text(data.data);
            }
        }
    });

    setTimeout('getMatterCount()',30000);
}




































































































































































