var $;
var form;
var layer;
var currKey;
var currNode0;
var currNode1;

layui.use(['form', 'layer', 'jquery'], function () {
    $ = layui.jquery;
    layer = layui.layer;
    form = layui.form;
    form.render();
    form.on('radio(vals)', function (data) {
        var thisObj = $("#vals" + data.value);
        var elseObj = $(".vals").not(thisObj);
        $(".vals").removeClass("key-vals-hide");
        $(".vals").removeClass("key-vals-show");
        thisObj.addClass("key-vals-show");
        elseObj.addClass("key-vals-hide");
    });
    initDbTree();
});

//树配置
var zTreeSetting = {
    check: {
        enable: false,
    },
    data: {
        keep: {
            parent: true
        },
        simpleData: {
            enable: true
        }
    },
    view: {
        showLine: true,
        showIcon: true,
        addDiyDom: showPageView
    },
    callback: {
        onClick: ztreeOnClick,
        onExpand: ztreeOnExpand
    }
};

//点击分页
function goPage(treeNode, page) {
    if (null == currNode0) {
        layer.msg("请选择一个要操作的库！");
        return false;
    }
    treeNode.page = page;
    if (treeNode.page < 1) {
        treeNode.page = 1;
    }
    if (treeNode.page > treeNode.maxPage) {
        treeNode.page = treeNode.maxPage;
    }
    loadDbData(treeNode, treeNode.pattern);
}

//显示分页
function showPageView(treeId, treeNode) {
    var pageSize = 50;
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
        goPage(treeNode, 1);
    });
    prev.bind("click", function () {
        goPage(treeNode, treeNode.page - 1);
    });
    next.bind("click", function () {
        goPage(treeNode, treeNode.page + 1);
    });
    last.bind("click", function () {
        goPage(treeNode, treeNode.maxPage);
    });
};

//树节点点击事件
function ztreeOnClick(event, treeId, treeNode) {
    if (!treeNode.isParent) {
        currNode1 = treeNode;
        getKeysInfo();
    } else {
        currNode0 = treeNode;
        //取消其他根节点点击样式
        for (var i = 0; i < 15; i++) {
            if (i != currNode0.index) {
                $("#keyTree" + i + "_1_a").removeClass("curSelectedNode");
            }
        }
    }
}

//树节点展开事件
function ztreeOnExpand(event, treeId, treeNode) {
    if (treeNode.isParent) {
        currNode0 = treeNode;
        loadDbData(treeNode, treeNode.pattern);
    }
}

//初始化库
function initDbTree() {
    layer.load(2);
    var data = JSON.parse(dataSinglesRouter.treeInit());
    for (var i = 0; i < data.length; i++) {
        $("#db-tree").append('<ul id="keyTree' + i + '" name="keyTree' + i + '" class="ztree"></ul>');
        $.fn.zTree.init($("#keyTree" + i), zTreeSetting, data[i]);
        $.fn.zTree.getZTreeObj("keyTree" + i).expandAll(false);
    }
    layer.closeAll('loading');
}


//加载数据
function loadDbData(node, pattern) {
    layer.load(2);
    var json = dataSinglesRouter.treeData(node.id, node.index, node.page, node.count, pattern);
    var data = JSON.parse(json);
    var zTree = $.fn.zTree.getZTreeObj('keyTree' + node.index);
    zTree.removeChildNodes(node);
    zTree.addNodes(node, 0, data);
    zTree.expandAll(true);
    layer.closeAll('loading');
}


