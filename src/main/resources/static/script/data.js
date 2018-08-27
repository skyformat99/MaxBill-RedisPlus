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
        enable: true, //启用异步加载
        dataType: "json",
        url: basePath + '/api/data/treeData',
        autoParam: ["id", "name"],
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

};

function loadKeyTree() {
    // layer.load(2);
    $.ajax({
        type: "get",
        url: basePath + '/api/data/treeData',
        sync: false,
        success: function (data) {
            $.fn.zTree.init($("#keyTree"), zTreeSetting, data.data);
            $.fn.zTree.getZTreeObj("keyTree").expandAll(false);
            // layer.closeAll('loading');
        }
    });
}