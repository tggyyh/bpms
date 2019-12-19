/**
 * Created by Dealing076 on 2018/3/30.
 */
//@ sourceURL=bondlink.js
var aryChecked = [];
var isEdit = true;
$(function () {

    if($("#spanLinkMan").text()<=0){
        isEdit = false;
        $("#btn_bond_save").attr("disabled", isEdit);
    }

    if($("#hidLinkNames").val()!=""){
        aryChecked = $("#hidLinkNames").val().split(",");
    }

    $('.selectpicker').selectpicker({
        noneSelectedText: '请选择(可多选)'
    });

    initArea();

    initBondDate();

    //初始化Buttons
    initBondLinkButtons();

    initBondLinkTable();
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

//初始日期
function initBondDate() {

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

    $('.c-disable').datetimepicker({
        format: 'yyyy年',
        language: 'zh-CN',
        weekStart: 1,
        todayBtn: 1,//今日按钮
        autoclose: 1,//选中之后自动隐藏日期选择框
        clearBtn: 0,//清除按钮
        todayHighlight: 1,
        startView: 4,
        minView: 4,
        forceParse: 0,
        pickerPosition: 'bottom-left'
    });
}


function refreshBondLinkData(){
    $('#tabBondLink').bootstrapTable('refresh');
}

function initBondLinkButtons() {
    $(".advancedSearch").addClass("hide");

    $("button[name='btn_query']").click(function(){
        $('#tabBondLink').bootstrapTable('refresh');
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
            $('#tabBondLink').bootstrapTable('refresh');
        }
    });

    $(".val-decimal").keyup(function(){
        this.value = replaceDecimal(this.value);
    });

    $("#btn_bond_update").click(function(){
        isEdit = false;
        $(".bootstrap-table input:checkbox").attr("disabled", isEdit);
        $("#btn_bond_save").attr("disabled", isEdit);
    });

    $("#btn_bond_save").click(function(){
        if(null!=aryChecked && aryChecked.length>0){
            var bondMatterList = [];
            for(var i=0;i<aryChecked.length;i++){
                var bondMatter = {};
                bondMatter.bondCode = aryChecked[i];
                bondMatter.templateId = $("#hidBondTemplateId").val();
                bondMatter.status = 0;
                bondMatterList.push(bondMatter);
            }

            $.ajax({
                url: contextPath + "/bond/bondLinkSave",
                type:"post",
                data: JSON.stringify(bondMatterList),//将对象序列化成JSON字符串
                dataType:"json",
                contentType : 'application/json;charset=utf-8', //设置请求头信息
                success:function(data){
                    if(data.code==0){
                        layer.alert('关联成功', {icon: 1, time: 3000});
                        layer.close(pageIndex);
                    }else{
                        layer.alert('关联失败', {icon: 1, time: 3000});
                    }
                },
                error:function(e){
                    layer.alert('关联失败', {icon: 1, time: 3000});
                }
            });
        }else{
            var paraJson={};
            paraJson.templateId = $("#hidBondTemplateId").val();
            $.ajax({
                url: contextPath + "/bond/deleteByTemplateId",
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
                    }
                },
                error:function(e){
                    layer.alert('关联失败', {icon: 1, time: 3000});
                }
            });
        }
    })
}

//table查询参数
function bondLinkQueryParams(params) {  //配置参数
    return {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
        pageSize: params.limit,
        offset: params.offset,
        templateId: $("#hidBondTemplateId").val(),
        linkman: $("#drop_search_linkman").val(),
        bondname: $("#txt_search_name").val(),
        provinceCode: $("#drop_search_province").val(),
        cityCode: $("#drop_search_city").val(),
        listedPlace: $("#drop_search_listedPlace").selectpicker('val'),
        type: $("#drop_search_type").selectpicker('val'),
        userId: $("#drop_search_user").selectpicker('val'),
        timeLimitBegin: $("#txt_search_timeLimitBegin").val(),
        timeLimitEnd: $("#txt_search_timeLimitEnd").val(),
        valueDateBegin: $("#txt_search_valueDateBegin").val(),
        valueDateEnd: $("#txt_search_valueDateEnd").val(),
        payDayBegin: $("#txt_search_payDayBegin").val().replace("年",""),
        payDayEnd: $("#txt_search_payDayEnd").val().replace("年",""),
        listedDateBegin: $("#txt_search_listedDateBegin").val(),
        listedDateEnd: $("#txt_search_listedDateEnd").val(),
        currentBalanceBegin: $("#txt_search_currentBalanceBegin").val(),
        currentBalanceEnd: $("#txt_search_currentBalanceEnd").val(),
        crossMarket: $("#drop_search_crossMarket").selectpicker('val'),
        guaranteeCompany: $("#txt_search_guaranteeCompany").val(),
        managerType: $("#drop_search_managerType").val(),
        bondManager: $("#txt_search_bondManager").val(),
        underwriterName: $("#txt_search_underwriterName").val()
    };
}

