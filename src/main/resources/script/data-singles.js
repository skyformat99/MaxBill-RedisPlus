var $;
var layer;
var currNode0;
var currNode1;
var currNode2;

window.onload = function () {
    layui.use(['layer', 'jquery'], function () {
        $ = layui.jquery;
        layer = layui.layer;
        layer.load(2);
        initDbTree();
        initDataView();
        layer.closeAll('loading');
    });
};

//初始化视图
function initDataView() {
    layer.msg("双击KEY名称可复制！");
    $("#keys").on({
        mouseover: function () {
            // layer.tips('双击复制KEY', this, {
            //     tips: [1, '#3595CC']
            // });
        },
        dblclick: function () {
            var text = $("#keys").html() + '';
            if (text !== '--') {
                copyToClipboard(text);
            }
        }
    });
}

//切换数据视图
function changeDataView(flag) {
    //数据按钮
    var thisBtn = $("#btn" + flag);
    var elseBtn = $(".layui-btn-fluid");
    elseBtn.removeClass("key-tool-abtn");
    elseBtn.addClass("key-tool-dbtn");
    thisBtn.removeClass("key-tool-dbtn");
    thisBtn.addClass("key-tool-abtn");
    //数据视图
    var thisObj = $("#vals" + flag);
    var valsObj = $(".vals");
    var elseObj = valsObj.not(thisObj);
    valsObj.removeClass("key-vals-hide");
    valsObj.removeClass("key-vals-show");
    thisObj.addClass("key-vals-show");
    elseObj.addClass("key-vals-hide");
}

//树配置
var zTreeSetting = {
    check: {
        enable: false
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
        onExpand: ztreeOnExpand,
        onDblClick: ztreeOnDblClick,
        onRightClick: ztreeOnRightClick
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
    var pageBox = "";
    pageBox += "<span class='button lastPage' id='lastBtn_" + treeNode.id + "' title='尾页' onfocus='this.blur();'></span>";
    pageBox += "<span class='button nextPage' id='nextBtn_" + treeNode.id + "' title='下一页' onfocus='this.blur();'></span>";
    pageBox += "<span class='button prevPage' id='prevBtn_" + treeNode.id + "' title='上一页' onfocus='this.blur();'></span>";
    pageBox += "<span class='button firstPage' id='firstBtn_" + treeNode.id + "' title='首页' onfocus='this.blur();'></span>";
    pageDiv.after(pageBox);
    var first = $("#firstBtn_" + treeNode.id);
    var prev = $("#prevBtn_" + treeNode.id);
    var next = $("#nextBtn_" + treeNode.id);
    var last = $("#lastBtn_" + treeNode.id);
    treeNode.maxPage = Math.round(treeNode.count / pageSize - .5) + (treeNode.count % pageSize === 0 ? 0 : 1);
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
        checkedOnTree(currNode0.index);
    }
}

//树节点展开事件
function ztreeOnExpand(event, treeId, treeNode) {
    if (treeNode.isParent) {
        currNode0 = treeNode;
        loadDbData(treeNode, treeNode.pattern);
    }
}

//树节点双击事件
function ztreeOnDblClick(event, treeId, treeNode) {
    if (!treeNode.isParent) {
        copyToClipboard(treeNode.name);
    }
}

//树右击事件
function ztreeOnRightClick(event, treeId, treeNode) {
    if (null != treeNode && treeNode.isParent) {
        currNode2 = treeNode;
        showZtreeMenu(event.clientX, event.clientY);
        $("body").bind("mousedown", onBodyMouseDown);
    }
}

//隐藏菜单鼠标监听事件
function onBodyMouseDown(event) {
    if (!(event.target.id === "ztree-menu" || $(event.target).parents("#ztree-menu").length > 0)) {
        hideZtreeMenu();
    }
}


//显示菜单
function showZtreeMenu(x, y) {
    $("#ztree-menu ul").show();
    y += document.body.scrollTop;
    x += document.body.scrollLeft;
    $("#ztree-menu").css({"top": y + "px", "left": x + "px", "visibility": "visible"});
}

//隐藏菜单
function hideZtreeMenu() {
    $("#ztree-menu").css({"visibility": "hidden"});
}


