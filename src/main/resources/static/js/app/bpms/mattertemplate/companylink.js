/**
 * Created by Dealing076 on 2018/3/29.
 */
//@ sourceURL=companylink.js
var aryChecked = [];
var isEdit = true;
$(function () {

    if($("#spanLinkMan").text()<=0){
        isEdit = false;
        $("#btn_company_save").attr("disabled", isEdit);
    }

    $('.selectpicker').selectpicker({
        noneSelectedText: '请选择(可多选)'
    });

    if($("#hidLinkNames").val()!=""){
        aryChecked = $("#hidLinkNames").val().split(",");
    }

    initCompanyLinkDate();

    //初始化Buttons
    initCompanyLinkButtons();

    initCompanyLinkTable();

    initArea();


});

//初始日期
function initCompanyLinkDate() {

    $('.dpicker').datetimepicker({
        format: 'yyyy-mm-dd',
        language: 'zh-CN',
        weekStart: 1,
        todayBtn: 1,//今日按钮
        autoclose: 1,//选中之后自动隐藏日期选择框
        clearBtn: 0,//清除按钮
        todayHighlight: 1,
        startView: 2,
        minView: 2,
        forceParse: 0,
        pickerPosition: 'bottom-left'
    });
}

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

function refreshCompanyLinkData(){
    $('#tabCompanyLink').bootstrapTable('refresh');
}

function initCompanyLinkButtons() {

    $(".advancedSearch").addClass("hide");

    $("button[name='btn_query']").click(function(){
        $('#tabCompanyLink').bootstrapTable('refresh');
    });

    $("input[name='cbxAdvanced']").click(function(){
        if($(this).prop("checked")){
            $("input[name='cbxAdvanced']").prop("checked","checked");
            $(".advancedSearch").removeClass("hide");
            $(".commonSearch").addClass("hide");
        }else{
            $("input[name='cbxAdvanced']").prop("checked","");
            $(".advancedSearch").addClass("hide");
            $(".commonSearch").removeClass("hide");

            $(".advancedSearch").find("select,input").each(function(index, item){
                $(item).val("");
                if($(item).hasClass("selectpicker")){
                    $(item).selectpicker("val",[]);
                }
            });
            $('#tabCompanyLink').bootstrapTable('refresh');
        }
    });

    $(".val-decimal").keyup(function(){
        this.value = replaceDecimal(this.value);
    });

    $("#btn_company_update").click(function(){
        isEdit = false;
        $(".ibox-content input:checkbox").attr("disabled", isEdit);
        $("#btn_company_save").attr("disabled", isEdit);
    });

    $("#btn_company_save").click(function(){
        if(null!=aryChecked && aryChecked.length>0){
            var companyMatterList = [];
            for(var i=0;i<aryChecked.length;i++){
                var companyMatter = {};
                companyMatter.companyName = aryChecked[i];
                companyMatter.templateId = $("#hidCompanyTemplateId").val();
                companyMatter.status = 0;
                companyMatterList.push(companyMatter);
            }

            $.ajax({
                url: contextPath + "/company/companyLinkSave",
                type:"post",
                data: JSON.stringify(companyMatterList),//将对象序列化成JSON字符串
                dataType:"json",
                contentType : 'application/json;charset=utf-8', //设置请求头信息
                success:function(data){
                    if(data.code==0){
                        layer.alert('关联成功', {icon: 1, time: 3000});
                        layer.close(pageIndex);
                    }else{
                        layer.alert('关联失败', {icon: 1, time: 3000});
                        //refreshCompanyLinkData();
                    }
                },
                error:function(e){
                    layer.alert('关联失败', {icon: 1, time: 3000});
                    //refreshCompanyLinkData();
                }
            });
        }else{
            var paraJson = {};
            paraJson.templateId = $("#hidCompanyTemplateId").val();
            $.ajax({
                url: contextPath + "/company/deleteByTemplateId",
                type:"post",
                data: JSON.stringify(paraJson),//将对象序列化成JSON字符串
                dataType:"json",
                contentType : 'application/json;charset=utf-8', //设置请求头信息
                success:function(data){
                    if(data.code==0){
                        layer.alert('关联成功', {icon: 1, time: 3000});
                        layer.close(pageIndex);
                    }else{
                        layer.alert('关联失败', {icon: 1, time: 3000});
                        //refreshCompanyLinkData();
                    }
                },
                error:function(e){
                    layer.alert('关联失败', {icon: 1, time: 3000});
                    //refreshCompanyLinkData();
                }
            });
        }


    })
}

