/**
 * Created by Dealing076 on 2017/1/20.
 */
//@ sourceURL=add.js
$(function () {
    editSave();
});

function editSave() {
    $("button[name='editSave']").click(function () {
        if (userValidate().form()) {
            var form = new FormData(document.getElementById("userForm"));
            $.ajax({
                url: contextPath + "/user/editSave",
                type: "post",
                data: form,
                processData: false,
                contentType: false,
                dataType: "json",
                success: function (data) {
                    if (data.code == 0) {
                        layer.close(pageIndex);
                        $('#tab').bootstrapTable('refresh');
                        layer.alert(data.message, {icon: 1, time: 3000});
                    } else {
                        layer.alert(data.message, {icon: 1, time: 3000});
                    }
                },
                error: function (e) {
                    layer.alert(data.message, {icon: 1, time: 3000});
                }
            });
        }
    });
}

function userValidate() {
    var icon = "<i class='fa fa-times-circle'></i> ";
    return $("#userForm").validate({
        rules: {
            account: "required",
            // fullName: "required",
            // shortName: "required",
            // assetScale:"required",
            // capitalAdequacyRatio:"required",
            // defectRate:"required",
            account: {
                required: true
            }
            // fullName: {
            //     required: true
            // },
            // assetScale: {
            //     digits: true,
            //     maxlength: 10
            // },
            // capitalAdequacyRatio: {
            //     number: true
            // },
            // defectRate: {
            //     number: true
            // },
            // shortName: {
            //     required: true
            // }
        },
        messages: {
            account: icon + "债券名称必输",
            // fullName: icon + "机构全称必输",
            // shortName: icon + "机构简称必输",
            // assetScale: {
            //     digits: icon + "请输入数字",
            //     maxlength: icon + "最大长度不能超过10位"
            // },
            // capitalAdequacyRatio: icon + "请输入数字",
            // defectRate: icon + "请输入数字",
        }
    });
}
