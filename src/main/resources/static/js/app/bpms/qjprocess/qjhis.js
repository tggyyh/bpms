//@ sourceURL=qjhis.js
$(function () {

    //定义按钮行为
    initTable();

});

//table查询参数
function queryParams(params) {  //配置参数
    return {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
        pageSize: params.limit,
        offset: params.offset
    };
}
//初始化Table
function initTable() {

    $('#tab').bootstrapTable('destroy');

    $('#tab').bootstrapTable({
        url: contextPath + '/qj-process/qjHis',        //请求后台的URL（*）
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
                title: 'processId',
                formatter: function (value, row, index) {  // value:field的值,row:行数据，用row.createdDtm找发起时间字段,index:行下标
                    return '<a class="mx">'+value+'</a>';
                },
                events: {
                    'click .mx': function (e, value, row, index) {
                        process(value);
                    }
                }
            },

            {
                field: 'name',
                title: '流程名称'
            },
            {
                field: 'startUser',
                title: '发起用户'
            },
            {
                field: 'createTime',
                title: '开始日期',
                formatter: function (value, row, index) {  // value:field的值,row:行数据，用row.createdDtm找发起时间字段,index:行下标
                    if(value!=null){
                        value= timestampToDatetime(value);
                    }
                    return value;
                }
            },
            {
                field: 'endTime',
                title: '结束日期',
                formatter: function (value, row, index) {  // value:field的值,row:行数据，用row.createdDtm找发起时间字段,index:行下标
                    if(value!=null){
                        value= timestampToDatetime(value);
                    }
                    return value;
                }
            },
            {
                title: '操作',
                formatter: function (value, row, index) {  // value:field的值,row:行数据，用row.createdDtm找发起时间字段,index:行下标
                    return '<a class="canpr">流程取消</a>';
                },
                events: {
                    'click .canpr': function (e, value, row, index) {
                        cancelProcess(row.id);
                    }
                }
            }

        ],
        onClickRow: function (row, element) {
            $(".selected").removeClass("selected");
            $(element).addClass("selected");
        }
    });
}

function  process(value) {
    $.ajax(contextPath + "qj-process/process/" + value).success(function (data) {
        console.log("success");
        pageTask = layer.open({
            type: 1,
            title: '流程明细',
            maxmin: false,
            shade: 0,
            area: ['70%', '99%'],
            content: data
        });
    });
}

function  cancelProcess(value) {
        $.ajax({
            url: contextPath + "qj-process/cancelProcess/" + value,
            type:"post",
            processData:false,
            contentType : false,
            dataType: "json",
            success:function(data){
                $('#tab').bootstrapTable('refresh');
                layer.alert(data.message, {icon: 1, time: 3000});
            },
            error:function(e){
                layer.alert(data.message, {icon: 1, time: 3000});
            }
        });
}
