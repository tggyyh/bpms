/**
 * Created by Dealing076 on 2018/4/19.
 */
$(function () {

    $('.selectpicker').selectpicker({
        noneSelectedText: '请选择(可多选)'
    });

    initArea();

    initDatepicker();

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

//初始日期
function initDatepicker() {

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

function refreshMatterData(){
    $('#tabMatter').bootstrapTable('refresh');
}

function initButton() {

    $(".advancedSearch").addClass("hide");

    //查询
    $("button[name='btn_query']").click(function(){
        $('#tabMatter').bootstrapTable('refresh');
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
            $('#tabMatter').bootstrapTable('refresh');
        }
    });

}

//table查询参数
function queryParams(params) {  //配置参数
    return {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
        pageSize: params.limit,
        offset: params.offset,
        tempname: $("#txt_search_name").val(),
        stime: $("#txt_search_stime").val(),
        etime: $("#txt_search_etime").val(),
        matterType: $("#drop_search_matterType").val(),
        completeDate: $("#txt_search_completeDate").val(),
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
        guaranteeCompany: $("#txt_search_guaranteeCompany").val(),
        managerType: $("#drop_search_managerType").val(),
        bondManager: $("#txt_search_bondManager").val()

    };
}

//初始化Table
function initTable() {

    $('#tabMatter').bootstrapTable('destroy');

    $('#tabMatter').bootstrapTable({
        url: contextPath + '/mattercalendar/findCalendarData',        //请求后台的URL（*）
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
                field: 'type',
                title: 'type',
                visible: false
            },
            {
                field: 'companyId',
                title: 'companyId',
                visible: false
            },
            {
                field: 'bondId',
                title: 'bondId',
                visible: false
            },
            {
                field: 'tempName',
                title: '事项名',
                formatter: function (value, row, index) {  // value:field的值,row:行数据，用row.createdDtm找发起时间字段,index:行下标
                    // var tempName = '<div class="col-sm-12">';
                    // tempName += '       <div class="col-sm-5">';
                    // tempName += '           <div class="col-sm-12 tmp-icon" data-toggle="tooltip" title="' + row.tempShortname + '" style="background-color:' + row.color + ';border-color:' + row.color + ';">'+ row.tempShortname + '</div>';
                    // tempName += '       </div>';
                    // tempName += '       <div class="col-sm-7">';
                    // tempName += '           <div class="col-sm-12 tmp-content" style="width:100%;">';
                    // tempName += '               <div class="content-name" data-toggle="tooltip" title="' + row.tempName + '">' + row.tempName + '</div>';
                    // tempName += '               <div class="content-text" data-toggle="tooltip" title="' + row.tempDescription + '">' + row.tempDescription + '</div>';
                    // tempName += '           </div>';
                    // tempName += '       </div>';
                    // tempName += '   </div>';
                    // return tempName;

                    var tempName = '<div>';
                    tempName += '<div class="tmp-icon" data-toggle="tooltip" title="' + row.tempShortname + '" style="background-color:' + row.color + ';border-color:' + row.color + ';">'+ row.tempShortname + '</div>';
                    tempName += '<div class="tmp-content" style="width:50%;">';
                    var nameHTML = row.tempName;
                    if(null!=row.rightLine && row.rightLine==1){
                        nameHTML = row.tempName + '--' + row.subName;
                    }
                    tempName += '<div class="content-name" data-toggle="tooltip" title="' + nameHTML + '">' + nameHTML + '</div>';
                    tempName += '<div class="content-text" data-toggle="tooltip" title="' + row.tempDescription + '">' + row.tempDescription + '</div>';
                    tempName += '</div>';
                    tempName += '</div>';
                    return tempName;
                }
            },
            {
                field: 'companyName',
                title: '发行人/项目名',
                formatter: function (value, row, index) {  // value:field的值,row:行数据，用row.createdDtm找发起时间字段,index:行下标
                    var compName = '';
                    if(row.type==0){
                        compName = null==row.companyName?'':row.companyName;
                    }else if(row.type==1){
                        var bondCode = null==row.bondCode?'':row.bondCode;
                        var bondName = null==row.bondShortname?'':row.bondShortname;
                        compName = bondName + '<br/>' + bondCode;
                    }

                    return '<a href="javascript:void(0)" class="linkinfo">' + compName + '</a>';
                },
                events: {
                    'click .linkinfo': function (e, value, row, index) {
                        if(row.type==0){
                            if(row.companyId==0){
                                layer.alert("该发行人信息已失效", {icon: 1, time: 3000});
                                return;
                            }else{
                                $.ajax(contextPath + "/company/info/" + row.companyId + "/" + row.companyName).success(function (data) {
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

                        }else if(row.type==1){
                            if(row.bondId==0) {
                                layer.alert("该项目息已失效", {icon: 1, time: 3000});
                                return;
                            }else{
                                $.ajax(contextPath + "/bond/info/" + row.bondId + "/" + row.bondCode + "/" + row.companyId).success(function (data) {
                                    console.log("success");
                                    layer.open({
                                        type: 1,
                                        title: '项目详情',
                                        maxmin: false,
                                        resize: false,
                                        shade: 0.3,
                                        area: ['95%', '98%'],
                                        scrollbar: false,
                                        content: data
                                    });
                                });
                            }

                        }
                    }
                }
            },
            {
                field: 'completeDate',
                title: '状态时间/需完成时间',
                formatter: function (value, row, index) {  // value:field的值,row:行数据，用row.createdDtm找发起时间字段,index:行下标
                    var remindDate = null==row.remindDate1 ? '' : timestampToDate(row.remindDate1);
                    var completeDate = null==row.completeDate1 ? '' : timestampToDate(row.completeDate1);
                    return '<div>' + remindDate + '</div><div style="color:#B20000;">' + completeDate + '</div>';
                }
            },
            {
                field: 'status',
                title: '状态',
                formatter: function (value, row, index) {  // value:field的值,row:行数据，用row.createdDtm找发起时间字段,index:行下标
                    if(value==0){
                        return '未触发';
                    }else if(value==1){
                        return '<div style="color:#CCCC66;">等待督导人员确认</div>';
                    }else if(value==2 || value==8){
                        return '<div style="color:#CC0000;">等待项目人员处理</div>';
                    }else if(value==4 || value==16){
                        return '<div style="color:#FF6600;">等待督导人员审核</div>';
                    }else if(value==32){
                        return '<div style="color:#00CC00;">事项完成</div>';
                    }else if(value==64){
                        return '流程取消';
                    }
                }
            }
        ],
        onClickRow: function (row, element) {
            $(".selected").removeClass("selected");
            //$(".row-selected").removeClass("row-selected");
            $(element).addClass("selected");
        }
    });

}