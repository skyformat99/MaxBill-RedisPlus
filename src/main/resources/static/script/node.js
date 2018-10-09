var basePath = $("#basePath").val();
var flag = $("#flag").val();
var isha = $("#isha").val();

document.oncontextmenu = function () {
    return false;
};
document.onselectstart = function () {
    return false;
};

$(document).ready(function () {

    if (flag === "1") {
        $("#shutBtn").val("断开");
        if (isha === "1") {
            $(".node-hide").css("display", "block");
            getNodeInfo();
        }
    } else {
        $("#shutBtn").val("连接");
        if (isha === "1") {
            $(".node-hide").css("display", "none");
        }
    }

    $("#shutBtn").on("click", function () {
        if (flag === "1") {
            var result = parent.closeConnect($("#id").val());
            if (result === 1) {
                flag = "0";
                $("#shutBtn").val("连接");
                if (isha === "1") {
                    $(".node-hide").css("display", "none");
                }
            }
        } else {
            var result = parent.openConnect($("#id").val());
            if (result === 1) {
                flag = "1";
                $("#shutBtn").val("断开");
                if (isha === "1") {
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


function delNodeInfo(id) {
    alert(id);
}


