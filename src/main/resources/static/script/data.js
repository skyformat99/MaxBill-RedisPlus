var currKey;
var currIndex = -1;
var pageSize = 100;
var basePath = $("#basePath").val();

$(document).ready(function () {
    layui.use('form', function () {
        var form = layui.form;
        form.render();
        form.on('radio()', function (data) {
            switch (data.value) {
                case "1":
                    $("#text").removeClass("key-vals-hide");
                    $("#text").addClass("key-vals-show");
                    $("#json").addClass("key-vals-hide");
                    $("#raws").addClass("key-vals-hide");
                    break;
                case "2":
                    $("#json").removeClass("key-vals-hide");
                    $("#text").addClass("key-vals-hide");
                    $("#json").addClass("key-vals-show");
                    $("#raws").addClass("key-vals-hide");
                    break;
                case "3":
                    $("#raws").removeClass("key-vals-hide");
                    $("#text").addClass("key-vals-hide");
                    $("#json").addClass("key-vals-hide");
                    $("#raws").addClass("key-vals-show");
                    break;
            }
        });
    });
    loadKeyTree();
});


//树配置
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
        showLine: false,
        addDiyDom: showPageView
    },
    async: {
        enable: true,
        dataType: "json",
        url: basePath + '/api/data/treeData',
        autoParam: ["id", "index", "page", "count", "pattern"],
        dataFilter: dataFilter
    },
    callback: {
        onClick: ztreeOnClick,
        onExpand: ztreeOnExpand
    }
};

//点击分页
function goPage(treeNode, page) {
    if (currIndex == -1) {
        layer.alert("请选择一个要操作的库！", {
            skin: 'layui-layer-lan',
            closeBtn: 0
        });
        return false;
    }
    treeNode.page = page;
    if (treeNode.page < 1) {
        treeNode.page = 1;
    }
    if (treeNode.page > treeNode.maxPage) {
        treeNode.page = treeNode.maxPage;
    }
    var zTree = $.fn.zTree.getZTreeObj("keyTree" + currIndex);
    zTree.reAsyncChildNodes(treeNode, "refresh");
}

//显示分页
function showPageView(treeId, treeNode) {
    if (treeNode.level > 0) {
        return;
    }
    var pageDiv = $("#" + treeNode.tId + "_a");
    if ($("#addBtn_" + treeNode.id).length > 0) {
        return;
    }
    if (treeNode.count <= pageSize) {
        return;
    }
    var pageBox = ""
    pageBox += "<span class='button lastPage' id='lastBtn_" + treeNode.id + "' title='尾页' onfocus='this.blur();'></span>";
    pageBox += "<span class='button nextPage' id='nextBtn_" + treeNode.id + "' title='下一页' onfocus='this.blur();'></span>";
    pageBox += "<span class='button prevPage' id='prevBtn_" + treeNode.id + "' title='上一页' onfocus='this.blur();'></span>";
    pageBox += "<span class='button firstPage' id='firstBtn_" + treeNode.id + "' title='首页' onfocus='this.blur();'></span>";
    pageDiv.after(pageBox);
    var first = $("#firstBtn_" + treeNode.id);
    var prev = $("#prevBtn_" + treeNode.id);
    var next = $("#nextBtn_" + treeNode.id);
    var last = $("#lastBtn_" + treeNode.id);
    treeNode.maxPage = Math.round(treeNode.count / pageSize - .5) + (treeNode.count % pageSize == 0 ? 0 : 1);
    first.bind("click", function () {
        if (!treeNode.isAjaxing) {
            goPage(treeNode, 1);
        }
    });
    prev.bind("click", function () {
        if (!treeNode.isAjaxing) {
            goPage(treeNode, treeNode.page - 1);
        }
    });
    next.bind("click", function () {
        if (!treeNode.isAjaxing) {
            goPage(treeNode, treeNode.page + 1);
        }
    });
    last.bind("click", function () {
        if (!treeNode.isAjaxing) {
            goPage(treeNode, treeNode.maxPage);
        }
    });
};


//过滤异步加载ztree时返回的数据
function dataFilter(treeId, parentNode, childNodes) {
    return childNodes.data;
}

//树节点点击事件
function ztreeOnClick(event, treeId, treeNode) {
    if (!treeNode.isParent) {
        currKey = treeNode.name;
        currIndex = treeNode.index;
        getKeyInfo();
    } else {
        currIndex = treeNode.index;
        //取消其他根节点点击样式
        for (var i = 0; i < 15; i++) {
            if (i != currIndex) {
                $("#keyTree" + i + "_1_a").removeClass("curSelectedNode");
            }
        }
    }
}

//树节点展开事件
function ztreeOnExpand(event, treeId, treeNode) {
    if (treeNode.isParent) {
        currIndex = treeNode.index;
    }
}

//初始化16个根库
function loadKeyTree() {
    var xhr = $.ajax({
        type: "get",
        url: basePath + '/api/data/treeInit',
        sync: true,
        timeout: 10000,
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

//模糊匹配
function loadPatternTree() {
    if (currIndex == -1) {
        layer.alert("请选择一个要操作的库！", {
            skin: 'layui-layer-lan',
            closeBtn: 0
        });
        return false;
    }
    var xhr = $.ajax({
        type: "post",
        url: basePath + '/api/data/likeInit',
        sync: true,
        data: {
            "index": currIndex,
            "pattern": $("#key-like-input").val()
        },
        timeout: 10000,
        success: function (data) {
            $.fn.zTree.init($("#keyTree" + currIndex), zTreeSetting, data.data);
            $.fn.zTree.getZTreeObj("keyTree" + currIndex).expandAll(false);
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
                $("#text").html(keyInfo.text);
                $("#json").html(getFormatJson(keyInfo.json));
                $("#raws").html(keyInfo.raws);
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