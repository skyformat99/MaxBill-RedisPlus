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
                var index = parent.layer.getFrameIndex(window.name);
                parent.layer.style(index, {
                    'height': '390px'
                });
            } else {
                showSshView();
                var index = parent.layer.getFrameIndex(window.name);
                parent.layer.style(index, {
                    'height': '550px',
                    'position': 'absolute',
                    'top': '20%'
                });
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
    $("#rhost-box").show();
    $("#spass-box").hide();
    $("#sname-box").hide();
    $("#shost-box").hide();
    $("#sport-box").hide();
    $("#sname-box input").attr('lay-verify', '');
    $("#shost-box input").attr('lay-verify', '');
    $("#sport-box input").attr('lay-verify', '');
    $("#spass-box input").attr('lay-verify', '');
    $("#rhost-box input").attr('lay-verify', 'required');
}

function showSshView() {
    $("#rhost-box").hide();
    $("#sname-box").show();
    $("#shost-box").show();
    $("#sport-box").show();
    $("#spass-box").show();
    $("#rhost-box input").attr('lay-verify', '');
    $("#sname-box input").attr('lay-verify', 'required');
    $("#shost-box input").attr('lay-verify', 'required');
    $("#sport-box input").attr('lay-verify', 'required');
    $("#spass-box input").attr('lay-verify', 'required');
}

function saveConnect() {
    $.ajax({
        type: "post",
        url: basePath + '/api/connect/insert',
        data: {
            "text": $("#text").val(),
            "type": $("#type").val(),
            "name": $("#sname").val(),
            "rhost": $("#rhost").val(),
            "rport": $("#rport").val(),
            "rpass": $("#rpass").val(),
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