//清空数据
function removeData() {
    if (currNode2 == null) {
        layer.msg("请选择一个要操作的库！");
        return false;
    }
    layer.load(2);
    var json = dataSinglesRouter.removeKey(currNode2.index);
    var data = JSON.parse(json);
    layer.closeAll('loading');
    if (data.code === 200) {
        layer.msg(data.msgs);
        initDbTree();
    } else {
        layer.alert(data.msgs, {
            skin: 'layui-layer-lan',
            closeBtn: 0
        });
    }
    hideZtreeMenu();
}

//备份数据
function backupData() {
    if (currNode2 == null) {
        layer.msg("请选择一个要操作的库！");
        return false;
    }
    hideZtreeMenu();
    layer.msg("数据备份任务正在后台执行...");
    var json = dataSinglesRouter.backupsKey(currNode2.index, currNode2.pattern);
    var data = JSON.parse(json);
    if (data.code === 200) {
        layer.msg(data.msgs);
    } else {
        layer.alert(data.msgs, {
            skin: 'layui-layer-lan',
            closeBtn: 0
        });
    }
}

//还原数据
function recoveData() {
    if (currNode2 == null) {
        layer.msg("请选择一个要操作的库！");
        return false;
    }
    hideZtreeMenu();
    layer.msg("数据还原任务正在后台执行...");
    var json = dataSinglesRouter.recoveKey(currNode2.index);
    var data = JSON.parse(json);
    layer.msg(data.msgs);
    initDbTree();
}


//高亮显示当前选中树
function checkedOnTree(index) {
    $(".ztree li a").removeClass("curSelectedNode");
    $("#keyTree" + index + "_1_a").addClass("curSelectedNode");
}

//初始化库
function initDbTree() {
    layer.load(2);
    var data = JSON.parse(dataSinglesRouter.treeInit());
    layer.closeAll('loading');
    if (data.code === 200) {
        for (var i = 0; i < data.data.length; i++) {
            $("#db-tree").append('<ul id="keyTree' + i + '" name="keyTree' + i + '" class="ztree"></ul>');
            $.fn.zTree.init($("#keyTree" + i), zTreeSetting, data.data[i]);
            $.fn.zTree.getZTreeObj("keyTree" + i).expandAll(false);
        }
        checkedOnTree(0);
    } else {
        layer.alert(data.msgs, {
            skin: 'layui-layer-lan',
            closeBtn: 0
        });
    }
}


