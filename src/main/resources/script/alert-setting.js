var $;
var form;
var layer;
var colorpicker;

layui.use(['jquery', 'form', 'layer', 'colorpicker'], function () {
    $ = layui.jquery;
    form = layui.form;
    layer = layui.layer;
    colorpicker = layui.colorpicker;
    //渲染表单
    form.render();
    //表单赋值
    var colors = otherRouter.getSetting("theme-color");
    $('#color-inp').val(colors);
    colorpicker.render({
        elem: '#color-box',
        color: colors,
        done: function (color) {
            $('#color-inp').val(color);
            otherRouter.setSetting("theme-color", color);
        }
    });
    //注册事件
    $("#color-btn").on("click", function () {
        otherRouter.setSetting("theme-color", "#D6D6D7");
    });
});
