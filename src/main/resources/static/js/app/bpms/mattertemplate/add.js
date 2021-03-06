/**
 * Created by Dealing076 on 2018/2/23.
 */
//@ sourceURL=add.js
var tmpJson = {};
$(function () {
    //文本编辑器
    initSummernote();
    //颜色选择器
    initColorpicker();
    //日期
    initDate();
    //控件
    initAddControl();

});

//文本编辑器
function initSummernote(){
    $('#summernote').summernote({
        lang: 'zh-CN',
        height: 300,
        minHeight: 300,
        maxHeight:null,
        disableDragAndDrop: false,   //禁止拖放
        dialogsInBody: true,
        placeholder: '这里填写事项内容',
        // toolbar工具栏默认
        toolbar: [
            ['style', ['style', 'bold', 'italic', 'underline', 'clear']],
            ['fontname', ['fontname']],
            ['fontsize', ['fontsize']],
            ['color', ['color']],
            ['para', ['ul', 'ol', 'paragraph']],
            ['table', ['table']],
            ['insert', ['link']],
            ['history', ['undo', 'redo']]
        ],
        onImageUpload: function (files) {
            //sendFile(files);
        }
    });

    $(".note-editor>.btn-toolbar>div").each(function(index, item){
        $(item).find(":button").each(function(vindex,vitem){
            var title = $(vitem).attr("data-original-title");
            $(vitem).attr("title", title);
        });
    });
}

//选择图片时把图片上传到服务器再读取服务器指定的存储位置显示在富文本区域内
function sendFile(files) {
    var formData = new FormData();
    formData.append('file',files[0]);

    var loadIndex = layer.load(2, {
        shade: [0.3,'#cacaca'],
        area: ['50px', '50px'],
        offset:['50%','50%'],
    });

    $.ajax({
        url: contextPath + "/file/uploadFile",
        type: "post",
        data: formData,
        processData: false,
        contentType: false,
        success: function (data) {
            if (data.code == 0) {
                $('#summernote').summernote('insertImage', data.url);
            } else {
                layer.alert(data.message, {icon: 1, time: 3000});
            }
            layer.close(loadIndex);
        },
        error: function (e) {
            layer.close(loadIndex);
            layer.alert('上传失败', {icon: 1, time: 3000});
        }
    });
}

//颜色选择器
function initColorpicker(){
    // $("#color").val("#FF0011");
    // $('#divColorpicker').colorpicker({
    //     color: '#FF0011'
    // }).on("changeColor ", function(e) {
    //     $("#showColor").css("background-color",e.color.toHex());
    // });

    $("#color").val("#FF0011");
    $('#showColor').css("background-color", "#FF0011");
    $("#color").bigColorpicker(function(el, color){
        //$(el).css("background-color", color);
        $("#color").val(color);
        $("#showColor").css("background-color", color);
    });
}

