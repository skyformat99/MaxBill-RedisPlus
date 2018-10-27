var $;
var layer;
var table;
var rowDataId;
var data = null;

layui.use(['jquery', 'table', 'layer'], function () {
    $ = layui.jquery;
    layer = layui.layer;
    table = layui.table;
    data = connectRouter.selectConnect();
    if (data == null) {
        connectRouter.changeWebview(1);
    }
    initConnectData();
});

/*初始化连接数据*/
function initConnectData() {
    table.render({
        id: 'dataList',
        elem: '#dataList',
        height: 'full-70',
        data: JSON.parse(data),
        cols: [[
            {field: 'text', title: '名称', event: 'setSign'},
            {
                title: '主机', event: 'setSign',
                templet: function (data) {
                    if (data.type === '0') {
                        return data.rhost;
                    } else {
                        return data.shost;
                    }
                }
            },
            {field: 'rport', title: '端口', event: 'setSign'},
            {
                title: '类型', event: 'setSign',
                templet: function (data) {
                    if (data.isha === '0') {
                        return "单机";
                    } else {
                        return "集群";
                    }
                }
            },
            {
                title: 'SSH', event: 'setSign',
                templet: function (data) {
                    if (data.type == '0') {
                        return "关闭";
                    } else {
                        return "启用";
                    }
                }
            },
            {field: 'time', title: '时间', event: 'setSign'},
        ]],
        page: true,
        done: function (res) {
            layer.msg('双击行连接服务!');
            var tbody = $('#tableDiv').find('.layui-table-body').find("table").find("tbody");
            //单击行选中数据
            tbody.children("tr").on('click', function () {
                var id = JSON.stringify(tbody.find(".layui-table-hover").data('index'));
                var obj = res.data[id];
                rowDataId = obj.id;
            });
            // //双击行连接服务
            tbody.children("tr").on('dblclick', function () {
                var id = JSON.stringify(tbody.find(".layui-table-hover").data('index'));
                var obj = res.data[id];
                openConnect(obj.id);
            });
            rowDataId = '';
        }
    });
}

/**添加连接数据*/
function addConnectData() {
    layer.open({
        title: '新增连接',
        type: 2,
        area: ['455px', '445px'],
        fixed: true,
        maxmin: false,
        resize: false,
        skin: 'layui-layer-lan',
        content: '../page/connect-save.html'
    });
}

/**刷新连接列表*/
function getConnectData() {
    layer.load(2);
    var data = connectRouter.selectConnect();
    table.reload({
        height: 'full-70',
        data: JSON.parse(data),
        page: {curr: 1},
        done: function (res) {
            var tbody = $('#tableDiv').find('.layui-table-body').find("table").find("tbody");
            //单击行选中数据
            tbody.children("tr").on('click', function () {
                var id = JSON.stringify(tbody.find(".layui-table-hover").data('index'));
                var obj = res.data[id];
                rowDataId = obj.id;
            });
            // //双击行连接服务
            tbody.children("tr").on('dblclick', function () {
                var id = JSON.stringify(tbody.find(".layui-table-hover").data('index'));
                var obj = res.data[id];
                openConnect(obj.id);
            });
            rowDataId = '';
            layer.closeAll('loading');
        }
    });
}

/**编辑连接数据*/
function updConnectData() {
    if (rowDataId == "" || rowDataId == null) {
        layer.msg('请选择要操作的数据行！');
        return false;
    }
    layer.open({
        title: '编辑连接',
        type: 2,
        area: ['455px', '445px'],
        fixed: true,
        maxmin: false,
        resize: false,
        skin: 'layui-layer-lan',
        content: '../page/connect-edit.html'
    });
}

/**删除连接数据*/
function delConnectData() {
    if (rowDataId == "" || rowDataId == null) {
        layer.msg('请选择要操作的数据行！');
        return false;
    }
    var index = layer.confirm('确认删除连接？', {
        btn: ['确定', '取消'],
        skin: 'layui-layer-lan',
        closeBtn: 0
    }, function () {
        var resultJson = parent.connectRouter.deleteConnect(rowDataId)
        var result = JSON.parse(resultJson);
        layer.close(index);
        if (result.code == 200) {
            getConnectData();
        } else {
            layer.alert(result.msgs, {
                skin: 'layui-layer-lan',
                closeBtn: 0
            });
        }
    });
}


/**操作连接数据*/
function setConnectData() {
    if (rowDataId == "" || rowDataId == null) {
        layer.msg('请选择要操作的数据行！');
        return false;
    }
    layer.open({
        title: '操作连接',
        type: 2,
        area: ['455px', '500px'],
        fixed: true,
        maxmin: false,
        skin: 'layui-layer-lan',
        content: '../page/connect-info.html'
    });
}

/**打开连接数据*/
function openConnect(id) {
    var result = 0;
    layer.load(2);
    var resultJson = connectRouter.createConnect(id);
    var result = JSON.parse(resultJson);
    if (result.code == 200) {
        result = 1;
    } else {
        layer.alert(result.msgs, {
            skin: 'layui-layer-lan',
            closeBtn: 0
        });
    }
    layer.closeAll('loading');
    return result;
}

/**断开连接数据*/
function closeConnect(id) {
    var result = 0;
    layer.load(2);
    var resultJson = connectRouter.disconConnect(id);
    var result = JSON.parse(resultJson);
    if (result.code == 200) {
        result = 1;
    }
    layer.closeAll('loading');
    return result;
}






