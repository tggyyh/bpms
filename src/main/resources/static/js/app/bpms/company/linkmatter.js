/**
 * Created by Dealing076 on 2018/3/7.
 */
//@ sourceURL=linkmatter.js
var isEdit = true;
$(function () {

    if($("#spanLinkCount").text()<=0 && $("#spanCustomLinkCount").text()<=0){
        isEdit = false;
        $("#btnLinkMatterSave").attr("disabled", isEdit);
        $("#divLinkmatter input:checkbox").attr("disabled", isEdit);
    }

    initCustomMatterData();

    //初始化Buttons
    initLinkButtons();

    $("#divLinkmatter").css("height", $(".layui-layer-page").height()-210 + "px");
    $(".layui-layer-content").css({"overflow-x":"hidden","overflow-y":"hidden"});
});

function initLinkButtons() {

    //全选
    $("#cbxMatterAll").change(function(){
        $("input[name='cbxMatterItem']").prop('checked', this.checked);
    });
    $("#cbxCustomMatterAll").change(function(){
        $("input[name='cbxCustomMatterItem']").prop('checked', this.checked);
    });

    $("#btnLinkMatterEdit").click(function(){
        isEdit = false;
        $("#btnLinkMatterSave").attr("disabled", isEdit);
        $("#divLinkmatter input:checkbox").attr("disabled", isEdit);
    });
    //保存
    $("#btnLinkMatterSave").click(function(){
        var loadIndex = layer.load(2, {
            shade: [0.3,'#cacaca'],
            area: ['50px', '50px'],
            offset:['50%','50%'],
        });

        var companyMatterList = [];
        $("input[name='cbxMatterItem']:checkbox:checked").each(function(index, item){
            var companyMatter = {};
            companyMatter.companyName = $("#hidCompanyName").val();
            companyMatter.templateId = $(item).val();
            companyMatter.status = 0;
            companyMatterList.push(companyMatter);
        });

        var customMatterIds = [];
        $("input[name='cbxCustomMatterItem']:checkbox:checked").each(function(index, item){
            customMatterIds.push(parseInt($(item).val()));
        });
        var paraData = {};
        paraData.companyName = $("#hidCompanyName").val();
        paraData.companyMatterList = companyMatterList;
        paraData.customCompanyMatterIds = customMatterIds;

        $.ajax({
            url: contextPath + "/company/linkMatterSave",
            type:"post",
            data: JSON.stringify(paraData),//将对象序列化成JSON字符串
            dataType:"json",
            contentType : 'application/json;charset=utf-8', //设置请求头信息
            success:function(data){
                layer.close(loadIndex);
                if(data.code==0){
                    layer.alert("保存成功", {icon: 1, time: 3000});
                    layer.close(pageIndex);
                    refreshCompanyData();
                }else{
                    layer.alert(data.message, {icon: 1, time: 3000});
                }
            },
            error:function(e){
                layer.close(loadIndex);
                layer.alert('保存失败', {icon: 1, time: 3000});
            }
        });
    });

    $("#btnCustomMatterAdd").click(function () {
        $.ajax(contextPath + "/custommatter/add/0/" + $("#hidCompanyName").val()).success(function (data) {
            console.log("success");
            pageCustombondmatter = layer.open({
                type: 1,
                title: '新增自定义事项',
                maxmin: false,
                resize: false,
                shade: 0.3,
                area: ['90%', '95%'],
                content: data
            });
        });
    });

    //展开收缩
    $("#spanCompanyTemp").click(function(){
        if($("#spanCompanyTemp").hasClass("glyphicon-triangle-bottom")){
            $(".tr-comp").addClass("hide");
            $("#spanCompanyTemp").removeClass("glyphicon-triangle-bottom");
            $("#spanCompanyTemp").addClass("glyphicon-triangle-top");
        }else if($("#spanCompanyTemp").hasClass("glyphicon-triangle-top")){
            $(".tr-comp").removeClass("hide");
            $("#spanCompanyTemp").addClass("glyphicon-triangle-bottom");
            $("#spanCompanyTemp").removeClass("glyphicon-triangle-top");
        }
    });
    $("#spanCustomTemp").click(function(){
        if($("#spanCustomTemp").hasClass("glyphicon-triangle-bottom")){
            $(".tr-custom").addClass("hide");
            $("#spanCustomTemp").removeClass("glyphicon-triangle-bottom");
            $("#spanCustomTemp").addClass("glyphicon-triangle-top");
        }else if($("#spanCustomTemp").hasClass("glyphicon-triangle-top")){
            $(".tr-custom").removeClass("hide");
            $("#spanCustomTemp").addClass("glyphicon-triangle-bottom");
            $("#spanCustomTemp").removeClass("glyphicon-triangle-top");
        }
    });
}