//初始化Table
function initBondLinkTable() {

    $('#tabBondLink').bootstrapTable('destroy');

    $('#tabBondLink').bootstrapTable({
        url: contextPath + '/mattertemplate/findBondLinkMatterAll',        //请求后台的URL（*）
        method: 'post',                         //请求方式（*）
        toolbar: '#toolbar',                //工具按钮用哪个容器
        striped: true,                      //是否显示行间隔色
        cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
        pagination: true,                   //是否显示分页（*）
        sortable: true,                     //是否启用排序
        sortOrder: "asc",                   //排序方式
        queryParams: bondLinkQueryParams,           //传递参数（*）
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
        detailView: false,                   //是否显示父子表

        columns: [
            {
                checkbox: true,
                formatter: function (value, row, index) {  // value:field的值,row:行数据，用row.createdDtm找发起时间字段,index:行下标

                    if(aryChecked.indexOf(row.code)>=0){
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
                field: 'code',
                title: 'code',
                visible: false
            },
            {
                field: 'name',
                title: '债券简称/代码',
                formatter: function (value, row, index) {  // value:field的值,row:行数据，用row.createdDtm找发起时间字段,index:行下标
                    var name = null==row.shortname ? '' : row.shortname;
                    var code = null==row.code ?' ' : row.code;
                    return name + '<br/>' + code;
                }
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
            },
            {
                field: 'provinceCode',
                title: '地域/发行人名称',
                formatter: function (value, row, index) {  // value:field的值,row:行数据，用row.createdDtm找发起时间字段,index:行下标
                    if(null==row.company){
                        return '';
                    }
                    var provinceName = null==row.company.provinceName ? '' : row.company.provinceName;
                    var cityName = null==row.company.cityName ?'' : row.company.cityName;
                    var companyName = null==row.companyName ? '' : row.companyName;
                    var viewHTML = '<div style="width:100%;" title="' + provinceName + ' ' + cityName + ' ' + companyName + '">';
                    viewHTML += '<div class="cell-content">'
                    viewHTML += provinceName + ' ' + cityName;
                    viewHTML += '</div>';
                    viewHTML += '<div class="cell-content">'
                    viewHTML += companyName;
                    viewHTML += '</div>';
                    viewHTML += '</div>';
                    return viewHTML;
                    //return provinceName + '  ' + cityName + '<br/>' + companyName;
                }
            },
            {
                field: 'listedPlace',
                title: '上市场所',
                formatter: function (value, row, index) {  // value:field的值,row:行数据，用row.createdDtm找发起时间字段,index:行下标
                    var sValue = null==value ? '' : value;
                    var viewHTML = '<div class="cell-content" title="' + sValue + '">';
                    viewHTML += sValue;
                    viewHTML += '</div>';
                    return viewHTML;
                }
            },
            {
                field: 'bondManagerList',
                title: '项目负责人',
                formatter: function (value, row, index) {  // value:field的值,row:行数据，用row.createdDtm找发起时间字段,index:行下标
                    var html = '';
                    var point = '';
                    var viewManager = new Array();
                    var titleManager = new Array();
                    if(null!=row.bondManagerList && row.bondManagerList.length>0){
                        for(var i=0;i<row.bondManagerList.length;i++){
                            if(null!=row.bondManagerList[i].sysUser && null!=row.bondManagerList[i].sysUser.name){
                                if(viewManager.length<3){
                                    viewManager.push(row.bondManagerList[i].sysUser.name);
                                }
                                titleManager.push(row.bondManagerList[i].sysUser.name);
                            }
                        }
                        if(row.bondManagerList.length>3){
                            point = '...';
                        }
                        html = '<div class="cell-content" title="' + titleManager.join("、") + '">' + (viewManager.length>0?(viewManager.join("、")+point):'') + '</div>';
                    }
                    return html;
                }
            },
        ],
        onLoadSuccess: function (data) {
            if(null!=data){
                $("#spanLinkMan").html(data.linkCount);
                $("spanUnLinkMan").html(data.unLinkCount);
            }
            $(".bootstrap-table input:checkbox").attr("disabled", isEdit);
        },
        onCheck:function(row){
            aryChecked.push(row.code);
        },
        onUncheck:function(row){
            var aryIndex = aryChecked.indexOf(row.code);
            if(aryIndex>=0){
                aryChecked.splice(aryIndex,1);
            }
        },
        onCheckAll:function(rows){
            for(var i=0;i<rows.length;i++){
                if(aryChecked.indexOf(rows[i].code)<0){
                    aryChecked.push(rows[i].code);
                }
            }
        },
        onUncheckAll:function(rows){
            for(var i=0;i<rows.length;i++){
                var aryIndex = aryChecked.indexOf(rows[i].code);
                if(aryIndex>=0){
                    aryChecked.splice(aryIndex,1);
                }
            }

        },
    });

}