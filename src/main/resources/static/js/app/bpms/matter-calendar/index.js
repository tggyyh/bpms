//@ sourceURL=index.js
var pageCount=10;
$(function () {
    var nowDate = new Date();
    //初始日历右边日期
    var monthDate = new Date();
    monthDate = new Date(monthDate.setDate(15));
    $("#hidMonthDate").html(monthDate.Format("yyyy-MM-dd"));
    setCalendarDate(nowDate.Format("yyyy-MM-dd"));
    //加载日历
    loadMatterDate(nowDate);
    //初始控件
    initMatterButton();

    //tab 选择事件
    $(".nav-tabs li a").click(function(){
        var tabID = $(this).attr("id");

        //加载选则的tab数据
        loadTabData(tabID);
    });

    var tabID = $("#calendar li[class='active'] a").attr("id");
    loadTabData(tabID);
});

//根据tabID加载数据
function loadTabData(tabID){
    pageCount = 10;

    if(tabID=="tab-remind"){
        initCalendarRemind();
        initTabRemind(0);
    }else if(tabID=="tab-complete"){
        initCalendarComplete();
        initTabComplete(0);
    }
}

function initCalendarRemind() {
    $("i[name='1']").attr("name","0");
    $(".calendar-day-tip").removeClass("calendar-day-tip");
    var paraJson = {};
    paraJson.monthDate = $("#hidMonthDate").html();
    paraJson.status = $("#drop_search_remind_status").val();
    paraJson.type = $("#drop_search_remind_type").val();

    var loadIndex = layer.load(2, {
        shade: [0.3,'#cacaca'],
        area: ['50px', '50px'],
        offset:['50%','50%'],
    });

    $.ajax({
        url: contextPath + "/mattercalendar/findCalendarMonthRemind",
        type:"post",
        data: JSON.stringify(paraJson),//将对象序列化成JSON字符串
        dataType:"json",
        contentType : 'application/json;charset=utf-8', //设置请求头信息
        success: function (data) {
            for(var i=0;i<data.length;i++){
                var objDate = $("div[name='" + timestampToDate(data[i].calendarDate) + "']");
                $(objDate).find("i").attr("name","1");
                if(!$(objDate).hasClass("calendar-day-selected")){
                    $(objDate).find("i").addClass("calendar-day-tip");
                }
            }
            layer.close(loadIndex);
        },
        error: function (e) {
            layer.close(loadIndex);
            layer.alert('加载数据失败', {icon: 1, time: 3000});
        }
    });
}
function initCalendarComplete() {
    $("i[name='1']").attr("name","0");
    $(".calendar-day-tip").removeClass("calendar-day-tip");
    var paraJson = {};
    paraJson.monthDate = $("#hidMonthDate").html();
    paraJson.status = $("#drop_search_complete_status").val();
    paraJson.type = $("#drop_search_complete_type").val();

    var loadIndex = layer.load(2, {
        shade: [0.3,'#cacaca'],
        area: ['50px', '50px'],
        offset:['50%','50%'],
    });

    $.ajax({
        url: contextPath + "/mattercalendar/findCalendarMonthComplete",
        type:"post",
        data: JSON.stringify(paraJson),//将对象序列化成JSON字符串
        dataType:"json",
        contentType : 'application/json;charset=utf-8', //设置请求头信息
        success: function (data) {
            for(var i=0;i<data.length;i++){
                var objDate = $("div[name='" + timestampToDate(data[i].calendarDate) + "']");
                $(objDate).find("i").attr("name","1");
                if(!$(objDate).hasClass("calendar-day-selected")){
                    $(objDate).find("i").addClass("calendar-day-tip");
                }
            }
            layer.close(loadIndex);
        },
        error: function (e) {
            layer.close(loadIndex);
            layer.alert('加载数据失败', {icon: 1, time: 3000});
        }
    });
}

//初始控件
function initMatterButton() {
    $("#divPrev").click(function () {
        var strDate = $("#hidMonthDate").html().split("-");
        var nowDate = addDate(new Date(strDate[0],parseInt(strDate[1])-1,1),-1);
        loadMatterDate(nowDate);

        nowDate.setDate(15);
        $("#hidMonthDate").html(nowDate.Format("yyyy-MM-dd"));
        initCalendarData();
    });
    $("#divNext").click(function () {
        var strDate = $("#hidMonthDate").html().split("-");
        var nowDate = addDate(new Date(strDate[0], parseInt(strDate[1])-1,28),4);
        loadMatterDate(nowDate);

        nowDate.setDate(15);
        $("#hidMonthDate").html(nowDate.Format("yyyy-MM-dd"));
        initCalendarData();
    });

    $("#drop_search_remind_status").change(function(){
        //refreshRemindData();
        initCalendarRemind();
        initTabRemind(0);
    });
    $("#drop_search_complete_status").change(function(){
        //refreshCompleteData();
        initCalendarComplete();
        initTabComplete(0);
    });
    $("#drop_search_complete_type").change(function(){
        initCalendarComplete();
        initTabComplete(0);
    });
    $("#drop_search_remind_type").change(function(){
        initCalendarRemind();
        initTabRemind(0);
    });
}

function initCalendarData(){
    var tabID = $("#calendar li[class='active'] a").attr("id");
    if(tabID=="tab-remind"){
        initCalendarRemind();
    }else if(tabID=="tab-complete"){
        initCalendarComplete();
    }
}

