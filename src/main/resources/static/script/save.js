var basePath = $("#basePath").val();

document.oncontextmenu = function () {
    return false;
};
document.onselectstart = function () {
    return false;
};

$(document).ready(function () {

    layui.use('form', function () {
        var form = layui.form;
        //渲染表单
        form.render();
        showConView();
        form.on('select(type)', function (data) {
            if (data.value == '0') {
                showConView();
            } else {
                showSshView();
            }
        });
        //监听提交
        form.on('submit(saveBtn)', function (data) {
            saveConnect();
            return false;
        });
    });
    //注册监听事件
    $("#backBtn").on("click", function () {
        //关闭弹出层
        var index = parent.layer.getFrameIndex(window.name);
        parent.layer.close(index);
    });

});

function showConView() {
    $("#rhost").removeAttr("disabled");
    $("#rhost").attr('lay-verify', 'required');
    $(".ssh-input").attr("disabled", "disabled");
    $(".ssh-input").attr('lay-verify', '');
}

function showSshView() {
    $("#rhost").val('');
    $("#rhost").attr("disabled", "disabled");
    $("#rhost").attr('lay-verify', '');
    $(".ssh-input").removeAttr("disabled");
    $(".ssh-input").attr('lay-verify', 'required');
}

function saveConnect() {
    $.ajax({
        type: "post",
        url: basePath + '/api/connect/insert',
        data: {
            "text": $("#text").val(),
            "type": $("#type").val(),
            "isha": $("#isha").val(),
            "rhost": $("#rhost").val(),
            "rport": $("#rport").val(),
            "rpass": $("#rpass").val(),
            "sname": $("#sname").val(),
            "shost": $("#shost").val(),
            "sport": $("#sport").val(),
            "spass": $("#spass").val()
        },
        success: function (data) {
            if (data.code == 200) {
                var index = parent.layer.getFrameIndex(window.name);
                parent.layer.close(index);
                parent.getConnectData();
            } else {
                layer.alert(data.msgs, {
                    skin: 'layui-layer-lan',
                    closeBtn: 0
                });
            }
        }
    });
}