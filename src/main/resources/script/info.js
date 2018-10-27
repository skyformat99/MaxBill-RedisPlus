var $;
var layer;

layui.use(['jquery', 'layer', 'element'], function () {
    $ = layui.jquery;
    layer = layui.layer;
    initBaseInfo();
    initLogsInfo();
});

/**初始化信息*/
function initBaseInfo() {
    var json = infoRouter.getBaseInfo();
    var data = JSON.parse(json);
    if (data.code === 200) {
        var redisData = data.data;
        $("#server").html(redisData.server);
        $("#client").html(redisData.client);
        $("#memory").html(redisData.memory);
        $("#persistence").html(redisData.persistence);
        $("#stats").html(redisData.stats);
        $("#cpu").html(redisData.cpu);
        var html = '';
        var users = redisData.users;
        for (var i = 0; i < users.length; i++) {
            html += '<tr>';
            html += '<td>' + users[i].id + '</td>';
            html += '<td>' + users[i].addr + '</td>';
            html += '<td>' + users[i].age + '</td>';
            html += '<td>' + users[i].db + '</td>';
            html += '</tr>';
        }
        $("#tbody01").html(html);
    } else {
        layer.msg(data.msgs);
    }
}


/**初始化信息*/
function initUserInfo(data) {
    var json = infoRouter.getUserInfo();
    var data = JSON.parse(json);
    if (data.code === 200) {
        var html = '';
        var users = redisData.users;
        for (var i = 0; i < users.length; i++) {
            html += '<tr>';
            html += '<td>' + users[i].id + '</td>';
            html += '<td>' + users[i].addr + '</td>';
            html += '<td>' + users[i].age + '</td>';
            html += '<td>' + users[i].db + '</td>';
            html += '</tr>';
        }
        $("#tbody").html(html);
    } else {
        layer.msg(data.msgs);
    }
}


/**初始化信息*/
function initLogsInfo() {
    var json = infoRouter.getLogsInfo();
    var data = JSON.parse(json);
    if (data.code === 200) {
        var html = '';
        var logs = data.data;
        for (var i = 0; i < logs.length; i++) {
            html += '<tr>';
            html += '<td>' + logs[i].id + '</td>';
            html += '<td>' + formatTimestamp(logs[i].timeStamp) + '</td>';
            html += '<td>' + logs[i].executionTime + '</td>';
            html += '<td>' + logs[i].args + '</td>';
            html += '</tr>';
        }
        $("#tbody02").html(html);
    } else {
        layer.msg(data.msgs);
    }
}


