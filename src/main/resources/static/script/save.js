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

function saveConnect() {
    $.ajax({
        type: "post",
        url: basePath + '/api/connect/insert',
        data: {
            "name": $("#name").val(),
            "host": $("#host").val(),
            "port": $("#port").val(),
            "pass": $("#pass").val()
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