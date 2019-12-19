/**
 * Created by Dealing076 on 2018/3/19.
 */
//@ sourceURL=edit.js
var tmpJson = {};
$(function () {
    //文本编辑器
    initSummernote();
    //颜色选择器
    initColorpicker();
    //日期
    initDate();
    //触发通知
    initMailUser();
    //控件
    initAddControl();

    //initSubMatter();

    //initMialBeoreDay();
});

//文本编辑器
function initSummernote(){
    $('.summernote').summernote({
        lang: 'zh-CN',
        height: 150,
        minHeight: 300,
        maxHeight:null,
        disableDragAndDrop: false,   //禁止拖放
        dialogsInBody: true,
        placeholder: '这里填写事项内容',
        backColor: '#FFFFFF',
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

    $(".note-editor").css("background","#FFFFFF");

    $(".note-editor>.btn-toolbar>div").each(function(index, item){
        $(item).find(":button").each(function(vindex,vitem){
            var title = $(vitem).attr("data-original-title");
            $(vitem).attr("title", title);
        });
    });

    $(".summernote").each(function(index, item){
        var vindex = $(item).attr("id").replace("summernote-","");
        $(item).code($("#hidSummernote-"+vindex).val());
    });
}

//颜色选择器
function initColorpicker(){
    $("#showColor").css("background-color", $("#color").val());
    $("#color").bigColorpicker(function(el, color){
        $("#color").val(color);
        $("#showColor").css("background-color", color);
    });

    // $('#divColorpicker').colorpicker({
    //     color: $("#color").val()
    // }).on("changeColor ", function(e) {
    //     $("#showColor").css("background-color",e.color.toHex());
    // });
    // $("#showColor").css("background-color", $("#color").val());
}

//初始日期
function initDate() {

    $('#custombondmatterForm .c-disable').datetimepicker({
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

function changeDate1(obj) {
    $(obj).next().datetimepicker('show');
}

function changeDate(obj) {
    var cDate = $(obj).val().split("-");
    if(null!=cDate && cDate.length>=3){
        $(obj).prev().val(cDate[1] + "月" + cDate[2] + "日");
    }
    setMailCompleteDate();
}

//日期删除
function removeTime(obj){
    if($("div[name='minus']").size()>1){
        $(obj).parent().remove();
        setMailCompleteDate();
    }
}

//触发通知
function initMailUser(){
    var mailUser = $("#hidMailUser").val();
    if(!mailUser || typeof(mailUser)==='undefined' || mailUser==''){
        return '';
    }
    $("input[name='cbxMailUser']").each(function(index, item){
        var cbxMailUserValue = parseInt($(item).val());
        if((parseInt(mailUser) & cbxMailUserValue) > 0){
            $(item).prop('checked','checked');
        }
    });
}

//初始控件
function initAddControl(){

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

    $("#rightLine").change(function(){
        initSubMatter();
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

            if($("#completeBeforeDay").val()==""){
                $("#divMailBeforeDay").text("工作日开始，每隔");
            }else{
                if($("#mailBeforeDay").val()==""){
                    $("#divMailBeforeDay").text("工作日开始，每隔");
                }else{
                    var mailDay = $("#completeBeforeDay").val()==""?0:parseInt($("#completeBeforeDay").val());
                    mailDay = mailDay + parseInt($("#mailBeforeDay").val());
                    evalMailCompleteBeforeDate($("#key").val(), mailDay, "divMailBeforeDay");
                }
            }
        }
        $("#templateruleType").val($(this).val());
    });

    //添加时间
    $("#divAddTime").click(function(){
        $("#divFTime").append("<div name='rule-0' class='divTime'>" + $(".divTime").html() + "</div>");
        $(".divTime:last").find("input[name='remindDate1']").val("");
        $(".divTime:last").find("input[name='completeDate1']").val("");
        $(".divTime:last").find("input[name='remindDate']").val("");
        $(".divTime:last").find("input[name='completeDate']").val("");

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
            attachment.type = $(item).find("input[name='customAttachment.type']").val();
            attachment.name = $(item).find("input[name='customAttachment.name']").val();
            attachment.url = $(item).find("input[name='customAttachment.url']").val();
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
    $("#editCustombondmatterSave").click(function(){
        if(addValidate()){

            var loadIndex = layer.load(2, {
                shade: [0.3,'#cacaca'],
                area: ['50px', '50px'],
                offset:['50%','50%'],
            });

            var form = new FormData(document.getElementById("custombondmatterForm"));
            $.ajax({
                url: contextPath + "/custommatter/editSave",
                type:"post",
                data: JSON.stringify(tmpJson),//将对象序列化成JSON字符串
                dataType:"json",
                contentType : 'application/json;charset=utf-8', //设置请求头信息
                success:function(data){
                    layer.close(loadIndex);
                    if(data.code==0){
                        layer.alert("编辑成功", {icon: 1, time: 3000});
                        layer.close(pageCustombondmatter);
                        initCustomMatterData();
                    }else{
                        layer.alert(data.message, {icon: 1, time: 3000});
                    }
                },
                error:function(e){
                    layer.close(loadIndex);
                    layer.alert('编辑失败', {icon: 1, time: 3000});
                }
            });
        }
    });

    $("#btnSubMatterAdd").click(function(){
        var lastIndex = $(".sub-matter:last").attr("name").replace("divSubMatter-","");
        var index = parseInt(lastIndex) + 1;

        var addSubName = '<div class="form-group tmp-col">' + $(".sub-matter:last>div").eq(0).html() + '</div>';
        var addSubRule = '<div class="form-group tmp-col">' + $(".sub-matter:last>div").eq(1).html() + '</div>';
        var addFrequency = '<div class="form-group tmp-col">' + $(".sub-matter:last>div").eq(2).html() + '</div>';
        var addSummernote = '<div class="form-group tmp-col">';
        addSummernote += '<label class="col-sm-2 control-label">';
        addSummernote += '<div class="glyphicon glyphicon-asterisk form-required"></div>事项内容：';
        addSummernote += '</label>';
        addSummernote += '<div class="col-sm-10">';
        addSummernote += '<div id="summernote-' + index + '" class="summernote"></div>';
        addSummernote += '</div>';
        addSummernote += '<div class="col-sm-12" style="background:#FFFFFF;height:10px;"></div>';
        addSummernote += '</div>';


        addSubHTML = '<div name="divSubMatter-' + index + '" class="sub-matter">' + addSubName + addSubRule + addFrequency + addSummernote + '</div>';
        $("#divSub").append(addSubHTML);
        $(".sub-matter:last").find("span[name='spanOrderIndex']").attr("id","spanOrderIndex-" + index);
        $(".sub-matter:last").find("span[name='spanOrderIndex']").html('子事项' + index + '：');
        $(".sub-matter:last").find("input[name='subName']").attr("id","subName-" + index);
        $(".sub-matter:last").find("input[name='subName']").val("");
        $(".sub-matter:last").find("div[name='divSubMatterColose']").attr("id","divSubMatterColose-" + index);
        $(".sub-matter:last").find("input[name='mailBeforeDay']").attr("id","mailBeforeDay-" + index);
        $(".sub-matter:last").find("input[name='mailBeforeDay']").val("");
        $(".sub-matter:last").find("div[name='divMailBeforeDay']").attr("id","divMailBeforeDay-" + index);
        $(".sub-matter:last").find("div[name='divMailBeforeDay']").html("工作日开始，每隔");
        $(".sub-matter:last").find("input[name='mailFrequency']").attr("id","mailFrequency-" + index);
        $(".sub-matter:last").find("input[name='mailFrequency']").val("");
        $(".sub-matter:last").find(".summernote").attr("id","summernote-" + index);

        $(".sub-matter:last").find("input[name='subCompleteBeforeDay']").attr("id","subCompleteBeforeDay-" + index);
        $(".sub-matter:last").find("input[name='subCompleteBeforeDay']").val("");
        $(".sub-matter:last").find("label[name='subCompleteBeforeDate']").attr("id","subCompleteBeforeDate-" + index);
        $(".sub-matter:last").find("label[name='subCompleteBeforeDate']").text("工作日");

        $("#summernote-"+index).summernote({
            lang: 'zh-CN',
            height: 150,
            minHeight: 300,
            maxHeight:null,
            disableDragAndDrop: false,   //禁止拖放
            dialogsInBody: true,
            placeholder: '这里填写事项内容',
            backColor: '#FFFFFF',
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

        $(".note-editor").css("background","#FFFFFF");

        $(".sub-matter:last").find(".note-editor>.btn-toolbar>div").each(function(index, item){
            $(item).find(":button").each(function(vindex,vitem){
                var title = $(vitem).attr("data-original-title");
                $(vitem).attr("title", title);
            });
        });

        $(".val-num").keyup(function(){
            this.value=this.value.replace(/[^\d]/g,'');
        });
    });

    $("#beforeDay").blur(function () {
        if($("#beforeDay").val()!=""){
            var paraData = {};
            paraData.bondCode = $("#key").val();
            paraData.beforeDay = $("#beforeDay").val();
            $.ajax({
                url: contextPath + "/custommatter/findRemindDate",
                type:"post",
                data: JSON.stringify(paraData),//将对象序列化成JSON字符串
                dataType:"text",
                contentType : 'application/json;charset=utf-8', //设置请求头信息
                success:function(data){
                    if(data!=""){
                        $("#lblRemindBeforeDate").text("工作日（" + data + "）");
                    }else{
                        $("#lblRemindBeforeDate").text("工作日");
                    }
                },
                error:function(e){
                    //layer.alert('获取日期失败', {icon: 1, time: 3000});
                    $("#lblRemindBeforeDate").text("工作日");
                }
            });
        }else{
            $("#lblRemindBeforeDate").text("工作日");
        }
    });

    $("#completeBeforeDay").blur(function () {
        if($("#completeBeforeDay").val()!=""){
            var paraData = {};
            paraData.bondCode = $("#key").val();
            paraData.completeDay = $("#completeBeforeDay").val();
            $.ajax({
                url: contextPath + "/custommatter/findCompleteDate",
                type:"post",
                data: JSON.stringify(paraData),//将对象序列化成JSON字符串
                dataType:"text",
                contentType : 'application/json;charset=utf-8', //设置请求头信息
                success:function(data){
                    if(data!=""){
                        $("#lblCompleteBeforeDate").text("工作日（" + data + "）");
                    }else{
                        $("#lblCompleteBeforeDate").text("工作日");
                    }
                },
                error:function(e){
                    //layer.alert('获取日期失败', {icon: 1, time: 3000});
                    $("#lblCompleteBeforeDate").text("工作日");
                }
            });

            //邮件提醒频率
            if($("#rightLine").val()==0){
                if($("#completeBeforeDay").val()=="" || $("#mailBeforeDay").val()==""){
                    $("#divMailBeforeDay").text("工作日开始，每隔");
                }else{
                    var mailDay = $("#mailBeforeDay").val()==""?0:parseInt($("#mailBeforeDay").val());
                    mailDay = parseInt($("#completeBeforeDay").val()) + mailDay;
                    evalMailCompleteBeforeDate($("#key").val(), mailDay, "divMailBeforeDay");
                }
            }
        }else{
            $("#lblCompleteBeforeDate").text("工作日");
            $("#divMailBeforeDay").text("工作日开始，每隔");
        }
    });


    $("#mailBeforeDay").blur(function(){
        if($("#mailBeforeDay").val()==""){
            $("#divMailBeforeDay").text("工作日开始，每隔");
        }else{
            if($("input:radio[name='radioTime']:checked").val()==0){
                setMailCompleteDate();
            }else if($("input:radio[name='radioTime']:checked").val()==1){
                if($("#completeBeforeDay").val()==""){
                    $("#divMailBeforeDay").text("工作日开始，每隔");
                }else{
                    var mailDay = $("#completeBeforeDay").val()==""?0:parseInt($("#completeBeforeDay").val());
                    mailDay = mailDay + parseInt($("#mailBeforeDay").val());
                    evalMailCompleteBeforeDate($("#key").val(), mailDay, "divMailBeforeDay");
                }
            }
        }
    });
}

//固定日期计算完成日
function setMailCompleteDate(){
    if($("#mailBeforeDay").val()!=""){
        var aryRuleDate = new Array();
        $(".divTime").each(function(index, item){
            var ruleDateJson = {};
            var stime = $(item).find("input[name='remindDate1']").val();
            var etime = $(item).find("input[name='completeDate1']").val();
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

function setCustomCompleteDate(obj){
    var subId = $(obj).attr("id").split("-")[1];
    var controlMailDate = "divMailBeforeDay-"+subId;
    if($(obj).val()==""){
        $("#subCompleteBeforeDate-"+subId).text("工作日");
        $("#"+controlMailDate).text("工作日开始，每隔");
    }else{
        if($("input:radio[name='radioTime']:checked").val()==0){
            setMailCompleteDate();
        }else if($("input:radio[name='radioTime']:checked").val()==1){
            //计算完成日期
            var paraData = {};
            paraData.bondCode = $("#key").val();
            paraData.completeDay = $(obj).val();
            $.ajax({
                url: contextPath + "/custommatter/findCompleteDate",
                type:"post",
                data: JSON.stringify(paraData),//将对象序列化成JSON字符串
                dataType:"text",
                contentType : 'application/json;charset=utf-8', //设置请求头信息
                success:function(data){
                    if(data!=""){
                        $("#subCompleteBeforeDate-"+subId).text("工作日（" + data + "）");
                    }else{
                        $("#subCompleteBeforeDate-"+subId).text("工作日");
                    }
                },
                error:function(e){
                    //layer.alert('获取日期失败', {icon: 1, time: 3000});
                    $("#subCompleteBeforeDate-"+subId).text("工作日");
                }
            });

            //计算提醒频率完成日期
            if($("#mailBeforeDay-"+subId).val()==""){
                $("#"+controlMailDate).text("工作日开始，每隔");
            }else{
                var subCompBeforeDay = parseInt($(obj).val());
                subCompBeforeDay = subCompBeforeDay + parseInt($("#mailBeforeDay-"+subId).val());
                evalMailCompleteBeforeDate($("#key").val(), subCompBeforeDay, controlMailDate);
            }
        }
    }
}
function setCustomMailCompleteDate(obj){
    var subId = $(obj).attr("id").split("-")[1];
    var controlMailDate = "divMailBeforeDay-"+subId;
    if($(obj).val()==""){
        $("#"+controlMailDate).text("工作日开始，每隔");
    }else{
        if($("input:radio[name='radioTime']:checked").val()==0){
            setMailCompleteDate();
        }else if($("input:radio[name='radioTime']:checked").val()==1){
            if($("#subCompleteBeforeDay-"+subId).val()==""){
                $("#"+controlMailDate).text("工作日开始，每隔");
            }else{
                var subCompBeforeDay = parseInt($("#subCompleteBeforeDay-"+subId).val());
                subCompBeforeDay = subCompBeforeDay + parseInt($(obj).val());
                evalMailCompleteBeforeDate($("#key").val(), subCompBeforeDay, controlMailDate);
            }
        }
    }
}
function evalMailCompleteBeforeDate(bondCode, completeBeforeDay, controlId) {
    var paraData = {};
    paraData.bondCode = bondCode;
    paraData.completeDay = completeBeforeDay;
    $.ajax({
        url: contextPath + "/custommatter/findCompleteDate",
        type:"post",
        data: JSON.stringify(paraData),//将对象序列化成JSON字符串
        dataType:"text",
        contentType : 'application/json;charset=utf-8', //设置请求头信息
        success:function(data){
            if(data!=""){
                $("#" + controlId).text("（" + data + "）工作日开始，每隔");
            }else{
                $("#" + controlId).text("工作日开始，每隔");
            }
        },
        error:function(e){
            //layer.alert('获取日期失败', {icon: 1, time: 3000});
            $("#" + controlId).text("工作日开始，每隔");
        }
    });
}

function removeSubMatter(obj){
    if($(".sub-matter").size()<=1){
        layer.alert("删除失败：至少有一条子事项", {icon: 1, time: 3000});
        return;
    }
    var index = $(obj).attr("id").replace("divSubMatterColose-","");
    $("div[name='divSubMatter-" + index + "'").remove();
    $(".sub-matter").each(function(index, item){
        var vIndex = index + 1;
        $(item).attr("name","divSubMatter-" + vIndex);
        $(item).find("span[name='spanOrderIndex']").attr("id","spanOrderIndex-" + vIndex);
        $(item).find("span[name='spanOrderIndex']").html("子事项" + vIndex + "：");
        $(item).find("input[name='subName']").attr("id","subName-" + vIndex);
        $(item).find("div[name='divSubMatterColose']").attr("id","divSubMatterColose-" + vIndex);
        $(item).find("input[name='mailBeforeDay']").attr("id","mailBeforeDay-" + vIndex);
        $(item).find("div[name='divMailBeforeDay']").attr("id","divMailBeforeDay-" + vIndex);
        $(item).find("input[name='mailFrequency']").attr("id","mailFrequency-" + vIndex);
        $(item).find("input[name='subCompleteBeforeDay']").attr("id","subCompleteBeforeDay-" + vIndex);
        $(item).find("label[name='subCompleteBeforeDate']").attr("id","subCompleteBeforeDate-" + vIndex);
        $(item).find(".summernote").attr("id","summernote-" + vIndex);
    });
}

function initSubMatter(){
    var matterType = $("#type").val();
    if(matterType==0){
        $(".subMatter").addClass("hide");
        $(".unSubMatter").removeClass("hide");
    }else if(matterType==1){
        var rightLine = $("#rightLine").val();
        if(rightLine=="0"){
            $(".subMatter").addClass("hide");
            $(".unSubMatter").removeClass("hide");

            $("#divRadioF").removeClass("hide");

            $("#divF").removeClass("hide");
            $("#divT").addClass("hide");

            $("#radioF").prop("checked","checked");
            $("#radioT").prop("checked","");

            $("#templateruleType").val(0);

            $(".base-completeBefore").show();
            $("#divMsgRule").html("“提醒时间”为触发该事项的时间，“需完成时间”为该事项最晚需完成的时间。按“年”为触发周期");
        }else{
            $(".subMatter").removeClass("hide");
            $(".unSubMatter").addClass("hide");

            $("#divRadioF").addClass("hide");

            $("#divF").addClass("hide");
            $("#divT").removeClass("hide");

            $("#radioF").prop("checked","");
            $("#radioT").prop("checked","checked");

            $("#templateruleType").val(1);

            $(".base-completeBefore").hide();
            $("#divMsgRule").html("“提醒时间”为触发该事项的时间，根据付息频率，按“年”为触发周期");
        }
    }
}

function initMialBeoreDay(){
    //邮件提醒频率
    if($("#rightLine").val()==0){
        //非行权付息
        if($("input:radio[name='radioTime']:checked").val()==0){
            setMailCompleteDate();
        }
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
        attachment.type = $(item).find("input[name='customAttachment.type']").val();
        attachment.name = $(item).find("input[name='customAttachment.name']").val();
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

    var form = new FormData(document.getElementById("custombondmatterForm"));

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
                    tmpFileHtml += '<input type="hidden" name="customAttachment.type" value="' + fileIcon + '"/>';
                    tmpFileHtml += '<input type="hidden" name="customAttachment.name" value="' + files[i].name + '"/>';
                    tmpFileHtml += '<input type="hidden" name="customAttachment.url" value="' + files[i].url + '"/>';

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

    var nameValue = $(objFile).parent().find("input[name='customAttachment.name']").val();
    var nameField = document.createElement("input");
    nameField.setAttribute("type", "hidden");
    nameField.setAttribute("name", "name");
    nameField.setAttribute("value",nameValue);

    var urlValue = $(objFile).parent().find("input[name='customAttachment.url']").val();
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
        "是否要删除该事项材料？",
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
            var stime = $(item).find("input[name='remindDate1']").val();
            if(stime==""){
                layer.alert("保存失败：提醒时间不能为空", {icon: 1, time: 3000});
                isFlag = false;
                return false;
            }
            var etime = $(item).find("input[name='completeDate1']").val();
            if(etime==""){
                layer.alert("保存失败：需完成时间不能为空", {icon: 1, time: 3000});
                isFlag = false;
                return false;
            }
            var ruleJson = {};
            ruleJson.id = $(item).attr("name").replace("rule-","");
            ruleJson.customId = $("#tmpId").val();
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
        if($("#rightLine").val()==0){
            if(isPositiveInteger($("#completeBeforeDay").val())==false){
                layer.alert("保存失败：提前天数不能为空且为数字", {icon: 1, time: 3000});
                isFlag = false;
                return false;
            }
        }

        var ruleJson = {};
        ruleJson.id = $("#divT").attr("name").replace("rule-","");
        ruleJson.customId = $("#tmpId").val();
        ruleJson.type = radioTime;
        ruleJson.beforeDay = $("#beforeDay").val();
        if($("#rightLine").val()==0){
            ruleJson.completeBeforeDay = $("#completeBeforeDay").val();
        }else{
            ruleJson.beforeDay = 0;
            ruleJson.completeBeforeDay = 0;
        }

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

    if($("#rightLine").val()==0){
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
    }

    // if($("#tmpFiles .tmp-file").size()<=0){
    //     layer.alert("保存失败：请添加模板材料", {icon: 1, time: 3000});
    //     isFlag = false;
    //     return false;
    // }else{
        $("#tmpFiles .tmp-file").each(function(index, item){
            var fileJson = {};
            fileJson.id = 0;
            fileJson.customId = $("#tmpId").val();
            fileJson.type = $(item).find("input[name='customAttachment.type']").val();
            fileJson.name = $(item).find("input[name='customAttachment.name']").val();
            fileJson.url = $(item).find("input[name='customAttachment.url']").val();
            aryFile.push(fileJson);
        });
    //}

    //if($("#rightLine").val()==0) {
        if (trim($("#summernote-0").code()) == "" || trim($("#summernote-0").code())=="<p><br></p>") {
            layer.alert("保存失败：事项内容不能为空", {icon: 1, time: 3000});
            isFlag = false;
            return false;
        }
    //}

    //督导确认
    var radioConfirm = $("input:radio[name='radioConfirm']:checked").val();
    //触发通知
    var mailUser = 0;
    $("input[name='cbxMailUser']:checkbox:checked").each(function(index, item){
        mailUser = mailUser | parseInt($(item).val());
    });

    var customSubList = [];
    if($("#type").val()==1 && $("#rightLine").val()==1){
        $(".sub-matter").each(function(index, item){
            var customSubJson = {};
            var vindex = index + 1;
            if(trim($("#subName-"+vindex).val())==""){
                layer.alert("保存失败：子事项" + vindex + "不能为空", {icon: 1, time: 3000});
                isFlag = false;
                return false;
            }
            if(isPositiveInteger($("#subCompleteBeforeDay-"+vindex).val())==false){
                layer.alert("保存失败：子事项" + vindex + "需完成时间不能为空且为数字", {icon: 1, time: 3000});
                isFlag = false;
                return false;
            }
            if(isPositiveInteger($("#mailBeforeDay-"+vindex).val())==false){
                layer.alert("保存失败：子事项" + vindex + "提醒频率不能为空且为数字", {icon: 1, time: 3000});
                isFlag = false;
                return false;
            }
            if(isPositiveInteger($("#mailFrequency-"+vindex).val())==false){
                layer.alert("保存失败：子事项" + vindex + "提醒频率不能为空且为数字", {icon: 1, time: 3000});
                isFlag = false;
                return false;
            }
            if(trim($("#summernote-"+vindex).code())=="" || trim($("#summernote-"+vindex).code())=="<p><br></p>") {
                layer.alert("保存失败：子事项" + vindex + "事项内容不能为空", {icon: 1, time: 3000});
                isFlag = false;
                return false;
            }
            customSubJson.id=0;
            customSubJson.customId=$("#tmpId").val();
            customSubJson.orderIndex = vindex;
            customSubJson.name = trim($("#subName-"+vindex).val());
            customSubJson.beforeDay = $("#beforeDay").val();
            customSubJson.completeBeforeDay = $("#subCompleteBeforeDay-" + vindex).val();
            customSubJson.mailBeforeDay = $("#mailBeforeDay-"+vindex).val();
            customSubJson.mailFrequency = $("#mailFrequency-"+vindex).val();
            customSubJson.content = trim($("#summernote-"+vindex).code());
            customSubList.push(customSubJson);
        });
    }

    tmpJson.id = $("#tmpId").val();
    tmpJson.type = $("#type").val();
    tmpJson.rightLine = $("#rightLine").val();
    tmpJson.key = $("#key").val();
    tmpJson.name = $("#name").val();
    tmpJson.shortname = $("#shortname").val();
    tmpJson.description = $("#description").val();
    tmpJson.confirm = radioConfirm;
    tmpJson.mailUser = mailUser;
    tmpJson.mailBeforeDay = $("#mailBeforeDay").val();
    tmpJson.mailFrequency = $("#mailFrequency").val();
    tmpJson.color = $("#color").val();
    tmpJson.content = $("#summernote-0").code();
    tmpJson.customRuleList = aryRule;
    tmpJson.customAttachmentList = aryFile;
    tmpJson.status = $("#status").val();
    tmpJson.relation = $("#relation").val();
    tmpJson.customSubMatterList = customSubList;

    return isFlag;
}