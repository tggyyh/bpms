/**
 * Created by Dealing076 on 2018/3/22.
 */
//@ sourceURL=add.js
var industryJson;
var economicJson;
var companyLinkmanList=[];
var pageBondman;
$(function () {

    $('#divBondLinkMan .selectpicker').selectpicker({
        noneSelectedText: '请选择',
        width: '100%'
    });
    $('#divBondBase .selectpicker').selectpicker({
        noneSelectedText: '请选择(可多选)'
    });
    // $(".bootstrap-select .dropdown-toggle").removeClass("btn-default");
    // $(".bootstrap-select .dropdown-toggle").addClass("btn-white");

    loadBondNewArea();

    initIndustry();
    initEconomic();

    initBondNewDate();

    initBondNewButtons();

    $("#divBondContent").css({"height": $(".layui-layer-page").height()-190 + "px","overflow-x":"hidden","overflow-y":"auto"});
    $(".layui-layer-content").css({"overflow-x":"hidden","overflow-y":"hidden"});
});

//初始日期
function initBondNewDate() {

    $('#divBondContent .form_datetime').datetimepicker({
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

    $("#valueDate").change(function(){
       if($("#valueDate").val()!="" && $("#payDay").val()!=""){
           var stime = $("#valueDate").val().replace(/\-/g, "\/");
           var etime = $("#payDay").val().replace(/\-/g, "\/");
           if(stime>etime){
               layer.alert('起息日期不能大于兑付日期', {icon: 1, time: 3000});
           }
       }
    });
    $("#payDay").change(function(){
        if($("#valueDate").val()!="" && $("#payDay").val()!=""){
            var stime = $("#valueDate").val().replace(/\-/g, "\/");
            var etime = $("#payDay").val().replace(/\-/g, "\/");
            if(stime>etime){
                layer.alert('起息日期不能大于兑付日期', {icon: 1, time: 3000});
            }
        }
    });
    $("#stockExchangeBegin").change(function(){
        if($("#stockExchangeBegin").val()!="" && $("#stockExchangeEnd").val()!=""){
            var stime = $("#stockExchangeBegin").val().replace(/\-/g, "\/");
            var etime = $("#stockExchangeEnd").val().replace(/\-/g, "\/");
            if(stime>etime){
                layer.alert('换股期间起始时间不能大于结束时间', {icon: 1, time: 3000});
            }
        }
    });
    $("#stockExchangeEnd").change(function(){
        if($("#stockExchangeBegin").val()!="" && $("#stockExchangeEnd").val()!=""){
            var stime = $("#stockExchangeBegin").val().replace(/\-/g, "\/");
            var etime = $("#stockExchangeEnd").val().replace(/\-/g, "\/");
            if(stime>etime){
                layer.alert('换股期间起始时间不能大于结束时间', {icon: 1, time: 3000});
            }
        }
    });
    $("#exerciseSchedule").keyup(function(){
        $("#exerciseSchedule").val($("#exerciseSchedule").val().replace(/\//g, "-"));
    });
}

function loadBondNewArea(){
    $("#provinceCode").empty();
    var provinceHTML = '<option value="">请选择省</option>';
    if(null!=areaJson){
        for(var i=0;i<areaJson.length;i++){
            if(areaJson[i].parentCode=='0'){
                provinceHTML += '<option value="' + areaJson[i].code + '">' + areaJson[i].name + '</option>';
            }
        }
    }
    $("#provinceCode").append(provinceHTML);

    $("#provinceCode").change(function(){
        loadBondCity();
    });
}
function loadBondCity(){
    $("#cityCode").empty();
    var provinceCode = $("#provinceCode").val();
    var cityHTML = '<option value="">请选择市</option>';
    if(null!=areaJson){
        for(var i=0;i<areaJson.length;i++){
            if(areaJson[i].parentCode==provinceCode){
                cityHTML += '<option value="' + areaJson[i].code + '">' + areaJson[i].name + '</option>';
            }
        }
    }

    $("#cityCode").html(cityHTML);
}

function initIndustry() {
    $.ajax({
        url: contextPath + "/bond/findIndustry",
        type: 'get',
        dataType: 'json',
        success: function (data) {
            industryJson = data;
            loadIndustry();
        },
        error: function (e) {
            layer.alert('加载数据失败', {icon: 1, time: 3000});
        }
    });
}

function loadIndustry(){
    $("#industryType").empty();
    var industryTypeHTML = '<option value="">请选择</option>';
    if(null!=industryJson){
        for(var i=0;i<industryJson.length;i++){
            if(industryJson[i].parentCode=='0'){
                industryTypeHTML += '<option value="' + industryJson[i].code + '">' + industryJson[i].mark + '</option>';
            }
        }
    }
    $("#industryType").append(industryTypeHTML);

    $("#industryType").change(function(){
        loadIndustryBigType();
    });
}
function loadIndustryBigType(){
    $("#industryBigType").empty();
    var industryType = $("#industryType").val();
    var industryBigTypeHTML = '<option value="">请选择</option>';
    if(null!=industryJson){
        for(var i=0;i<industryJson.length;i++){
            if(industryJson[i].parentCode==industryType){
                industryBigTypeHTML += '<option value="' + industryJson[i].code + '">' + industryJson[i].mark + '</option>';
            }
        }
    }

    $("#industryBigType").html(industryBigTypeHTML);
}

function initEconomic() {
    $.ajax({
        url: contextPath + "/bond/findEconomicDepartment",
        type: 'get',
        dataType: 'json',
        success: function (data) {
            economicJson = data;
            loadEconomicDepartment();
        },
        error: function (e) {
            layer.alert('加载数据失败', {icon: 1, time: 3000});
        }
    });
}

function loadEconomicDepartment(){
    $("#economicDepartment").empty();
    var economicDepartmentHTML = '<option value="">请选择</option>';
    if(null!=economicJson){
        for(var i=0;i<economicJson.length;i++){
            if(economicJson[i].parentCode=='0'){
                economicDepartmentHTML += '<option value="' + economicJson[i].code + '">' + economicJson[i].mark + '</option>';
            }
        }
    }
    $("#economicDepartment").append(economicDepartmentHTML);

    $("#economicDepartment").change(function(){
        loadEconomicDepartmentDetail();
    });
}
function loadEconomicDepartmentDetail(){
    $("#economicDepartmentDetail").empty();
    var economicDepartment = $("#economicDepartment").val();
    var economicDepartmentDetailHTML = '<option value="">请选择</option>';
    if(null!=economicJson){
        for(var i=0;i<economicJson.length;i++){
            if(economicJson[i].parentCode==economicDepartment){
                economicDepartmentDetailHTML += '<option value="' + economicJson[i].code + '">' + economicJson[i].mark + '</option>';
            }
        }
    }

    $("#economicDepartmentDetail").html(economicDepartmentDetailHTML);
}

function initBondNewButtons() {
    $("#code").keyup(function(){
        this.value = replaceCharacter1(this.value);
    });
    $("#companyName").keyup(function(){
        this.value = replaceCharacter(this.value);
    });

    $(".val-num").keyup(function(){
        this.value=this.value.replace(/[^\d]/g,'');
    });

    $(".val-decimal").keyup(function(){
        this.value = replaceDecimal(this.value);
    });

    $("#btnUpload").click(function(){
        $("#uploadTmpFile").click();
    });

    $("#divAddBankMan").click(function(){
        var addHTML = getTrTemp('','','','');
        $("#tabBankMan").append(addHTML);
    });

    $("#btnLinkMatterNext").click(function () {
        var isFlag = true;
        // $("#divBondBase .form-required").each(function(index, item){
        //     var msg = $(item).next().text().replace(":","").replace("：","");
        //     var htmlControl = $(item).parent().next().find("input,select");
        //     if(undefined!=htmlControl){
        //         if("string"==typeof($(htmlControl).val())){
        //             if(trim($(htmlControl).val())==""){
        //                 //layer.alert(msg + "不能为空", {icon: 1, time: 3000});
        //                 isFlag = false;
        //                 //return false;
        //             }
        //         }else if("object"==typeof($(htmlControl).val())){
        //             if(null==$(htmlControl).val()){
        //                 //layer.alert(msg + "不能为空", {icon: 1, time: 3000});
        //                 isFlag = false;
        //                 //return false;
        //             }
        //         }
        //     }
        // });
        // if(!isFlag){
        //     layer.alert("必填项不能为空", {icon: 1, time: 3000});
        //     return;
        // }

        if (formValidate().form()) {
            if ($("#valueDate").val() != "" && $("#payDay").val() != "") {
                var stime = $("#valueDate").val().replace(/\-/g, "\/");
                var etime = $("#payDay").val().replace(/\-/g, "\/");
                if (stime > etime) {
                    layer.alert('起息日期不能大于兑付日期', {icon: 1, time: 3000});
                    return false;
                }
            }
            if ($("#stockExchangeBegin").val() != "" && $("#stockExchangeEnd").val() != "") {
                var stime = $("#stockExchangeBegin").val().replace(/\-/g, "\/");
                var etime = $("#stockExchangeEnd").val().replace(/\-/g, "\/");
                if (stime > etime) {
                    layer.alert('换股期间起始时间不能大于结束时间', {icon: 1, time: 3000});
                    return false;
                }
            }

            if($("#dropListedDate").val()=="1" && $("#listedDate").val()==""){
                layer.alert("保存失败：上市日期不能为空", {icon: 1, time: 3000});
                isFlag = false;
                return false;
            }

            $("#divBondBase").addClass("hide");
            $("#divBondLinkMan").removeClass("hide");
            $("#btnLinkMatterNext").addClass("hide");
            $("#btnLinkMatterPrev").removeClass("hide");
            $("#btnLinkMatterSave").removeClass("hide");
            $("#divStep1").removeClass("bond-step-selected");
            $("#divStep2").addClass("bond-step-selected");

            var bondCode = trim($("#code").val());
            var companyName = trim($("#companyName").val());
            var paraData = {};
            paraData.bondCode = bondCode;
            paraData.companyName = companyName;

            $.ajax({
                url: contextPath + "/bond/findIsExistence",
                type: 'post',
                data: JSON.stringify(paraData),//将对象序列化成JSON字符串
                dataType: "text",
                contentType: 'application/json;charset=utf-8', //设置请求头信息
                success: function (data) {
                    $("#hidNew").val(data);
                },
                error: function (e) {
                    $("#hidNew").val(0);
                }
            });

            if (trim($("#code").val()) != trim($("#hidBaseBondCode").val()) || trim($("#companyName").val()) != trim($("#hidBaseCompanyName").val())) {
                $.ajax({
                    url: contextPath + "/bond/findLinkman",
                    type: 'post',
                    data: JSON.stringify(paraData),//将对象序列化成JSON字符串
                    dataType: "json",
                    contentType: 'application/json;charset=utf-8', //设置请求头信息
                    success: function (data) {
                        loadNewBondLinkMan(data);
                    },
                    error: function (e) {
                        layer.alert('加载数据失败', {icon: 1, time: 3000});
                    }
                });
            }
        }else{
            layer.alert("必填项不能为空", {icon: 1, time: 3000});
        }

    });
    $("#btnLinkMatterPrev").click(function () {
        $("#divBondBase").removeClass("hide");
        $("#divBondLinkMan").addClass("hide");
        $("#btnLinkMatterNext").removeClass("hide");
        $("#btnLinkMatterPrev").addClass("hide");
        $("#btnLinkMatterSave").addClass("hide");
        $("#divStep1").addClass("bond-step-selected");
        $("#divStep2").removeClass("bond-step-selected");
    });

    $("#otherLinkMan").click(function () {
        if(trim($("#code").val())=="" || trim($("#companyName").val())==""){
            layer.alert('债券代码或发行人名称不能为空', {icon: 1, time: 3000});
            return false;
        }
        $.ajax(contextPath + "/bondmanager/other/" + trim($("#code").val()) + "/" + trim($("#companyName").val())).success(function (data) {
            console.log("success");
            layer.open({
                type: 1,
                title: '查看项目负责人',
                maxmin: false,
                resize: false,
                shade: 0.3,
                area: ['60%', '50%'],
                content: data
            });
        });
    });

    $("#managerLinkMan").click(function () {
        if(trim($("#companyName").val())==""){
            layer.alert('发行人名称不能为空', {icon: 1, time: 3000});
            return false;
        }
        $.ajax(contextPath + "/companylinkman/bondlinkman/" + trim($("#companyName").val())).success(function (data) {
            console.log("success");
            layer.open({
                type: 1,
                title: '管理发行人对接人',
                maxmin: false,
                shade: 0.3,
                area: ['75%', '70%'],
                content: data,
                cancel: function(index, layero){
                    companyLinkmanList = [];
                    var linkman = [];
                    $("#tabInfoLinkCompany tr:not(:first):not(:last)").each(function(trIndex, trItem){
                        var name = $(trItem).find('div[name="divLinkmanName"]').html();
                        var phone = $(trItem).find('div[name="divLinkmanPhone"]').html();
                        var mobile = $(trItem).find('div[name="divLinkmanMobile"]').html();
                        var mail = $(trItem).find('div[name="divLinkmanMail"]').html();
                        var companyLinkman = {};
                        companyLinkman.id = 0;
                        companyLinkman.companyName = $("#companyName").val();
                        companyLinkman.name = name;
                        companyLinkman.phone = phone;
                        companyLinkman.mobile = mobile;
                        companyLinkman.mail = mail;
                        companyLinkmanList.push(companyLinkman);
                        linkman.push(name);
                    });
                    var viewCompanyMan = ""==linkman.join(",")?"无":linkman.join(",");
                    $("#spanCompanyMan").html(viewCompanyMan);
                }
            });
        });
    });

    $("#btnLinkMatterSave").click(function () {
        if(saveBondValidate()){
            var bond = getBondData();
            if(null==bond){
                layer.alert("保存失败：数据有误", {icon: 1, time: 3000});
                return;
            }

            if($("#hidNew").val()=="1"){
                layer.confirm(
                    '该项目已存在，是否需要重新新增更新项目相关字段？',
                    {icon: 3, title: '新增项目提示'},
                    function (index) {
                        saveBondData(bond);
                        layer.close(index);
                    });
            }else{
                saveBondData(bond);
            }
        }
    });

    $("#btnBondMan").click(function(){
        $.ajax(contextPath + "/bond/bondman").success(function (data) {
            console.log("success");
            pageBondman = layer.open({
                type: 1,
                title: '设置项目负责人',
                maxmin: false,
                resize: false,
                shade: 0.3,
                area: ['80%', '85%'],
                scrollbar: false,
                content: data
            });
        });
    });

    $("#guaranteeCompany1").blur(function(){
       if($("#guaranteeCompany1").val()=="无" || $("#guaranteeCompany1").val()==""){
           $("#guaranteeMode1").val("GU04-信用/免担保");
       }
    });
    $("#guaranteeCompany2").blur(function(){
        if($("#guaranteeCompany2").val()=="无" || $("#guaranteeCompany2").val()==""){
            $("#guaranteeMode2").val("GU04-信用/免担保");
        }
    });

    $("#dropListedDate").change(function(){
        if($(this).val()=="0"){
            $("#listedDate").val("");
            $("#divListedDate").addClass("hide");
        }else{
            $("#divListedDate").removeClass("hide");
        }
    });

}

function saveBondData(bond){
    var loadIndex = layer.load(2, {
        shade: [0.3,'#cacaca'],
        area: ['50px', '50px'],
        offset:['50%','50%'],
    });

    $.ajax({
        url: contextPath + "/bond/addSave",
        type:"post",
        data: JSON.stringify(bond),//将对象序列化成JSON字符串
        dataType:"json",
        contentType : 'application/json;charset=utf-8', //设置请求头信息
        success:function(data){
            layer.close(loadIndex);
            if(data.code==0){
                layer.alert("保存成功", {icon: 1, time: 3000});
                layer.close(pageIndex);
                refreshBondData();
            }else{
                layer.alert(data.message, {icon: 1, time: 3000});
            }
        },
        error:function(e){
            layer.close(loadIndex);
            layer.alert('保存失败', {icon: 1, time: 3000});
        }
    });
}

function loadNewBondLinkMan(bond){
    var aryBondMan = [];
    var aryBondManId = [];
    var otherBondMan = 0;

    //加载项目负责人
    if($("#code").val()!=$("#hidBaseBondCode").val()){
        $("#tabBankMan").html("");
        if(null!=bond && typeof(bond)!='undefined'){
            //项目负责人
            if(null!=bond.bondManagerList && bond.bondManagerList.length>0){
                for(var i=0;i<bond.bondManagerList.length;i++){
                    if(bond.code==bond.bondManagerList[i].bondCode){
                        //装载项目负责人
                        aryBondMan.push(bond.bondManagerList[i].sysUser.name);
                        aryBondManId.push(bond.bondManagerList[i].userId);
                    }else{
                        //记录其它项目负责人
                        otherBondMan = otherBondMan + 1;
                    }
                }
            }
            //监管银行负责人
            var bankManagerHTML = '';
            if(null!=bond.bankManagerList && bond.bankManagerList.length>0){
                for(var i=0;i<bond.bankManagerList.length;i++){
                    //加载监管银行负责人
                    bankManagerHTML += getTrTemp(bond.bankManagerList[i].name,bond.bankManagerList[i].phone,bond.bankManagerList[i].mobile,bond.bankManagerList[i].email);

                }
                if(bankManagerHTML!=""){
                    $("#tabBankMan").html(bankManagerHTML);
                }
            }
        }

        $("#txtBondMan").val(aryBondMan.join(","));
        $("#hidBondManId").val(aryBondManId.join(","));
        if(otherBondMan>0){
            $("#divLinkBonMan").removeClass("hide");
        }else{
            $("#divLinkBonMan").addClass("hide");
        }
        $("#hidBaseBondCode").val($("#code").val());
    }

    //加载发行人对接人
    if($("#companyName").val()!=$("#hidBaseCompanyName").val()){
        companyLinkmanList = [];
        var aryLinkMan = [];
        if(null!=bond && typeof(bond)!='undefined'){
            if(null!=bond.company && null!=bond.company.companyLinkmanList && bond.company.companyLinkmanList.length>0){

                for(var i=0;i<bond.company.companyLinkmanList.length;i++){
                    aryLinkMan.push(bond.company.companyLinkmanList[i].name);
                    companyLinkmanList.push(bond.company.companyLinkmanList[i]);
                }
            }
        }

        if(aryLinkMan.length>0){
            $("#spanCompanyMan").html(aryLinkMan.join(","));
        }else{
            $("#spanCompanyMan").text("无");
        }
        $("#hidBaseCompanyName").val($("#companyName").val());
    }
}


//文件上传
function setTmpFile(objFile) {
    // var tmpFiles = $("#uploadTmpFile").val();
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

    var form = new FormData(document.getElementById("bondNewForm"));

    $.ajax({
        url: contextPath + "/file/parse",
        type: "post",
        data: form,
        processData: false,
        contentType: false,
        success: function (data) {

            if (data.code == 0) {
                setBonData(data.data);
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

function setBonData(bond) {
    bond.code = replaceCharacter1(bond.code);

    bond.companyName = replaceCharacter(bond.companyName);

    $("#hidBaseBondCode").val("");
    $("#hidBaseCompanyName").val("");
    if(null!=bond){
        // $("#hidBaseBondCode").val(bond.code);
        // $("#hidBaseCompanyName").val(bond.companyName);

        $("#code").val(bond.code);
        $("#name").val(bond.name);
        $("#shortname").val(bond.shortname);
        $("#type").val(bond.type);
        $("#companyName").val(bond.companyName);
        $("#rateType").val(bond.rateType);
        $("#currentBalance").val(bond.currentBalance);
        $("#bondManager").val(bond.bondManager);
        $("#interestPayMode").val(bond.interestPayMode);
        $("#repayMode").val(bond.repayMode);
        $("#payFrequency").val(bond.payFrequency);
        $("#timeLimit").val(bond.timeLimit);
        $("#specialTimeLimit").val(bond.specialTimeLimit);
        $("#valueDate").val(timestampToDate(bond.valueDate));
        $("#payDay").val(timestampToDate(bond.payDay));

        var aryOptionType = [];
        if(null!=bond.optionType){
            aryOptionType = bond.optionType.split(";");
        }
        $("#optionType").selectpicker('val', aryOptionType);

        $("#exerciseSchedule").val(bond.exerciseSchedule.replace(/\//g, "-"));
        $("#currentPeriodAmount").val(bond.currentPeriodAmount);
        $("#rate").val(bond.rate);
        $("#rateHigh").val(bond.rateHigh);
        $("#rateLow").val(bond.rateLow);
        $("#specialTerm").val(bond.specialTerm);

        var arySpecailTermType = [];
        if(null!=bond.specailTermType){
            arySpecailTermType = bond.specailTermType.split(";");
        }
        $("#specailTermType").selectpicker('val', arySpecailTermType);

        $("#documentAmount").val(bond.documentAmount);
        $("#stockExchangePrice").val(bond.stockExchangePrice);
        $("#stockExchangeRatio").val(bond.stockExchangeRatio);
        if(null!=bond.stockExchangeBegin && ""!=bond.stockExchangeBegin){
            $("#stockExchangeBegin").val(timestampToDate(bond.stockExchangeBegin));
        }
        if(null!=bond.stockExchangeEnd && ""!=bond.stockExchangeEnd){
            $("#stockExchangeEnd").val(timestampToDate(bond.stockExchangeEnd));
        }

        $("#ransomTerm").val(bond.ransomTerm);
        $("#backSaleTerm").val(bond.backSaleTerm);
        $("#exchangeStockTerm").val(bond.exchangeStockTerm);
        $("#writeDownTerm").val(bond.writeDownTerm);
        $("#bidStockCode").val(bond.bidStockCode);

        var aryListedPlace = [];
        if(null!=bond.listedPlace){
            aryListedPlace = bond.listedPlace.split(";");
        }
        $("#listedPlace").selectpicker('val', aryListedPlace);

        $("#crossMarket").val(bond.crossMarket);
        $("#scale").val(bond.scale);
        $("#price").val(bond.price);
        $("#publishType").val(bond.publishType);
        $("#fixPriceType").val(bond.fixPriceType);
        $("#underwriterName").val(bond.underwriterName);
        $("#viceUnderwriterName").val(bond.viceUnderwriterName);
        $("#underwritingMode").val(bond.underwritingMode);
        $("#bondRatingCompany1").val(bond.bondRatingCompany1);
        var rating1 = null==bond.rating1 || "" ==bond.rating1 ? "IC27-无评级" : bond.rating1;
        $("#rating1").val(rating1);
        $("#bondRatingCompany2").val(bond.bondRatingCompany2);
        $("#bondRatingOrgCode2").val(bond.bondRatingOrgCode2);
        var rating2 = null==bond.rating2 || "" ==bond.rating2 ? "IC27-无评级" : bond.rating2;
        $("#rating2").val(rating2);

        $("#guaranteeMode1").val(bond.guaranteeMode1);
        var guaranteeCompany1 = bond.guaranteeCompany1==""?"无":bond.guaranteeCompany1;
        $("#guaranteeCompany1").val(guaranteeCompany1);
        if(null==bond.guaranteeMode1 || bond.guaranteeMode1==""){
            if($("#guaranteeCompany1").val()=="" || $("#guaranteeCompany1").val()=="无"){
                $("#guaranteeMode1").val("GU04-信用/免担保");
            }
        }

        $("#guaranteeMode2").val(bond.guaranteeMode2);
        var guaranteeCompany2 = bond.guaranteeCompany2==""?"无":bond.guaranteeCompany2;
        $("#guaranteeCompany2").val(guaranteeCompany2);
        if(null==bond.guaranteeMode2 || bond.guaranteeMode2==""){
            if($("#guaranteeCompany2").val()=="" || $("#guaranteeCompany2").val()=="无"){
                $("#guaranteeMode2").val("GU04-信用/免担保");
            }
        }
        $("#guaranteeOrgCode2").val(bond.guaranteeOrgCode2);

        $("#creditMode").val(bond.creditMode);

        if(null!=bond.listedDate && bond.listedDate!=""){
            $("#listedDate").val(timestampToDate(bond.listedDate));
        }

        $("#companyCount").val(bond.company.companyCount);
        $("#industryScale").val(bond.company.industryScale);
        $("#economicSector").val(bond.company.economicSector);

        $("#industryType").find("option:contains('" + bond.company.industryType + "')").attr("selected",true);
        loadIndustryBigType();
        $("#industryBigType").find("option:contains('" + bond.company.industryBigType + "')").attr("selected",true);

        $("#economicDepartment").find("option:contains('" + bond.company.economicDepartment + "')").attr("selected",true);
        loadEconomicDepartmentDetail();
        $("#economicDepartmentDetail").find("option:contains('" + bond.company.economicDepartmentDetail + "')").attr("selected",true);


        $("#stockCode").val(bond.company.stockCode);
        if(null!=bond.company.provinceName && bond.company.provinceName!=""){
            $("#provinceCode").find("option:contains('" + bond.company.provinceName + "')").attr("selected",true);
            loadBondCity();
            if(null!=bond.company.cityName && bond.company.cityName!=""){
                $("#cityCode").find("option:contains('" + bond.company.cityName + "')").attr("selected",true);
            }
        }
        
        $("#ratingCompany1").val(bond.company.ratingCompany1);
        var corporateRating1 = null==bond.company.corporateRating1 || "" ==bond.company.corporateRating1 ? "IC27-无评级" : bond.company.corporateRating1;
        $("#corporateRating1").val(corporateRating1);
        $("#ratingCompany2").val(bond.company.ratingCompany2);
        var corporateRating2 = null==bond.company.corporateRating2 || "" ==bond.company.corporateRating2 ? "IC27-无评级" : bond.company.corporateRating2;
        $("#corporateRating2").val(corporateRating2);
        $("#corporateOrgCode2").val(bond.company.corporateOrgCode2);

    }

    $("#divBondBase").removeClass("hide");
    $("#divBondLinkMan").addClass("hide");
    $("#btnLinkMatterNext").removeClass("hide");
    $("#btnLinkMatterPrev").addClass("hide");
    $("#btnLinkMatterSave").addClass("hide");

}

function getTrTemp(name,phone,mobile,mail){
    var trTemp = '<tr>';
    trTemp += '<td style="padding:5px;">';
    trTemp += '<input type="text" name="txtBankName" value="' + name + '" class="form-control" placeholder="姓名" maxlength="64" />';
    trTemp += '</td>';
    trTemp += '<td style="padding:5px;">';
    trTemp += '<input type="text" name="txtBankPhone" value="' + phone + '" class="form-control" placeholder="电话" maxlength="64" />';
    trTemp += '</td>';
    trTemp += '<td style="padding:5px;">';
    trTemp += '<input type="text" name="txtBankMobile" value="' + mobile + '" class="form-control" placeholder="手机" maxlength="11" />';
    trTemp += '</td>';
    trTemp += '<td style="padding:5px;">';
    trTemp += '<input type="text" name="txtBankMail" value="' + mail + '" class="form-control" placeholder="邮箱" maxlength="64" />';
    trTemp += '</td>';
    trTemp += '<td style="padding:5px;">';
    trTemp += '<div name="minus" title="remove" class="glyphicon glyphicon-minus-sign tmp-time-minus" class="form-control" onclick="removeBankMan(this)">';
    trTemp += '</div>';
    trTemp += '</td>';
    trTemp += '</tr>';
    return trTemp;
}

function removeBankMan(obj){
    $(obj).parent().parent().remove();
}

function saveBondValidate() {
    var isFlag = true;
    $("#divBondBase .form-required").each(function(index, item){
        var msg = $(item).next().text().replace(":","").replace("：","");
        var htmlControl = $(item).parent().next().find("input,select");
        if(undefined!=htmlControl){
            if("string"==typeof($(htmlControl).val())){
                if(trim($(htmlControl).val())==""){
                    layer.alert("保存失败：" + msg + "不能为空", {icon: 1, time: 3000});
                    isFlag = false;
                    return false;
                }
            }else if("object"==typeof($(htmlControl).val())){
                if(null==$(htmlControl).val()){
                    layer.alert("保存失败：" + msg + "不能为空", {icon: 1, time: 3000});
                    isFlag = false;
                    return false;
                }
            }
        }
    });

    if($("#valueDate").val()!="" && $("#payDay").val()!=""){
        var stime = $("#valueDate").val().replace(/\-/g, "\/");
        var etime = $("#payDay").val().replace(/\-/g, "\/");
        if(stime>etime){
            layer.alert('起息日期不能大于兑付日期', {icon: 1, time: 3000});
            return false;
        }
    }
    if($("#stockExchangeBegin").val()!="" && $("#stockExchangeEnd").val()!=""){
        var stime = $("#stockExchangeBegin").val().replace(/\-/g, "\/");
        var etime = $("#stockExchangeEnd").val().replace(/\-/g, "\/");
        if(stime>etime){
            layer.alert('换股期间起始时间不能大于结束时间', {icon: 1, time: 3000});
            return false;
        }
    }

    if($("#dropListedDate").val()=="1" && $("#listedDate").val()==""){
        layer.alert("保存失败：上市日期不能为空", {icon: 1, time: 3000});
        isFlag = false;
        return false;
    }
    if($("#txtBondMan").val()==""){
        layer.alert("保存失败：项目负责人不能为空", {icon: 1, time: 3000});
        isFlag = false;
        return false;
    }

    if(null==companyLinkmanList || companyLinkmanList.length<=0){
        layer.alert("保存失败：请选择发行人对接人", {icon: 1, time: 3000});
        isFlag = false;
        return false;
    }
    return isFlag;
}

function getBondData() {
    var bond = {};
    bond.code = trim($("#code").val());
    bond.name = trim($("#name").val());
    bond.shortname = trim($("#shortname").val());
    bond.type =trim( $("#type").val());
    bond.companyName = trim($("#companyName").val());
    bond.rateType = trim($("#rateType").val());
    bond.currentBalance = trim($("#currentBalance").val());
    bond.bondManager = trim($("#bondManager").val());
    bond.interestPayMode = trim($("#interestPayMode").val());
    bond.repayMode = trim($("#repayMode").val());
    bond.payFrequency = trim($("#payFrequency").val());
    bond.timeLimit = trim($("#timeLimit").val());
    bond.specialTimeLimit = trim($("#specialTimeLimit").val());
    bond.valueDate = trim($("#valueDate").val());
    bond.payDay = trim($("#payDay").val());

    var aryOptionType = $("#optionType").selectpicker('val');
    if(aryOptionType!=null && aryOptionType.length>0){
        bond.optionType = aryOptionType.join(";");
    }else{
        bond.optionType = "";
    }

    bond.exerciseSchedule = trim($("#exerciseSchedule").val());
    bond.currentPeriodAmount = trim($("#currentPeriodAmount").val());
    bond.rate = trim($("#rate").val());
    bond.rateHigh = trim($("#rateHigh").val());
    bond.rateLow = trim($("#rateLow").val());
    bond.specialTerm = trim($("#specialTerm").val());

    var arySpecailTermType = $("#specailTermType").selectpicker('val');
    if(arySpecailTermType!=null && arySpecailTermType.length>0){
        bond.specailTermType = arySpecailTermType.join(";");
    }else{
        bond.specailTermType = "";
    }

    bond.documentAmount = trim($("#documentAmount").val());
    bond.stockExchangePrice = trim($("#stockExchangePrice").val());
    bond.stockExchangeRatio = trim($("#stockExchangeRatio").val());
    bond.stockExchangeBegin = trim($("#stockExchangeBegin").val());
    bond.stockExchangeEnd = trim($("#stockExchangeEnd").val());
    bond.ransomTerm = trim($("#ransomTerm").val());
    bond.backSaleTerm = trim($("#backSaleTerm").val());
    bond.exchangeStockTerm = trim($("#exchangeStockTerm").val());
    bond.writeDownTerm = trim($("#writeDownTerm").val());
    bond.bidStockCode = trim($("#bidStockCode").val());

    var aryListedPlace = $("#listedPlace").selectpicker('val');
    if(aryListedPlace!=null && aryListedPlace.length>0){
        bond.listedPlace = aryListedPlace.join(";");
    }else{
        bond.listedPlace = "";
    }

    bond.crossMarket = trim($("#crossMarket").val());
    bond.scale = trim($("#scale").val());
    bond.price = trim($("#price").val());
    bond.publishType = trim($("#publishType").val());
    bond.fixPriceType = trim($("#fixPriceType").val());
    bond.underwriterName = trim($("#underwriterName").val());
    bond.viceUnderwriterName = trim($("#viceUnderwriterName").val());
    bond.underwritingMode = trim($("#underwritingMode").val());
    bond.bondRatingCompany1 = trim($("#bondRatingCompany1").val());
    bond.rating1 = trim($("#rating1").val());
    bond.bondRatingCompany2 = trim($("#bondRatingCompany2").val());
    bond.bondRatingOrgCode2 = trim($("#bondRatingOrgCode2").val());
    bond.rating2 = trim($("#rating2").val());
    bond.guaranteeCompany1 =trim( $("#guaranteeCompany1").val());
    bond.guaranteeMode1 = trim($("#guaranteeMode1").val());
    bond.guaranteeCompany2 = trim($("#guaranteeCompany2").val());
    bond.guaranteeOrgCode2 = trim($("#guaranteeOrgCode2").val());
    bond.guaranteeMode2 = trim($("#guaranteeMode2").val());
    bond.creditMode = trim($("#creditMode").val());
    bond.listedDate = trim($("#listedDate").val());

    var company = {};
    company.name = trim($("#companyName").val());
    company.stockCode = trim($("#stockCode").val());
    company.industryScale = trim($("#industryScale").val());
    company.industryType = trim($("#industryType").val());
    company.industryBigType = trim($("#industryBigType").val());
    company.economicDepartment = trim($("#economicDepartment").val());
    company.economicDepartmentDetail = trim($("#economicDepartmentDetail").val());
    company.economicSector = trim($("#economicSector").val());
    company.provinceCode = trim($("#provinceCode").val());
    company.cityCode = trim($("#cityCode").val());
    company.ratingCompany1 = trim($("#ratingCompany1").val());
    company.corporateRating1 = trim($("#corporateRating1").val());
    company.ratingCompany2 = trim($("#ratingCompany2").val());
    company.corporateRating2 = trim($("#corporateRating2").val());
    company.corporateOrgCode2 = trim($("#corporateOrgCode2").val());
    company.companyCount = trim($("#companyCount").val());
    company.status = 0;

    //项目负责人
    var bondManagerList = [];
    var aryBondManager = $("#hidBondManId").val().split(",");
    if(null!=aryBondManager && aryBondManager.length>0){
        for(var i=0;i<aryBondManager.length;i++){
            var bondManagerJson = {};
            bondManagerJson.bondCode = trim($("#code").val());
            bondManagerJson.userId = aryBondManager[i];
            bondManagerList.push(bondManagerJson);
        }
    }

    //发行人对接人
    company.companyLinkmanList = companyLinkmanList;

    //监管银行负责人
    var bankManagerList = [];
    $("#tabBankMan tr").each(function(index, item){
        var bankManJson = {};
        bankManJson.bondCode = $("#code").val();
        bankManJson.name = $(item).find('input[name="txtBankName"]').val();
        bankManJson.phone = $(item).find('input[name="txtBankPhone"]').val();
        bankManJson.mobile = $(item).find('input[name="txtBankMobile"]').val();
        bankManJson.email = $(item).find('input[name="txtBankMail"]').val();
        bankManagerList.push(bankManJson);
    });

    bond.company = company;
    bond.bondManagerList = bondManagerList;
    bond.bankManagerList = bankManagerList;
    return bond;
}

function formValidate() {
    var icon = "<i class='fa fa-times-circle'></i> ";
    return $("#bondNewForm").validate({
        rules: {
            type: "required",
            shortname: "required",
            name: "required",
            code: "required",
            rateType: "required",
            currentBalance: "required",
            bondManager: "required",
            interestPayMode: "required",
            repayMode: "required",
            payFrequency: "required",
            timeLimit: "required",
            valueDate: "required",
            payDay: "required",
            optionType: "required",
            exerciseSchedule: "required",
            currentPeriodAmount: "required",
            rate: "required",
            listedPlace: "required",
            crossMarket: "required",
            companyName: "required",
            industryScale: "required",
            industryType: "required",
            industryBigType: "required",
            economicDepartment: "required",
            economicSector: "required",
            provinceCode: "required",
            cityCode: "required",
            scale: "required",
            price: "required",
            publishType: "required",
            fixPriceType: "required",
            underwriterName: "required",
            underwritingMode: "required",
            bondRatingCompany1: "required",
            rating1: "required",

            type: { required: true },
            shortname: { required: true },
            name: { required: true },
            code: { required: true },
            rateType: { required: true },
            currentBalance: { required: true },
            bondManager: { required: true },
            interestPayMode: { required: true },
            repayMode: { required: true },
            payFrequency: { required: true },
            timeLimit: { required: true },
            valueDate: { required: true },
            payDay: { required: true },
            optionType: { required: true },
            exerciseSchedule: { required: true },
            currentPeriodAmount: { required: true },
            rate: { required: true },
            listedPlace: { required: true },
            crossMarket: { required: true },
            companyName: { required: true },
            industryScale: { required: true },
            industryType: { required: true },
            industryBigType: { required: true },
            economicDepartment: { required: true },
            economicSector: { required: true },
            provinceCode: { required: true },
            cityCode: { required: true },
            scale: { required: true },
            price: { required: true },
            publishType: { required: true },
            fixPriceType: { required: true },
            underwriterName: { required: true },
            underwritingMode: { required: true },
            bondRatingCompany1: { required: true },
            ratingCompany1: {required: true},
            corporateRating1: {required: true},
            rating1: { required: true },


        },
        messages: {
            type: icon + "请输入债券类型",
        }
    });
}