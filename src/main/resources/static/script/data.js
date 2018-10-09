//当前选中的键
var currKey;
//默认数据库0
var currIndex = 0;
//每次加载的数据量
var pageSize = 50;
var basePath = $("#basePath").val();

$(document).ready(function () {
    layui.use(['form', 'jquery'], function () {
        var form = layui.form;
        form.render();
        form.on('radio(vals)', function (data) {
            var thisObj = $("#vals" + data.value);
            var elseObj = $(".vals").not(thisObj);
            $(".vals").removeClass("key-vals-hide");
            $(".vals").removeClass("key-vals-show");
            thisObj.addClass("key-vals-show");
            elseObj.addClass("key-vals-hide");
        });
    });

    //加载数据库树
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
        showLine: true,
        showIcon: true,
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
        onExpand: ztreeOnExpand,
        beforeAsync: beforeAsync,
        onAsyncError: onAsyncError,
        onAsyncSuccess: onAsyncSuccess
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

//异步加载前
function beforeAsync() {
    layer.load(2);
}

//异步加载错误
function onAsyncError() {
    layer.closeAll('loading');
}

//异步加载成功
function onAsyncSuccess() {
    layer.closeAll('loading');
}

//初始化16个根库
function loadKeyTree() {
    var xhr = $.ajax({
        type: "get",
        url: basePath + '/api/data/treeInit',
        async: true,
        timeout: 10000,
        success: function (data) {
            for (var i = 0; i < 16; i++) {
                $.fn.zTree.init($("#keyTree" + i), zTreeSetting, data.data[i]);
                $.fn.zTree.getZTreeObj("keyTree" + i).expandAll(false);
            }
            //展开默认库
            //$("#keyTree" + currIndex + "_1_switch").click();
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


//回车查询事件
function keydownLoadTree() {
    if (event.keyCode == 13) {
        loadPatternTree();
    }
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
        async: true,
        data: {
            "index": currIndex,
            "pattern": $("#key-like-input").val()
        },
        timeout: 10000,
        success: function (data) {
            var ztreeObj = $.fn.zTree.init($("#keyTree" + currIndex), zTreeSetting, data.data);
            ztreeObj.expandAll(false);
            //增加默认选中样式
            //$("#keyTree" + currIndex + "_1_a").addClass("curSelectedNode");
            //展开默认库
            $("#keyTree" + currIndex + "_1_switch").click();
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
                async: false,
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
            async: false,
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

//设置key的失效时间
function retimeKey() {
    if (currKey == "" || currKey == null) {
        layer.alert("请选择要操作的key！", {
            skin: 'layui-layer-lan',
            closeBtn: 0
        });
        return false;
    }
    layer.prompt(
        {
            title: '输入TTL的值',
            formType: 3,
            value: -1,
            skin: 'layui-layer-lan',
            closeBtn: 0,
        },
        function (text, index) {
            if (text == -1) {
                layer.close(index);
                return;
            }
            var checkFlag = /^(0|[1-9][0-9]*)$/.test(text);
            if (!checkFlag) {
                layer.msg('只能输入整数值');
                return;
            }
            var xhr = $.ajax({
                type: "post",
                url: basePath + "/api/data/retimeKey",
                data: {
                    'key': currKey,
                    'time': text,
                    'index': currIndex
                },
                timeout: 10000,
                async: false,
                success: function (data) {
                    layer.close(index);
                    if (data.code == 200) {
                        getKeyInfo();
                        layer.msg('设置成功');
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

//检索key
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
        async: false,
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
        async: false,
        success: function (data) {
            var keyInfo = data.data;
            $("#key-input").val(currKey);
            if (data.code == 200 && keyInfo) {
                $("#key").text(keyInfo.key);
                $("#type").text(keyInfo.type);
                $("#size").text(keyInfo.size);
                $("#ttl").text(keyInfo.ttl);
                $("#vals1").html(keyInfo.text);
                $("#vals2").html(getFormatJson(keyInfo.json));
                $("#vals3").html(keyInfo.raws);
                if (keyInfo.type == 'string') {
                    $("#vals4").html(getEditView(keyInfo.type, keyInfo.text));
                } else {
                    $("#vals4").html(getEditView(keyInfo.type, keyInfo.json));
                }
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

//数据编辑视图
function getEditView(type, data) {
    var view = '';
    switch (type) {
        case "set":
            var set = $.parseJSON(data);
            view += '<div class="key-vals-tool">';
            view += '<button class="layui-btn layui-btn-primary layui-btn-sm set-color" onclick="insertSet()">';
            view += '<i class="layui-icon">&#xe61f;</i>添加</button>';
            view += '</div>';
            view += '<div class="data-table-box">';
            view += '<table class="layui-table">';
            view += '<colgroup><col><col width="100"></colgroup>';
            view += '<thead><tr><th>value</th><th>tool</th></tr></thead>';
            view += '<tbody>';
            for (var i = 0; i < set.length; i++) {
                view += '<tr>';
                view += '<td>' + set[i] + '</td>';
                view += '<td>';
                view += '<button class="layui-btn layui-btn-primary layui-btn-sm set-color" onclick="deleteSet(\'' + set[i] + '\')">';
                view += '<i class="layui-icon">&#x1006;</i>删除</button>';
                view += '</td>';
                view += '</tr>';
            }
            view += '</tbody>';
            view += '</table>';
            view += '</div>';
            break;
        case "list":
            var list = $.parseJSON(data);
            view += '<div class="key-vals-tool">';
            view += '<button class="layui-btn layui-btn-primary layui-btn-sm set-color" onclick="insertList()">';
            view += '<i class="layui-icon">&#xe61f;</i>添加</button>';
            view += '</div>';
            view += '<div class="data-table-box">';
            view += '<table class="layui-table">';
            view += '<colgroup><col width="20"><col><col width="100"></colgroup>';
            view += '<thead><tr><th>index</th><th>value</th><th>tool</th></tr></thead>';
            view += '<tbody>';
            for (var i = 0; i < list.length; i++) {
                view += '<tr>';
                view += '<td>' + i + '</td>';
                view += '<td>' + list[i] + '</td>';
                view += '<td>';
                view += '<button class="layui-btn layui-btn-primary layui-btn-sm set-color" onclick="deleteList(\'' + i + '\')">';
                view += '<i class="layui-icon">&#x1006;</i>删除</button>';
                view += '</td>';
                view += '</tr>';
            }
            view += '</tbody>';
            view += '</table>';
            view += '</div>';
            break;
        case "zset":
            var zset = $.parseJSON(data);
            view += '<div class="key-vals-tool">';
            view += '<button class="layui-btn layui-btn-primary layui-btn-sm set-color" onclick="insertZset()">';
            view += '<i class="layui-icon">&#xe61f;</i>添加</button>';
            view += '</div>';
            view += '<div class="data-table-box">';
            view += '<table class="layui-table">';
            view += '<colgroup><col><col width="100"></colgroup>';
            view += '<thead><tr><th>value</th><th>tool</th></tr></thead>';
            view += '<tbody>';
            for (var i = 0; i < zset.length; i++) {
                view += '<tr>';
                view += '<td>' + zset[i] + '</td>';
                view += '<td>';
                view += '<button class="layui-btn layui-btn-primary layui-btn-sm set-color" onclick="deleteZset(\'' + zset[i] + '\')">';
                view += '<i class="layui-icon">&#x1006;</i>删除</button>';
                view += '</td>';
                view += '</tr>';
            }
            view += '</tbody>';
            view += '</table>';
            view += '</div>';
            break;
        case "hash":
            var map = $.parseJSON(data);
            view += '<div class="key-vals-tool">';
            view += '<button class="layui-btn layui-btn-primary layui-btn-sm set-color" onclick="insertHash()">';
            view += '<i class="layui-icon">&#xe61f;</i>添加</button>';
            view += '</div>';
            view += '<div class="data-table-box">';
            view += '<table class="layui-table">';
            view += '<colgroup><col><col><col width="100"></colgroup>';
            view += '<thead><tr><th>key</th><th>value</th><th>tool</th></tr></thead>';
            view += '<tbody>';
            for (var key in map) {
                view += '<tr>';
                view += '<td>' + key + '</td>';
                view += '<td>' + map[key] + '</td>';
                view += '<td>';
                view += '<button class="layui-btn layui-btn-primary layui-btn-sm set-color" onclick="deleteHash(\'' + key + '\')">';
                view += '<i class="layui-icon">&#x1006;</i>删除</button>';
                view += '</td>';
                view += '</tr>';
            }
            view += '</tbody>';
            view += '</table>';
            view += '</div>';
            break;
        case "string":
            view += '<textarea id="currVal" class="layui-textarea key-vals-textarea">' + data + '</textarea>';
            view += '<div class="key-vals-tool">';
            view += '<button class="layui-btn layui-btn-primary layui-btn-sm set-color" onclick="updateStr()">';
            view += '<i class="layui-icon">&#x1005;</i>提交</button></div>';
            break;
    }
    return view;
}

//修改string类型
function updateStr() {
    if (currKey == "" || currKey == null) {
        layer.alert("请选择要操作的key！", {
            skin: 'layui-layer-lan',
            closeBtn: 0
        });
        return false;
    }
    var xhr = $.ajax({
        type: "post",
        url: basePath + '/api/data/updateStr',
        data: {
            'key': currKey,
            'val': $("#currVal").val(),
            'index': currIndex
        },
        timeout: 10000,
        async: false,
        success: function (data) {
            if (data.code == 200) {
                getKeyInfo();
                layer.msg('修改成功');
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


//新增list的item
function insertList() {
    if (currKey == "" || currKey == null) {
        layer.alert("请选择要操作的key！", {
            skin: 'layui-layer-lan',
            closeBtn: 0
        });
        return false;
    }
    layer.prompt(
        {
            title: '输入添加的值',
            formType: 3,
            value: "",
            skin: 'layui-layer-lan',
            closeBtn: 0,
        },
        function (text, index) {
            var xhr = $.ajax({
                type: "post",
                url: basePath + "/api/data/insertList",
                data: {
                    'key': currKey,
                    'val': text,
                    'index': currIndex
                },
                timeout: 10000,
                async: false,
                success: function (data) {
                    layer.close(index);
                    if (data.code == 200) {
                        getKeyInfo();
                        layer.msg('添加成功');
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

//删除list的item
function deleteList(keyIndex) {
    if (currKey == "" || currKey == null) {
        layer.alert("请选择要操作的key！", {
            skin: 'layui-layer-lan',
            closeBtn: 0
        });
        return false;
    }
    var xhr = $.ajax({
        type: "post",
        url: basePath + '/api/data/deleteList',
        data: {
            'key': currKey,
            'index': currIndex,
            'keyIndex': keyIndex
        },
        timeout: 10000,
        async: false,
        success: function (data) {
            if (data.code == 200) {
                getKeyInfo();
                layer.msg('删除成功');
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


//新增set的item
function insertSet() {
    if (currKey == "" || currKey == null) {
        layer.alert("请选择要操作的key！", {
            skin: 'layui-layer-lan',
            closeBtn: 0
        });
        return false;
    }
    layer.prompt(
        {
            title: '输入添加的值',
            formType: 3,
            value: "",
            skin: 'layui-layer-lan',
            closeBtn: 0,
        },
        function (text, index) {
            var xhr = $.ajax({
                type: "post",
                url: basePath + "/api/data/insertSet",
                data: {
                    'key': currKey,
                    'val': text,
                    'index': currIndex
                },
                timeout: 10000,
                async: false,
                success: function (data) {
                    layer.close(index);
                    if (data.code == 200) {
                        getKeyInfo();
                        layer.msg('添加成功');
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


//删除set的item
function deleteSet(val) {
    if (currKey == "" || currKey == null) {
        layer.alert("请选择要操作的key！", {
            skin: 'layui-layer-lan',
            closeBtn: 0
        });
        return false;
    }
    var xhr = $.ajax({
        type: "post",
        url: basePath + '/api/data/deleteSet',
        data: {
            'key': currKey,
            'val': val,
            'index': currIndex
        },
        timeout: 10000,
        async: false,
        success: function (data) {
            if (data.code == 200) {
                getKeyInfo();
                layer.msg('删除成功');
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


//新增set的item
function insertZset() {
    if (currKey == "" || currKey == null) {
        layer.alert("请选择要操作的key！", {
            skin: 'layui-layer-lan',
            closeBtn: 0
        });
        return false;
    }
    layer.prompt(
        {
            title: '输入添加的值',
            formType: 3,
            value: "",
            skin: 'layui-layer-lan',
            closeBtn: 0,
        },
        function (text, index) {
            var xhr = $.ajax({
                type: "post",
                url: basePath + "/api/data/insertZset",
                data: {
                    'key': currKey,
                    'val': text,
                    'index': currIndex
                },
                timeout: 10000,
                async: false,
                success: function (data) {
                    layer.close(index);
                    if (data.code == 200) {
                        getKeyInfo();
                        layer.msg('添加成功');
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


//删除zset的item
function deleteZset(val) {
    if (currKey == "" || currKey == null) {
        layer.alert("请选择要操作的key！", {
            skin: 'layui-layer-lan',
            closeBtn: 0
        });
        return false;
    }
    var xhr = $.ajax({
        type: "post",
        url: basePath + '/api/data/deleteZset',
        data: {
            'key': currKey,
            'val': val,
            'index': currIndex
        },
        timeout: 10000,
        async: false,
        success: function (data) {
            if (data.code == 200) {
                getKeyInfo();
                layer.msg('删除成功');
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

//增加hash的item
function insertHash() {
    layer.prompt({
        closeBtn: 0,
        formType: 3,
        title: '输入添加的值',
        skin: 'layui-layer-lan',
        yes: function (index) {
            var mapKey = $("#mapKey").val();
            var mapVal = $("#mapVal").val();
            if (mapKey === '') {
                return;
            }
            if (mapVal === '') {
                return;
            }
            var xhr = $.ajax({
                type: "post",
                url: basePath + '/api/data/insertHash',
                data: {
                    'key': currKey,
                    'mapKey': mapKey,
                    'mapVal': mapVal,
                    'index': currIndex
                },
                timeout: 10000,
                async: false,
                success: function (data) {
                    if (data.code == 200) {
                        getKeyInfo();
                        layer.msg('添加成功');
                        layer.close(index);
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
    });
    $(".layui-layer-content").html("");
    $(".layui-layer-content").append("<input type=\"text\" value=\"\" id= \"mapKey\" class=\"layui-layer-input\" placeholder=\"请输入Key\"/>");
    $(".layui-layer-content").append("<input type=\"text\" value=\"\" id= \"mapVal\" class=\"layui-layer-input\" placeholder=\"请输入Val\"/>");
}

//删除hash的item
function deleteHash(mapKey) {
    if (currKey == "" || currKey == null) {
        layer.alert("请选择要操作的key！", {
            skin: 'layui-layer-lan',
            closeBtn: 0
        });
        return false;
    }
    var xhr = $.ajax({
        type: "post",
        url: basePath + '/api/data/deleteHash',
        data: {
            'key': currKey,
            'mapKey': mapKey,
            'index': currIndex
        },
        timeout: 10000,
        async: false,
        success: function (data) {
            if (data.code == 200) {
                getKeyInfo();
                layer.msg('删除成功');
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