//加载日历
function loadMatterDate(nowDate){
    $("#divDate").html(nowDate.Format("yyyy年M月"));
    var startDate = new Date();
    var endDate = new Date();
    var firstDate = new Date(nowDate.setDate(1));
    var firstWeek = firstDate.getDay();
    var difDay = firstWeek==0?6:(firstWeek-1);
    difDay = difDay==0 ? 7 : difDay;
    startDate=addDate(firstDate,-difDay);
    endDate=addDate(startDate,41);

    $("#tabDate tr:not(:first)").remove();

    var selectDate = $("#divSelectDate").html();

    var trHTML = '';
    for(var i=0;i<6;i++){

        var tdHTML = '';
        for(var j=0;j<7;j++){
            var stepDate = new Date();
            var stepDay = j + (i*7);
            stepDate=addDate(startDate,stepDay);


            var selectClass = '';
            //var tipClass = 'calendar-day-tip';
            if(selectDate==stepDate.Format("yyyy-MM-dd")){
                selectClass = 'calendar-day-selected';
                //tipClass = '';
            }

            var dayHTML = '<div name="' + stepDate.Format("yyyy-MM-dd") + '" class="' + selectClass + '">' + stepDate.getDate();
            dayHTML += '<i class="" name="0"></i>';
            dayHTML += '</div>';
            if(firstDate.getMonth()==stepDate.getMonth()){
                tdHTML += '<td class="calendar-day">' + dayHTML + '</td>';
            }else{
                tdHTML += '<td class="calendar-day calendar-no-day">' + dayHTML + '</td>';
            }

        }
        trHTML += '<tr>' + tdHTML + '</tr>';
    }
    $("#tabDate tr:first").after(trHTML);

    $(".calendar-day div").click(function(){
        dayClick($(this));
    });
}

function dayClick(obj){
    if($(obj).hasClass("calendar-day-selected")){
        return;
        // $(this).removeClass("calendar-day-selected");
        // var tip = $(this).find("i").attr("name");
        // if(tip==1){
        //     $(this).find("i").addClass("calendar-day-tip");
        // }
    }else{
        $(".calendar-day-selected").removeClass("calendar-day-selected");
        $("i[name='1']").addClass("calendar-day-tip");
        $(obj).addClass("calendar-day-selected");
        $(obj).find(".calendar-day-tip").removeClass("calendar-day-tip");

        var selectedDate = $(obj).attr("name");
        setCalendarDate(selectedDate);

        var tabID = $("#calendar li[class='active'] a").attr("id");
        if(tabID=="tab-remind"){
            //refreshRemindData();
            initTabRemind(0);
        }else if(tabID=="tab-complete"){
            //refreshCompleteData();
            initTabComplete(0);
        }
    }
}

//初始日历右边日期
function setCalendarDate(objDate) {
    var arySelectedDate = objDate.split("-");
    var selectedDate = new Date(arySelectedDate[0],arySelectedDate[1]-1,arySelectedDate[2]);
    $("#divDate").html(selectedDate.getFullYear()+"年"+(selectedDate.getMonth()+1)+"月");
    $("#divDay").html(selectedDate.getDate());
    var myddy=selectedDate.getDay();//获取存储当前日期
    var weekday=["周日","周一","周二","周三","周四","周五","周六"];
    $("#divWeek").html(weekday[myddy]);

    var today = new Date();
    if(today.Format("yyyy-MM-dd")==selectedDate.Format("yyyy-MM-dd")){
        $("#divToday").removeClass("hide");
    }else{
        $("#divToday").addClass("hide");
    }
    $("#divSelectDate").html(selectedDate.Format("yyyy-MM-dd"));

}

function refreshRemindData(){
    $('#tabRemind').bootstrapTable('refresh');
}
function refreshCompleteData(){
    $('#tabComplete').bootstrapTable('refresh');
}


function initTabRemind(pageOffset){
    var para = {};
    para.pSize = pageCount;
    para.pNum = pageOffset;
    para.type = $("#drop_search_remind_type").val();
    para.remindDate = $("#divSelectDate").html();
    para.status = $("#drop_search_remind_status").val();


    var loadIndex = layer.load(2, {
        shade: [0.3,'#cacaca'],
        area: ['50px', '50px'],
        offset:['50%','50%'],
    });

    $.ajax({
        url: contextPath + "/mattercalendar/findCalendarDayRemind",
        type:"post",
        data: JSON.stringify(para),//将对象序列化成JSON字符串
        dataType:"json",
        contentType : 'application/json;charset=utf-8', //设置请求头信息
        success: function (data) {
            // addCompleteRows(data);
            // layer.close(loadIndex);
            if(null!=data && data.rows.length>0){
                addRemindRows(data);
            }else{
                $("#tabRemind tbody").html("");
            }
            initRemindPagination(data.total, pageOffset);
            layer.close(loadIndex);
        },
        error: function (e) {
            layer.close(loadIndex);
            layer.alert('加载数据失败', {icon: 1, time: 3000});
            $("#tabRemind tbody").html("");
        }
    });
}

