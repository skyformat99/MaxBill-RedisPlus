function toPage(flag, page) {
    var isOpen = connectRouter.isopenConnect();
    if (flag === 1 && isOpen === 0) {
        layer.alert('请先连接一个可用的服务！', {
            skin: 'layui-layer-lan',
            closeBtn: 0
        });
        return false;
    }
    connectRouter.changeWebview(page);
}

function toPageBack(flag, page) {
    layer.load(2);
    var connect = JSON.parse(connectRouter.querysConnect(rowDataId));
    var isOpen = connectRouter.isopenConnect();
    layer.closeAll('loading');
    if (flag === 1 && isOpen === 0) {
        layer.alert('请先连接一个可用的服务！', {
            skin: 'layui-layer-lan',
            closeBtn: 0
        });
        return false;
    }
    var src = '../page/';
    switch (page) {
        case 1:
            src = src + 'connect.html';
            break;
        case 2:
            if (connect.isha === '0') {
                src = src + 'data-singles.html';
            }
            if (connect.isha === '1') {
                src = src + 'data-cluster.html';
            }
            layer.msg(src);
            break;
        case 3:
            src = src + 'infobox.html';
            break;
        case 4:
            src = src + 'configs.html';
            break;
        case 5:
            src = src + 'monitor.html';
            break;
    }
    window.location.href = src;
}
