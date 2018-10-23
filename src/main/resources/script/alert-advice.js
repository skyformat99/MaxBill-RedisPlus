var $;
var form;
var layer;

layui.use(['form', 'layer', 'jquery'], function () {
    $ = layui.jquery;
    form = layui.form;
    layer = layui.layer;
    form.on('submit(sendMail)', function () {
        sendMail();
        return false;
    });
});

function sendMail() {
    $("#sendMail").text("发送中...").attr("disabled", "disabled").addClass("layui-disabled");
    layer.load(2);
    var addr = $("#mailAddr").val();
    var text = $("#mailText").val();
    var resultJson = otherRouter.sendMail(addr, text);
    var result = JSON.parse(resultJson);
    if (result.code == 200) {
        layer.msg(result.msgs);
        $("#mailAddr").val('');
        $("#mailText").val('');
    } else {
        layer.alert(result.msgs, {
            skin: 'layui-layer-lan',
            closeBtn: 0
        });
    }
    layer.closeAll('loading');
    $("#sendMail").text("发送邮件").removeAttr("disabled").removeClass("layui-disabled");
}