var form;

layui.use(['form'], function () {
    var form = layui.form;
    //监听提交
    form.on('submit(sendMail)', function () {
        sendMail();
        return false;
    });
});

function sendMail() {
    layer.load(2);
    $.ajax({
        type: "post",
        url: basePath + '/api/self/sendMail',
        data: {
            "mailAddr": $("#mailAddr").val(),
            "mailText": $("#mailText").val()
        },
        success: function (data) {
            $("#mailAddr").val('');
            $("#mailText").val('');
            layer.closeAll('loading');
            layer.alert(data.msgs, {
                skin: 'layui-layer-lan',
                closeBtn: 0
            });
        }
    });
}