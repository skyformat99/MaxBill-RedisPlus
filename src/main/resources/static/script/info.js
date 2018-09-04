var $;

var data01 = [
    {name: '0', value: 0},
    {name: '0', value: 0},
    {name: '0', value: 0},
    {name: '0', value: 0},
    {name: '0', value: 0}
];

var data02 = [
    {name: '0', value: 0},
    {name: '0', value: 0},
    {name: '0', value: 0},
    {name: '0', value: 0},
    {name: '0', value: 0}
];

var chart01;
var chart02;

layui.use(['element', 'jquery'], function () {
    $ = layui.jquery;
    getRealtimeData();
});

function getRealtimeData() {
    $.ajax({
        type: 'get',
        url: "/api/info/realInfo",
        sync: false,
        success: function (data) {
            var key = data.data.key;
            var val01 = data.data.val01;
            var val02 = data.data.val02;
            moveData(1, data01, key, val01);
            moveData(2, data02, key, val02);
            new sChart('chart01', 'line', data01, {
                title: '已占用内存量实时监控',
                bgColor: '#FFFFFF',
                titleColor: '#00887C',
                fillColor: 'red',
                contentColor: 'rgba(46,199,201,0.3)'
            });
            new sChart('chart02', 'line', data02, {
                title: '已占用CPU信息实时监控',
                bgColor: '#FFFFFF',
                titleColor: '#00887C',
                fillColor: 'red',
                contentColor: 'rgba(46,199,201,0.3)'
            });
            setTimeout("getRealtimeData()", 2000);
        }
    });
}

function moveData(type, data, key, val) {
    var dataTemp = [
        {name: '0', value: 0},
        {name: '0', value: 0},
        {name: '0', value: 0},
        {name: '0', value: 0},
        {name: '0', value: 0}
    ];
    for (var i = 0; i < 5; i++) {
        if (i == 4) {
            dataTemp[i].name = key;
            dataTemp[i].value = val;
        } else {
            dataTemp[i].name = data[i + 1].name;
            dataTemp[i].value = data[i + 1].value;
        }
    }
    switch (type) {
        case 1:
            data01 = dataTemp;
            break;
        case 2:
            data02 = dataTemp;
            break;
    }
}
