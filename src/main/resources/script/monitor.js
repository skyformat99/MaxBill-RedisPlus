var $;
var layer;
var table;
var chart1;
var chart2;
var chart3;
var chart4;
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

layui.use(['jquery', 'layer'], function () {
    $ = layui.jquery;
    layer = layui.layer;
    window.onresize = function () {
        $('#container1').highcharts().reflow();
        $('#container2').highcharts().reflow();
        $('#container3').highcharts().reflow();
        $('#container4').highcharts().reflow();
    }
    showCharts1();
    showCharts2();
    showCharts3();
    showCharts4();
});

Highcharts.setOptions({
    global: {
        useUTC: false
    }
});

function activeLastPointToolip(chart) {
    var points = chart.series[0].points;
    chart.tooltip.refresh(points[points.length - 1]);
}

function showCharts1() {
    Highcharts.chart('container1', {
        chart: {
            type: 'spline',
            marginRight: 10,
            events: {
                load: function () {
                    var chart = this;
                    var series01 = this.series[0];
                    var series02 = this.series[1];
                    activeLastPointToolip(chart);
                    setInterval(function () {
                        //获取实时内存数据

                        var x = (new Date()).getTime(), // 当前时间
                            y = Math.random();          // 随机值
                        series01.addPoint([x, y], true, true);
                        series02.addPoint([x, y], true, true);
                        activeLastPointToolip(chart);
                    }, 1000);
                }
            }
        },
        credits: {
            enabled: false
        },
        title: {
            text: '当前内存占用量实时监控（MB）'
        },
        xAxis: {
            type: 'datetime',
            tickPixelInterval: 150
        },
        yAxis: {
            title: {
                text: null
            }
        },
        tooltip: {
            formatter: function () {
                return '<b>' + this.series.name + '</b><br/>' +
                    Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) + '<br/>' +
                    Highcharts.numberFormat(this.y, 2);
            }
        },
        legend: {
            enabled: false
        },
        series: [{
            name: '随机数据',
            data: (function () {
                var pots;
                var data = [];
                var time = (new Date()).getTime();
                for (pots = -15; pots <= 0; pots += 1) {
                    data.push({
                        x: time + pots * 1000,
                        y: 0
                    });
                }
                return data;
            }())
        }, {
            name: '随机数据',
            data: (function () {
                var pots;
                var data = [];
                var time = (new Date()).getTime();
                for (pots = -15; pots <= 0; pots += 1) {
                    data.push({
                        x: time + pots * 1000,
                        y: 0
                    });
                }
                return data;
            }())
        }]
    });
}


function showCharts2() {
    var chart2 = Highcharts.chart('container2', {
        chart: {
            type: 'spline',
            marginRight: 10,
            events: {
                load: function () {
                    var series = this.series[0],
                        chart = this;
                    activeLastPointToolip(chart);
                    setInterval(function () {
                        var x = (new Date()).getTime(), // 当前时间
                            y = Math.random();          // 随机值
                        series.addPoint([x, y], true, true);
                        activeLastPointToolip(chart);
                    }, 1000);
                }
            }
        },
        credits: {
            enabled: false
        },
        title: {
            text: '主进程核心态累计CPU耗时（S）'
        },
        xAxis: {
            type: 'datetime',
            tickPixelInterval: 150
        },
        yAxis: {
            title: {
                text: null
            }
        },
        tooltip: {
            formatter: function () {
                return '<b>' + this.series.name + '</b><br/>' +
                    Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) + '<br/>' +
                    Highcharts.numberFormat(this.y, 2);
            }
        },
        legend: {
            enabled: false
        },
        series: [{
            name: '随机数据',
            data: (function () {
                // 生成随机值
                var data = [],
                    time = (new Date()).getTime(),
                    i;
                for (i = -19; i <= 0; i += 1) {
                    data.push({
                        x: time + i * 1000,
                        y: Math.random()
                    });
                }
                return data;
            }())
        }]
    });
}


function showCharts3() {
    var chart = Highcharts.chart('container3', {
        chart: {
            type: 'spline',
            marginRight: 10,
            events: {
                load: function () {
                    var series = this.series[0],
                        chart = this;
                    activeLastPointToolip(chart);
                    setInterval(function () {
                        var x = (new Date()).getTime(), // 当前时间
                            y = Math.random();          // 随机值
                        series.addPoint([x, y], true, true);
                        activeLastPointToolip(chart);
                    }, 1000);
                }
            }
        },
        credits: {
            enabled: false
        },
        title: {
            text: '当前各数据库实时键个数（个）'
        },
        xAxis: {
            type: 'datetime',
            tickPixelInterval: 150
        },
        yAxis: {
            title: {
                text: null
            }
        },
        tooltip: {
            formatter: function () {
                return '<b>' + this.series.name + '</b><br/>' +
                    Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) + '<br/>' +
                    Highcharts.numberFormat(this.y, 2);
            }
        },
        legend: {
            enabled: false
        },
        series: [{
            name: '随机数据',
            data: (function () {
                // 生成随机值
                var data = [],
                    time = (new Date()).getTime(),
                    i;
                for (i = -19; i <= 0; i += 1) {
                    data.push({
                        x: time + i * 1000,
                        y: Math.random()
                    });
                }
                return data;
            }())
        }]
    });
}


function showCharts4() {
    var chart = Highcharts.chart('container4', {
        chart: {
            type: 'spline',
            marginRight: 10,
            events: {
                load: function () {
                    var series = this.series[0],
                        chart = this;
                    activeLastPointToolip(chart);
                    setInterval(function () {
                        var x = (new Date()).getTime(), // 当前时间
                            y = Math.random();          // 随机值
                        series.addPoint([x, y], true, true);
                        activeLastPointToolip(chart);
                    }, 1000);
                }
            }
        },
        credits: {
            enabled: false
        },
        title: {
            text: '动态模拟实时数据'
        },
        xAxis: {
            type: 'datetime',
            tickPixelInterval: 150
        },
        yAxis: {
            title: {
                text: null
            }
        },
        tooltip: {
            formatter: function () {
                return '<b>' + this.series.name + '</b><br/>' +
                    Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) + '<br/>' +
                    Highcharts.numberFormat(this.y, 2);
            }
        },
        legend: {
            enabled: false
        },
        series: [{
            name: '随机数据',
            data: (function () {
                // 生成随机值
                var data = [],
                    time = (new Date()).getTime(),
                    i;
                for (i = -19; i <= 0; i += 1) {
                    data.push({
                        x: time + i * 1000,
                        y: Math.random()
                    });
                }
                return data;
            }())
        }]
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