function addRemindRows(objData){
    $("#tabRemind tbody").html("");
    for(var i=0;i<objData.rows.length;i++){
        var row = objData.rows[i];
        var tempName = "";
        var compName = "";
        var tempDate = "";
        var tempStatus = "";
        if(row.type==3){
            //自定义事项
            for(var j=0;j<row.matterCalendarList.length;j++){
                var objRow = row.matterCalendarList[j];

                //事项名称
                tempName = '<div style="padding-left:20px;">';
                tempName += '<div class="tmp-icon" data-toggle="tooltip" title="' + objRow.tempShortname + '" style="background-color:' + objRow.color + ';border-color:' + objRow.color + ';">'+ objRow.tempShortname + '</div>';
                tempName += '<div class="tmp-content" style="width:50%;">';
                var tempNameHtml=  objRow.tempName;
                if(objRow.rightLine==1){
                    tempNameHtml = objRow.tempName + '-' + objRow.subName;
                }
                tempName += '<div class="content-name" data-toggle="tooltip" title="' + tempNameHtml + '">' + tempNameHtml + '</div>';
                tempName += '<div class="content-text" data-toggle="tooltip" title="' + objRow.tempDescription + '">' + objRow.tempDescription + '</div>';
                tempName += '</div>';
                tempName += '</div>';

                //发行人项目名
                var companyName = null==objRow.companyName?'':objRow.companyName;
                if(objRow.type==0){
                    compName = companyName;
                }else if(objRow.type==1){
                    var bondCode = null==objRow.bondCode?'':objRow.bondCode;
                    var bondName = null==objRow.bondShortname?'':objRow.bondShortname;
                    compName = bondName + '<br/>' + bondCode;
                }
                var infoJson = {};
                infoJson.type = objRow.type;
                infoJson.companyId = objRow.companyId;
                infoJson.companyName = companyName;
                infoJson.bondId = objRow.bondId;
                infoJson.bondCode = null==objRow.bondCode?"":objRow.bondCode;
                compName = "<a href='javascript:void(0)' data-info='"+JSON.stringify(infoJson).replace(/\'/g,"\"")+"' class='linkinfo' onclick='linkInfo(this)'>" + compName + "</a>";

                //触发时间
                var remindDate = null==objRow.remindDate1 ? '' : timestampToDate(objRow.remindDate1);
                var completeDate = null==objRow.completeDate1 ? '' : timestampToDate(objRow.completeDate1);
                tempDate = '<div>' + remindDate + '</div><div style="color:#B20000;">' + completeDate + '</div>';

                //事项状态
                if(objRow.status==0){
                    tempStatus = '<div style="color:#CC0000;">等待项目人员处理</div>';
                }else if(objRow.status==1){
                    tempStatus = '<div style="color:#CCCC66;">等待督导人员确认</div>';
                }

                var tr = '<tr style="height:90px;" class="tmp-border-bottom"><td>' + tempName + '</td><td style="padding-left:20px;">' + compName + '</td><td style="padding-left:20px;">' + tempDate + '</td><td style="padding-left:20px;">' + tempStatus + '</td></tr>';
                $("#tabRemind tbody").append(tr);
            }
        }else{
            var tempNameHTML = '<div style="padding-left:20px;">';
            tempNameHTML += '<div class="tmp-icon" data-toggle="tooltip" title="' + row.shortname + '" style="background-color:' + row.color + ';border-color:' + row.color + ';">'+ row.shortname + '</div>';
            tempNameHTML += '<div class="tmp-content" style="width:50%;">';
            tempNameHTML += '<div class="content-name" data-toggle="tooltip" title="' + row.name + '">' + row.name + '</div>';
            tempNameHTML += '<div class="content-text" data-toggle="tooltip" title="' + row.description + '">' + row.description + '</div>';
            tempNameHTML += '</div>';
            tempNameHTML += '</div>';

            var tr = '<tr style="height:90px;" class="tmp-border-bottom"><td>' + tempNameHTML + '</td><td></td><td></td><td style="padding-left:20px;"><span id="spanCompanyTemp-' + row.id + '" class="glyphicon glyphicon-triangle-bottom cursor-pointer" onclick="showRemindTempItem(this)"></td></tr>';
            for(var j=0;j<row.matterCalendarList.length;j++) {
                var objRow = row.matterCalendarList[j];

                //事项名称
                tempName = '<div style="padding-left:10px">';
                tempName += '<div style="overflow:hidden; white-space:nowrap; text-overflow:ellipsis;" data-toggle="tooltip" title="' + objRow.tempName + '">' + objRow.tempName + '</div>';
                tempName += '<div style="color:#9F9C99;overflow:hidden; white-space:nowrap; text-overflow:ellipsis;" data-toggle="tooltip" title="' + objRow.tempDescription + '">' + objRow.tempDescription + '</div>';
                tempName += '</div>';

                //发行人项目名
                var companyName = null==objRow.companyName?'':objRow.companyName;
                if(objRow.type==0){
                    compName = null==objRow.companyName?'':objRow.companyName;
                }else if(objRow.type==1){
                    var bondCode = null==objRow.bondCode?'':objRow.bondCode;
                    var bondName = null==objRow.bondShortname?'':objRow.bondShortname;
                    compName = bondName + '<br/>' + bondCode;
                }
                var infoJson = {};
                infoJson.type = objRow.type;
                infoJson.companyId = objRow.companyId;
                infoJson.companyName = companyName;
                infoJson.bondId = objRow.bondId;
                infoJson.bondCode = null==objRow.bondCode?"":objRow.bondCode;
                compName = "<a href='javascript:void(0)' data-info='"+JSON.stringify(infoJson).replace(/\'/g,"\"")+"' class='linkinfo' onclick='linkInfo(this)'>" + compName + "</a>";

                //触发时间
                var remindDate = null==objRow.remindDate1 ? '' : timestampToDate(objRow.remindDate1);
                var completeDate = null==objRow.completeDate1 ? '' : timestampToDate(objRow.completeDate1);
                tempDate = '<div>' + remindDate + '</div><div style="color:#B20000;">' + completeDate + '</div>';

                //事项状态
                //事项状态
                if(objRow.status==0){
                    tempStatus = '<div style="color:#CC0000;">等待项目人员处理</div>';
                }else if(objRow.status==1){
                    tempStatus = '<div style="color:#CCCC66;">等待督导人员确认</div>';
                }

                tr += '<tr name="tr-' + row.id + '" class="tmp-border-bottom hide"><td style="padding:5px 20px;">' + tempName + '</td><td style="padding:5px 20px;">' + compName + '</td><td style="padding:5px 20px;">' + tempDate + '</td><td style="padding:5px 20px;">' + tempStatus + '</td></tr>';
            }
            $("#tabRemind tbody").append(tr);
        }
    }
}

function showRemindTempItem(obj){
    var id = $(obj).attr("id").split("-")[1];
    if($(obj).hasClass("glyphicon-triangle-bottom")){
        $("#tabRemind tbody tr[name='tr-" + id + "']").removeClass("hide");
        $(obj).removeClass("glyphicon-triangle-bottom").addClass("glyphicon-triangle-top");
    }else{
        $("#tabRemind tbody tr[name='tr-" + id + "']").addClass("hide");
        $(obj).removeClass("glyphicon-triangle-top").addClass("glyphicon-triangle-bottom");
    }
}

//分页
//totalCount总条数
//pageOffset每页显示数
function initRemindPagination(totalCount, pageOffset) {
    var aryPageCount = [10,20,50,100];
    var pageHTML = '';
    //总页数
    var pageCounts = 0;
    if(totalCount%pageCount>0){
        pageCounts = parseInt(totalCount/pageCount) + 1;
    }else{
        pageCounts = parseInt(totalCount/pageCount);
    }
    pageHTML += '<ul class="pagination">';
    pageHTML += '<li id="li-first" class="first" onclick="eventRemindPagination(this)"><a href="#">&laquo;</a></li>';
    pageHTML += '<li id="li-prev" class="previous" onclick="eventRemindPagination(this)"><a href="#">&lt;</a></li>';

    var index = parseInt(pageOffset)+1;
    var sIndex = 1;
    var eIndex = pageCounts>5?5:pageCounts;

    if(pageCounts>5){
        if(index>=4 && (index+2)<=pageCounts){
            sIndex = index-2;
            if((index+2)>pageCounts){
                eIndex = pageCounts;
            }else{
                eIndex = index + 2;
            }
        }else if(index>=4 && (index+2)>pageCounts){
            sIndex = pageCounts - 4;
            eIndex = pageCounts;
        }
    }

    for(var i=sIndex;i<=eIndex;i++){
        if(i==index){
            pageHTML += '<li id="li-' + i + '" class="active" onclick="eventRemindPagination(this)"><a href="#">' + i + '</a></li>';
        }else{
            pageHTML += '<li id="li-' + i + '" onclick="eventRemindPagination(this)"><a href="#">' + i + '</a></li>';
        }
    }

    pageHTML += '<li id="li-next" class="next" onclick="eventRemindPagination(this)"><a href="#">&gt;</a></li>';
    pageHTML += '<li id="li-last" class="last" onclick="eventRemindPagination(this)"><a href="#">&raquo;</a></li>';
    pageHTML += '</ul>';

    //分页显示
    var sNum = (index-1) * pageCount + 1;
    var eNum = 0;
    var viewPage = '';
    if(index*pageCount >= parseInt(totalCount)){
        eNum = parseInt(totalCount);
    }else{
        eNum = index * pageCount;
    }
    if(totalCount<=0){
        sNum = 0;
    }
    if(sNum==eNum){
        viewPage = '第 ' + sNum + ' 条，共 ' + totalCount + ' 条';
    }else{
        viewPage = '第 ' + sNum + ' 到 ' + eNum + ' 条，共 ' + totalCount + ' 条';
    }

    var pageCountHTML = '<div class="col-sm-1 text-right" style="margin:20px 0;">每页</div>';
    pageCountHTML += '<div class="col-sm-2" style="margin:10px 0;">';
    pageCountHTML += '<div class="col-sm-9">';
    pageCountHTML += '<select id="dropRemindPageCount" class="form-control" onchange="setRemindPageCount()">';
    for(var i=0;i<aryPageCount.length;i++){
        if(aryPageCount[i]==pageCount){
            pageCountHTML += '<option value="' + aryPageCount[i] + '" selected="selected">' + aryPageCount[i] + '</option>';
        }else{
            pageCountHTML += '<option value="' + aryPageCount[i] + '">' + aryPageCount[i] + '</option>';
        }
    }
    pageCountHTML += '</select>';
    pageCountHTML += '</div>';
    pageCountHTML += '<div class="col-sm-3" style="margin:10px 0;">条</div>';
    pageCountHTML += '</div>';

    $("#divRemindPagination").remove();
    var paginationHTML = '';
    paginationHTML += '<div id="divRemindPagination" class="col-sm-11">';
    paginationHTML += '     <input type="hidden" id="hidRemindPageCount" value="' + pageCounts + '" />';
    paginationHTML += '     <div class="col-sm-4" style="margin:20px 0;">' + viewPage + '</div>';
    paginationHTML += pageCountHTML;
    paginationHTML += '     <div class="col-sm-5 text-right">' + pageHTML + '</div>';
    paginationHTML += '</div>';
    $("#remindPagePagination").append(paginationHTML);

}

function setRemindPageCount(){
    pageCount = $("#dropRemindPageCount").val();
    initTabRemind(0);
}

function eventRemindPagination(obj) {
    var offset = 0;

    //分页前的页索引
    var oldIndex = $("#divRemindPagination .active").attr("id").replace("li-","");
    //当前点击分页的索引
    var eventIndex = $(obj).attr("id").replace("li-","");
    //总页码数
    var total = $("#hidRemindPageCount").val();

    if(eventIndex=='first'){
        //首页
        if(parseInt(oldIndex)<=1){
            return;
        }
        offset = 0;
    }else if(eventIndex=='prev'){
        //上页
        if(parseInt(oldIndex)<=1){
            return;
            offset = 0;
        }else{
            offset = oldIndex-2;
        }
    }else if(eventIndex=='next'){
        //下页
        if(parseInt(oldIndex)>=parseInt(total)){
            return;
            offset = parseInt(total) - 1;
        }else{
            offset = oldIndex;
        }
    }else if(eventIndex=='last'){
        if(parseInt(oldIndex)>=parseInt(total)){
            return;
        }
        offset = parseInt(total)-1;
    }else{
        //页码
        if(oldIndex==eventIndex){
            return;
        }
        offset = eventIndex - 1;
    }

    initTabRemind(offset);
}



function initTabComplete(pageOffset){
    var para = {};
    para.pSize = pageCount;
    para.pNum = pageOffset;
    para.completeDate = $("#divSelectDate").html();
    para.status = $("#drop_search_complete_status").val();
    para.type = $("#drop_search_complete_type").val();

    var loadIndex = layer.load(2, {
        shade: [0.3,'#cacaca'],
        area: ['50px', '50px'],
        offset:['50%','50%'],
    });

    $.ajax({
        url: contextPath + "/mattercalendar/findCalendarDayComplete",
        type:"post",
        data: JSON.stringify(para),//将对象序列化成JSON字符串
        dataType:"json",
        contentType : 'application/json;charset=utf-8', //设置请求头信息
        success: function (data) {
            // addCompleteRows(data);
            // layer.close(loadIndex);
            if(null!=data && data.rows.length>0){
                addCompleteRows(data);
            }else{
                $("#tabComplete tbody").html("");
            }
            initCompletePagination(data.total, pageOffset);
            layer.close(loadIndex);
        },
        error: function (e) {
            layer.close(loadIndex);
            layer.alert('加载数据失败', {icon: 1, time: 3000});
            $("#tabComplete tbody").html("");
        }
    });
}

function addCompleteRows(objData){
    $("#tabComplete tbody").html("");
    for(var i=0;i<objData.rows.length;i++){
        var row = objData.rows[i];
        var tempName = "";
        var compName = "";
        var tempDate = "";
        var tempStatus = "";
        if(row.type==3){
            //自定义事项
            for(var j=0;j<row.matterCalendarList.length;j++){
                var objRow = row.matterCalendarList[j];

                //事项名称
                tempName = '<div style="padding-left:20px;">';
                tempName += '<div class="tmp-icon" data-toggle="tooltip" title="' + objRow.tempShortname + '" style="background-color:' + objRow.color + ';border-color:' + objRow.color + ';">'+ objRow.tempShortname + '</div>';
                tempName += '<div class="tmp-content" style="width:50%;">';
                var tempNameHtml=  objRow.tempName;
                if(objRow.rightLine==1){
                    tempNameHtml = objRow.tempName + '-' + objRow.subName;
                }
                tempName += '<div class="content-name" data-toggle="tooltip" title="' + tempNameHtml + '">' + tempNameHtml + '</div>';
                tempName += '<div class="content-text" data-toggle="tooltip" title="' + objRow.tempDescription + '">' + objRow.tempDescription + '</div>';
                tempName += '</div>';
                tempName += '</div>';

                //发行人项目名
                var companyName = null==objRow.companyName?'':objRow.companyName;
                if(objRow.type==0){
                    compName = companyName;
                }else if(objRow.type==1){
                    var bondCode = null==objRow.bondCode?'':objRow.bondCode;
                    var bondName = null==objRow.bondShortname?'':objRow.bondShortname;
                    compName = bondName + '<br/>' + bondCode;
                }
                var infoJson = {};
                infoJson.type = objRow.type;
                infoJson.companyId = objRow.companyId;
                infoJson.companyName = companyName;
                infoJson.bondId = objRow.bondId;
                infoJson.bondCode = null==objRow.bondCode?"":objRow.bondCode;
                compName = "<a href='javascript:void(0)' data-info='"+JSON.stringify(infoJson).replace(/\'/g,"\"")+"' class='linkinfo' onclick='linkInfo(this)'>" + compName + "</a>";

                //触发时间
                var remindDate = null==objRow.remindDate1 ? '' : timestampToDate(objRow.remindDate1);
                var completeDate = null==objRow.completeDate1 ? '' : timestampToDate(objRow.completeDate1);
                tempDate = '<div>' + remindDate + '</div><div style="color:#B20000;">' + completeDate + '</div>';

                //事项状态
                if(objRow.status==0){
                    tempStatus = '未触发';
                }else if(objRow.status==1){
                    tempStatus = '<div style="color:#CCCC66;">等待督导人员确认</div>';
                }else if(objRow.status==2 || objRow.status==8){
                    tempStatus = '<div style="color:#CC0000;">等待项目人员处理</div>';
                }else if(objRow.status==4 || objRow.status==16){
                    tempStatus = '<div style="color:#FF6600;">等待督导人员审核</div>';
                }

                var tr = '<tr style="height:90px;" class="tmp-border-bottom"><td>' + tempName + '</td><td style="padding-left:20px;">' + compName + '</td><td style="padding-left:20px;">' + tempDate + '</td><td style="padding-left:20px;">' + tempStatus + '</td></tr>';
                $("#tabComplete tbody").append(tr);
            }
        }else{
            var tempNameHTML = '<div style="padding-left:20px;">';
            tempNameHTML += '<div class="tmp-icon" data-toggle="tooltip" title="' + row.shortname + '" style="background-color:' + row.color + ';border-color:' + row.color + ';">'+ row.shortname + '</div>';
            tempNameHTML += '<div class="tmp-content" style="width:50%;">';
            tempNameHTML += '<div class="content-name" data-toggle="tooltip" title="' + row.name + '">' + row.name + '</div>';
            tempNameHTML += '<div class="content-text" data-toggle="tooltip" title="' + row.description + '">' + row.description + '</div>';
            tempNameHTML += '</div>';
            tempNameHTML += '</div>';

            var tr = '<tr style="height:90px;" class="tmp-border-bottom"><td>' + tempNameHTML + '</td><td></td><td></td><td style="padding-left:20px;"><span id="spanCompanyTemp-' + row.id + '" class="glyphicon glyphicon-triangle-bottom cursor-pointer" onclick="showCompleteTempItem(this)"></td></tr>';
            for(var j=0;j<row.matterCalendarList.length;j++) {
                var objRow = row.matterCalendarList[j];

                //事项名称
                tempName = '<div style="padding-left:10px">';
                tempName += '<div style="overflow:hidden; white-space:nowrap; text-overflow:ellipsis;" data-toggle="tooltip" title="' + objRow.tempName + '">' + objRow.tempName + '</div>';
                tempName += '<div style="color:#9F9C99;overflow:hidden; white-space:nowrap; text-overflow:ellipsis;" data-toggle="tooltip" title="' + objRow.tempDescription + '">' + objRow.tempDescription + '</div>';
                tempName += '</div>';

                //发行人项目名
                var companyName = null==objRow.companyName?'':objRow.companyName;
                if(objRow.type==0){
                    compName = companyName;
                }else if(objRow.type==1){
                    var bondCode = null==objRow.bondCode?'':objRow.bondCode;
                    var bondName = null==objRow.bondShortname?'':objRow.bondShortname;
                    compName = bondName + '<br/>' + bondCode;
                }
                var infoJson = {};
                infoJson.type = objRow.type;
                infoJson.companyId = objRow.companyId;
                infoJson.companyName = companyName;
                infoJson.bondId = objRow.bondId;
                infoJson.bondCode = null==objRow.bondCode?"":objRow.bondCode;
                compName = "<a href='javascript:void(0)' data-info='"+JSON.stringify(infoJson).replace(/\'/g,"\"")+"' class='linkinfo' onclick='linkInfo(this)'>" + compName + "</a>";

                //触发时间
                var remindDate = null==objRow.remindDate1 ? '' : timestampToDate(objRow.remindDate1);
                var completeDate = null==objRow.completeDate1 ? '' : timestampToDate(objRow.completeDate1);
                tempDate = '<div>' + remindDate + '</div><div style="color:#B20000;">' + completeDate + '</div>';

                //事项状态
                if(objRow.status==0){
                    tempStatus = '未触发';
                }else if(objRow.status==1){
                    tempStatus = '<div style="color:#CCCC66;">等待督导人员确认</div>';
                }else if(objRow.status==2 || objRow.status==8){
                    tempStatus = '<div style="color:#CC0000;">等待项目人员处理</div>';
                }else if(objRow.status==4 || objRow.status==16){
                    tempStatus = '<div style="color:#FF6600;">等待督导人员审核</div>';
                }

                tr += '<tr name="tr-' + row.id + '" class="tmp-border-bottom hide"><td style="padding:5px 20px;">' + tempName + '</td><td style="padding:5px 20px;">' + compName + '</td><td style="padding:5px 20px;">' + tempDate + '</td><td style="padding:5px 20px;">' + tempStatus + '</td></tr>';
            }
            $("#tabComplete tbody").append(tr);
        }
    }
}

function showCompleteTempItem(obj){
    var id = $(obj).attr("id").split("-")[1];
    if($(obj).hasClass("glyphicon-triangle-bottom")){
        $("#tabComplete tbody tr[name='tr-" + id + "']").removeClass("hide");
        $(obj).removeClass("glyphicon-triangle-bottom").addClass("glyphicon-triangle-top");
    }else{
        $("#tabComplete tbody tr[name='tr-" + id + "']").addClass("hide");
        $(obj).removeClass("glyphicon-triangle-top").addClass("glyphicon-triangle-bottom");
    }
}

//分页
//totalCount总条数
//pageOffset每页显示数
function initCompletePagination(totalCount, pageOffset) {
    var aryPageCount = [10,20,50,100];
    var pageHTML = '';
    //总页数
    var pageCounts = 0;
    if(totalCount%pageCount>0){
        pageCounts = parseInt(totalCount/pageCount) + 1;
    }else{
        pageCounts = parseInt(totalCount/pageCount);
    }
    pageHTML += '<ul class="pagination">';
    pageHTML += '<li id="li-first" class="first" onclick="eventCompletePagination(this)"><a href="#">&laquo;</a></li>';
    pageHTML += '<li id="li-prev" class="previous" onclick="eventCompletePagination(this)"><a href="#">&lt;</a></li>';

    var index = parseInt(pageOffset)+1;
    var sIndex = 1;
    var eIndex = pageCounts>5?5:pageCounts;

    if(pageCounts>5){
        if(index>=4 && (index+2)<=pageCounts){
            sIndex = index-2;
            if((index+2)>pageCounts){
                eIndex = pageCounts;
            }else{
                eIndex = index + 2;
            }
        }else if(index>=4 && (index+2)>pageCounts){
            sIndex = pageCounts - 4;
            eIndex = pageCounts;
        }
    }

    for(var i=sIndex;i<=eIndex;i++){
        if(i==index){
            pageHTML += '<li id="li-' + i + '" class="active" onclick="eventCompletePagination(this)"><a href="#">' + i + '</a></li>';
        }else{
            pageHTML += '<li id="li-' + i + '" onclick="eventCompletePagination(this)"><a href="#">' + i + '</a></li>';
        }
    }

    pageHTML += '<li id="li-next" class="next" onclick="eventCompletePagination(this)"><a href="#">&gt;</a></li>';
    pageHTML += '<li id="li-last" class="last" onclick="eventCompletePagination(this)"><a href="#">&raquo;</a></li>';
    pageHTML += '</ul>';

    //分页显示
    var sNum = (index-1) * pageCount + 1;
    var eNum = 0;
    var viewPage = '';
    if(index*pageCount >= parseInt(totalCount)){
        eNum = parseInt(totalCount);
    }else{
        eNum = index * pageCount;
    }
    if(totalCount<=0){
        sNum = 0;
    }
    if(sNum==eNum){
        viewPage = '第 ' + sNum + ' 条，共 ' + totalCount + ' 条';
    }else{
        viewPage = '第 ' + sNum + ' 到 ' + eNum + ' 条，共 ' + totalCount + ' 条';
    }

    var pageCountHTML = '<div class="col-sm-1 text-right" style="margin:20px 0;">每页</div>';
    pageCountHTML += '<div class="col-sm-2" style="margin:10px 0;">';
    pageCountHTML += '<div class="col-sm-9">';
    pageCountHTML += '<select id="dropCompletePageCount" class="form-control" onchange="setCompletePageCount()">';
    for(var i=0;i<aryPageCount.length;i++){
        if(aryPageCount[i]==pageCount){
            pageCountHTML += '<option value="' + aryPageCount[i] + '" selected="selected">' + aryPageCount[i] + '</option>';
        }else{
            pageCountHTML += '<option value="' + aryPageCount[i] + '">' + aryPageCount[i] + '</option>';
        }
    }
    pageCountHTML += '</select>';
    pageCountHTML += '</div>';
    pageCountHTML += '<div class="col-sm-3" style="margin:10px 0;">条</div>';
    pageCountHTML += '</div>';

    $("#divCompletePagination").remove();
    var paginationHTML = '';
    paginationHTML += '<div id="divCompletePagination" class="col-sm-11">';
    paginationHTML += '     <input type="hidden" id="hidCompletePageCount" value="' + pageCounts + '" />';
    paginationHTML += '     <div class="col-sm-4" style="margin:20px 0;">' + viewPage + '</div>';
    paginationHTML += pageCountHTML;
    paginationHTML += '     <div class="col-sm-5 text-right">' + pageHTML + '</div>';
    paginationHTML += '</div>';
    $("#completePagePagination").append(paginationHTML);

}

function setCompletePageCount(){
    pageCount = $("#dropCompletePageCount").val();
    initTabComplete(0);
}

function eventCompletePagination(obj) {
    var offset = 0;

    //分页前的页索引
    var oldIndex = $("#divCompletePagination .active").attr("id").replace("li-","");
    //当前点击分页的索引
    var eventIndex = $(obj).attr("id").replace("li-","");
    //总页码数
    var total = $("#hidCompletePageCount").val();

    if(eventIndex=='first'){
        //首页
        if(parseInt(oldIndex)<=1){
            return;
        }
        offset = 0;
    }else if(eventIndex=='prev'){
        //上页
        if(parseInt(oldIndex)<=1){
            return;
            offset = 0;
        }else{
            offset = oldIndex-2;
        }
    }else if(eventIndex=='next'){
        //下页
        if(parseInt(oldIndex)>=parseInt(total)){
            return;
            offset = parseInt(total) - 1;
        }else{
            offset = oldIndex;
        }
    }else if(eventIndex=='last'){
        if(parseInt(oldIndex)>=parseInt(total)){
            return;
        }
        offset = parseInt(total)-1;
    }else{
        //页码
        if(oldIndex==eventIndex){
            return;
        }
        offset = eventIndex - 1;
    }

    initTabComplete(offset);
}


function linkInfo(obj){
    var objRow = JSON.parse($(obj).attr("data-info"));

    if(null==objRow || undefined==objRow){
        return false;
    }
    var type = objRow.type;
    var companyId = null==objRow.companyId?0:objRow.companyId;
    var companyName = null==objRow.companyName?"":objRow.companyName;
    var bondId = null==objRow.bondId?0:objRow.bondId;
    var bondCode = null==objRow.bondCode?"":objRow.bondCode;

    if(type==0){
        if(companyId==0){
            layer.alert("该发行人信息已失效", {icon: 1, time: 3000});
            return;
        }else{
            $.ajax(contextPath + "/company/info/" + companyId + "/" + companyName).success(function (data) {
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

    }else if(type==1){
        if(bondId==0){
            layer.alert("该项目息已失效", {icon: 1, time: 3000});
            return;
        }else{
            $.ajax(contextPath + "/bond/info/" + bondId + "/" + bondCode + "/" + companyId).success(function (data) {
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

//table查询参数
function completeParams(params) {  //配置参数
    return {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
        pSize: params.limit,
        pNum: params.offset,
        completeDate: $("#divSelectDate").html(),
        status: $("#drop_search_complete_status").val()
    };
}
function initTabCompletes(){
    $('#tabComplete').bootstrapTable('destroy');

    var loadTableIndex = layer.load(2, {
        shade: [0.3,'#cacaca'],
        area: ['50px', '50px'],
        offset:['50%','50%'],
    });

    $('#tabComplete').bootstrapTable({
        url: contextPath + '/mattercalendar/findCalendarDayComplete',        //请求后台的URL（*）
        method: 'post',                         //请求方式（*）
        toolbar: '#toolbar',                //工具按钮用哪个容器
        striped: true,                      //是否显示行间隔色
        cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
        pagination: true,                   //是否显示分页（*）
        sortable: true,                     //是否启用排序
        sortOrder: "asc",                   //排序方式
        queryParams: completeParams,           //传递参数（*）
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
                title: '事项名称',
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
                    var tempNameHtml=  row.tempName;
                    if(row.rightLine==1){
                        tempNameHtml = row.tempName + '-' + row.subName;
                    }
                    tempName += '<div class="content-name" data-toggle="tooltip" title="' + tempNameHtml + '">' + tempNameHtml + '</div>';
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
                            if(row.bondId==0){
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
                title: '触发时间/需完成时间',
                formatter: function (value, row, index) {  // value:field的值,row:行数据，用row.createdDtm找发起时间字段,index:行下标
                    var remindDate = null==row.remindDate1 ? '' : timestampToDate(row.remindDate1);
                    var completeDate = null==row.completeDate1 ? '' : timestampToDate(row.completeDate1);
                    return '<div>' + remindDate + '</div><div style="color:#B20000;">' + completeDate + '</div>';
                }
            },
            {
                field: 'status',
                title: '事项当前所处状态',
                formatter: function (value, row, index) {  // value:field的值,row:行数据，用row.createdDtm找发起时间字段,index:行下标
                    if(value==0){
                        return '未触发';
                    }else if(value==1){
                        return '<div style="color:#CCCC66;">等待督导人员确认</div>';
                    }else if(value==2 || value==8){
                        return '<div style="color:#CC0000;">等待项目人员处理</div>';
                    }else if(value==4 || value==16){
                        return '<div style="color:#FF6600;">等待督导人员审核</div>';
                    }
                }
            }
        ],
        onClickRow: function (row, element) {
            $(".selected").removeClass("selected");
            //$(".row-selected").removeClass("row-selected");
            $(element).addClass("selected");
        },
        onLoadSuccess: function(data){
            layer.close(loadTableIndex);
        },
        onLoadError: function (status) {
            layer.close(loadTableIndex);
        }
    });
}

function remindParams(params) {  //配置参数
    return {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
        pSize: params.limit,
        pNum: params.offset,
        remindDate: $("#divSelectDate").html(),
        status: $("#drop_search_remind_status").val()
    };
}
function initTabReminds(){
    $('#tabRemind').bootstrapTable('destroy');

    $('#tabRemind').bootstrapTable({
        url: contextPath + '/mattercalendar/findCalendarDayRemind',        //请求后台的URL（*）
        method: 'post',                         //请求方式（*）
        toolbar: '#toolbar',                //工具按钮用哪个容器
        striped: true,                      //是否显示行间隔色
        cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
        pagination: true,                   //是否显示分页（*）
        sortable: true,                     //是否启用排序
        sortOrder: "asc",                   //排序方式
        queryParams: remindParams,           //传递参数（*）
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
                title: '事项名称',
                formatter: function (value, row, index) {  // value:field的值,row:行数据，用row.createdDtm找发起时间字段,index:行下标
                    var tempName = '<div>';
                    tempName += '<div class="tmp-icon" data-toggle="tooltip" title="' + row.tempShortname + '" style="background-color:' + row.color + ';border-color:' + row.color + ';">'+ row.tempShortname + '</div>';
                    tempName += '<div class="tmp-content" style="width:50%;">';
                    var tempNameHtml=  row.tempName;
                    if(row.rightLine==1){
                        tempNameHtml = row.tempName + '-' + row.subName;
                    }
                    tempName += '<div class="content-name" data-toggle="tooltip" title="' + tempNameHtml + '">' + tempNameHtml + '</div>';
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
                        }else if(row.type==1){
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
            },
            {
                field: 'completeDate',
                title: '触发时间/需完成时间',
                formatter: function (value, row, index) {  // value:field的值,row:行数据，用row.createdDtm找发起时间字段,index:行下标
                    var remindDate = null==row.remindDate1 ? '' : timestampToDate(row.remindDate1);
                    var completeDate = null==row.completeDate1 ? '' : timestampToDate(row.completeDate1);
                    return '<div>' + remindDate + '</div><div style="color:#B20000;">' + completeDate + '</div>';
                }
            },
            {
                field: 'status',
                title: '事项将处状态',
                formatter: function (value, row, index) {  // value:field的值,row:行数据，用row.createdDtm找发起时间字段,index:行下标
                    if(value==0){
                        return '<div style="color:#CC0000;">等待项目人员处理</div>';
                    }else if(value==1){
                        return '<div style="color:#CCCC66;">等待督导人员确认</div>';
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
