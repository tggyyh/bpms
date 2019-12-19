//@ sourceURL=rolepermission.js
$(function () {
    initTable();
    initButton();
});

function initTable() {
    var roleId = $("#roleId").val();
    $.ajax({
        url: contextPath + 'module/findAllAddPremission/' + roleId,
        type: 'post',
        dataType: 'json',
        success: function (data) {
            createTr(data);
        },
        error: function (e) {
            layer.alert('加载数据失败', {icon: 1, time: 3000});
        }
    });
}

function createTr(modules) {
    for (var i = 0; i < modules.length; i++) {
        var module = modules[i];
        var tr = '<tr id="tr_' + module.id + '">' +
            '<td > <input type="hidden" name="module" id="module_' + module.id + '"value="' + module.id +
            '" />' + module.name + '</td>' +
            '<td >';
        var permissions = module.permissions;
        for (var j = 0; j < permissions.length; j++) {
            var permission = permissions[j];
            var input = "";
            if (permission.checked) {
                input = '<input type="checkbox" name="permission" id="permission_' + permission.id + '" value="' + permission.id +
                    '" checked=""/>' + permission.name;
            } else {
                input = '<input type="checkbox" name="permission" id="permission_' + permission.id + '" value="' + permission.id +
                    '"/>' + permission.name;
            }

            tr += input;
        }
        tr = tr + '</td></tr>';
        $("#roleBody").append(tr);
        if (module.modules != null && module.modules.length > 0) {
            createTr(module.modules);
        }
    }
}


function initButton() {
    $("#save").click(function () {
        var roleId = $("#roleId").val();
        var permissionIds = [];
        $("input[name='permission']:checked").each(function(){
            permissionIds.push($(this).val());
        });
        if(permissionIds.length==0){
            permissionIds.push(0);
        }
        $.ajax({
            url: contextPath + "/role/saveRolePermission/"+roleId+"/"+permissionIds,
            type: "post",
            processData: false,
            contentType: false,
            success: function (data) {
                if (data.code == 0) {
                    layer.close(pageEditIndex);
                    layer.alert(data.message, {icon: 1, time: 3000});
                } else {
                    layer.alert(data.message, {icon: 1, time: 3000});
                }
            },
            error: function (e) {
                layer.alert('保存失败', {icon: 1, time: 3000});
            }
        });
    });
}
