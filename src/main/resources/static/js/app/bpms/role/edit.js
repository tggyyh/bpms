/**
 * Created by Dealing076 on 2017/1/20.
 */
//@ sourceURL=add.js
$(function () {
    editSave();
});

function editSave() {
    $("button[name='editSave']").click(function () {
        if (roleValidate().form()) {
            var form = new FormData(document.getElementById("roleForm"));
            $.ajax({
                url: contextPath + "/role/editSave",
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

function roleValidate() {
    var icon = "<i class='fa fa-times-circle'></i> ";
    return $("#roleForm").validate({
        rules: {
            name: "required",
            code: "required",
            name: {
                required: true
            },
            code: {
                required: true
            }
        },
        messages: {
            name: icon + "名称必输",
            code: icon + "角色码必输",
        }
    });
}
