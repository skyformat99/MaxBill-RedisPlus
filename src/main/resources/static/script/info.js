var $;
var keyArr = ['0', '0', '0', '0', '0'];
var valueArr01 = [0, 0, 0, 0, 0];
var valueArr02 = [0, 0, 0, 0, 0];
var valueArr03 = [0, 0, 0, 0, 0];
var chart01 = echarts.init(document.getElementById("chart01"), null, {renderer: 'svg'});
var chart02 = echarts.init(document.getElementById("chart02"), null, {renderer: 'svg'});
// var chart01 = echarts.init(document.getElementById("chart01"));
// var chart02 = echarts.init(document.getElementById("chart02"));

layui.use(['element', 'jquery'], function () {
    $ = layui.jquery;
    getRealtimeData();
});

function getRealtimeData() {
    $.ajax({
        type: 'get',
        url: "/api/info/realInfo",
        timeout: 5000,
        sync: false,
        success: function (data) {
            if (data.code == 200) {
                var key = data.data.key;
                var val01 = data.data.val01;
                var val02 = data.data.val02;
                var val03 = data.data.val03;
                realtimeInfo01(moveKeyArray(keyArr, key), moveVal01Array(valueArr01, val01));
                realtimeInfo02(moveKeyArray(keyArr, key), moveVal02Array(valueArr02, val02), moveVal03Array(valueArr03, val03));
                setTimeout("getRealtimeData()", 3000)
            }
        }
    });
}


function realtimeInfo01(key, value) {
    var option = {
        title: {
            left: 'center',
            top: '20px',
            text: '已占用内存量实时监控',
        },
        tooltip: {
            trigger: 'axis'
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: {
            type: 'category',
            boundaryGap: false,
            data: key
        },
        yAxis: {
            type: 'value'
        },
        series: [{
            data: value,
            type: 'line',
            areaStyle: {}
        }]
    };
    if (option && typeof option === "object") {
        chart01.setOption(option, true);
    }
}

function realtimeInfo02(key, value01, value02) {
    var option = {
        title: {
            left: 'center',
            top: '20px',
            text: '已占用CPU信息实时监控',
        },
        tooltip: {
            trigger: 'axis'
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: {
            type: 'category',
            boundaryGap: false,
            data: key
        },
        yAxis: {
            type: 'value'
        },
        series: [
            {
                name: '系统CPU占用',
                type: 'line',
                stack: '占用率',
                data: value01
            },
            {
                name: '用户CPU占用',
                type: 'line',
                stack: '占用率',
                data: value02
            }
        ]
    };
    if (option && typeof option === "object") {
        chart02.setOption(option, true);
    }
}

function moveKeyArray(dataArray, val) {
    var valArray = new Array(5);
    valArray[0] = dataArray[1];
    valArray[1] = dataArray[2];
    valArray[2] = dataArray[3];
    valArray[3] = dataArray[4];
    valArray[4] = val;
    keyArr = valArray;
    return valArray;
}

function moveVal01Array(dataArray, val) {
    var valArray = new Array(5);
    valArray[0] = dataArray[1];
    valArray[1] = dataArray[2];
    valArray[2] = dataArray[3];
    valArray[3] = dataArray[4];
    valArray[4] = val;
    valueArr01 = valArray;
    return valArray;
}


function moveVal02Array(dataArray, val) {
    var valArray = new Array(5);
    valArray[0] = dataArray[1];
    valArray[1] = dataArray[2];
    valArray[2] = dataArray[3];
    valArray[3] = dataArray[4];
    valArray[4] = val;
    valueArr02 = valArray;
    return valArray;
}

function moveVal03Array(dataArray, val) {
    var valArray = new Array(5);
    valArray[0] = dataArray[1];
    valArray[1] = dataArray[2];
    valArray[2] = dataArray[3];
    valArray[3] = dataArray[4];
    valArray[4] = val;
    valueArr03 = valArray;
    return valArray;
}

