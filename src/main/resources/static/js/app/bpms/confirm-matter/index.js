//@ sourceURL=index.js
var limit=10;
var offset=1;
$(function () {
    //定义按钮行为
    // initTable();
    initArea();
    initTable();
    initButton();
    initDate();
    initBondDate();
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

    $('#txt_search_valueDate').datetimepicker({
        format: 'mm月dd日',
        language: 'zh-CN',
        weekStart: 1,
        todayBtn: 1,//今日按钮
        autoclose: 1,//选中之后自动隐藏日期选择框
        clearBtn: 0,//清除按钮
        todayHighlight: 1,
        startView: 3,
        minView: 2,
        forceParse: 0,
        pickerPosition: 'bottom-left'
    });

    $('#txt_search_payDay').datetimepicker({
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


function initButton() {
    $("#search").click(function(){
        offset=1;
        initTable();
    });
    $("#matterAll").change(function(){
        if($(this).prop("checked")){
            $("#table").find("input[name='matterId']").prop("checked",true);
        } else{
            $("#table").find("input[name='matterId']").prop("checked",false);
        }
    });
    $("#batchConfirm").click(function() {
        var matterIds = [];
        $("#table").find("input[name='matterId']:checked").each(function () {
            matterIds.push($(this).val());
          });
            if (matterIds.length < 1) {
                layer.alert('请选择事项', {icon: 1, time: 3000});
                return;
            }
            layer.confirm(
                '您确定需要批量确认选中的事项吗？确认后，事项将流转到项目负责人待办事项中！',
                {icon: 3, title: '提示'},
                function (index) {
                    $.ajax({
                        url: contextPath + "confirm-matter/batch-confirm/" + matterIds,
                        type: "post",
                        dataType: "json",
                        processData: false,
                        contentType: 'application/json;charset=utf-8', //设置请求头信息
                        success: function (data) {
                            refreshTable();
                            layer.alert(data.message, {icon: 1, time: 3000});
                        },
                        error: function (e) {
                            layer.alert(e, {icon: 1, time: 3000});
                        }
                    });
                    layer.close(index);
                }
            )
        });
    $("#batchCancel").click(function() {
        var matterIds = [];
        $("#table").find("input[name='matterId']:checked").each(function () {
            matterIds.push($(this).val());
        });
        if (matterIds.length < 1) {
            layer.alert('请选择事项', {icon: 1, time: 3000});
            return;
        }
        layer.confirm(
            '您确定需要批量作废选中的事项吗？作废后，事项将自动作废消失，不会流转到项目负责人待办事项中,且不会重新触发当前事项！',
            {icon: 3, title: '提示'},
            function (index) {
                $.ajax({
                    url: contextPath + "confirm-matter/batch-cancel/" + matterIds,
                    type: "post",
                    dataType: "json",
                    processData: false,
                    contentType: 'application/json;charset=utf-8', //设置请求头信息
                    success: function (data) {
                        refreshTable();
                        layer.alert(data.data, {icon: 1, time: 3000});
                    },
                    error: function (e) {
                        layer.alert(e, {icon: 1, time: 3000});
                    }
                });
                layer.close(index);
            }
        )
    });
    $("#batchDelete").click(function() {
        var matterIds = [];
        $("#table").find("input[name='matterId']:checked").each(function () {
            matterIds.push($(this).val());
        });
        if (matterIds.length < 1) {
            layer.alert('请选择事项', {icon: 1, time: 3000});
            return;
        }
        layer.confirm(
            '请确保已把需要作废并重新触发的当天模板/自定义事项修改完后，' +
            '才可在“确认事项”版块，选中需要作废并重新触发的当天事项，' +
            '点击操作“作废并重新触发当天事项”按钮，操作“确定”后，则已触发的当天事项将被作废，' +
            '同时会重新产生新的当天可触发的事项，系统左下角点击“当天可触发”，' +
            '则会重新触发已作废的当天事项。',
            {icon: 3, title: '提示'},
            function (index) {
                $.ajax({
                    url: contextPath + "confirm-matter/batch-delete/" + matterIds,
                    type: "post",
                    dataType: "json",
                    processData: false,
                    contentType: 'application/json;charset=utf-8', //设置请求头信息
                    success: function (data) {
                        refreshTable();
                        layer.alert(data.data, {icon: 1, time: 3000});
                    },
                    error: function (e) {
                        layer.alert(e, {icon: 1, time: 3000});
                    }
                });
                layer.close(index);
            }
        )
    });
}

function initDate() {
    $('.form_date').datetimepicker({
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
    }).on('hide', function (ev) {
        $("#btn_query").focus();
    });

    $('.form_date_time').datetimepicker({
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
//table查询参数
function queryParams() {  //配置参数
    var type="";
    if($("#drop_search_listedPlace").val()!=""||
        $("#drop_search_type").val()!="" ||
        $("#txt_search_valueDate_begin").val()!="" ||
        $("#txt_search_valueDate_end").val()!="" ||
        $("#txt_search_payDay_begin").val()!="" ||
        $("#txt_search_payDay_end").val()!="" ||
        $("#txt_search_timeLimit_begin").val()!="" ||
        $("#txt_search_timeLimit_end").val()!="" ||
        $("#drop_search_user").val()!="" ||
        $("#txt_search_guaranteeCompany").val()!="" ||
        $("#drop_search_bondManagerType").val()!="" ||
        $("#txt_search_bondManager").val()!="" ||
        $("#search_matter_type").val()=="1"
    ){
        type="1";
    }
    if($("#search_matter_type").val()=="0"  ){
        type="0";
    }
    return {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
        pageSize: limit,
        offset: offset,
        common: $("#search_common").val(),
        type: type,
        completeDate: $("#search_completeDate").val(),
        listedPlace: $("#drop_search_listedPlace").val(),
        bondType: $("#drop_search_type").val(),
        province: $("#drop_search_province").val(),
        city: $("#drop_search_city").val(),
        valueDateBegin: $("#txt_search_valueDate_begin").val(),
        valueDateEnd: $("#txt_search_valueDate_end").val(),
        payDayBegin: $("#txt_search_payDay_begin").val().replace("年",""),
        payDayEnd: $("#txt_search_payDay_end").val().replace("年",""),
        timeLimitBegin: Number($("#txt_search_timeLimit_begin").val()),
        timeLimitEnd: Number($("#txt_search_timeLimit_end").val()),
        user: $("#drop_search_user").val(),
        guaranteeCompany: $("#txt_search_guaranteeCompany").val(),
        bondManagerType: $("#drop_search_bondManagerType").val(),
        bondManager: $("#txt_search_bondManager").val()
    };
}
function initTable() {
    var param = queryCommonParams();
    $.ajax({
        url: contextPath + "/confirm-matter/findConfirmMatters",
        type:"post",
        data: JSON.stringify(param),
        dataType: "json",
        processData:false,
        contentType : 'application/json;charset=utf-8', //设置请求头信息
        success:function(data){
            $("#table").html('');
            var vHTML = '';
            for(var i=0;i<data.rows.length;i++){
                var vIndex = i+1;
                var matters = new Array();
                matters = data.rows[i];
                var html = '';
                if(null != matters && matters.length>0){
                    html +=createConpanyTitle(matters[0].processInfo.companyName);
                    for(var j=0;j<matters.length;j++){
                        var matter=matters[j];
                        var processInfo = matter.processInfo;
                        var preMatter;
                        if(j==0){
                            html += createTabSecond(processInfo);
                        }
                        if(j>0){
                            preMatter=matters[j-1];
                            if(processInfo.bondCode!=preMatter.processInfo.bondCode){
                                html += endTabSecond();
                                html += createTabSecond(processInfo);
                            }
                        }
                        html +=createTr(processInfo,matter);
                    }
                    j=null;
                    html += endTabSecond();
                    html += '</div>';
                }

                vHTML +=html;

            }
            $("#table").append(vHTML);
            initPagination(data.total,offset,limit);
        },
        error:function(e){
            layer.alert("查询失败", {icon: 1, time: 3000});
        }
    });
}

function updatePagination(currentPageSize,currentpageNumber){
    limit = currentPageSize;
    offset = currentpageNumber;
    initTable();
}
function refreshTable(){
    initTable();
}
function createTr(processInfo,matter) {
    var html = ' <tr>';
    html +=' <td style="width: 22%">';
    html +='<div class="td-div-checkbox"><input class="matter-checkbox" name="matterId" type="checkbox" value="'+matter.id+'"></input></div>';
    html +='<div class="td-div-icon" data-toggle="tooltip" title="'+processInfo.shortname+'" style="background-color:'+processInfo.color+';border-color:'+processInfo.color+'">'+processInfo.shortname+'</div>';
    html +='<div class="td-div-content">';
    html +='<div class="content-name" data-toggle="tooltip" title="'+processInfo.name+'">'+processInfo.name+'</div>';
    html +='<div class="content-text" data-toggle="tooltip" title="'+processInfo.description+'">'+processInfo.description+'</div>';
    html +=' </div></td><td style="width:22%">材料：';
    html +='<span class="fa fa-paperclip"></span>';
    html +=' <span>共';
    html +='<span>'+processInfo.processTemplateAttachmentList.length+'</span>个文件';
    html +=' </span>';
    html +=' </div><div>' ;
    var processAttachmentList = processInfo.processTemplateAttachmentList;
    for(var k=0;k<processAttachmentList.length;k++){
        var attachment = processAttachmentList[k];
        html += '<div class="index-file">';
        html += '<span class="file_title" title="'+attachment.name+'">'+attachment.name+'</span></div>';
    }
    k=null;

    html +=' <td style="width:18%">' ;
    html +=' <div class="td-div-row">'+timestampToDate(processInfo.remindDate)+'</div>';
    if(matter.orderIndex==0){
        html +=' <div class="td-div-row">'+timestampToDate(processInfo.completeDate)+'</div>';
    }
    html +=' </td>' ;
    html +=' <td style="width:12%"><div class="td-div-row">'+convertStatus(processInfo.status)+'</div></td>' ;
    html +=' <td style="width:10%">' ;
    html +=' <div class="td-div-row">'+convertOperate(processInfo.completeDate,processInfo.processId,processInfo.status,matter.id,matter.orderIndex)+'</div></td>' ;
    return html;
}
function convertOperate(completeDate,pId,status,id,orderIndex) {
    var result = '';
    var date = new Date().Format("yyyy-MM-dd");
    var arr = date.split("-");
    var dateTime = new Date(arr[0], arr[1], arr[2]);
    var dateTimes = dateTime.getTime();

    var arrs = new Date(completeDate).Format("yyyy-MM-dd").split("-");
    var completeTime = new Date(arrs[0], arrs[1], arrs[2]);
    var completeTimes = completeTime.getTime();

    switch (status) {
        case 1:
            if(orderIndex==1){
                if(completeTimes<dateTimes){
                    result = '<a onclick="cancelRightLineMatter('+pId+')">作废事项</a>';
                }else{
                    result = '<a onclick="confirmRightLineMatter('+id+')">确认事项</a>';
                }
            }else{
                if(completeTimes<dateTimes){
                    result = '<a onclick="cancelMatter('+pId+')">作废事项</a>';
                }else{
                    result = '<a onclick="confirmMatter('+id+')">确认事项</a>';
                }

            }
            break;
    }
    return result;
}
function convertStatus(status) {
    var result = '';
    switch (status) {
        case 1:
            result = '等待确认';
            break;
        case 2:
            result = '等待项目负责人处理';
            break;
        case 4:
            result = '等待审核';
            break;
        case 8:
            result = '等待项目负责人重新处理';
            break;
        case 16:
            result = '等待重新审核';
            break;
        case 32:
            result = '正常完成';
            break;
        case 64:
            result = '流程取消';
            break;
    }
    return result;
}
function createConpanyTitle(companyName) {
    var  html ='<p></p>';
    html += '<div class="tab-title-second-color">';
    html +='<span class="glyphicon glyphicon-minus" style="cursor:pointer;padding-left: 5px;" onclick="minusPlus(this)"/>';
    html +='<a class="company-font" onclick="companyInfo(this)">'+companyName+'</a>';
    html +='<span class="span_space">发行人对接人：</span>';
    html +='<a class="span_space" onclick="linkmanInfo(this)">查看</a>';
    html +='</div>';
    html +='<div>';
    html +='<p></p>'
    return html;
}

function minusPlus(ob) {
    if($(ob).hasClass("glyphicon glyphicon-minus")){
        $(ob).removeClass();
        $(ob).parent().next().hide();
        $(ob).addClass("glyphicon glyphicon-plus");
    }else if($(ob).hasClass("glyphicon glyphicon-plus")){
        $(ob).removeClass();
        $(ob).parent().next().show();
        $(ob).addClass("glyphicon glyphicon-minus");
    }
}

function minusPlusAll(ob) {
    if($(ob).hasClass("glyphicon glyphicon-minus")){
        $(ob).removeClass();
        $(".tab-title-second-color .glyphicon-minus").each(function(index,item){
            $(item).removeClass();
            $(item).parent().next().hide();
            $(item).addClass("glyphicon glyphicon-plus");
        });
        $(ob).addClass("glyphicon glyphicon-plus");
    }else if($(ob).hasClass("glyphicon glyphicon-plus")){
        $(ob).removeClass();
        $(".tab-title-second-color .glyphicon-plus").each(function(index,item){
            $(item).removeClass();
            $(item).parent().next().show();
            $(item).addClass("glyphicon glyphicon-minus");
        });
        $(ob).addClass("glyphicon glyphicon-minus");
    }
}
function endTabSecond(processInfo) {
    var html = ' </table>';
    html +='</div>';
    html +='<p></p>'
    return html;
}
function createTabSecond(processInfo) {
    var html = '<div class="tab-second">';
    html +='<div class="tab-title-second-color">';
    if(processInfo.type==0){
        html +='<a class="company-font" onclick="companyInfo(this)">'+processInfo.companyName+'</a>';
        html +='<span class="company-font">（发行人事项）</span>';
    }
    if(processInfo.type==1){
        html +='<a class="company-font" onclick="bondInfo1(this)">'+processInfo.bondShortname+'</a>';
        html +='<a class="span_space company-font" onclick="bondInfo(this)">'+processInfo.bondCode+'</a>';
        html +='<span class="span_space">项目负责人:</span>';
        html +='<a class="span_space" onclick="managerInfo(this)">查看</a>';
    }
    html +='</div>';
    html +=' <table class="table table-bordered table-striped">';
    return html;
}


function  cancelProcess(value) {
    $.ajax({
        url: contextPath + "/confirm-matter/cancel-process/"+value,
        type:"post",
        processData:false,
        contentType :false,
        success:function(data){
            layer.alert(data.message, {icon: 1, time: 3000});
            $('#tab').bootstrapTable('refresh');
        },
        error:function(e){
            layer.alert('处理失败', {icon: 1, time: 3000});
        }
    });
}


function minusPlus(ob) {
    if($(ob).hasClass("glyphicon glyphicon-minus")){
        $(ob).removeClass();
        $(ob).parent().next().hide();
        $(ob).addClass("glyphicon glyphicon-plus");
    }else if($(ob).hasClass("glyphicon glyphicon-plus")){
        $(ob).removeClass();
        $(ob).parent().next().show();
        $(ob).addClass("glyphicon glyphicon-minus");
    }
}

function minusPlusAll(ob) {
    if($(ob).hasClass("glyphicon glyphicon-minus")){
        $(ob).removeClass();
        $(".tab-title-second-color .glyphicon-minus").each(function(index,item){
            $(item).removeClass();
            $(item).parent().next().hide();
            $(item).addClass("glyphicon glyphicon-plus");
        });
        $(ob).addClass("glyphicon glyphicon-plus");
    }else if($(ob).hasClass("glyphicon glyphicon-plus")){
        $(ob).removeClass();
        $(".tab-title-second-color .glyphicon-plus").each(function(index,item){
            $(item).removeClass();
            $(item).parent().next().show();
            $(item).addClass("glyphicon glyphicon-minus");
        });
        $(ob).addClass("glyphicon glyphicon-minus");
    }
}
