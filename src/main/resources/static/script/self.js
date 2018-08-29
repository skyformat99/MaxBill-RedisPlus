var $;
var layer1;
var form;
var basePath;

layui.use(['form', 'layedit', 'jquery', 'layer'], function () {
    $ = layui.jquery;
    form = layui.form;
    layer1 = layui.layer;
    basePath = $("#basePath").val();
    //监听提交
    form.on('submit(sendMail)', function () {
        sendMail();
    });
});

function sendMail() {
    layer1.load(2);
    $.ajax({
        type: "post",
        url: basePath + '/api/self/sendMail',
        data: {
            "mailAddr": $("#mailAddr").val(),
            "mailText": $("#mailText").val()
        },
        sync: false,
        success: function (data) {
            console.log(data);
            alert(data.msgs);
            //layer.closeAll('loading');
            layer1.alert(data.msgs, {
                skin: 'layui-layer-lan',
                closeBtn: 0
            });
        }
    });
}