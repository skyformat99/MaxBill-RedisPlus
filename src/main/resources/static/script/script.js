var $;
var layer;
var basePath;

layui.use(['jquery', 'layer'], function () {
    $ = layui.jquery;
    layer = layui.layer;
    basePath = $("#basePath").val();
});

function toPage(page) {
    if (page == 'root' || page == 'self') {
        window.location.href = basePath + '/' + page;
    } else {
        $.ajax({
            type: "post",
            url: basePath + '/api/connect/isopen',
            sync: false,
            success: function (data) {
                if (data == 1) {
                    window.location.href = basePath + '/' + page;
                } else {
                    layer.alert('请先连接一个可用的服务！', {
                        skin: 'layui-layer-lan',
                        closeBtn: 0
                    });
                }
            }
        });
    }
}