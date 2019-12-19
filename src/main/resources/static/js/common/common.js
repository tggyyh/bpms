/**
 * Created by Dealing076 on 2017/2/13.
 */

//获取当前日期 yyyy-mm-dd
function getFormatDate() {
    var date = new Date();
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentdate = date.getFullYear() + "-" + month + "-" + strDate;
    return currentdate;
}
//根据日期加减天数后的日期
function addDate(dd,dadd){
    var a = new Date(dd)
    a = a.valueOf()
    a = a + dadd * 24 * 60 * 60 * 1000
    a = new Date(a)
    return a;
}
//获取当前日期时间 yyyy-mm-dd hh:mm:ss
function getFormatDatetime() {
    var date = new Date();
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    var hours = date.getHours();
    var minutes = date.getMinutes();
    var seconds = date.getSeconds();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    if (hours >= 0 && hours <= 9) {
        hours = "0" + hours;
    }
    if (minutes >= 0 && minutes <= 9) {
        minutes = "0" + minutes;
    }
    if (seconds >= 0 && seconds <= 9) {
        seconds = "0" + seconds;
    }
    var currentdate = date.getFullYear() + "-" + month + "-" + strDate + " "
        + hours + ":" + minutes + ":" + seconds;
    return currentdate;
}

//根据时间戳生成的日期对象 yyyy-mm-dd
function timestampToDate(datetime){
    var d = new Date(datetime);
    var date =
        (d.getFullYear()) + "-" +
        (d.getMonth()+1 < 10 ? '0'+(d.getMonth()+1) : d.getMonth()+1) + "-" +
        (d.getDate() < 10 ? '0'+(d.getDate()) : d.getDate())
    return date;
}

//根据时间戳生成的日期时间对象 yyyy-mm-dd hh:mm:ss
function timestampToDatetime(datetime){
	if(datetime==null){
		return;
	}
    var d = new Date(datetime);
    var date = (d.getFullYear()) + "-" +
        (d.getMonth()+1 < 10 ? '0'+(d.getMonth()+1) : d.getMonth()+1) + "-" +
        (d.getDate() < 10 ? '0'+(d.getDate()) : d.getDate()) + " " +
        (d.getHours() < 10 ? '0'+(d.getHours()) : d.getHours()) + ":" +
        (d.getMinutes() < 10 ? '0'+(d.getMinutes()) : d.getMinutes()) + ":" +
        (d.getSeconds() < 10 ? '0'+(d.getSeconds()) : d.getSeconds());
    return date;
}