function loadRuleMore(objRule){
    var addHTML = "";
    $(objRule).prev().children().each(function(index, item){
        addHTML += "<div>" + $(item).html() + "</div>";
    });
    $(objRule).next().children().eq(1).html("");
    $(objRule).next().children().eq(1).append(addHTML);
    $(objRule).next().removeClass("hide");
}

function closeRuleMore(objRule){
    $(objRule).next().html("");
    $(objRule).parent().addClass("hide");
}

function initCustomMatterData() {
    $.ajax({
        url: contextPath + "/custommatter/findByKey/0/" + $("#hidCompanyName").val(),
        type: 'get',
        dataType: 'json',
        success: function (data) {
            loadCustomMatterData(data);
        },
        error: function (e) {
            layer.alert('加载数据失败', {icon: 1, time: 3000});
        }
    });
}

function loadCustomMatterData(customBondMatterList) {
    $("#tabInfoLinkCompany tr[name='trCustom']").remove();
    var trHTML = '';
    for(var i=0;i<customBondMatterList.length;i++){
        trHTML += '<tr name="trCustom" class="tr-hover tr-custom">';
        trHTML += '<td>';
        if(customBondMatterList[i].relation==1){
            trHTML += '<input type="checkbox" id="cbxCustomMatterItem-' + customBondMatterList[i].id + '" name="cbxCustomMatterItem" value="' + customBondMatterList[i].id + '" checked="checked" class="customChecked" />';
        }else{
            trHTML += '<input type="checkbox" id="cbxCustomMatterItem-' + customBondMatterList[i].id + '" name="cbxCustomMatterItem" value="' + customBondMatterList[i].id + '" />';
        }
        trHTML += '</td>';
        trHTML += '<td>';
        trHTML += '<div class="tmp-row tmp-name-row">';
        trHTML += '<div class="tmp-border-right">';
        trHTML += '<div class="tmp-icon" data-toggle="tooltip" title="' + customBondMatterList[i].shortname + '" style="background-color:' + customBondMatterList[i].color + ';border-color:' + customBondMatterList[i].color + ';">' + customBondMatterList[i].shortname + '</div>';
        trHTML += '<div class="tmp-content">';
        trHTML += '<div class="content-name" data-toggle="tooltip" title="' + customBondMatterList[i].name + '">' + customBondMatterList[i].name + '</div>';
        trHTML += '<div class="content-text" data-toggle="tooltip" title="' + customBondMatterList[i].description + '">' + customBondMatterList[i].description + '</div>';
        trHTML += '</div>';
        trHTML += '</div>';
        trHTML += '</div>';
        trHTML += '</td>';
        trHTML += '<td>';
        trHTML += '<div class="tmp-row tmp-rule-row">';
        trHTML += '<div class="tmp-border-right linkmatter-noborder">';
        trHTML += '<div>提醒规则：</div>';

        var customRule = customBondMatterList[i].customRuleList;
        ruleHTML = '';
        for(var j=0;j<customRule.length;j++){
            ruleHTML += '<div class="' + (j>1?'hide':'"') + '">';
            if(customRule[j].type==0){
                ruleHTML += '<span>提醒：<span>' + new Date(customRule[j].remindDate).Format('MM月dd日') + '</span></span>&nbsp;&nbsp;';
                ruleHTML += '<span>需完成：<span>' + new Date(customRule[j].completeDate).Format('MM月dd日') + '</span></span>';
            }else if(customRule[j].type==1){
                ruleHTML += '<span>提醒：<span>付息日 提前' + customRule[j].beforeDay + '</span></span>&nbsp;';
                ruleHTML += '<span>需完成：<span>付息日 提前' + customRule[j].completeBeforeDay + '</span></span>';
            }
            ruleHTML += '</div>';
        }
        trHTML += '<div class="tmp-rule">' + ruleHTML + '</div>';
        if(customRule!=null && customRule.length>2){
            trHTML += '<div class="tmp-link" onclick="loadRuleMore(this)">更多</div>';
        }
        trHTML += '<div class="tmp-rule-tip hide">';
        trHTML += '<div class="glyphicon glyphicon-remove text-right tmp-rule-tip-close" onclick="closeRuleMore(this)"></div>';
        trHTML += '<div class="tmp-rule-tip-content">';
        trHTML += '</div>';
        trHTML += '</div>';
        trHTML += '</div>';
        trHTML += '</div>';
        trHTML += '</td>';
        trHTML += '<td>';
        trHTML += '<div class="linkman-option-view">';
        trHTML += '<span class="linkman-span-option" onclick="editCustomBondMatter(this)">修改</span>';
        trHTML += '<span class="linkman-span-option-d" onclick="deleteCustomBondMatter(this,'+customBondMatterList[i].relation+')">删除</span>';
        trHTML += '</div>';
        trHTML += '</td>';
        trHTML += '</tr>';
    }
    $("#trCustomHeader").after(trHTML);
    $("#spanCustomCount").html($("#tabInfoLinkCompany tr[name='trCustom']").size());
    $("#spanCustomLinkCount").html($(".customChecked").size());

    $("#divLinkmatter input:checkbox").attr("disabled", isEdit);

    if($("#hidAddCustomId").val()!="" && isEdit==true){
        $("#cbxCustomMatterItem-" + $("#hidAddCustomId").val()).attr("disabled", false);
        $("#btnLinkMatterSave").attr("disabled", false);
    }

}