//获取key信息
function getKeysInfo() {
    if (null == currNode1) {
        layer.msg("请选择要操作的KEY！");
        return false;
    }
    layer.load(2);
    var json = dataSinglesRouter.keysData(currNode0.index, currNode1.name);
    var data = JSON.parse(json);
    layer.closeAll('loading');
    if (data.code === 200) {
        var keyInfo = data.data;
        $("#keys").text(keyInfo.key);
        $("#type").text(keyInfo.type);
        $("#size").text(keyInfo.size);
        $("#ttls").text(keyInfo.ttl);
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
}


//回车查询事件
function keydownLoadTree() {
    if (event.keyCode === 13) {
        loadLikeTree();
    }
}

//模糊匹配
function loadLikeTree() {
    if (null == currNode0) {
        var zTreeObj = $.fn.zTree.getZTreeObj("keyTree0");
        currNode0 = zTreeObj.getNodesByFilter(function (node) {
            return node.level === 0
        }, true);
    }
    layer.load(2);
    var pattern = $("#key-like-input").val();
    var json = dataSinglesRouter.likeInit(currNode0.index, pattern);
    var data = JSON.parse(json);
    layer.closeAll('loading');
    if (data.code === 200) {
        var ztreeObj = $.fn.zTree.init($("#keyTree" + currNode0.index), zTreeSetting, data.data);
        currNode0 = ztreeObj.getNodesByFilter(function (node) {
            return node.level === 0
        }, true);
        loadDbData(currNode0, pattern);
    } else {
        layer.alert(data.msgs, {
            skin: 'layui-layer-lan',
            closeBtn: 0
        });
    }
}

//重命名key
function renameKey() {
    if (null == currNode1) {
        layer.msg("请选择要操作的KEY！");
        return false;
    }
    layer.prompt({
        title: '输入新的key',
        formType: 3,
        value: currNode1.name,
        skin: 'layui-layer-lan',
        closeBtn: 0,
    }, function (text, index) {
        layer.load(2);
        var json = dataSinglesRouter.renameKey(currNode0.index, currNode1.name, text);
        var data = JSON.parse(json);
        layer.closeAll('loading');
        layer.close(index);
        if (data.code === 200) {
            currNode1.name = text;
            var zTreeObj = $.fn.zTree.getZTreeObj("keyTree" + currNode0.index);
            zTreeObj.updateNode(currNode1);
            layer.msg(data.msgs);
            getKeysInfo();
        } else {
            layer.alert(data.msgs, {
                skin: 'layui-layer-lan',
                closeBtn: 0
            });
        }
    });
}


//设置key的失效时间
function retimeKey() {
    if (null == currNode1) {
        layer.msg("请选择要操作的KEY！");
        return false;
    }
    layer.prompt({
        title: '输入TTL的值',
        formType: 3,
        value: -1,
        skin: 'layui-layer-lan',
        closeBtn: 0,
    }, function (text, index) {
        if (text == -1) {
            layer.close(index);
            return;
        }
        var checkFlag = /^(0|[1-9][0-9]*)$/.test(text);
        if (!checkFlag) {
            layer.msg('只能输入整数值');
            return;
        }
        layer.load(2);
        var json = dataSinglesRouter.retimeKey(currNode0.index, currNode1.name, text);
        var data = JSON.parse(json);
        layer.closeAll('loading');
        layer.close(index);
        if (data.code === 200) {
            getKeysInfo();
            layer.msg(data.msgs);
        } else {
            layer.alert(data.msgs, {
                skin: 'layui-layer-lan',
                closeBtn: 0
            });
        }
    });
}


//删除key
function deleteKey() {
    if (null == currNode1) {
        layer.msg("请选择要操作的KEY！");
        return false;
    }
    var index = layer.confirm('确认删除该项？', {
        btn: ['确定', '取消'],
        skin: 'layui-layer-lan',
        closeBtn: 0
    }, function () {
        layer.load(2);
        var json = dataSinglesRouter.deleteKey(currNode0.index, currNode1.name);
        var data = JSON.parse(json);
        layer.closeAll('loading');
        layer.close(index);
        if (data.code === 200) {
            var zTreeObj = $.fn.zTree.getZTreeObj("keyTree" + currNode0.index);
            zTreeObj.removeNode(currNode1);
            currNode1 = null;
            $("#keys").text("");
            $("#type").text("");
            $("#size").text("");
            $("#ttls").text("");
            layer.msg(data.msgs);
        } else {
            layer.alert(data.msgs, {
                skin: 'layui-layer-lan',
                closeBtn: 0
            });
        }
    });
}

//重新加载key
function reloadKey() {
    if (null == currNode1) {
        layer.msg("请选择要操作的KEY！");
        return false;
    }
    getKeysInfo();
}


//数据编辑视图
function getEditView(type, data) {
    var view = '';
    switch (type) {
        case "set":
            var set = $.parseJSON(data);
            view += '<button class="layui-btn layui-btn-primary layui-btn-sm set-color" onclick="insertSet()">';
            view += '<i class="layui-icon">&#xe61f;</i>添加</button>';
            view += '<div class="data-table-box">';
            view += '<table class="layui-table">';
            view += '<colgroup><col><col width="100"></colgroup>';
            view += '<thead><tr><th>值</th><th>操作</th></tr></thead>';
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
            view += '<button class="layui-btn layui-btn-primary layui-btn-sm set-color" onclick="insertList()">';
            view += '<i class="layui-icon">&#xe61f;</i>添加</button>';
            view += '<div class="data-table-box">';
            view += '<table class="layui-table">';
            view += '<colgroup><col width="60"><col><col width="100"></colgroup>';
            view += '<thead><tr><th>位置</th><th>值</th><th>操作</th></tr></thead>';
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
            view += '<button class="layui-btn layui-btn-primary layui-btn-sm set-color" onclick="insertZset()">';
            view += '<i class="layui-icon">&#xe61f;</i>添加</button>';
            view += '<div class="data-table-box">';
            view += '<table class="layui-table">';
            view += '<colgroup><col><col width="100"></colgroup>';
            view += '<thead><tr><th>值</th><th>操作</th></tr></thead>';
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
            view += '<button class="layui-btn layui-btn-primary layui-btn-sm set-color" onclick="insertHash()">';
            view += '<i class="layui-icon">&#xe61f;</i>添加</button>';
            view += '<div class="data-table-box">';
            view += '<table class="layui-table">';
            view += '<colgroup><col><col><col width="100"></colgroup>';
            view += '<thead><tr><th>键</th><th>值</th><th>操作</th></tr></thead>';
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
            view += '<textarea id="currVal" class="layui-textarea key-vals-text">' + data + '</textarea>';
            view += '<button class="layui-btn layui-btn-primary layui-btn-sm set-color key-vals-btns" onclick="updateStr()">';
            view += '<i class="layui-icon">&#x1005;</i>提交</button>';
            break;
    }
    return view;
}

//修改string类型
function updateStr() {
    if (null == currNode1) {
        layer.msg("请选择要操作的KEY！");
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
    if (null == currNode1) {
        layer.msg("请选择要操作的KEY！");
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
    if (null == currNode1) {
        layer.msg("请选择要操作的KEY！");
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
    if (null == currNode1) {
        layer.msg("请选择要操作的KEY！");
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
    if (null == currNode1) {
        layer.msg("请选择要操作的KEY！");
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
    if (null == currNode1) {
        layer.msg("请选择要操作的KEY！");
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
    if (null == currNode1) {
        layer.msg("请选择要操作的KEY！");
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
    $(".layui-layer-content").append("<input type=\"text\" value=\"\" id= \"mapKey\" class=\"layui-layer-input\" placeholder=\"请输入KEY\"/>");
    $(".layui-layer-content").append("<input type=\"text\" value=\"\" id= \"mapVal\" class=\"layui-layer-input\" placeholder=\"请输入VAL\"/>");
}

//删除hash的item
function deleteHash(mapKey) {
    if (null == currNode1) {
        layer.msg("请选择要操作的KEY！");
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