//初始日期
function initDate() {

    $('.c-disable').datetimepicker({
        format: 'mm月dd日',
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

//日期删除
function removeTime(obj){
    if($("div[name='minus']").size()>1){
        $(obj).parent().remove();
        setMailCompleteDate();
    }
}

//初始控件
function initAddControl(){

    //模板材料个数
    $("#spanFileNum").text($("#tmpFiles .tmp-file").size());

    $(".val-num").keyup(function(){
        this.value=this.value.replace(/[^\d]/g,'');
    });

    //事项分类change
    $("#type").change(function(){
        if($("#type").val()==0){

            $("#radioF").attr("checked","checked");
            $("#radioF").prop("checked",true);
            $("#templateruleType").val(0);

            $("#divRadioT").addClass("hide");

            $("#divF").removeClass("hide");
            $("#divT").addClass("hide");
        }else if($("#type").val()==1){
            $("#divRadioT").removeClass("hide");
        }
    });

    //提醒规则change
    $("input[name='radioTime']").change(function () {
        if($(this).val()==0){
            $("#divF").removeClass("hide");
            $("#divT").addClass("hide");

            setMailCompleteDate();
        } else if($(this).val()==1){
            $("#divF").addClass("hide");
            $("#divT").removeClass("hide");

            $("#divMailBeforeDay").text("工作日开始，每隔");
        }
        $("#templateruleType").val($(this).val());
    });

    //添加时间
    $("#divAddTime").click(function(){
        $("#divFTime").append("<div class='divTime'>" + $(".divTime").html() + "</div>");
        initDate();
    });

    //模板材料添加
    $("#addTmpFile").click(function () {
        $("#uploadTmpFile").click();
    });

    //全部下载
    $("#fileDownloadAll").click(function () {
        var ary =  new Array();
        $("#tmpFiles .tmp-file").each(function(index, item){
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
    });

    //全部删除
    $("#fileRemoveAll").click(function () {
        layer.confirm(
            "是否要删除所有事项材料？",
            {icon: 3, title:'事项材料删除'},
            function(index) {
                $("#tmpFiles").html("");
                layer.close(index);
            }
        );
    });

    //保存
    $("#addSave").click(function(){
        if(addValidate()){

            var loadIndex = layer.load(2, {
                shade: [0.3,'#cacaca'],
                area: ['50px', '50px'],
                offset:['50%','50%'],
            });

            $.ajax({
                url: contextPath + "/mattertemplate/addSave",
                type:"post",
                data: JSON.stringify(tmpJson),//将对象序列化成JSON字符串
                dataType:"json",
                contentType : 'application/json;charset=utf-8', //设置请求头信息
                success:function(data){
                    layer.close(loadIndex);
                    if(data.code==0){
                        layer.alert("添加成功", {icon: 1, time: 3000});
                        layer.close(pageIndex);
                        refreshMattertemplate();
                    }else{
                        layer.alert(data.message, {icon: 1, time: 3000});
                    }
                },
                error:function(e){
                    layer.close(loadIndex);
                    layer.alert('添加失败', {icon: 1, time: 3000});
                }
            });
        }
    });

    $("#mailBeforeDay").blur(function(){
        if($("input:radio[name='radioTime']:checked").val()==0){
            if($("#mailBeforeDay").val()==""){
                $("#divMailBeforeDay").text("工作日开始，每隔");
            }else{
                setMailCompleteDate();
            }
        }
    });
}

function setMailCompleteDate(){
    if($("#mailBeforeDay").val()!=""){
        var aryRuleDate = new Array();
        $(".divTime").each(function(index, item){
            var ruleDateJson = {};
            var stime = $(item).find("input[name='remindDate']").val();
            var etime = $(item).find("input[name='completeDate']").val();
            if(stime!=""){
                stime = (new Date().getFullYear()) + '-' + stime.replace("月","-").replace("日","");
            }
            if(etime!=""){
                etime = (new Date().getFullYear()) + '-' + etime.replace("月","-").replace("日","");
            }
            if(etime!=""){
                ruleDateJson.beforeDay = $("#mailBeforeDay").val();
                ruleDateJson.remindDate = stime;
                ruleDateJson.completeDate = etime;
                aryRuleDate.push(ruleDateJson);
            }
        });
        if(aryRuleDate.length>0){
            $.ajax({
                url: contextPath + "/custommatter/findMailCompleteDate",
                type:"post",
                data: JSON.stringify(aryRuleDate),//将对象序列化成JSON字符串
                dataType:"text",
                contentType : 'application/json;charset=utf-8', //设置请求头信息
                success:function(data){
                    if(data!=""){
                        $("#divMailBeforeDay").text("（" + data + "）工作日开始，每隔");
                    }else{
                        $("#divMailBeforeDay").text("工作日开始，每隔");
                    }
                },
                error:function(e){
                    //layer.alert('获取日期失败', {icon: 1, time: 3000});
                    $("#divMailBeforeDay").text("工作日开始，每隔");
                }
            });
        }else{
            $("#divMailBeforeDay").text("工作日开始，每隔");
        }
    }else{
        $("#divMailBeforeDay").text("工作日开始，每隔");
    }

}

//文件上传
function setTmpFile(objFile) {
    // var tmpFiles = $("#uploadTmpFile")[0].files;
    // if(tmpFiles.length<=0){
    //     layer.alert("上传失败：请选择要上传的文件", {icon: 1, time: 3000});
    //     return;
    // }

    if(objFile.files[0]==null){
        return;
    }
    var loadIndex = layer.load(2, {
        shade: [0.3,'#cacaca'],
        area: ['50px', '50px'],
        offset:['50%','50%'],
    });

    var isFlag = false;
    $("#tmpFiles .tmp-file").each(function(index, item){
        if(isFlag===true){
            return false;
        }
        var attachment = {};
        attachment.type = $(item).find("input[name='templateAttachment.type']").val();
        attachment.name = $(item).find("input[name='templateAttachment.name']").val();
        for(var i=0;i<objFile.files.length;i++){
            if(objFile.files[i].name===attachment.name){
                isFlag = true;
                break;
            }
        }
    });
    if(isFlag===true){
        layer.alert("上传失败，上传材料的文件名已存在！", {icon: 1, time: 3000});
        $("#uploadTmpFile").val("");
        layer.close(loadIndex);
        return false;
    }

    var form = new FormData(document.getElementById("mattertemplateForm"));

    $.ajax({
        url: contextPath + "/file/upload",
        type: "post",
        data: form,
        processData: false,
        contentType: false,
        success: function (data) {

            if (data.code == 0) {
                var files = data.data;
                for(var i=0;i<files.length;i++){
                    var fileIcon = getTmpFileIcon(files[i].type);
                    var tmpFileHtml = '<div class="tmp-file" style="width:100%;text-align:left;">';
                    tmpFileHtml += '<input type="hidden" name="templateAttachment.type" value="' + fileIcon + '"/>';
                    tmpFileHtml += '<input type="hidden" name="templateAttachment.name" value="' + files[i].name + '"/>';
                    tmpFileHtml += '<input type="hidden" name="templateAttachment.url" value="' + files[i].url + '"/>';

                    // tmpFileHtml += '<div class="tmp-file-' + fileIcon + '" title="' + files[i].name + '"></div>';
                    // tmpFileHtml += '<div class="tmp-file-content tmp-font-12" title="' + files[i].name + '">' + files[i].name + '</div>';
                    // tmpFileHtml += '<div class="tmp-file-down" onclick="downFile(this)">下载</div>';
                    // tmpFileHtml += '<div class="tmp-file-remove" onclick="fileRemove(this)">删除</div>';
                    // tmpFileHtml += '</div>';

                    tmpFileHtml += '<div title="' + files[i].name + '" style="float:left;max-width:85%;padding:5px 0;display:block;text-overflow:ellipsis;white-space:nowrap;overflow:hidden;">' + files[i].name +'</div>';
                    tmpFileHtml += '<div class="tmp-file-down" style="padding:5px;" onclick="downFile(this)">下载</div>';
                    tmpFileHtml += '<div class="tmp-file-remove" style="padding:5px;" onclick="fileRemove(this)">删除</div>';

                    $("#tmpFiles").append(tmpFileHtml);
                }
                $("#spanFileNum").text($("#tmpFiles .tmp-file").size());
                layer.alert("上传成功", {icon: 1, time: 3000});
            } else {
                layer.alert(data.message, {icon: 1, time: 3000});
            }
            $("#uploadTmpFile").val("");
            layer.close(loadIndex);
        },
        error: function (e) {
            $("#uploadTmpFile").val("");
            layer.close(loadIndex);
            layer.alert('上传失败', {icon: 1, time: 3000});
        }
    });
}

//文件下载
function downFile(objFile) {
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

//文件删除
function fileRemove(fileObj) {
    layer.confirm(
        "是否要删除该 事项材料？",
        {icon: 3, title:'事项材料删除'},
        function(index) {
            $(fileObj).parent().remove();
            $("#spanFileNum").text($("#tmpFiles .tmp-file").size());
            layer.close(index);
        }
    );
}

//验证数据
function addValidate(){
    var isFlag = true;

    var aryRule = new Array();  //规则
    var aryFile = new Array();  //附件

    if(trim($("#name").val())==""){
        layer.alert("保存失败：请输入事项名称", {icon: 1, time: 3000});
        isFlag = false;
        return false;
    }
    var radioTime = $("input:radio[name='radioTime']:checked").val();
    if(radioTime==0){
        $(".divTime").each(function(index, item){
            var stime = trim($(item).find("input[name='remindDate']").val());
            if(stime==""){
                layer.alert("保存失败：提醒时间不能为空", {icon: 1, time: 3000});
                isFlag = false;
                return false;
            }
            var etime = trim($(item).find("input[name='completeDate']").val());
            if(etime==""){
                layer.alert("保存失败：需完成时间不能为空", {icon: 1, time: 3000});
                isFlag = false;
                return false;
            }
            var ruleJson = {};
            ruleJson.id = 0;
            ruleJson.templateId = 0;
            ruleJson.type = radioTime;
            ruleJson.remindDate = (new Date().getFullYear()) + '-' + stime.replace("月","-").replace("日","");
            ruleJson.completeDate = (new Date().getFullYear()) + '-' + etime.replace("月","-").replace("日","");
            aryRule.push(ruleJson);
        });
    }else if(radioTime==1){
        if(isPositiveInteger($("#beforeDay").val())==false){
            layer.alert("保存失败：提前天数不能为空且为数字", {icon: 1, time: 3000});
            isFlag = false;
            return false;
        }
        if(isPositiveInteger($("#completeBeforeDay").val())==false){
            layer.alert("保存失败：提前天数不能为空且为数字", {icon: 1, time: 3000});
            isFlag = false;
            return false;
        }
        var ruleJson = {};
        ruleJson.id = 0;
        ruleJson.templateId = 0;
        ruleJson.type = radioTime;
        ruleJson.beforeDay = $("#beforeDay").val();
        ruleJson.completeBeforeDay = $("#completeBeforeDay").val();
        aryRule.push(ruleJson);
    }else{
        alert("radioTime");
        layer.alert("保存失败：提醒规则不能为空", {icon: 1, time: 3000});
        isFlag = false;
        return false;
    }
    if(isFlag==false){
        return false;
    }

    if($("input[name='cbxMailUser']:checkbox:checked").size()<=0){
        layer.alert("保存失败：请选择触发通知", {icon: 1, time: 3000});
        isFlag = false;
        return false;
    }

    if(isPositiveInteger($("#mailBeforeDay").val())==false){
        layer.alert("保存失败：提醒频率不能为空且为数字", {icon: 1, time: 3000});
        isFlag = false;
        return false;
    }
    if(isPositiveInteger($("#mailFrequency").val())==false){
        layer.alert("保存失败：提醒频率不能为空且为数字", {icon: 1, time: 3000});
        isFlag = false;
        return false;
    }

    // if($("#tmpFiles .tmp-file").size()<=0){
    //     layer.alert("保存失败：请添加模板材料", {icon: 1, time: 3000});
    //     isFlag = false;
    //     return false;
    // }else{
        $("#tmpFiles .tmp-file").each(function(index, item){
            var fileJson = {};
            fileJson.id = 0;
            fileJson.templateId = 0;
            fileJson.type = $(item).find("input[name='templateAttachment.type']").val();
            fileJson.name = $(item).find("input[name='templateAttachment.name']").val();
            fileJson.url = $(item).find("input[name='templateAttachment.url']").val();
            aryFile.push(fileJson);
        });
    //}

    if(trim($("#summernote").code())=="" || trim($("#summernote").code())=="<p><br></p>") {
        layer.alert("保存失败：事项内容不能为空", {icon: 1, time: 3000});
        isFlag = false;
        return false;
    }

    //督导确认
    var radioConfirm = $("input:radio[name='radioConfirm']:checked").val();
    //触发通知
    var mailUser = 0;
    $("input[name='cbxMailUser']:checkbox:checked").each(function(index, item){
        mailUser = mailUser | parseInt($(item).val());
    });



    tmpJson.id = 0;
    tmpJson.type = $("#type").val();
    tmpJson.name = trim($("#name").val());
    tmpJson.shortname = trim($("#shortname").val());
    tmpJson.description = trim($("#description").val());
    tmpJson.confirm = radioConfirm;
    tmpJson.mailUser = mailUser;
    tmpJson.mailBeforeDay = $("#mailBeforeDay").val();
    tmpJson.mailFrequency = $("#mailFrequency").val();
    tmpJson.color = $("#color").val();
    tmpJson.content = $("#summernote").code();
    tmpJson.templateRuleList = aryRule;
    tmpJson.templateAttachmentList = aryFile;
    tmpJson.status = 0;
    tmpJson.autoRelate = $("#autoRelate").val();

    return isFlag;
}