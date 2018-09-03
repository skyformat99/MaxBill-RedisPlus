var currKey;
var currIndex = -1;
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
    } else {
        currIndex = treeNode.index;
    }
};

function loadKeyTree() {
    var xhr = $.ajax({
        type: "get",
        url: basePath + '/api/data/treeInit',
        sync: true,
        timeout: 15000,
        success: function (data) {
            for (var i = 0; i < 16; i++) {
                $.fn.zTree.init($("#keyTree" + i), zTreeSetting, data.data[i]);
                $.fn.zTree.getZTreeObj("keyTree" + i).expandAll(false);
            }
        },
        complete: function (XMLHttpRequest, status) {
            //请求完成后最终执行参数
            if (status == 'timeout') {
                xhr.abort();
                //超时,status还有success,error等值的情况
                layer.alert("请求超时，请检查网络连接", {
                    skin: 'layui-layer-lan',
                    closeBtn: 0
                });
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
            var xhr = $.ajax({
                type: "post",
                url: basePath + "/api/data/renameKey",
                data: {
                    'oldKey': currKey,
                    'newKey': text,
                    'index': currIndex
                },
                timeout: 10000,
                sync: false,
                success: function (data) {
                    layer.close(index);
                    if (data.code == 200) {
                        currKey = text;
                        getKeyInfo();
                        loadKeyTree();
                    } else {
                        layer.alert(data.msgs, {
                            skin: 'layui-layer-lan',
                            closeBtn: 0
                        });
                    }
                },
                complete: function (XMLHttpRequest, status) {
                    //请求完成后最终执行参数
                    if (status == 'timeout') {
                        xhr.abort();
                        //超时,status还有success,error等值的情况
                        layer.alert("请求超时，请检查网络连接", {
                            skin: 'layui-layer-lan',
                            closeBtn: 0
                        });
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
        var xhr = $.ajax({
            type: "post",
            url: basePath + "/api/data/deleteKey",
            data: {
                'key': currKey,
                'index': currIndex
            },
            timeout: 10000,
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
                    layer.alert(data.msgs, {
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

function selectKey() {
    var key = $("#key-input").val();
    if (key == "") {
        layer.alert("请输入要查询的key！", {
            skin: 'layui-layer-lan',
            closeBtn: 0
        });
        return false;
    }
    alert(currIndex);
    if (currIndex == -1) {
        layer.alert("请选择一个要操作的库！", {
            skin: 'layui-layer-lan',
            closeBtn: 0
        });
        return false;
    }
    var xhr = $.ajax({
        type: "post",
        url: basePath + '/api/data/keysData',
        data: {
            'key': key,
            'index': currIndex
        },
        timeout: 10000,
        sync: false,
        success: function (data) {
            var keyInfo = data.data;
            if (data.code == 200 && keyInfo) {
                currKey = key;
                $("#key-input").val(currKey);
                $("#key").text(keyInfo.key);
                $("#type").text(keyInfo.type);
                $("#size").text(keyInfo.size);
                $("#ttl").text(keyInfo.ttl);
                $("#value").text(keyInfo.value);
            } else {
                layer.alert(data.msgs, {
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

//获取key信息
function getKeyInfo() {
    if (currKey == "" || currKey == null) {
        layer.alert("请选择要操作的key！", {
            skin: 'layui-layer-lan',
            closeBtn: 0
        });
        return false;
    }
    var xhr = $.ajax({
        type: "post",
        url: basePath + '/api/data/keysData',
        data: {
            'key': currKey,
            'index': currIndex
        },
        timeout: 10000,
        sync: false,
        success: function (data) {
            var keyInfo = data.data;
            $("#key-input").val(currKey);
            if (data.code == 200 && keyInfo) {
                $("#key").text(keyInfo.key);
                $("#type").text(keyInfo.type);
                $("#size").text(keyInfo.size);
                $("#ttl").text(keyInfo.ttl);
                $("#value").text(keyInfo.value);
            } else {
                layer.alert(data.msgs, {
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