//table查询参数
function companyLinkQueryParams(params) {  //配置参数
    return {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
        pageSize: params.limit,
        offset: params.offset,
        templateId: $("#hidCompanyTemplateId").val(),
        linkman: $("#drop_search_linkman").val(),
        companyname: $("#txt_search_name").val(),
        province: $("#drop_search_province").val(),
        city: $("#drop_search_city").val(),
        sValueDate: $("#txt_search_valueDateBegin").val(),
        eValueDate: $("#txt_search_valueDateEnd").val(),
        sListedDate: $("#txt_search_listedDateBegin").val(),
        eListedDate: $("#txt_search_listedDateEnd").val(),
        sCurrentBalance: $("#txt_search_currentBalanceBegin").val(),
        eCurrentBalance: $("#txt_search_currentBalanceEnd").val(),
        bondType: $("#drop_search_type").selectpicker('val'),
        crossMarket: $("#drop_search_crossMarket").selectpicker('val'),
        managerType: $("#drop_search_managerType").val(),
        bondManager: $("#txt_search_bondManager").val(),
        underwriterName: $("#txt_search_underwriterName").val()

    };
}

//初始化Table
function initCompanyLinkTable() {

    $('#tabCompanyLink').bootstrapTable('destroy');

    $('#tabCompanyLink').bootstrapTable({
        url: contextPath + '/mattertemplate/findCompanyLinkMatterAll',        //请求后台的URL（*）
        method: 'post',                         //请求方式（*）
        toolbar: '#toolbar',                //工具按钮用哪个容器
        striped: true,                      //是否显示行间隔色
        cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
        pagination: true,                   //是否显示分页（*）
        sortable: true,                     //是否启用排序
        sortOrder: "asc",                   //排序方式
        queryParams: companyLinkQueryParams,           //传递参数（*）
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
        height: 500,                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
        uniqueId: "id",                     //每一行的唯一标识，一般为主键列
        showToggle: false,                    //是否显示详细视图和列表视图的切换按钮
        cardView: false,                    //是否显示详细视图
        detailView: true,                   //是否显示父子表

        columns: [
            {
                checkbox: true,
                formatter: function (value, row, index) {  // value:field的值,row:行数据，用row.createdDtm找发起时间字段,index:行下标
                    //if(null!=row.matterId){
                    if(aryChecked.indexOf(row.name)>=0){
                        return {
                            checked : true,
                            disabled : isEdit
                        };
                    }else{
                        return {
                            disabled: isEdit
                        }
                    }
                }
            },
            {
                field: 'id',
                title: 'id',
                visible: false
            },
            {
                field: 'name',
                title: '发行人名称',
            },
            // {
            //     field: 'valueDate',
            //     title: '起息日',
            //     formatter: function (value, row, index) {  // value:field的值,row:行数据，用row.createdDtm找发起时间字段,index:行下标
            //         if(null!=value){
            //             value = timestampToDate(value);
            //         }
            //         return value;
            //     },
            // },
            // {
            //     field: 'listedDate',
            //     title: '上市日',
            //     formatter: function (value, row, index) {  // value:field的值,row:行数据，用row.createdDtm找发起时间字段,index:行下标
            //         if(null!=value){
            //             value = timestampToDate(value);
            //         }
            //         return value;
            //     },
            // },
            // {
            //     field: 'currentBalance',
            //     title: '当前余额',
            // },
            // {
            //     field: 'type',
            //     title: '债券类型',
            // },
            // {
            //     field: 'crossMarket',
            //     title: '是否跨市场',
            // },
            // {
            //     field: 'bondManager',
            //     title: '受托管理人',
            // },
            // {
            //     field: 'underwriterName',
            //     title: '主承销商',
            //     // formatter: function (value, row, index) {  // value:field的值,row:行数据，用row.createdDtm找发起时间字段,index:行下标
            //     //     return '<div id="showitem-' + index + '" class="tmp-link">展开</div>';
            //     // },
            //     // events: {
            //     //     'click .tmp-link': function (e, value, row, index) {
            //     //         if($(this).text()=="展开"){
            //     //             for(var i=0;i<row.bondList.length;i++){
            //     //                 $("#tabCompanyLink").bootstrapTable('insertRow', {index: index+1, row: row.bondList[i]});
            //     //             }
            //     //         }
            //     //     }
            //     // }
            // }
        ],
        onExpandRow: function (index, row, $detail) {
            initSubTable(index, row, $detail);
        },
        onLoadSuccess: function (data) {
            if(null!=data){
                $("#spanLinkMan").html(data.linkCount);
                $("spanUnLinkMan").html(data.unLinkCount);
            }
            $(".ibox-content input:checkbox").attr("disabled", isEdit);
        },
        onCheck:function(row){
            aryChecked.push(row.name);
        },
        onUncheck:function(row){
            var aryIndex = aryChecked.indexOf(row.name);
            if(aryIndex>=0){
                aryChecked.splice(aryIndex,1);
            }
        },
        onCheckAll:function(rows){
            for(var i=0;i<rows.length;i++){
                if(aryChecked.indexOf(rows[i].name)<0){
                    aryChecked.push(rows[i].name);
                }
            }
        },
        onUncheckAll:function(rows){
            for(var i=0;i<rows.length;i++){
                var aryIndex = aryChecked.indexOf(rows[i].name);
                if(aryIndex>=0){
                    aryChecked.splice(aryIndex,1);
                }
            }

        },
    });

}

