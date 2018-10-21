var basePath = $("#basePath").val();
var type = $("#type").val();
var isha = $("#isha").val();

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
        showDataView();
        //监听提交
        form.on('submit(saveBtn)', function () {
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

function showDataView() {
    if (isha == '0') {
        $('.div-input05 .layui-form-checkbox[lay-skin="primary"] i').css('background', '#E3DFDD');
    } else {
        $('.div-input05 .layui-form-checkbox[lay-skin="primary"] i').css('background', '#5FB878');
    }
    if (type == '0') {
        $("#rhost").removeAttr("disabled");
        $("#rhost").attr('lay-verify', 'required');
        $("#rhost").css('background', 'transparent');
        $(".ssh-input").val('');
        $(".ssh-input").attr("disabled", "disabled");
        $(".ssh-input").attr('lay-verify', '');
        $(".ssh-input").css('background', '#EEEAE6');
        $('.div-input04 .layui-form-checkbox[lay-skin="primary"] i').css('background', '#E3DFDD');
    } else {
        $("#rhost").val('');
        $("#rhost").attr("disabled", "disabled");
        $("#rhost").css('background', '#EEEAE6');
        $("#rhost").attr('lay-verify', '');
        $(".ssh-input").removeAttr("disabled");
        $(".ssh-input").attr('lay-verify', 'required');
        $(".ssh-input").css('background', 'transparent');
        $('.div-input04 .layui-form-checkbox[lay-skin="primary"] i').css('background', '#5FB878');
    }
}


function saveConnect() {
    $.ajax({
        type: "post",
        url: basePath + '/api/connect/update',
        data: {
            "id": $("#id").val(),
            "text": $("#text").val(),
            "type": $("#type").val(),
            "isha": $("#isha").val(),
            "sname": $("#sname").val(),
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