// 对Date的扩展，将 Date 转化为指定格式的String
// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，
// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
// 例子：
// (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423
// (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18
Date.prototype.Format = function (fmt) { //author: meizz
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "h+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}

//正则：数字，字母，下划线
function regNumLetterLine(data){
    var regex = /^\w+$/ig;
    var rValue = regex.test(data);
    return rValue;
}
//正则：座机
function regPhone(data) {
    var regex = /^((0\d{2,3})-)(\d{7,8})(-(\d{3,}))?$/;
    var rValue = regex.test(data);
    return rValue;
}

//是否为正整数
function isPositiveInteger(s){
    var re = /^[0-9]+$/ ;
    return re.test(s)
}

//email验证
function isEmail(str){
    //var reg = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/;
    var reg = /^[a-zA-Z0-9]+([._\\-]*[a-zA-Z0-9])*@([a-zA-Z0-9]+[-a-zA-Z0-9]*[a-zA-Z0-9]+.){1,63}[a-zA-Z0-9]+$/;
    return reg.test(str);
}

//获取文件后辍
function getFileType(filePath){
    var startIndex = filePath.lastIndexOf(".");
    if(startIndex != -1)
        return filePath.substring(startIndex+1, filePath.length).toLowerCase().toLowerCase();
    else return "";
}

//获取文件图标类型
function getTmpFileIcon(fileType){
    if(null==fileType || ""==fileType){
        return "";
    }
    var fType = fileType.toLocaleString(fileType);
    var tmpIcon = {
        "txt":"txt", "doc":"word", "docx":"word", "xls":"excel", "xlsx":"excel",
        "pdf":"pdf", "zip":"rar", "rar":"rar",
        "png":"img", "jpg":"img", "jpeg":"img", "gif":"img", "bmp":"img", "tif":"img", "psd":"img"};
    if(tmpIcon[fType]==undefined){
        return "other";
    }
    return tmpIcon[fType];
}

function stringToBytes( str ) {

    var ch, st, re = [];
    for (var i = 0; i < str.length; i++ ) {
        ch = str.charCodeAt(i);  // get char
        st = [];                 // set up "stack"

        do {
            st.push( ch & 0xFF );  // push byte to stack
            ch = ch >> 8;          // shift value down by 1 byte
        }

        while ( ch );
        // add stack contents to result
        // done because chars have "wrong" endianness
        re = re.concat( st.reverse() );
    }
    // return an array of bytes
    return re;
}

function byteToString(arr) {
    if(typeof arr === 'string') {
        return arr;
    }
    var str = '',
        _arr = arr;
    for(var i = 0; i < _arr.length; i++) {
        var one = _arr[i].toString(2),
            v = one.match(/^1+?(?=0)/);
        if(v && one.length == 8) {
            var bytesLength = v[0].length;
            var store = _arr[i].toString(2).slice(7 - bytesLength);
            for(var st = 1; st < bytesLength; st++) {
                store += _arr[st + i].toString(2).slice(2);
            }
            str += String.fromCharCode(parseInt(store, 2));
            i += bytesLength - 1;
        } else {
            str += String.fromCharCode(_arr[i]);
        }
    }
    return str;
}

//去两边空格
function trim(str){
    if(null==str){
        return "";
    }
    return str.replace(/(^\s*)|(\s*$)/g, "")
}

//替换特殊字符为空 ./\?&+#=:!@%
function replaceCharacter(character){
    if(character==null || character==undefined || character==""){
        return "";
    }else{

        character = character.replace(/\./g, "");//清除“.”
        character = character.replace(/\//g, "");//清除“/”
        character = character.replace(/\?/g, "");//清除“?”
        character = character.replace(/\？/g, "");//清除“?”
        character = character.replace(/\&/g, "");//清除“&”
        character = character.replace(/\+/g, "");//清除“+”
        character = character.replace(/\#/g, "");//清除“#”
        character = character.replace(/\=/g, "");//清除“=”
        character = character.replace(/\:/g, "");//清除“:”
        character = character.replace(/\\/g, "");//清除“\”
        character = character.replace(/\!/g, "");//清除“!”
        character = character.replace(/\@/g, "");//清除“@”
        character = character.replace(/\%/g, "");//清除“%”
        character = character.replace(/\;/g, "");//清除“;”
        character = character.replace(/\；/g, "");//清除“；”
        character = character.replace(/\，/g, ",");//清除“%”；
        return character;
    }
}
function replaceCharacter1(character){
    if(character==null || character==undefined || character==""){
        return "";
    }else{
        character = character.replace(/\//g, "");//清除“/”
        character = character.replace(/\?/g, "");//清除“?”
        character = character.replace(/\？/g, "");//清除“?”
        character = character.replace(/\&/g, "");//清除“&”
        character = character.replace(/\+/g, "");//清除“+”
        character = character.replace(/\#/g, "");//清除“#”
        character = character.replace(/\=/g, "");//清除“=”
        character = character.replace(/\:/g, "");//清除“:”
        character = character.replace(/\\/g, "");//清除“\”
        character = character.replace(/\!/g, "");//清除“!”
        character = character.replace(/\@/g, "");//清除“@”
        character = character.replace(/\%/g, "");//清除“%”
        character = character.replace(/\;/g, "");//清除“;”
        character = character.replace(/\；/g, "");//清除“;”
        character = character.replace(/\，/g, ",");//清除“%”；
        return character;
    }
}

function replaceDecimal(decimalNum) {
    if(null==decimalNum || decimalNum==undefined || decimalNum==""){
        return decimalNum;
    }else{
        decimalNum = decimalNum.replace(/[^\d.]/g, "");//清除“数字”和“.”以外的字符
        decimalNum = decimalNum.replace(/^\./g, "");//验证第一个字符是数字而不是.
        decimalNum = decimalNum.replace(/\.{2,}/g, ".");//只保留第一个. 清除多余的.
        decimalNum = decimalNum.replace(".", "$#$").replace(/\./g,"").replace("$#$", ".");
        return decimalNum;
    }
}