function initSubTable(index, row, $detail){
    var cur_table = $detail.html('<table></table>').find('table');
    $(cur_table).bootstrapTable({
        //url: contextPath + '/mattertemplate/findCompanyLinkMatterAll',        //请求后台的URL（*）
        method: 'post',                         //请求方式（*）
        //toolbar: '',                //工具按钮用哪个容器
        striped: true,                      //是否显示行间隔色
        cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
        showHeader: true,
        pagination: false,                   //是否显示分页（*）
        sortable: true,                     //是否启用排序
        sortOrder: "asc",                   //排序方式
        //queryParams: companyLinkQueryParams,           //传递参数（*）
        //queryParamsType: "limit",           //参数格式,发送标准的RESTFul类型的参数请求
        sidePagination: "client",           //分页方式：client客户端分页，server服务端分页（*）
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
        //height: 500,                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
        uniqueId: "id",                     //每一行的唯一标识，一般为主键列
        showToggle: false,                    //是否显示详细视图和列表视图的切换按钮
        cardView: false,                    //是否显示详细视图
        detailView: false,                   //是否显示父子表

        columns: [
            {
                field: 'id',
                title: 'id',
                visible: false
            },
            {
                field: 'name',
                title: '债券简称',
                formatter: function (value, row, index) {  // value:field的值,row:行数据，用row.createdDtm找发起时间字段,index:行下标
                    var shortname = null==row.shortname?'':row.shortname;
                    return shortname;
                },
            },
            {
                field: 'valueDate',
                title: '起息日',
                formatter: function (value, row, index) {  // value:field的值,row:行数据，用row.createdDtm找发起时间字段,index:行下标
                    if(null!=value){
                        value = timestampToDate(value);
                    }
                    return value;
                },
            },
            {
                field: 'listedDate',
                title: '上市日',
                formatter: function (value, row, index) {  // value:field的值,row:行数据，用row.createdDtm找发起时间字段,index:行下标
                    if(null!=value){
                        value = timestampToDate(value);
                    }
                    return value;
                },
            },
            {
                field: 'currentBalance',
                title: '当前余额',
            },
            {
                field: 'type',
                title: '债券类型',
            },
            {
                field: 'crossMarket',
                title: '是否跨市场',
            },
            {
                field: 'bondManager',
                title: '受托管理人',
            },
            {
                field: 'underwriterName',
                title: '主承销商',
            }
        ]

    });

    $(cur_table).bootstrapTable("load", row.bondList);
}