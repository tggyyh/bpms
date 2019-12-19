/**
 * Created by Dealing076 on 2018/3/13.
 */
//@ sourceURL=infoAttachment.js
var uploadPage;
var pageNum = 0;
var pageCount=10;
$(function () {

    initInfoAttachmentDate();

    initInfoAttachmentButtons();

    findInfoAttachmentData(0);

    $("#divDocumentFiles").css("height", ($(".layui-layer-page").height()-240) + "px");

});

function initInfoAttachmentDate() {

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

function initInfoAttachmentButtons(){
    $("#btn_infoAttahment_query").click(function(){
        findInfoAttachmentData(0);
    });

    $("#btnUpload").click(function(){
        $.ajax({
            type: 'GET',
            url: contextPath + "/upload/addCompany/"+$("#hidCompanyName").val(),
            cache: false,
            success: function (data) {
                console.log("success");
                uploadPage = layer.open({
                    type: 1,
                    title: '上传资料',
                    maxmin: false,
                    resize: false,
                    shade: 0.3,
                    area: ['80%', '85%'],
                    content: data
                });
            }
        });
    });
}

function findInfoAttachmentData(pageOffset) {
    pageNum = pageOffset;
    var dataJson = {
        pageSize: pageCount,
        offset: pageOffset,
        companyId: $("#hidCompanyId").val(),
        stime: $("#stime").val(),
        etime: $("#etime").val(),
        processType: $("#drop_search_process_templatetype").val(),
        processName: $("#txt_search_process_name").val(),
        attachmentName: $("#txt_search_attachment_name").val()
    };

    $.ajax({
        url: contextPath + "/company/findDocumentFileInfo",
        type:"post",
        data: JSON.stringify(dataJson),//将对象序列化成JSON字符串
        dataType:"json",
        contentType : 'application/json;charset=utf-8', //设置请求头信息
        success:function(data){
            if(null!=data && data.rows.length>0){
                loadInfoAttachmentData(data);
            }else{
                $("#vertical-timeline").html("");
                //layer.alert("暂无事项材料存档", {icon: 1, time: 3000});
            }

            initPagination(data.total, pageOffset);
        },
        error:function(e){
            $("#vertical-timeline").html("");
            layer.alert('获取信息失败', {icon: 1, time: 3000});
        }
    });
}

function loadInfoAttachmentData(infoAttachmentData){
    var html = "";
    $("#vertical-timeline").html("");
    for(var i=0;i<infoAttachmentData.rows.length;i++){
        var infoAttachmentDataRow = infoAttachmentData.rows[i];
        var tag = '';
        var tagCode = '';
        if(infoAttachmentDataRow.type==0){
            tag = '发行人事项';
        }else{
            tag = infoAttachmentDataRow.bondShortname;
            tagCode = infoAttachmentDataRow.bondCode;
        }
        var fileNum = null==infoAttachmentDataRow.documentAttachmentList ? 0 : infoAttachmentDataRow.documentAttachmentList.length;
        html += '<div class="vertical-timeline-block">';
        html += '   <div class="vertical-timeline-icon attachment-vertical-icon"></div>';
        html += '   <div class="vertical-timeline-content">';
        html += '       <div class="row">';
        html += '           <div class="col-sm-10">';
        html += '           <span class="attachment-vertical-content-date">' + timestampToDatetime(infoAttachmentDataRow.updateTime) + '</span>';
        html += '           <span class="attachment-vertical-content-name">' + tag + '</span>';
        html += '           <span class="attachment-vertical-content-code" style="background:#FFFFFF;">' + tagCode + '</span>';
        html += '           <input type="hidden" name="hidUploadId" value="' + infoAttachmentDataRow.id + '" />';
        html += '           <input type="hidden" name="hidUploadType" value="' + infoAttachmentDataRow.type + '" />';
        html += '           <input type="hidden" name="hidUploadCompanyName" value="' + infoAttachmentDataRow.companyName + '" />';
        html += '           <input type="hidden" name="hidUploadBondCode" value="' + infoAttachmentDataRow.bondCode + '" />';
        html += '           <input type="hidden" name="hidUploadBondName" value="' + infoAttachmentDataRow.bondShortname + '" />';
        html += '           <input type="hidden" name="hidUploadUserId" value="' + infoAttachmentDataRow.userId + '" />';
        html += '           &nbsp;&nbsp;<span class="attachment-vertical-content-name">上传</span>';

        if(infoAttachmentDataRow.isFiles==1 && infoAttachmentDataRow.status==1 && $("#hidRoleCode").val()=="1" && $("#hidUserId").val() == infoAttachmentDataRow.userId) {
            //自己待审核
            html += '&nbsp;&nbsp;<span style="color:#FF9F3F;">待确认</span>';
            html += '&nbsp;&nbsp;<span style="color:#0099FF;padding:5px;cursor:pointer;text-decoration: underline;" onclick="editUpload(this)">修改</span>';
            html += '&nbsp;&nbsp;<span style="color:#FF0000;padding:5px;cursor:pointer;text-decoration: underline;" onclick="deleteUpload(this)">删除</span>';
        }else if(infoAttachmentDataRow.isFiles==1 && infoAttachmentDataRow.status==1 && $("#hidRoleCode").val()=="0"){
            //督导人员审核
            html += '&nbsp;&nbsp;<span style="color:#0099FF;padding:5px;cursor:pointer;text-decoration: underline;" onclick="auditUpload(this)">通过</span>';
            html += '&nbsp;&nbsp;<span style="color:#FF0000;padding:5px;cursor:pointer;text-decoration: underline;" onclick="repellentUpload(this)">拒绝</span>';
        }else if(infoAttachmentDataRow.isFiles==1 && infoAttachmentDataRow.status==0 && $("#hidRoleCode").val()=="0"){
            //督导人员已审核
            html += '&nbsp;&nbsp;<span style="color:#0099FF;padding:5px;cursor:pointer;text-decoration: underline;" onclick="editUpload(this)">修改</span>';
            html += '&nbsp;&nbsp;<span style="color:#FF0000;padding:5px;cursor:pointer;text-decoration: underline;" onclick="deleteUpload(this)">删除</span>';
        }

        html += '</div>';
        html += '<div class="col-sm-2">';
        if (infoAttachmentDataRow.roleCode == "manager_handle") {
            html += '项目人员：' + infoAttachmentDataRow.userName;
        } else if (infoAttachmentDataRow.roleCode == "inspector_check") {
            html += '督导人员：' + infoAttachmentDataRow.userName;
        }
        html += '</div>';
        html += '</div>';

        html += '       <div name="divDescription" class="row attachment-vertical-content-description">' + (null==infoAttachmentDataRow.description?'':infoAttachmentDataRow.description) + '</div>';
        html += '       <div class="row tmp-bgcolor-y">';
        html += '           <span class="fa fa-paperclip tmp-font-20 tmp-padding-5"></span>';
        html += '           <span class="tmp-padding-5">共<span id="spanFileNum">' + fileNum + '</span>个文件</span>';
        html += '           <span class="tmp-link tmp-padding-5" onclick="downLoadTempFiles(this)">全部下载</span>';
        html += '       </div>';
        html += '       <div class="row" class="tmpFiles">';
        if(fileNum>0){

            for(var j=0;j<infoAttachmentDataRow.documentAttachmentList.length;j++){
                html += '<div class="tmp-file" style="width:100%;text-align:left;">';
                html += '<input type="hidden" name="templateAttachment.id" value="' + infoAttachmentDataRow.documentAttachmentList[j].id + '" />';
                html += '<input type="hidden" name="templateAttachment.type" value="' + infoAttachmentDataRow.documentAttachmentList[j].type + '" />';
                html += '<input type="hidden" name="templateAttachment.name" value="' + infoAttachmentDataRow.documentAttachmentList[j].name + '" />';
                html += '<input type="hidden" name="templateAttachment.url" value="' + infoAttachmentDataRow.documentAttachmentList[j].url + '"/>';

                html += '<div><div title="' + infoAttachmentDataRow.documentAttachmentList[j].name + '" style="float:left;max-width:90%;padding:5px 0;display:block;text-overflow:ellipsis;white-space:nowrap;overflow:hidden;">' + infoAttachmentDataRow.documentAttachmentList[j].name +'</div>';
                if(infoAttachmentDataRow.isFiles==1) {
                    html += '<div class="tmp-file-down" style="padding:5px;" onclick="downFile(this)">下载</div>';
                    if(infoAttachmentDataRow.status==1 && $("#hidRoleCode").val()=="1" && $("#hidUserId").val() == infoAttachmentDataRow.userId) {
                        //自己待审核
                        html += '<div class="tmp-file-remove" style="padding:5px;" onclick="fileRemove(this)">删除</div>';
                    }else if(infoAttachmentDataRow.status==0 && $("#hidRoleCode").val()=="0"){
                        //督导人员已审核
                        html += '<div class="tmp-file-remove" style="padding:5px;" onclick="fileRemove(this)">删除</div>';
                    }
                }else{
                    html += '<div class="tmp-file-down" style="padding:5px;" onclick="downFile(this)">下载</div>';
                }

                html += '</div></div>';
            }
        }

        html += '       </div>';
        html += '   </div>';
        html += '</div>';
    }
    $("#vertical-timeline").append(html);
}

//文件下载
function downFile(objFile) {
    var form = document.createElement('form');
    form.setAttribute('method', 'post');
    form.setAttribute('action', contextPath + "/file/download");

    var nameValue = $(objFile).parent().parent().find("input[name='templateAttachment.name']").val();
    var nameField = document.createElement("input");
    nameField.setAttribute("type", "hidden");
    nameField.setAttribute("name", "name");
    nameField.setAttribute("value",nameValue);

    var urlValue = $(objFile).parent().parent().find("input[name='templateAttachment.url']").val();
    var urlField = document.createElement("input");
    urlField.setAttribute("type", "hidden");
    urlField.setAttribute("name", "url");
    urlField.setAttribute("value",urlValue);
    form.appendChild(nameField);
    form.appendChild(urlField);
    objFile.appendChild(form);
    form.submit();
    if (form.remove) {
        form.remove();
    } else {
        form.removeNode();
    }
}

//文件审核通过
function auditUpload(objFile){
    var uploadId = $(objFile).parent().parent().find("input[name='hidUploadId']").val();
    $.ajax({
        url: contextPath + "/upload/auditUpload",
        type: "post",
        data: JSON.stringify(uploadId),
        dataType: "json",
        contentType : 'application/json;charset=utf-8', //设置请求头信息
        success: function (data) {
            if (data.code == 0) {
                layer.alert('审核通过', {icon: 1, time: 3000});
                findInfoAttachmentData(pageNum);
            } else {
                layer.alert(data.message, {icon: 1, time: 3000});
            }
        },
        error: function (e) {
            layer.alert('审核失败', {icon: 1, time: 3000});
        }
    });
}
//文件拒绝
function repellentUpload(objFile){
    var uploadId = $(objFile).parent().parent().find("input[name='hidUploadId']").val();
    var uploadType = $(objFile).parent().parent().find("input[name='hidUploadType']").val();
    var uploadCompanyName = $(objFile).parent().parent().find("input[name='hidUploadCompanyName']").val();
    var uploadBondCode = $(objFile).parent().parent().find("input[name='hidUploadBondCode']").val();
    var uploadBondName = $(objFile).parent().parent().find("input[name='hidUploadBondName']").val();
    var uploadDescription = $(objFile).parent().parent().parent().find("div[name='divDescription']").html();
    var uploadUserId = $(objFile).parent().parent().find("input[name='hidUploadUserId']").val();
    var upload = {};
    upload.id = uploadId;
    upload.type = uploadType;
    upload.companyName = uploadCompanyName;
    upload.bondCode = uploadBondCode;
    upload.bondName = uploadBondName;
    upload.description = uploadDescription;
    upload.userId = uploadUserId;
    $.ajax({
        url: contextPath + "/upload/repellentUpload",
        type: "post",
        data: JSON.stringify(upload),
        dataType: "json",
        contentType : 'application/json;charset=utf-8', //设置请求头信息
        success: function (data) {
            if (data.code == 0) {
                layer.alert('拒绝成功', {icon: 1, time: 3000});
                findInfoAttachmentData(pageNum);
            } else {
                layer.alert(data.message, {icon: 1, time: 3000});
            }
        },
        error: function (e) {
            layer.alert('拒绝失败', {icon: 1, time: 3000});
        }
    });
}


//文件删除
function fileRemove(objFile) {
    var attachmentId = $(objFile).parent().parent().find("input[name='templateAttachment.id']").val();

    layer.confirm(
        "是否要删除事项材料？",
        {icon: 3, title:'事项材料删除'},
        function(index) {

            $.ajax({
                url: contextPath + "/upload/deleteUploadAttachmentById",
                type: "post",
                data: JSON.stringify(attachmentId),
                dataType: "json",
                contentType : 'application/json;charset=utf-8', //设置请求头信息
                success: function (data) {
                    if (data.code == 0) {
                        var objParent = $(objFile).parent().parent().parent();
                        $(objFile).parent().parent().remove();
                        var fileNum = $(objParent).find(".tmp-file").size();
                        $(objParent).parent().find("#spanFileNum").html(fileNum);
                    } else {
                        layer.alert(data.message, {icon: 1, time: 3000});
                    }
                },
                error: function (e) {
                    layer.alert('删除失败', {icon: 1, time: 3000});
                }
            });

            layer.close(index);
        }
    );


}

function downLoadTempFiles(obj){
    var ary =  new Array();
    $($(obj).parent().next().find(".tmp-file")).each(function(index, item){
        var attachment = {};
        attachment.type = $(item).find("input[name='templateAttachment.type']").val();
        attachment.name = $(item).find("input[name='templateAttachment.name']").val();
        attachment.url = $(item).find("input[name='templateAttachment.url']").val();
        ary.push(attachment);
    });
    if(ary.length<1){
        layer.alert('没有文件可以下载', {icon: 1, time: 3000});
        return;
    }
    var form = document.createElement('form');
    form.setAttribute('method', 'post');
    form.setAttribute('action', contextPath + "/file/downloadZip");
    for(var i=0;i<ary.length;i++){
        var nameValue = ary[i].name;
        var nameField = document.createElement("input");
        nameField.setAttribute("type", "hidden");
        nameField.setAttribute("name", "attachments["+i+"].name");
        nameField.setAttribute("value",nameValue);

        var urlValue = ary[i].url;
        var urlField = document.createElement("input");
        urlField.setAttribute("type", "hidden");
        urlField.setAttribute("name", "attachments["+i+"].url");
        urlField.setAttribute("value",urlValue);
        form.appendChild(nameField);
        form.appendChild(urlField);
    }
    $(".ibox-content").append(form);
    form.submit();
    form.remove();

}

function editUpload(objFile) {
    var editId = $(objFile).parent().find("input[name='hidUploadId']").val();
    $.ajax({
        type: 'GET',
        url: contextPath + "/upload/edit/"+editId,
        cache: false,
        success: function (data) {
            console.log("success");
            uploadPage = layer.open({
                type: 1,
                title: '上传资料',
                maxmin: false,
                resize: false,
                shade: 0.3,
                area: ['80%', '85%'],
                content: data
            });
        }
    });
}

function deleteUpload(objFile) {
    var editId = $(objFile).parent().find("input[name='hidUploadId']").val();

    layer.confirm(
        "是否要删除事项材料？",
        {icon: 3, title:'事项材料删除'},
        function(index) {

            $.ajax({
                url: contextPath + "/upload/deleteUpload",
                type: "post",
                data: JSON.stringify(editId),
                dataType: "json",
                contentType : 'application/json;charset=utf-8', //设置请求头信息
                success: function (data) {
                    if (data.code == 0) {
                        $(objFile).parent().parent().parent().remove();
                        layer.alert('已删除', {icon: 1, time: 3000});
                    } else {
                        layer.alert(data.message, {icon: 1, time: 3000});
                    }
                },
                error: function (e) {
                    layer.alert('删除失败', {icon: 1, time: 3000});
                }
            });

            layer.close(index);
        }
    );

}



//分页
//totalCount总条数
//pageOffset每页显示数
function initPagination(totalCount, pageOffset) {
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
    pageHTML += '<li id="li-first" class="first" onclick="eventPagination(this)"><a href="#">&laquo;</a></li>';
    pageHTML += '<li id="li-prev" class="previous" onclick="eventPagination(this)"><a href="#">&lt;</a></li>';

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
            pageHTML += '<li id="li-' + i + '" class="active" onclick="eventPagination(this)"><a href="#">' + i + '</a></li>';
        }else{
            pageHTML += '<li id="li-' + i + '" onclick="eventPagination(this)"><a href="#">' + i + '</a></li>';
        }
    }

    pageHTML += '<li id="li-next" class="next" onclick="eventPagination(this)"><a href="#">&gt;</a></li>';
    pageHTML += '<li id="li-last" class="last" onclick="eventPagination(this)"><a href="#">&raquo;</a></li>';
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
    pageCountHTML += '<select id="dropPageCount" class="form-control" onchange="setPageCount()">';
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

    $("#divPagination").remove();
    var paginationHTML = '';
    paginationHTML += '<div id="divPagination" class="col-sm-11">';
    paginationHTML += '     <input type="hidden" id="hidPageCount" value="' + pageCounts + '" />';
    paginationHTML += '     <div class="col-sm-4" style="margin:20px 0;">' + viewPage + '</div>';
    paginationHTML += pageCountHTML;
    paginationHTML += '     <div class="col-sm-5 text-right">' + pageHTML + '</div>';
    paginationHTML += '</div>';
    $("#pagePagination").append(paginationHTML);

}

function setPageCount(){
    pageCount = $("#dropPageCount").val();
    findInfoAttachmentData(0);
}

function eventPagination(obj) {
    var offset = 0;

    //分页前的页索引
    var oldIndex = $("#divPagination .active").attr("id").replace("li-","");
    //当前点击分页的索引
    var eventIndex = $(obj).attr("id").replace("li-","");
    //总页码数
    var total = $("#hidPageCount").val();

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

    findInfoAttachmentData(offset);
}