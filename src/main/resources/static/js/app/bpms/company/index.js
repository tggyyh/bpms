/**
 * Created by Dealing076 on 2018/3/5.
 */
var areaJson;
var pageIndex;
$(function () {

    initArea();

    //定义按钮行为
    initButton();

    //初始化Table
    initTable();

});

function initArea() {
    $.ajax({
        url: contextPath + "/company/findArea",
        type: 'get',
        dataType: 'json',
        success: function (data) {
            areaJson = data;
            loadArea();
        },
        error: function (e) {
            layer.alert('加载数据失败', {icon: 1, time: 3000});
        }
    });
}

function loadArea(){
    $("#drop_search_province").empty();
    var provinceHTML = '<option value="">请选择省</option>';
    if(null!=areaJson){
        for(var i=0;i<areaJson.length;i++){
            if(areaJson[i].parentCode=='0'){
                provinceHTML += '<option value="' + areaJson[i].code + '">' + areaJson[i].name + '</option>';
            }
        }
    }
    $("#drop_search_province").append(provinceHTML);

    $("#drop_search_province").change(function(){
        loadCity();
    });
}
function loadCity(){
    $("#drop_search_city").empty();
    var provinceCode = $("#drop_search_province").val();
    var cityHTML = '<option value="">请选择市</option>';
    if(null!=areaJson){
        for(var i=0;i<areaJson.length;i++){
            if(areaJson[i].parentCode==provinceCode){
                cityHTML += '<option value="' + areaJson[i].code + '">' + areaJson[i].name + '</option>';
            }
        }
    }

    $("#drop_search_city").html(cityHTML);
}

function refreshCompanyData(){
    $('#tabCompany').bootstrapTable('refresh');
}

function initButton() {

    //查询
    $("#btn_query").click(function () {
        $('#tabCompany').bootstrapTable('refresh');
    });

}

//table查询参数
function queryParams(params) {  //配置参数
    return {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
        pageSize: params.limit,
        offset: params.offset,
        companyname: $("#txt_search_name").val(),
        provinceCode: $("#drop_search_province").val(),
        cityCode: $("#drop_search_city").val(),
    };
}

