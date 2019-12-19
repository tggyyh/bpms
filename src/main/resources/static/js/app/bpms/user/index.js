//@ sourceURL=index.js
$(function () {
    initTable();
    initButton();
});

//table查询参数
function queryParams(params) {  //配置参数
    return {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
        pageSize: params.limit,
        offset: params.offset,
        name: $("#search_name").val(),
        accountNumber: $("#search_account").val()
    };
}

//初始化Table
function initTable() {

    $('#tab').bootstrapTable('destroy');

    $('#tab').bootstrapTable({
        url: contextPath + '/user/findByPage',        //请求后台的URL（*）
        method: 'post',                         //请求方式（*）
        toolbar: '#toolbar',                //工具按钮用哪个容器
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
                title: '姓名'
            },
            {
                field: 'accountNumber',
                title: '账号'
            },
            {
                field: 'roleName',
                title: '角色'
            },
            {
                field: 'email',
                title: 'E-mail'
            },
            {
                field: 'phone',
                title: '电话'
            },
            {
                field: 'createTime',
                title: '添加时间',
                formatter: function (value, row, index) {  // value:field的值,row:行数据，用row.createdDtm找发起时间字段,index:行下标
                    return timestampToDatetime(value);
                }
            }
        ],
        onClickRow: function (row, element) {
            $(".selected").removeClass("selected");
            $(element).addClass("selected");
        }
    });
}

function initButton() {
    //查询
    $("#btn_query").click(function () {
        initTable();
    });
    //添加
    $("#btn_add").click(function () {
        $.ajax(contextPath + "user/add/").success(function (data) {
            console.log("success");
            pageIndex = layer.open({
                id: 'add',
                type: 1,
                title: '添加账号信息',
                maxmin: true,
                shade: 0.3,
                zIndex :1000,
                area: ['60%', '70%'],
                content: data
            });
        })
    });

    //修改
    $("#btn_edit").click(function () {
        var orgRow = $("#tab").bootstrapTable('getSelections');
//        alert(JSON.stringify(orgRow));
        if (orgRow.length <= 0) {
            layer.alert('请选择要编辑的条目', {icon: 1, time: 3000});
            return;
        } else if (orgRow.length > 1) {
            layer.alert('一次只能编辑一条', {icon: 1, time: 3000});
            return;
        }

        $.ajax(contextPath + "/user/edit/" + orgRow[0].id).success(function (data) {
            console.log("success");
            pageIndex = layer.open({
                id: 'edit',
                type: 1,
                title: '编辑账号信息',
                maxmin: true,
                zIndex :1000,
                shade: 0.3,
                area: ['60%', '70%'],
                content: data
            });
        })
    });
    //删除
    $("#btn_delete").click(function () {
        var orgRow = $("#tab").bootstrapTable('getSelections');
        if (orgRow.length <= 0) {
            layer.alert('请选择要删除条目', {icon: 1, time: 3000});
            return;
        }
        var ids = new Array();
        for (var i = 0; i < orgRow.length; i++) {
            ids.push(orgRow[i].id);
        }
        layer.confirm(
            '您确定要删除吗？',
            {icon: 3, title: '提示'},
            function (index) {
                $.ajax({
                    url: contextPath + "/user/delete/" + ids,
                    type: "post",
                    dataType: "json",
                    success: function (data) {
                        if (data.code == 0) {
                            $('#tab').bootstrapTable('refresh');
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