//加载数据
function loadDbData(node, pattern) {
    layer.load(2);
    var json = dataSinglesRouter.treeData(node.id, node.index, node.page, node.count, pattern);
    var data = JSON.parse(json);
    layer.closeAll('loading');
    if (data.code === 200) {
        var zTree = $.fn.zTree.getZTreeObj('keyTree' + node.index);
        zTree.removeChildNodes(node);
        zTree.addNodes(node, 0, data.data);
        zTree.expandAll(true);
    } else {
        layer.alert(data.msgs, {
            skin: 'layui-layer-lan',
            closeBtn: 0
        });
    }
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
        if (keyInfo.type === 'string') {
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
        if (text === -1) {
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
            $("#keys").text("--");
            $("#type").text("--");
            $("#size").text("--");
            $("#ttls").text("--");
            $(".vals").html("");
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

//添加新KEY
function insertKey() {
    layer.open({
        title: '新增数据',
        type: 2,
        area: ['460px', '410px'],
        fixed: true,
        maxmin: false,
        resize: false,
        skin: 'layui-layer-lan',
        content: '../page/data-singles-increase.html'
    });
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
    layer.load(2);
    var json = dataSinglesRouter.updateStr(currNode0.index, currNode1.name, $("#currVal").val());
    var data = JSON.parse(json);
    layer.closeAll('loading');
    if (data.code === 200) {
        getKeysInfo();
        layer.msg(data.msgs);
    } else {
        layer.alert(data.msgs, {
            skin: 'layui-layer-lan',
            closeBtn: 0
        });
    }
}


//新增list的item
function insertList() {
    if (null == currNode1) {
        layer.msg("请选择要操作的KEY！");
        return false;
    }
    layer.prompt({
        title: '输入添加的值',
        formType: 3,
        value: "",
        skin: 'layui-layer-lan',
        closeBtn: 0,
    }, function (text, index) {
        layer.load(2);
        var json = dataSinglesRouter.insertVal(3, currNode0.index, currNode1.name, text);
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

//删除list的item
function deleteList(keyIndex) {
    if (null == currNode1) {
        layer.msg("请选择要操作的KEY！");
        return false;
    }
    layer.load(2);
    var json = dataSinglesRouter.deleteVal(3, currNode0.index, currNode1.name, keyIndex);
    var data = JSON.parse(json);
    layer.closeAll('loading');
    if (data.code === 200) {
        getKeysInfo();
        layer.msg(data.msgs);
    } else {
        layer.alert(data.msgs, {
            skin: 'layui-layer-lan',
            closeBtn: 0
        });
    }
}


//新增set的item
function insertSet() {
    if (null == currNode1) {
        layer.msg("请选择要操作的KEY！");
        return false;
    }
    layer.prompt({
        title: '输入添加的值',
        formType: 3,
        value: "",
        skin: 'layui-layer-lan',
        closeBtn: 0,
    }, function (text, index) {
        layer.load(2);
        var json = dataSinglesRouter.insertVal(1, currNode0.index, currNode1.name, text);
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


//删除set的item
function deleteSet(val) {
    if (null == currNode1) {
        layer.msg("请选择要操作的KEY！");
        return false;
    }
    layer.load(2);
    var json = dataSinglesRouter.deleteVal(1, currNode0.index, currNode1.name, val);
    var data = JSON.parse(json);
    layer.closeAll('loading');
    if (data.code === 200) {
        getKeysInfo();
        layer.msg(data.msgs);
    } else {
        layer.alert(data.msgs, {
            skin: 'layui-layer-lan',
            closeBtn: 0
        });
    }
}


//新增set的item
function insertZset() {
    if (null == currNode1) {
        layer.msg("请选择要操作的KEY！");
        return false;
    }
    layer.prompt({
        title: '输入添加的值',
        formType: 3,
        value: "",
        skin: 'layui-layer-lan',
        closeBtn: 0,
    }, function (text, index) {
        layer.load(2);
        var json = dataSinglesRouter.insertVal(2, currNode0.index, currNode1.name, text);
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


//删除zset的item
function deleteZset(val) {
    if (null == currNode1) {
        layer.msg("请选择要操作的KEY！");
        return false;
    }
    layer.load(2);
    var json = dataSinglesRouter.deleteVal(2, currNode0.index, currNode1.name, val);
    var data = JSON.parse(json);
    layer.closeAll('loading');
    if (data.code === 200) {
        getKeysInfo();
        layer.msg(data.msgs);
    } else {
        layer.alert(data.msgs, {
            skin: 'layui-layer-lan',
            closeBtn: 0
        });
    }
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
            layer.load(2);
            var mapVal = mapKey + ":" + mapVal;
            var json = dataSinglesRouter.insertVal(4, currNode0.index, currNode1.name, mapVal);
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
        }
    });
    var layerContent = $(".layui-layer-content");
    layerContent.html("");
    layerContent.append("<input type=\"text\" value=\"\" id= \"mapKey\" class=\"layui-layer-input\" placeholder=\"请输入KEY\"/>");
    layerContent.append("<input type=\"text\" value=\"\" id= \"mapVal\" class=\"layui-layer-input\" placeholder=\"请输入VAL\"/>");
}

//删除hash的item
function deleteHash(mapKey) {
    if (null == currNode1) {
        layer.msg("请选择要操作的KEY！");
        return false;
    }
    layer.load(2);
    var json = dataSinglesRouter.deleteVal(4, currNode0.index, currNode1.name, mapKey);
    var data = JSON.parse(json);
    layer.closeAll('loading');
    if (data.code === 200) {
        getKeysInfo();
        layer.msg(data.msgs);
    } else {
        layer.alert(data.msgs, {
            skin: 'layui-layer-lan',
            closeBtn: 0
        });
    }
}

