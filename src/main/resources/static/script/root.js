var $;
var layer;
var table;
var basePath;
var rowDataId;

layui.use(['jquery', 'table', 'layer'], function () {
    $ = layui.jquery;
    layer = layui.layer;
    table = layui.table;
    basePath = $("#basePath").val();
    //加载连接数据
    layer.load(2);
    table.render({
        id: 'dataList',
        elem: '#dataList',
        height: 700,
        type: 'post',
        url: basePath + '/api/connect/select',
        cols: [[
            {field: 'name', title: '名称', event: 'setSign'},
            {field: 'host', title: '主机', event: 'setSign'},
            {field: 'port', title: '端口', event: 'setSign'},
            {field: 'time', title: '时间', event: 'setSign'},
        ]],
        response: {
            statusName: 'code',
            statusCode: 200,
            countName: 'count',
            msgName: 'msgs',
            dataName: 'data'
        },
        page: false,
        done: function (res) {
            layer.closeAll('loading');
            var tbody = $('#tableDiv').find('.layui-table-body').find("table").find("tbody");
            //单击行选中数据
            tbody.children("tr").on('click', function () {
                var id = JSON.stringify(tbody.find(".layui-table-hover").data('index'));
                var obj = res.data[id];
                rowDataId = obj.id;
            });
            //双击行连接服务
            tbody.children("tr").on('click', function () {
                var id = JSON.stringify(tbody.find(".layui-table-hover").data('index'));
                var obj = res.data[id];

            });
        }
    });

});


/**
 * 添加连接数据
 */
function addConnectData() {
    layer.open({
        title: '新增连接',
        type: 2,
        area: ['345px', '345px'],
        fixed: true,
        maxmin: false,
        skin: 'layui-layer-lan',
        content: basePath + '/root/save'
    });
}

/**
 * 刷新连接列表
 */
function getConnectData() {
    layer.load(2);
    table.reload("dataList", {
        done: function () {
            layer.closeAll('loading');
        }
    });
}

/**
 * 编辑连接数据
 */
function updConnectData() {
    if (rowDataId == "" || rowDataId == null) {
        layer.alert("请选择要操作的数据行！", {
            skin: 'layui-layer-lan',
            closeBtn: 0
        });
        return false;
    }
    layer.open({
        title: '编辑连接',
        type: 2,
        area: ['345px', '345px'],
        fixed: true,
        maxmin: false,
        skin: 'layui-layer-lan',
        content: basePath + '/root/edit?id=' + rowDataId
    });
}

/**
 * 删除连接数据
 */
function delConnectData() {
    if (rowDataId == "" || rowDataId == null) {
        layer.alert("请选择要操作的数据行！", {
            skin: 'layui-layer-lan',
            closeBtn: 0
        });
        return false;
    }
    $.ajax({
        type: "post",
        url: basePath + '/api/connect/delete',
        data: {"id": rowDataId},
        success: function (data) {
            if (data.code == 200) {
                getConnectData();
            } else {
                layer.alert(data.msgs, {
                    skin: 'layui-layer-lan',
                    closeBtn: 0
                });
            }
        }
    });
}




