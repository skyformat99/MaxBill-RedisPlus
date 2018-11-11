var $;
var form;
var layer;
var connect;

layui.use(['jquery', 'form', 'layer'], function () {
    $ = layui.jquery;
    form = layui.form;
    layer = layui.layer;
    //渲染表单
    form.render();
    loadPageData();
    showDataView();
    form.on('checkbox(type)', function (data) {
        if (!data.elem.checked) {
            showConView();
        } else {
            showSshView();
        }
    });
    form.on('checkbox(isha)', function (data) {
        if (!data.elem.checked) {
            $('.div-input05 .layui-form-checkbox[lay-skin="primary"] i').css('background', '#E0E0E2');
        } else {
            $('.div-input05 .layui-form-checkbox[lay-skin="primary"] i').css('background', '#5FB878');
        }
    });
    //监听提交
    form.on('submit(saveBtn)', function () {
        saveConnect();
        return false;
    });
    //注册监听事件
    $("#backBtn").on("click", function () {
        //关闭弹出层
        var index = parent.layer.getFrameIndex(window.name);
        parent.layer.close(index);
    });
});

/**加载页面数据*/
function loadPageData() {
    connect = JSON.parse(parent.connectRouter.querysConnect(parent.rowDataId));
    $("#text").val(connect.text);
    $("#rhost").val(connect.rhost);
    $("#rport").val(connect.rport);
    $("#rpass").val(connect.rpass);
    if (connect.type === '1') {
        $("#sname").val(connect.sname);
        $("#shost").val(connect.shost);
        $("#sport").val(connect.sport);
        $("#spass").val(connect.spass);
    }
}

/**显示数据视图*/
function showDataView() {
    if (connect.isha === '0') {
        $('.div-input05 .layui-form-checkbox[lay-skin="primary"] i').css('background', '#E0E0E2');
    } else {
        $('.div-input05 .layui-form-checkbox[lay-skin="primary"] i').css('background', '#5FB878');
    }
    var rhost = $("#rhost");
    var sshInput = $(".ssh-input");
    if (connect.type === '0') {
        rhost.removeAttr("disabled");
        rhost.attr('lay-verify', 'required');
        rhost.css('background', 'transparent');
        sshInput.val('');
        sshInput.attr("disabled", "disabled");
        sshInput.attr('lay-verify', '');
        sshInput.css('background', '#E0E0E2');
        $('.div-input04 .layui-form-checkbox[lay-skin="primary"] i').css('background', '#E0E0E2');
    } else {
        rhost.val('');
        rhost.attr("disabled", "disabled");
        rhost.css('background', '#E0E0E2');
        rhost.attr('lay-verify', '');
        sshInput.removeAttr("disabled");
        sshInput.attr('lay-verify', 'required');
        sshInput.css('background', 'transparent');
        $('.div-input04 .layui-form-checkbox[lay-skin="primary"] i').css('background', '#5FB878');
    }
}


/**显示连接视图*/
function showConView() {
    var rhost = $("#rhost");
    var sshInput = $(".ssh-input");
    rhost.removeAttr("disabled");
    rhost.attr('lay-verify', 'required');
    rhost.css('background', 'transparent');
    sshInput.attr("disabled", "disabled");
    sshInput.val('');
    sshInput.attr('lay-verify', '');
    sshInput.css('background', '#E0E0E2');
    $('.div-input04 .layui-form-checkbox[lay-skin="primary"] i').css('background', '#E0E0E2');
}


/**显示SSH视图*/
function showSshView() {
    var rhost = $("#rhost");
    var sshInput = $(".ssh-input");
    rhost.val('');
    rhost.attr("disabled", "disabled");
    rhost.css('background', '#E0E0E2');
    rhost.attr('lay-verify', '');
    sshInput.removeAttr("disabled");
    sshInput.attr('lay-verify', 'required');
    sshInput.css('background', 'transparent');
    $('.div-input04 .layui-form-checkbox[lay-skin="primary"] i').css('background', '#5FB878');
}

/**保存连接信息*/
function saveConnect() {
    var type, isha;
    ($("#type").get(0).checked) ? type = "1" : type = "0";
    ($("#isha").get(0).checked) ? isha = "1" : isha = "0";
    var data = {
        "id": parent.rowDataId,
        "type": type,
        "isha": isha,
        "text": $("#text").val(),
        "rhost": $("#rhost").val(),
        "rport": $("#rport").val(),
        "rpass": $("#rpass").val(),
        "sname": $("#sname").val(),
        "shost": $("#shost").val(),
        "sport": $("#sport").val(),
        "spass": $("#spass").val()
    }
    var json = parent.connectRouter.updateConnect(JSON.stringify(data));
    var result = JSON.parse(json);
    if (result.code === 200) {
        var index = parent.layer.getFrameIndex(window.name);
        parent.layer.close(index);
        parent.getConnectData();
    } else {
        layer.alert(result.msgs, {
            skin: 'layui-layer-lan',
            closeBtn: 0
        });
    }
}