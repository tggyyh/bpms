var pageIndex;
$(function () {
    //初始化Buttons
    initButtons();

    //初始化Table
    initTree();
    initTable();
});

function initButtons(){
    //添加
    $("#btn_add").click(function(){
        $.ajax(contextPath + "/module/add").success(function (data) {
            pageIndex = layer.open({
                id: 'addPage',
                type: 1,
                title: '添加模块',
                maxmin: true,
                shade: 0.3,
                area: ['60%', '80%'],
                content: data
            });
        });
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

        $.ajax(contextPath + "/module/edit/" + orgRow[0].id).success(function (data) {
            console.log("success");
            pageIndex = layer.open({
                id: 'edit',
                type: 1,
                title: '编辑模块信息',
                maxmin: true,
                zIndex :1000,
                shade: 0.3,
                area: ['60%', '80%'],
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
            '删除会删除对应叶子模块,您确定要删除吗？',
            {icon: 3, title: '提示'},
            function (index) {
                $.ajax({
                    url: contextPath + "/module/delete/" + ids,
                    type: "post",
                    dataType: "json",
                    success: function (data) {
                        if (data.code == 0) {
                            initTree();
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

function initTree(){
    $.ajax({
        url:contextPath + '/module/findAll',
        type: 'get',
        dataType: 'json',
        success: function (data) {
            setTreeData(data);
        },
        error: function (e) {
            layer.alert('加载数据失败', {icon: 1, time: 3000});
        }
    });
}

function setTreeData(data){
    var groupData = getTreeData(data);

    $('#moduleTree').treeview({
        showIcon: false,
        showCheckbox: false,
        showTags: true,
        levels: 3,
        data: groupData,
        onNodeSelected: function(event, node){
            $("#hidModuleId").val(node.id);
            $("#hidParentId").val(node.parentId);
            $("#hidStatus").val(node.status);
            $('#tab').bootstrapTable('refresh');
        },
        onNodeUnselected: function(event, node){
            $("#hidModuleId").val("");
            $("#hidParentId").val("");
            $("#hidStatus").val("");
            // $('#tab').bootstrapTable('refresh');
        }
    });
}

function getTreeData(treeData){
    var groupData = [];
    for(var i=0;i<treeData.length;i++){
        var groupJson = {};
        groupJson.id = treeData[i].id;
        groupJson.text = treeData[i].name;
        groupJson.parendId = treeData[i].parentId;
        groupJson.status = treeData[i].status;
        var treeUrl = "<span>" + treeData[i].url + "</span>";
        // var treeStatus = treeData[i].status==1?"<span>有效</span>":"<span style='color:#6d0a1a;'>无效</span>";
        // groupJson.tags = [treeStatus, treeUrl];
        if(treeData[i].modules!=null && treeData[i].modules.length>0){
            groupJson.nodes = getTreeData(treeData[i].modules);
        }
        groupData.push(groupJson);
    }
    return groupData;
}

//table查询参数
function queryParams(params) {  //配置参数
    return {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
        pageSize: params.limit,
        offset: params.offset,
        moduleId: $("#hidModuleId").val()
    };
}

//初始化Table
function initTable() {

    $('#tab').bootstrapTable('destroy');

    $('#tab').bootstrapTable({
        url: contextPath + '/module/findByPage',        //请求后台的URL（*）
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
                title: '模块名称'
            },
            {
                field: 'code',
                title: '模块码'
            },
            {
                field: 'url',
                title: '模块地址'
            },
            {
                field: 'priority',
                title: '优先级'
            },
            {
                field: 'createTime',
                title: '操作',
                formatter: function (value, row, index) {  // value:field的值,row:行数据，用row.createdDtm找发起时间字段,index:行下标
                    return '<a onclick="" class="bw">'+'编辑功能'+'</a>';
                },
                events: {
                    'click .bw': function (e, value, row, index) {
                        editPermission(row.id);
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

function  backRoot() {
    $("#hidModuleId").val("");
    $("#hidParentId").val("");
    $("#hidStatus").val("");
    $('#tab').bootstrapTable('refresh');
}

function editPermission(moduleId) {
    $.ajax(contextPath + "permission/index/" + moduleId).success(function (data) {
        console.log("success");
        pageEditIndex = layer.open({
            type: 1,
            title: '编辑功能',
            maxmin: false,
            shade: 0,
            area: ['60%', '95%'],
            content: data
        });
    });
}