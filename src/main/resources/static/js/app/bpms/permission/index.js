//@ sourceURL=index.js
$(function () {
    //初始化Buttons
    initPermissionButtons();
    initPermissionTable();
});

function initPermissionButtons(){
        $("#addSavePermission").click(function(){
            if(permissionValidate().form()){
                var form = new FormData(document.getElementById("permissionForm"));
                $.ajax({
                    url: contextPath + "/permission/addSave",
                    type:"post",
                    data:form,
                    processData:false,
                    contentType:false,
                    success:function(data){
                        if(data.code==0){
                            $('#tabPermission').bootstrapTable('refresh');
                        }else{
                            layer.alert(data.message, {icon: 1, time: 3000});
                        }
                    },
                    error:function(e){
                        layer.alert('添加失败', {icon: 1, time: 3000});
                    }
                });
            }
        });
    //删除
    $("#btn_deletePermission").click(function () {
        var row = $("#tabPermission").bootstrapTable('getSelections');
        if (row.length <= 0) {
            layer.alert('请选择要删除条目', {icon: 1, time: 3000});
            return;
        }
        var ids = new Array();
        for (var i = 0; i < row.length; i++) {
            ids.push(row[i].id);
        }
        layer.confirm(
            '您确定要删除吗？',
            {icon: 3, title: '提示'},
            function (index) {
                $.ajax({
                    url: contextPath + "/permission/delete/" + ids,
                    type: "post",
                    dataType: "json",
                    success: function (data) {
                        if (data.code == 0) {
                            $('#tabPermission').bootstrapTable('refresh');
                            layer.alert(data.message, {icon: 1, time: 3000});
                        } else {
                            layer.alert(data.message, {icon: 1, time: 3000});
                        }
                    },
                    error: function (e) {
                        layer.alert('删除失败', {icon: 1, time: 3000});
                    }
                });

                layer.close(index);
            });
    });
}
//table查询参数
function queryParams(params) {  //配置参数
    return {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
        pageSize: params.limit,
        offset: params.offset,
        moduleId: $("#moduleId").val()
    };
}

//初始化Table
function initPermissionTable() {

    $('#tabPermission').bootstrapTable('destroy');

    $('#tabPermission').bootstrapTable({
        url: contextPath + '/permission/findByPage',        //请求后台的URL（*）
        method: 'post',                         //请求方式（*）
        toolbar: 'toolbarPermission',                //工具按钮用哪个容器
        striped: false,                      //是否显示行间隔色
        cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
        pagination: true,                   //是否显示分页（*）
        sortable: true,                     //是否启用排序
        sortOrder: "asc",                   //排序方式
        queryParams: queryParams,           //传递参数（*）
        //queryParamsType: "limit",           //参数格式,发送标准的RESTFul类型的参数请求
        sidePagination: "server",           //分页方式：client客户端分页，server服务端分页（*）
        pageNumber: 1,                      //初始化加载第一页，默认第一页
        pageSize: 20,                       //每页的记录行数（*）
        pageList: [10, 25, 50, 100],        //可供选择的每页的行数（*）
        search: false,                       //是否显示表格搜索，此搜索是客户端搜索，不会进服务端
        strictSearch: true,
        showColumns: false,                  //是否显示所有的列
        showRefresh: false,                  //是否显示刷新按钮
        minimumCountColumns: 2,             //最少允许的列数
        clickToSelect: true,                //是否启用点击选中行

        // height: 500,                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
//        uniqueId: "id",                     //每一行的唯一标识，一般为主键列
        showToggle: false,                    //是否显示详细视图和列表视图的切换按钮
        cardView: false,                    //是否显示详细视图
        detailView: false,                   //是否显示父子表
        undefinedText: '',
        columns: [
            {
                checkbox: true
            },
            {
                field: 'id',
                title: 'id',
                visible: false
            },
            {
                field: 'name',
                title: '权限名称'
            },
            {
                field: 'code',
                title: '权限码'
            },
            {
                field: 'description',
                title: '描述'
            }
        ],
        onClickRow: function (row, element) {
            $(".selected").removeClass("selected");
            $(element).addClass("selected");
        }
    });
}

function permissionValidate(){
    var icon = "<i class='fa fa-times-circle'></i> ";
    return $("#permissionForm").validate({
        rules: {
            name: "required",
            code: "required"
        },
        messages: {
            name: icon + "请输入名称",
            code: icon + "请输入code"
        }
    });
}