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
    var xhr = $.ajax({
        type: "post",
        url: basePath + '/api/self/sendMail',
        data: {
            "mailAddr": $("#mailAddr").val(),
            "mailText": $("#mailText").val()
        },
        timeout: 10000,
        success: function (data) {
            $("#mailAddr").val('');
            $("#mailText").val('');
            layer.closeAll('loading');
            layer.alert(data.msgs, {
                skin: 'layui-layer-lan',
                closeBtn: 0
            });
        },
        complete: function (XMLHttpRequest, status) {
            //请求完成后最终执行参数
            if (status == 'timeout') {
                //超时,status还有success,error等值的情况
                xhr.abort();
                layer.alert("请求超时，请检查网络连接", {
                    skin: 'layui-layer-lan',
                    closeBtn: 0
                });
            }
        }
    });
}