//初始化Table
function initTable() {

    $('#tabCompany').bootstrapTable('destroy');

    $('#tabCompany').bootstrapTable({
        url: contextPath + '/company/findAll',        //请求后台的URL（*）
        method: 'post',                         //请求方式（*）
        toolbar: '#toolbar',                //工具按钮用哪个容器
        striped: true,                      //是否显示行间隔色
        cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
        pagination: true,                   //是否显示分页（*）
        sortable: true,                     //是否启用排序
        sortOrder: "asc",                   //排序方式
        queryParams: queryParams,           //传递参数（*）
        //queryParamsType: "limit",           //参数格式,发送标准的RESTFul类型的参数请求
        sidePagination: "server",           //分页方式：client客户端分页，server服务端分页（*）
        pageNumber: 1,                      //初始化加载第一页，默认第一页
        pageSize: 10,                       //每页的记录行数（*）
        pageList: [10, 25, 50, 100],        //可供选择的每页的行数（*）
        search: false,                       //是否显示表格搜索，此搜索是客户端搜索，不会进服务端
        strictSearch: true,
        showColumns: false,                  //是否显示所有的列
        showRefresh: false,                  //是否显示刷新按钮
        minimumCountColumns: 2,             //最少允许的列数
        clickToSelect: false,                //是否启用点击选中行
        singleSelect: false,
        height: 700,                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
        uniqueId: "id",                     //每一行的唯一标识，一般为主键列
        showToggle: false,                    //是否显示详细视图和列表视图的切换按钮
        cardView: false,                    //是否显示详细视图
        detailView: false,                   //是否显示父子表

        columns: [
            {
                checkbox: true,
                formatter: function (value, row, index) {  // value:field的值,row:行数据，用row.createdDtm找发起时间字段,index:行下标
                    if(row.proNum1>0){
                        return {
                            disabled : true,//设置是否可用
                        };
                    }
                },
                visible: false
            },
            {
                field: 'id',
                title: 'id',
                visible: false
            },
            {
                field: 'code',
                title: 'code',
                visible: false
            },
            {
                field: 'name',
                title: '发行人名称',
                formatter: function (value, row, index) {  // value:field的值,row:行数据，用row.createdDtm找发起时间字段,index:行下标
                    var companyName = null==value?'':value;
                    return '<div class="cell-content" title="' + companyName + '">' + value + '</div>';
                }
            },
            {
                field: 'provinceCode',
                title: '地域',
                formatter: function (value, row, index) {  // value:field的值,row:行数据，用row.createdDtm找发起时间字段,index:行下标
                    var provinceName = null==row.provinceName ? '' : row.provinceName;
                    var cityName = null==row.cityName ?' ' : row.cityName;
                    return provinceName + ' ' + cityName;
                }
            },
            {
                field: 'proNum1',
                title: '有效/到期项目',
                formatter: function (value, row, index) {  // value:field的值,row:行数据，用row.createdDtm找发起时间字段,index:行下标
                    var pronum1 = null==row.proNum1 ? 0 : row.proNum1;
                    var pronum2 = null==row.proNum2 ?' ' : row.proNum2;
                    return pronum1 + ' / ' + pronum2;
                }
            },
            {
                field: 'companyMatterList',
                title: '发行人关联事项',
                formatter: function (value, row, index) {  // value:field的值,row:行数据，用row.createdDtm找发起时间字段,index:行下标
                    var html = '';
                    var matterCount = 0;
                    if(null!=row.companyMatterList && row.companyMatterList.length>0){
                        matterCount += row.companyMatterList.length;
                    }
                    if(null!=row.customMatterList && row.customMatterList.length>0){
                        matterCount += row.customMatterList.length;
                    }
                    html = '<div>共 ' + matterCount + ' 个事项</div>';
                    var viewCount = 0;
                    for(var i=0;i<row.companyMatterList.length;i++){
                        if(viewCount==5){
                            html += '...';
                            break;
                        }
                        viewCount+=1;
                        var templateName = null==row.companyMatterList[i].matterTemplate?'':row.companyMatterList[i].matterTemplate.name;
                        html += '<div class="cell-content" title="' + templateName + '">&nbsp;' + viewCount + '.' + templateName + '</div>';
                    }
                    for(var i=0;i<row.customMatterList.length;i++){
                        if(viewCount==5){
                            html += '...';
                            break;
                        }
                        viewCount+=1;
                        var templateName = null==row.customMatterList[i].name?'':row.customMatterList[i].name;
                        html += '<div class="cell-content" title="' + templateName + '">&nbsp;' + viewCount + '.' + templateName + '</div>';
                    }
                    return html;
                }
            },
            {
                field: 'operation',
                title: '操作',
                formatter: function (value, row, index) {  // value:field的值,row:行数据，用row.createdDtm找发起时间字段,index:行下标
                    if($("#hasPermission").length>0){
                        var returnHTML = '';
                        if((null==row.companyMatterList || row.companyMatterList.length<=0) && (null==row.customMatterList || row.customMatterList.length<=0)){
                            returnHTML += '<a href="javascript:void(0)" class="linkmatter">关联事项</a>';
                        }else{
                            returnHTML += '<a href="javascript:void(0)" class="linkmatter">修改事项</a>';
                        }
                        if(row.proNum1==0 && row.proNum2==0){
                            returnHTML += '<br/><br/><a href="javascript:void(0)" class="deletelinkmatter" style="color:red;">删除</a>';
                        }
                        return returnHTML;
                    }else{
                        return '';
                    }

                },
                events: {
                    'click .linkmatter': function (e, value, row, index) {
                        $.ajax(contextPath + "/company/linkmatterInfo/0/" + row.name).success(function (data) {
                            console.log("success");
                            pageIndex = layer.open({
                                type: 1,
                                title: '编辑关联事项',
                                maxmin: false,
                                resize: false,
                                shade: 0.3,
                                area: ['95%', '98%'],
                                content: data
                            });
                        });
                    },
                    'click .deletelinkmatter': function (e, value, row, index) {
                        var parData = {};
                        parData.id = row.id;
                        parData.companyName = row.name;
                        layer.confirm(
                            '您确定要删除选中的发行人吗？删除后，该发行人今后不会再触发任何事项。',
                            {icon: 3, title: '提示'},
                            function (index) {
                                $.ajax({
                                    url: contextPath + "/company/updateStatus",
                                    type: "post",
                                    data: JSON.stringify(parData),
                                    dataType: "json",
                                    contentType : 'application/json;charset=utf-8', //设置请求头信息
                                    success: function (data) {
                                        if (data.code == 0) {
                                            $('#tabCompany').bootstrapTable('refresh');
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
                    }
                }
            },
        ],
        onClickRow: function (row, element) {
            $(".selected").removeClass("selected");
            //$(".row-selected").removeClass("row-selected");
            $(element).addClass("selected");
        },
        onDblClickRow: function (row, element) {
            var id = row.id;
            var companyName = null==row.name?'':row.name;
            $.ajax(contextPath + "/company/info/" + id + "/" + companyName).success(function (data) {
                console.log("success");
                layer.open({
                    type: 1,
                    title: '发行人详情',
                    maxmin: false,
                    resize: false,
                    shade: 0.3,
                    area: ['95%', '98%'],
                    scrollbar: false,
                    content: data
                });
            });
        }
    });

}