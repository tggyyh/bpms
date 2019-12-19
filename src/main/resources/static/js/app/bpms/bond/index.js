/**
 * Created by Dealing076 on 2018/3/16.
 */
var areaJson;
var pageIndex;
$(function () {

    $('.selectpicker').selectpicker({
        noneSelectedText: '请选择(可多选)'
    });

    initArea();

    initBondDate();

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

function refreshBondData(){
    $('#tabBond').bootstrapTable('refresh');
}

function initButton() {

    $(".advancedSearch").addClass("hide");

    //查询
    $("button[name='btn_query']").click(function(){
        $('#tabBond').bootstrapTable('refresh');
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
            $('#tabBond').bootstrapTable('refresh');
        }
    });

    $("#btnBondAdd").click(function(){
        $.ajax(contextPath + "/bond/add").success(function (data) {
            console.log("success");
            pageIndex = layer.open({
                type: 1,
                title: '新增项目',
                maxmin: false,
                resize: false,
                shade: 0.3,
                area: ['95%', '98%'],
                content: data
            });
        });
    });

    $("#btnDelete").click(function () {
        var orgRow = $("#tabBond").bootstrapTable('getSelections');
        if (orgRow.length <= 0) {
            layer.alert('请选择要删除的数据', {icon: 1, time: 3000});
            return;
        }
        var ids = new Array();
        for (var i = 0; i < orgRow.length; i++) {
            ids.push(orgRow[i].id);
        }
        layer.confirm(
            '您确定要删除选中的项目吗？删除后，该项目今后不会再触发任何事项',
            {icon: 3, title: '提示'},
            function (index) {
                $.ajax({
                    url: contextPath + "/bond/deleteSave",
                    type: "post",
                    data: JSON.stringify(ids),
                    dataType: "json",
                    contentType : 'application/json;charset=utf-8', //设置请求头信息
                    success: function (data) {
                        if (data.code == 0) {
                            layer.alert(data.message, {icon: 1, time: 3000});
                            $('#tabBond').bootstrapTable('refresh');
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

    $("#divDownExcel").click(function(){
        var form = document.createElement('form');
        form.setAttribute('method', 'post');
        form.setAttribute('action', contextPath + "/bond/downExcel");

        // 创建一个输入
        var bondname = document.createElement("input");
        var provinceCode = document.createElement("input");
        var cityCode = document.createElement("input");
        var listedPlace = document.createElement("input");
        var type = document.createElement("input");
        var userId = document.createElement("input");
        var timeLimitBegin = document.createElement("input");
        var timeLimitEnd = document.createElement("input");
        var valueDateBegin = document.createElement("input");
        var valueDateEnd = document.createElement("input");
        var payDayBegin = document.createElement("input");
        var payDayEnd = document.createElement("input");
        var guaranteeCompany = document.createElement("input");
        var managerType = document.createElement("input");
        var bondManager = document.createElement("input");
        // 设置相应参数
        bondname.type = "hidden";
        bondname.name = "bondname";
        bondname.value = $("#txt_search_name").val();

        provinceCode.type = "hidden";
        provinceCode.name = "provinceCode";
        provinceCode.value = $("#drop_search_province").val();

        cityCode.type = "hidden";
        cityCode.name = "cityCode";
        cityCode.value = $("#drop_search_city").val();

        listedPlace.type = "hidden";
        listedPlace.name = "listedPlace";
        listedPlace.value = $("#drop_search_listedPlace").selectpicker('val');

        type.type = "hidden";
        type.name = "type";
        type.value = $("#drop_search_type").selectpicker('val');

        userId.type = "hidden";
        userId.name = "userId";
        userId.value = $("#drop_search_user").selectpicker('val');

        timeLimitBegin.type = "hidden";
        timeLimitBegin.name = "timeLimitBegin";
        timeLimitBegin.value = $("#txt_search_timeLimitBegin").val();

        timeLimitEnd.type = "hidden";
        timeLimitEnd.name = "timeLimitEnd";
        timeLimitEnd.value = $("#txt_search_timeLimitEnd").val();

        valueDateBegin.type = "hidden";
        valueDateBegin.name = "valueDateBegin";
        valueDateBegin.value = $("#txt_search_valueDateBegin").val();

        valueDateEnd.type = "hidden";
        valueDateEnd.name = "valueDateEnd";
        valueDateEnd.value = $("#txt_search_valueDateEnd").val();

        payDayBegin.type = "hidden";
        payDayBegin.name = "payDayBegin";
        payDayBegin.value = $("#txt_search_payDayBegin").val().replace("年","");

        payDayEnd.type = "hidden";
        payDayEnd.name = "payDayEnd";
        payDayEnd.value = $("#txt_search_payDayEnd").val().replace("年","");

        guaranteeCompany.type = "hidden";
        guaranteeCompany.name = "guaranteeCompany";
        guaranteeCompany.value = $("#txt_search_guaranteeCompany").val();

        managerType.type = "hidden";
        managerType.name = "managerType";
        managerType.value = $("#drop_search_managerType").val();

        bondManager.type = "hidden";
        bondManager.name = "bondManager";
        bondManager.value = $("#txt_search_bondManager").val();

        // 将该输入框插入到 form 中
        form.appendChild(bondname);
        form.appendChild(provinceCode);
        form.appendChild(cityCode);
        form.appendChild(listedPlace);
        form.appendChild(type);
        form.appendChild(userId);
        form.appendChild(timeLimitBegin);
        form.appendChild(timeLimitEnd);
        form.appendChild(valueDateBegin);
        form.appendChild(valueDateEnd);
        form.appendChild(payDayBegin);
        form.appendChild(payDayEnd);
        form.appendChild(guaranteeCompany);
        form.appendChild(managerType);
        form.appendChild(bondManager);


        // add form to body and submit
        document.body.appendChild(form);
        form.submit();
    });

}

//table查询参数
function queryParams(params) {  //配置参数
    return {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
        pageSize: params.limit,
        offset: params.offset,
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
        guaranteeCompany: $("#txt_search_guaranteeCompany").val(),
        managerType: $("#drop_search_managerType").val(),
        bondManager: $("#txt_search_bondManager").val()
    };
}

//初始化Table
function initTable() {

    $('#tabBond').bootstrapTable('destroy');

    $('#tabBond').bootstrapTable({
        url: contextPath + '/bond/findAll',        //请求后台的URL（*）
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
        height: 'auto',                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
        uniqueId: "id",                     //每一行的唯一标识，一般为主键列
        showToggle: false,                    //是否显示详细视图和列表视图的切换按钮
        cardView: false,                    //是否显示详细视图
        detailView: false,                   //是否显示父子表

        columns: [
            {
                checkbox: true,
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
                title: '债券简称/代码',
                formatter: function (value, row, index) {  // value:field的值,row:行数据，用row.createdDtm找发起时间字段,index:行下标
                    var name = null==row.shortname ? '' : row.shortname;
                    var code = null==row.code ?' ' : row.code;
                    var viewHTML = '<div style="width:100%;" title="' + name + ' ' + code + '">';
                    if(row.status==1|| row.status==2){
                        viewHTML += '<div class="bond-overtime-icon"></div>';
                    }

                    viewHTML += '<div class="cell-content">' + name + '</div>';
                    viewHTML += '<div class="cell-content">' + code + '</div>';
                    viewHTML += '</div>';
                    return viewHTML;
                },
                cellStyle:function(value,row,index){
                    if (row.status==1 || row.status==2){
                        return {css:{"background-color":"#4B4B4B"}}
                    }else{
                        return {}
                    }
                }
            },
            {
                field: 'provinceCode',
                title: '地域/发行人名称',
                formatter: function (value, row, index) {  // value:field的值,row:行数据，用row.createdDtm找发起时间字段,index:行下标
                    var provinceName = '';
                    var cityName = '';
                    if(null!=row.company && null!=row.company.provinceName){
                        provinceName = row.company.provinceName;
                    }
                    if(null!=row.company && null!=row.company.cityName){
                        cityName = row.company.cityName;
                    }
                    var companyName = null==row.companyName ? '' : row.companyName;
                    var viewHTML = '<div style="width:100%;" title="' + provinceName + '  ' + cityName + ' ' + companyName + '">';
                    viewHTML += '<div class="cell-content">' + provinceName + '  ' + cityName + '</div>';
                    viewHTML += '<div class="cell-content">' + companyName + '</div>';
                    viewHTML += '</div>';
                    return viewHTML;
                },
                cellStyle:function(value,row,index){
                    if (row.status==1 || row.status==2){
                        return {css:{"background-color":"#4B4B4B"}}
                    }else{
                        return {}
                    }
                }
            },
            {
                field: 'listedPlace',
                title: '上市场所',
                formatter: function (value, row, index) {  // value:field的值,row:行数据，用row.createdDtm找发起时间字段,index:行下标
                    var place = null==value?'':value;
                    var viewHTML = '<div class="cell-content" title="' + place + '">' + place + '</div>';
                    return viewHTML;
                },
                cellStyle:function(value,row,index){
                    if (row.status==1 || row.status==2){
                        return {css:{"background-color":"#4B4B4B"}}
                    }else{
                        return {}
                    }
                }
            },
            {
                field: 'bondMatterList',
                title: '项目关联事项',
                formatter: function (value, row, index) {  // value:field的值,row:行数据，用row.createdDtm找发起时间字段,index:行下标
                    var html = '';
                    var matterCount = 0;
                    if(null!=row.bondMatterList && row.bondMatterList.length>0){
                        matterCount += row.bondMatterList.length;
                    }
                    if(null!=row.customMatterList && row.customMatterList.length>0){
                        matterCount += row.customMatterList.length;
                    }
                    html = '<div>共 ' + matterCount + ' 个事项</div>';
                    var viewCount = 0;
                    for(var i=0;i<row.bondMatterList.length;i++){
                        if(viewCount==5){
                            html += '...';
                            break;
                        }
                        viewCount+=1;
                        var templateName = null==row.bondMatterList[i].matterTemplate?'':row.bondMatterList[i].matterTemplate.name;
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
                },
                cellStyle:function(value,row,index){
                    if (row.status==1 || row.status==2){
                        return {css:{"background-color":"#4B4B4B"}}
                    }else{
                        return {}
                    }
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
                },
                cellStyle:function(value,row,index){
                    if (row.status==1 || row.status==2){
                        return {css:{"background-color":"#4B4B4B"}}
                    }else{
                        return {}
                    }
                }
            },
            {
                field: 'operation',
                title: '操作',
                formatter: function (value, row, index) {  // value:field的值,row:行数据，用row.createdDtm找发起时间字段,index:行下标

                    if($("#hidManager").val()==0){
                        if(row.status==0){
                            if((null==row.bondMatterList || row.bondMatterList.length<=0) && (null==row.customMatterList || row.customMatterList.length<=0)){
                                return '<a href="javascript:void(0)" class="linkedit">修改项目</a><br/><a href="javascript:void(0)" class="linkmatter">关联事项</a><br/><a href="javascript:void(0)" class="linksetdate" style="color:red;">设置到期</a>';
                            }else{
                                return '<a href="javascript:void(0)" class="linkedit">修改项目</a><br/><a href="javascript:void(0)" class="linkmatter">修改事项</a><br/><a href="javascript:void(0)" class="linksetdate" style="color:red;">设置到期</a>';
                            }
                        } else if(row.status==1){
                            return '<a href="javascript:void(0)" class="linkremove" style="color:red;">删除项目</a><br/><br/>';
                        } else if(row.status==2){
                            return '<a href="javascript:void(0)" class="linkresetdate" style="color:red;">撤销到期</a><br/><br/><a href="javascript:void(0)" class="linkremove" style="color:red;">删除项目</a>';
                        }
                    }else{
                        return '';
                    }


                },
                cellStyle:function(value,row,index){
                    if (row.status==1 || row.status==2){
                        return {css:{"background-color":"#4B4B4B"}}
                    }else{
                        return {}
                    }
                },
                events: {
                    'click .linkedit': function (e, value, row, index) {
                        $.ajax(contextPath + "/bond/editInfo/" + row.id + "/" + row.code).success(function (data) {
                            console.log("success");
                            pageIndex = layer.open({
                                type: 1,
                                title: '修改项目',
                                maxmin: false,
                                resize: false,
                                shade: 0.3,
                                area: ['95%', '98%'],
                                content: data
                            });
                        });
                    },
                    'click .linkmatter': function (e, value, row, index) {
                        $.ajax(contextPath + "/bond/linkmatterInfo/1/" + row.code).success(function (data) {
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
                    'click .linksetdate': function (e, value, row, index) {

                        layer.confirm(
                            '您确定要设置该项目提前到期吗？设置到期后，今后将不再触发任何事项！',
                            {icon: 3, title: '提示'},
                            function (index) {
                                var loadIndex = layer.load(2, {
                                    shade: [0.3, '#cacaca'],
                                    area: ['50px', '50px'],
                                    offset: ['50%', '50%'],
                                });

                                var bond = {};
                                bond.id = row.id;
                                bond.code = row.code;
                                bond.status = 0;

                                $.ajax({
                                    url: contextPath + "/bond/setdate",
                                    type: "post",
                                    data: JSON.stringify(bond),//将对象序列化成JSON字符串
                                    dataType: "json",
                                    contentType: 'application/json;charset=utf-8', //设置请求头信息
                                    success: function (data) {
                                        layer.close(loadIndex);
                                        if (data.code == 0) {
                                            layer.alert("设置到期成功", {icon: 1, time: 3000});
                                            refreshBondData();
                                        } else {
                                            layer.alert(data.message, {icon: 1, time: 3000});
                                        }
                                    },
                                    error: function (e) {
                                        layer.alert('设置到期失败', {icon: 1, time: 3000});
                                    }
                                });

                                layer.close(index);
                            })
                    },
                    'click .linkresetdate': function (e, value, row, index) {

                        layer.confirm(
                            '您确定要撤消恢复该项目吗？撤消后，该项目在到期日之前将会继续触发事项！',
                            {icon: 3, title: '提示'},
                            function (index) {
                                var loadIndex = layer.load(2, {
                                    shade: [0.3, '#cacaca'],
                                    area: ['50px', '50px'],
                                    offset: ['50%', '50%'],
                                });

                                var bond = {};
                                bond.id = row.id;
                                bond.code = row.code;
                                bond.status = 2;

                                $.ajax({
                                    url: contextPath + "/bond/resetdate",
                                    type: "post",
                                    data: JSON.stringify(bond),//将对象序列化成JSON字符串
                                    dataType: "json",
                                    contentType: 'application/json;charset=utf-8', //设置请求头信息
                                    success: function (data) {
                                        layer.close(loadIndex);
                                        if (data.code == 0) {
                                            layer.alert("撤消到期成功", {icon: 1, time: 3000});
                                            refreshBondData();
                                        } else {
                                            layer.alert(data.message, {icon: 1, time: 3000});
                                        }
                                    },
                                    error: function (e) {
                                        layer.alert('撤消到期失败', {icon: 1, time: 3000});
                                    }
                                });
                                layer.close(index);
                            })
                    },
                    'click .linkremove': function (e, value, row, index) {
                        layer.confirm(
                            '您确定要删除该项目吗？',
                            {icon: 3, title: '提示'},
                            function (index) {
                                var loadIndex = layer.load(2, {
                                    shade: [0.3, '#cacaca'],
                                    area: ['50px', '50px'],
                                    offset: ['50%', '50%'],
                                });

                                var bond = {};
                                bond.id = row.id;
                                bond.code = row.code;
                                bond.status = 2;

                                $.ajax({
                                    url: contextPath + "/bond/remove",
                                    type: "post",
                                    data: JSON.stringify(bond),//将对象序列化成JSON字符串
                                    dataType: "json",
                                    contentType: 'application/json;charset=utf-8', //设置请求头信息
                                    success: function (data) {
                                        layer.close(loadIndex);
                                        if (data.code == 0) {
                                            layer.alert("删除成功", {icon: 1, time: 3000});
                                            refreshBondData();
                                        } else {
                                            layer.alert(data.message, {icon: 1, time: 3000});
                                        }
                                    },
                                    error: function (e) {
                                        layer.alert('删除失败', {icon: 1, time: 3000});
                                    }
                                });
                                layer.close(index);
                            })
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
            if(null==row.company){
                layer.alert('发行人信息已经失效', {icon: 1, time: 3000});
                return false;
            }
            $.ajax(contextPath + "/bond/info/" + row.id + "/" + row.code + "/" + row.company.id).success(function (data) {
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
    });

}