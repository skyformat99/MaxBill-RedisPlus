var $;
var form;
var layer;
var upload;

layui.use(['jquery', 'form', 'layer', 'upload'], function () {
    $ = layui.jquery;
    form = layui.form;
    layer = layui.layer;
    upload = layui.upload;
    form.render();
    initUpload();

    //注册监听返回事件
    $("#gobackBtn").on("click", function () {
        var index = parent.layer.getFrameIndex(window.name);
        parent.layer.close(index);
        return false;
    });

});


function initUpload() {

    upload.render({
        elem: '#test8'
        , url: '/upload/'
        , auto: false
        , bindAction: '#test9'
        , choose: function (obj) {
            layer.alert(1);
            console.log(obj);
            var reader = new FileReader();
            reader.readAsText(files[0], "UTF-8");
            reader.onload = function (evt) {
                console.log(evt.target.result);
            }

        }
        , done: function (res) {
            layer.alert(1);
            console.log(res)
        }
    });
}