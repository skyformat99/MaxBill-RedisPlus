var $;
var form;
var layer;

layui.use(['jquery', 'form', 'layer'], function () {
    $ = layui.jquery;
    form = layui.form;
    layer = layui.layer;
    form.render();

    form.on('select(type)', function (data) {
        var mkey = $("#mkey");
        var mval = $("#mval");
        var vals = $("#vals");
        var hashData = $("#hash-data");
        var elseData = $("#else-data");
        if (data.value === '4') {
            hashData.css('display', 'block');
            elseData.css('display', 'none');
            vals.attr('lay-verify', '');
            mkey.attr('lay-verify', 'required');
            mval.attr('lay-verify', 'required');
        } else {
            hashData.css('display', 'none');
            elseData.css('display', 'block');
            vals.attr('lay-verify', 'required');
            mkey.attr('lay-verify', '');
            mval.attr('lay-verify', '');
        }
    });

    //监听提交
    form.on('submit(submitBtn)', function () {
        var keys = $("#keys").val();
        var type = $("#type").val();
        var time = $("#time").val();
        var checkFlag = /^(0|[1-9][0-9]*)$/.test(time);
        if (!checkFlag) {
            layer.msg('只能输入整数值');
            return;
        }
        var vals;
        if (type === '4') {
            vals = $("#mkey").val() + ":" + $("#mval").val();
        } else {
            vals = $("#vals").val();
        }
        layer.load(2);
        //判断是集群还是单机
        var db;
        if (parent.currNode0 == null) {
            db = 0;
        } else {
            db = parent.currNode0.index;
        }
        var json = dataSinglesRouter.insertKey(type, db, keys, vals, time);
        var data = JSON.parse(json);
        layer.closeAll('loading');
        layer.close(index);
        if (data.code === 200) {
            layer.msg(data.msgs);
        } else {
            layer.alert(data.msgs, {
                skin: 'layui-layer-lan',
                closeBtn: 0
            });
        }
        return false;
    });

    //注册监听返回事件
    $("#gobackBtn").on("click", function () {
        var index = parent.layer.getFrameIndex(window.name);
        parent.layer.close(index);
        return false;
    });

});
