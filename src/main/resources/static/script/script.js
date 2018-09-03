var $;
var layer;
var basePath;


document.oncontextmenu = function () {
    return false;
};
document.onselectstart = function () {
    return false;
};

layui.use(['jquery', 'layer'], function () {
    $ = layui.jquery;
    layer = layui.layer;
    basePath = $("#basePath").val();
    //动态计算handle高度
    var clientHeight = $(document).height();
    var handleHeight = clientHeight - 100;
    $(".handle").height(handleHeight);
});

function toPage(page) {
    if (page == 'root' || page == 'self') {
        window.location.href = basePath + '/' + page;
    } else {
        var xhr = $.ajax({
            type: "post",
            url: basePath + '/api/connect/isopen',
            timeout: 10000,
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
}