var basePath = $("#basePath").val();
var flag = $("#flag").val();

document.oncontextmenu = function () {
    return false;
};
document.onselectstart = function () {
    return false;
};

$(document).ready(function () {

    if (flag === "1") {
        $("#shutBtn").val("断开");
    } else {
        $("#shutBtn").val("连接");
    }

    $("#shutBtn").on("click", function () {
        if (flag === "1") {
            flag = "0";
            parent.closeConnect($("#id").val());
            $("#shutBtn").val("连接");

        } else {
            flag = "1";
            parent.openConnect($("#id").val());
            $("#shutBtn").val("断开");
        }
    });

    $("#backBtn").on("click", function () {
        //关闭弹出层
        var index = parent.layer.getFrameIndex(window.name);
        parent.layer.close(index);
    });

});


