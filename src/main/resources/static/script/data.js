var currKey;
var currIndex;
var basePath = $("#basePath").val();

$(document).ready(function () {
    loadKeyTree();
});

var zTreeSetting = {
    check: {
        enable: false,
    },
    data: {
        simpleData: {
            enable: true
        }
    },
    view: {
        showLine: false
    },
    async: {
        enable: true,
        dataType: "json",
        url: basePath + '/api/data/treeData',
        autoParam: ["id", "index"],
        dataFilter: filter
    },
    callback: {
        onClick: zTreeOnClick
    }
};

//过滤异步加载ztree时返回的数据
function filter(treeId, parentNode, childNodes) {
    return childNodes.data;
}

function zTreeOnClick(event, treeId, treeNode) {
    if (!treeNode.isParent) {
        currKey = treeNode.name;
        currIndex = treeNode.index;
        getKeyInfo();
    }
};

function loadKeyTree() {
    $.ajax({
        type: "get",
        url: basePath + '/api/data/treeInit',
        sync: false,
        success: function (data) {
            for (var i = 0; i < 16; i++) {
                $.fn.zTree.init($("#keyTree" + i), zTreeSetting, data.data[i]);
                $.fn.zTree.getZTreeObj("keyTree" + i).expandAll(false);
            }
        }
    });
}

//重命名key
function renameKey() {
    if (currKey == "" || currKey == null) {
        layer.alert("请选择要操作的key！", {
            skin: 'layui-layer-lan',
            closeBtn: 0
        });
        return false;
    }
    layer.prompt(
        {
            title: '输入新的key',
            formType: 3,
            value: currKey,
            skin: 'layui-layer-lan',
            closeBtn: 0,
        },
        function (text, index) {
            $.ajax({
                type: "post",
                url: basePath + "/api/data/renameKey",
                data: {
                    'oldKey': currKey,
                    'newKey': text,
                    'index': currIndex
                },
                sync: false,
                success: function (data) {
                    layer.close(index);
                    if (data.code == 200) {
                        currKey = text;
                        getKeyInfo();
                        loadKeyTree();
                    } else {
                        layer.alert(data.msgs);
                    }
                }
            });
        }
    );
}

//删除key
function deleteKey() {
    if (currKey == "" || currKey == null) {
        layer.alert("请选择要操作的key！", {
            skin: 'layui-layer-lan',
            closeBtn: 0
        });
        return false;
    }
    var index = layer.confirm('确认删除该项？', {
        btn: ['确定', '取消'],
        skin: 'layui-layer-lan',
        closeBtn: 0
    }, function () {
        $.ajax({
            type: "post",
            url: basePath + "/api/data/deleteKey",
            data: {
                'key': currKey,
                'index': currIndex
            },
            sync: false,
            success: function (data) {
                layer.close(index);
                if (data.code == 200) {
                    currKey = "";
                    currKey = "";
                    loadKeyTree();
                    $("#key").text("");
                    $("#type").text("");
                    $("#size").text("");
                    $("#ttl").text("");
                    $("#value").text("");
                } else {
                    layer.alert(data.msgs);
                }
            }
        });
    });
}

//重新加载key
function reloadKey() {
    if (currKey == "" || currKey == null) {
        layer.alert("请选择要操作的key！", {
            skin: 'layui-layer-lan',
            closeBtn: 0
        });
        return false;
    }
    getKeyInfo();
}

//获取key信息
function getKeyInfo() {
    if (currKey == "" || currKey == null) {
        layer.alert("请选择要操作的key！", {
            skin: 'layui-layer-lan',
            closeBtn: 0
        });
        return false;
    }
    $.ajax({
        type: "post",
        url: basePath + '/api/data/keysData',
        data: {
            'key': currKey,
            'index': currIndex
        },
        sync: false,
        success: function (data) {
            var keyInfo = data.data;
            if (data.code == 200 && keyInfo) {
                $("#key").text(keyInfo.key);
                $("#type").text(keyInfo.type);
                $("#size").text(keyInfo.size);
                $("#ttl").text(keyInfo.ttl);
                $("#value").text(keyInfo.value);
            }
        }
    });
}