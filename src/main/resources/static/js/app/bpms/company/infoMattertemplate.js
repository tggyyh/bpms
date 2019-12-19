/**
 * Created by Dealing076 on 2018/3/9.
 */
//@ sourceURL=infoMattertemplate.js
$(function () {

    initInfoMattertemplateButtons();
    initMailUser();
});

function initInfoMattertemplateButtons() {

    $(".link-menu-text").click(function(){
        $(".link-menu-text").removeClass("link-menu-active");
        $(this).addClass("link-menu-active");
        var companyId = $(this).attr("id").split("-");
        var tempType = companyId[0];
        var templateId = companyId[1];
        var reqURL = '';
        if(tempType=='company'){
            reqURL = contextPath + '/mattertemplate/findByTemplateId/' + templateId;
        }else if(tempType=='customcompany'){
            reqURL = contextPath + '/custommatter/findById/' + templateId;
        }
        $.ajax({
            url: reqURL,
            type: 'get',
            dataType: 'json',
            cache: false,
            success: function (data) {
                loadTemplateValue(data, tempType);
            },
            error: function (e) {
                layer.alert('加载数据失败', {icon: 1, time: 3000});
            }
        });
    });

    if($("div[name='divCompanyMatter']").size()>0){
        $("div[name='divCompanyMatter']").first().click();
    }else if($("div[name='divCustomBond']").size()>0){
        $("div[name='divCustomBond']").first().click();
    }

    $("#divMenuCompany").click(function(){
        if($("#spanCompanyMenuIcon").hasClass("glyphicon-triangle-bottom")){
            $("div[name='divCompanyMatter']").addClass("hide");
            $("#spanCompanyMenuIcon").removeClass("glyphicon-triangle-bottom");
            $("#spanCompanyMenuIcon").addClass("glyphicon-triangle-top");
        }else if($("#spanCompanyMenuIcon").hasClass("glyphicon-triangle-top")){
            $("div[name='divCompanyMatter']").removeClass("hide");
            $("#spanCompanyMenuIcon").addClass("glyphicon-triangle-bottom");
            $("#spanCompanyMenuIcon").removeClass("glyphicon-triangle-top");
        }
    });

    $("#divMenuCustomBond").click(function(){
        if($("#spanCustomBondMenuIcon").hasClass("glyphicon-triangle-bottom")){
            $("div[name='divCustomBond']").addClass("hide");
            $("#spanCustomBondMenuIcon").removeClass("glyphicon-triangle-bottom");
            $("#spanCustomBondMenuIcon").addClass("glyphicon-triangle-top");
        }else if($("#spanCustomBondMenuIcon").hasClass("glyphicon-triangle-top")){
            $("div[name='divCustomBond']").removeClass("hide");
            $("#spanCustomBondMenuIcon").addClass("glyphicon-triangle-bottom");
            $("#spanCustomBondMenuIcon").removeClass("glyphicon-triangle-top");
        }
    });

}