function editCustomBondMatter(obj){
    var index = $(obj).parent().parent().parent().index();  //当前行索引
    var objTR = $("#tabInfoLinkCompany tr:eq(" + index + ")");
    var id = $(objTR).find("input[name='cbxCustomMatterItem']").val();
    $.ajax(contextPath + "/custommatter/edit/" + id).success(function (data) {
        console.log("success");
        pageCustombondmatter = layer.open({
            type: 1,
            title: '编辑自定义事项',
            maxmin: false,
            shade: 0.3,
            area: ['90%', '95%'],
            content: data
        });
    });
}
function deleteCustomBondMatter(obj, relation) {
    var index = $(obj).parent().parent().parent().index();  //当前行索引
    var objTR = $("#tabInfoLinkCompany tr:eq(" + index + ")");
    var id = $(objTR).find("input[name='cbxCustomMatterItem']").val();
    var customMatter = {};
    customMatter.id = id;
    customMatter.type = 0;
    customMatter.key = $("#hidCompanyName").val();

    var confirmMsg = relation==1?'该自定义事项已经处在被应用中，删除后将导致项目中彻底删除了该自定义事项，您是否确认要删除该自定义事项？':'您是否确认要删除该自定义事项？';
    layer.confirm(
        confirmMsg,
        {icon: 3, title: '提示'},
        function (index) {
            $.ajax({
                url: contextPath + "/custommatter/deleteSave",
                type: 'post',
                data: JSON.stringify(customMatter),//将对象序列化成JSON字符串
                contentType: 'application/json;charset=utf-8', //设置请求头信息
                success: function (data) {
                    if (data.code == 0) {
                        $(objTR).remove();
                        $("#spanCustomCount").html($("#tabInfoLinkCompany tr[name='trCustom']").size());
                        $("#spanCustomLinkCount").html($(".customChecked").size());

                        if ($("#hidAddCustomId").val() == id) {
                            $("#hidAddCustomId").val("");
                            $("#btnLinkMatterSave").attr("disabled", isEdit);
                        }
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
}



