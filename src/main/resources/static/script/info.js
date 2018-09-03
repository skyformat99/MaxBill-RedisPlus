var $;
var keyArr = ['0', '0', '0', '0', '0'];
var valArr01 = [0, 0, 0, 0, 0];
var valArr02 = [0, 0, 0, 0, 0];
var valArr03 = [0, 0, 0, 0, 0];

var chart01;
var chart02;

layui.use(['element', 'jquery'], function () {
    $ = layui.jquery;
    realtimeInfo01();
    realtimeInfo02();
    getRealtimeData();
});

function getRealtimeData() {
    $.ajax({
        type: 'get',
        url: "/api/info/realInfo",
        sync: false,
        success: function (data) {
            if (data.code == 200) {
                var key = data.data.key;
                var val01 = data.data.val01;
                var val02 = data.data.val02;
                var val03 = data.data.val03;
                moveKeyArray(keyArr, key);
                moveVal01Array(valArr01, val01);
                moveVal02Array(valArr02, val02);
                moveVal03Array(valArr03, val03);
                chart01.xAxis[0].setCategories(keyArr);
                chart02.xAxis[0].setCategories(keyArr);
                chart01.series[0].setData(valArr01);
                chart02.series[0].setData(valArr02);
                chart02.series[1].setData(valArr03);
                setTimeout("getRealtimeData()", 2000)
            }
        }
    });
}

function realtimeInfo01() {
    chart01 = Highcharts.chart('chart01', {
        chart: {
            type: 'area'
        },
        legend: {
            enabled: false
        },
        credits: {
            enabled: false
        },
        exporting: {
            enabled: false
        },
        title: {
            y: 20,
            margin: 20,
            align: 'center',
            text: '已占用内存量实时监控'
        },
        xAxis: {
            allowDecimals: false
        },
        yAxis: {
            title: {
                text: '占用量(kb)'
            }
        },
        series: [{
            name: '占用量',
            data: valArr01
        }]
    });
}

function realtimeInfo02() {
    chart02 = Highcharts.chart('chart02', {
        chart: {
            type: 'line'
        },
        legend: {
            enabled: false
        },
        credits: {
            enabled: false
        },
        exporting: {
            enabled: false
        },
        title: {
            y: 20,
            margin: 20,
            align: 'center',
            text: '已占用CPU信息实时监控'
        },
        xAxis: {
            categories: keyArr
        },
        yAxis: {
            title: {
                text: '占用率(%)'
            }
        },
        plotOptions: {
            line: {
                dataLabels: {
                    enabled: true//数据标签
                },
                enableMouseTracking: true //鼠标跟踪
            }
        },
        series: [{
            name: '系统占用率',
            data: valArr02
        }, {
            name: '用户占用率',
            data: valArr03
        }]
    });
}

function moveKeyArray(dataArray, val) {
    var valArray = new Array(5);
    valArray[0] = dataArray[1];
    valArray[1] = dataArray[2];
    valArray[2] = dataArray[3];
    valArray[3] = dataArray[4];
    valArray[4] = val;
    keyArr = valArray;
}

function moveVal01Array(dataArray, val) {
    var valArray = new Array(5);
    valArray[0] = dataArray[1];
    valArray[1] = dataArray[2];
    valArray[2] = dataArray[3];
    valArray[3] = dataArray[4];
    valArray[4] = val;
    valArr01 = valArray;
}

function moveVal02Array(dataArray, val) {
    var valArray = new Array(5);
    valArray[0] = dataArray[1];
    valArray[1] = dataArray[2];
    valArray[2] = dataArray[3];
    valArray[3] = dataArray[4];
    valArray[4] = val;
    valArr02 = valArray;
}

function moveVal03Array(dataArray, val) {
    var valArray = new Array(5);
    valArray[0] = dataArray[1];
    valArray[1] = dataArray[2];
    valArray[2] = dataArray[3];
    valArray[3] = dataArray[4];
    valArray[4] = val;
    valArr03 = valArray;
}

