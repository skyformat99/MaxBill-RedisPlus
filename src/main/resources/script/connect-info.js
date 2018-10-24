var $;
var flag;
var layer;
var form;
var connect;

document.oncontextmenu = function () {
    return false;
};
document.onselectstart = function () {
    return false;
};

layui.use(['jquery', 'form', 'layer'], function () {
    $ = layui.jquery;
    layer = layui.layer;
    form = layui.form;
    connect = JSON.parse(parent.connectRouter.querysConnect(parent.rowDataId));
    flag = parent.connectRouter.isopenConnect();
    $("#name").text(connect.text);
    $("#addr").text(connect.rhost + ":" + connect.rport);
    if (connect.isha == 1) {
        $("#isha").text("集群模式");
    } else {
        $("#isha").text("单机模式");
    }
    if (flag === 1) {
        $("#shutBtn").val("断开");
        if (connect.isha === "1") {
            $(".node-hide").css("display", "block");
            getNodeInfo();
        }
    } else {
        $("#shutBtn").val("连接");
        if (connect.isha === "1") {
            $(".node-hide").css("display", "none");
        }
    }

    $("#shutBtn").on("click", function () {
        if (flag === 1) {
            var result = parent.closeConnect(connect.id);
            if (result === 1) {
                flag = 0;
                $("#shutBtn").val("连接");
                if (connect.isha === "1") {
                    $(".node-hide").css("display", "none");
                }
            }
        } else {
            var result = parent.openConnect(connect.id);
            if (result === 1) {
                flag = 1;
                $("#shutBtn").val("断开");
                if (connect.isha === "1") {
                    $(".node-hide").css("display", "block");
                    getNodeInfo();
                }
            }
        }
    });

    $("#backBtn").on("click", function () {
        //关闭弹出层
        var index = parent.layer.getFrameIndex(window.name);
        parent.layer.close(index);
    });
});


function getNodeInfo() {
    $.ajax({
        type: "post",
        url: basePath + '/api/many/nodeInfo',
        async: false,
        success: function (data) {
            if (data.code == 200 && data.data) {
                var html = '';
                for (var i = 0; i < data.data.length; i++) {
                    html += '<tr>';
                    html += '<td>' + data.data[i].addr + '</td>';
                    html += '<td>' + data.data[i].flag + '</td>';
                    html += '</tr>';
                }
                $("#node-body").html(html);
            }
        }
    });
}