function loadTemplateValue(matterTemplate, tempType){
    clearTemplateValue();

    var templateHTML = '<div id="divIcon" class="tmp-icon" data-toggle="tooltip" title="' + matterTemplate.shortname + '" style="background-color:' + matterTemplate.color + ';border-color:' + matterTemplate.color + '">' + matterTemplate.shortname + '</div>';
    $("#tdTemplateIcon").html(templateHTML);

    templateHTML = '<div class="link-item-tlte" data-toggle="tooltip" title="' + matterTemplate.name + '">' + matterTemplate.name + '</div>';
    templateHTML += '<div class="link-item-description" data-toggle="tooltip" title="' + matterTemplate.description + '">' + matterTemplate.description + '</div>';
    $("#tdTemplateName").html(templateHTML);

    var autoRelate = '';
    var templateRuleList;
    if(tempType=='company'){
        templateRuleList = matterTemplate.templateRuleList;

        autoRelate = matterTemplate.autoRelate==1?'是':'否';
    }else{
        templateRuleList = matterTemplate.customRuleList;

        autoRelate = '否';
    }

    $("#tdAutoRelate").html(autoRelate);

    if(templateRuleList!=null && templateRuleList.length>0){
        templateHTML = '<div>' + (templateRuleList[0].type==0?'按指定日期触发':'按T-N触发') + '</div>';
        $("#tdTemplateRule").html(templateHTML);

        templateHTML = "";
        for(var i=0;i<templateRuleList.length;i++){

            templateHTML += '<tr name="trTemplateRuleDate">';
            templateHTML += '<td></td>';
            templateHTML += '<td class="tmp-padding-5">';
            if(templateRuleList[i].type==0){
                templateHTML += '提醒时间：<span>' + (new Date(templateRuleList[i].remindDate)).Format("MM月dd日") + '</span>，';
                templateHTML += '需要完成时间：<span>' + (new Date(templateRuleList[i].completeDate)).Format("MM月dd日") + '</span>';
            }else{
                templateHTML += '提醒时间：付息日提前 ' + templateRuleList[i].beforeDay + ' 工作日，';
                templateHTML += '需要完成时间：付息日提前 ' + templateRuleList[i].completeBeforeDay + ' 工作日';
            }
            templateHTML += '</td>';
            templateHTML += '</tr>';
        }
        $("#trRule").after(templateHTML);
    }


    templateHTML = '<input type="radio" name="confirm" checked="checked"/>';
    templateHTML += '<span>' + (matterTemplate.confirm==0?"不需要":"需要") + '</span>';
    $("#tdTemplateConfirm").append(templateHTML);

    $("#hidMailUser").val(matterTemplate.mailUser);
    initMailUser();

    templateHTML = '需完成时间提前<span class="link-item-mailFrequency">' + matterTemplate.mailBeforeDay + '</span>（' + (new Date(matterTemplate.mailBeforeDate)).Format("yyyy-MM-dd") + '）工作日开始，每隔';
    templateHTML += '<span class="link-item-mailFrequency">' + matterTemplate.mailFrequency + '</span>';
    templateHTML += '工作日&nbsp;&nbsp;项目负责人仍未提交处理，将再次邮件提醒项目负责人';
    $("#tdTemplateFrequency").html(templateHTML);

    var templateAttachmentList;
    if(tempType=='company'){
        templateAttachmentList = matterTemplate.templateAttachmentList;
    }else{
        templateAttachmentList = matterTemplate.customAttachmentList;
    }

    templateHTML = '<div class="tmp-bgcolor-y">';
    templateHTML += '<span class="fa fa-paperclip tmp-font-20 tmp-padding-5"></span>';
    templateHTML += '<span class="tmp-padding-5">共<span id="spanFileNum">' + (null==templateAttachmentList?0:templateAttachmentList.length) + '</span>个文件</span>';
    templateHTML += '<span id="fileDownloadAll" class="tmp-link tmp-padding-5" onclick="downloadAllFiles()">全部下载</span>';
    templateHTML += '</div>';
    $("#tdTemplateFileTile").html(templateHTML);

    if(null!=templateAttachmentList){
        for(var i=0;i<templateAttachmentList.length;i++){
            templateHTML = '<div class="tmp-file" style="width:100%;text-align:left;">';
            templateHTML += '<input type="hidden" name="templateAttachment.type" value="' + templateAttachmentList[i].type + '" />';
            templateHTML += '<input type="hidden" name="templateAttachment.name" value="' + templateAttachmentList[i].name + '" />';
            templateHTML += '<input type="hidden" name="templateAttachment.url" value="' + templateAttachmentList[i].url + '" />';
            // if(templateAttachmentList[i].type=="img"){
            //     templateHTML += '<div><img src="' + templateAttachmentList[i].url + '" class="tmp-file-img" /></div>';
            // }else{
            //     templateHTML += '<div class="tmp-file-' + templateAttachmentList[i].type + '"></div>';
            // }
            // templateHTML += '<div class="tmp-file-' + templateAttachmentList[i].type + '" title="' + (null==templateAttachmentList[i].name?'':templateAttachmentList[i].name) + '"></div>';
            // templateHTML += '<div class="tmp-file-content tmp-font-12" title="' + (null==templateAttachmentList[i].name?'':templateAttachmentList[i].name) + '">' + (null==templateAttachmentList[i].name?'':templateAttachmentList[i].name) + '</div>';
            // templateHTML += '<div class="tmp-file-down" style="padding:0 18px;" onclick="infoTemplateDownFile(this)">下载</div>';
            // templateHTML += '</div>';

            templateHTML += '<div title="' + templateAttachmentList[i].name + '" style="float:left;max-width:90%;padding:5px 0;display:block;text-overflow:ellipsis;white-space:nowrap;overflow:hidden;">' + templateAttachmentList[i].name +'</div>';
            templateHTML += '<div class="tmp-file-down" style="padding:5px;" onclick="downFile(this)">下载</div>';
            $("#divTemplateFiles").append(templateHTML);
        }
    }

    $("#divTemplateContent").html(matterTemplate.content);
}

function clearTemplateValue(){
    $("#tdTemplateIcon").html("");
    $("#tdTemplateName").html("");
    $("#tdTemplateRule").html("");
    $("tr[name='trTemplateRuleDate']").remove();
    $("#tdTemplateConfirm").html("");
    $("#divMailUser").html("");
    $("#tdTemplateFrequency").html("");
    $("#tdTemplateFileTile").html("");
    $("#divTemplateFiles").html("");
    $("#divTemplateContent").html("");
    $("#tdAutoRelate").html("");
}

//触发通知
function initMailUser(){
    var mailUser = $("#hidMailUser").val();
    if(!mailUser || typeof(mailUser)==='undefined' || mailUser==''){
        return '';
    }
    var aryMailUser = new Array();
    if((parseInt(mailUser) & 1) > 0){
        aryMailUser.push("发行人对接人");
    }
    if((parseInt(mailUser) & 2) > 0){
        aryMailUser.push("项目负责人");
    }
    if((parseInt(mailUser) & 4) > 0){
        aryMailUser.push("督导人员");
    }
    $("#divMailUser").html(aryMailUser.join("、"));
}

//文件下载
function infoTemplateDownFile(objFile) {
    var form = document.createElement('form');
    form.setAttribute('method', 'post');
    form.setAttribute('action', contextPath + "/file/download");

    var nameValue = $(objFile).parent().find("input[name='templateAttachment.name']").val();
    var nameField = document.createElement("input");
    nameField.setAttribute("type", "hidden");
    nameField.setAttribute("name", "name");
    nameField.setAttribute("value",nameValue);

    var urlValue = $(objFile).parent().find("input[name='templateAttachment.url']").val();
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

function downloadAllFiles(){
    var ary =  new Array();
    $("#divTemplateFiles .tmp-file").each(function(index, item){
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
