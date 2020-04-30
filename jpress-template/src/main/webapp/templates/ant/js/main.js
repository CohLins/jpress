$(document).ready(function () {

    initAutoAjaxSubmit();
    initElegantDate();
    init();
});

function initEditor(editor, height) {
    height = height || 467;
    return initCkEdtior(editor, height);
}

function initCkEdtior(editor, height) {

    CKEDITOR.config.toolbar =
        [
            ['Bold', 'Italic', 'Underline', 'Strike', 'RemoveFormat'],
            ['Blockquote', 'CodeSnippet', 'Image', 'Flash', 'Table', 'HorizontalRule'],
            ['Link', 'Unlink', 'Anchor'],
            ['Outdent', 'Indent'],
            ['NumberedList', 'BulletedList'],
            ['JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock'],
            '/',
            ['Format', 'FontSize'],
            ['TextColor', 'BGColor'],
            ['Undo', 'Redo'],
            ['Maximize', 'Source']
        ];

    CKEDITOR.config.disallowedContent = 'img{width,height};img[width,height]';

    var bastPath = typeof jpress == "undefined" ? '' : jpress.cpath;

    return CKEDITOR.replace(editor, {
        autoUpdateElement: true,
        removePlugins: 'easyimage,cloudservices',
        extraPlugins: 'codesnippet,uploadimage,flash,image',
        codeSnippet_theme: 'monokai_sublime',
        height: height,
        uploadUrl: bastPath + '/commons/ckeditor/upload',
        imageUploadUrl: bastPath + '/commons/ckeditor/upload',
        filebrowserUploadUrl: bastPath + '/commons/ckeditor/upload',
        language: 'zh-cn'

    });
}

function initAutoAjaxSubmit() {
    $('.autoAjaxSubmit').on('submit', function () {

        if (typeof (CKEDITOR) != "undefined") {
            for (instance in CKEDITOR.instances) {
                CKEDITOR.instances[instance].updateElement();
            }
        }

        var validateFunction = $(this).attr('data-val-function');
        if( validateFunction && !eval(validateFunction)()){
            return false;
        }
        var okFunction = $(this).attr('data-ok-function');
        var okHref = $(this).attr('data-ok-href');
        var okMessage = $(this).attr('data-ok-message');

        var failFunction = $(this).attr('data-fail-function');
        var failMessage = $(this).attr('data-fail-message');


        $(this).ajaxSubmit({
            type: "post",
            success: function (result) {
                if (result.state == "ok") {
                    if (okFunction) {
                        eval(okFunction)(result);
                        return;
                    }

                    if (okHref) {
                        location.href = okHref;
                        return
                    }

                    if (okMessage) {
                        if (typeof toastr != "undefined") {
                            toastr.success(okMessage);
                        } else {
                            alert(okMessage);
                        }
                        return;
                    }

                    location.reload();

                }
                //fail
                else {
                    if (failFunction) {
                        eval(failFunction)(result);
                        return;
                    }

                    if (failMessage) {
                        showErrorMessage(failMessage);
                        return
                    }

                    if (result.message) {
                        showErrorMessage(result.message);
                    } else {
                        showErrorMessage('操作失败。')
                    }
                }
            },
            error: function () {
                showErrorMessage('系统错误，请稍后重试。');
            }
        });

        return false;
    });
}

function showErrorMessage(msg) {
    if (typeof toastr != "undefined") {
        toastr.error(msg, '操作失败');
    } else {
        alert(msg);
    }
}

function initElegantDate() {
    $(".elegantDate").each(function () {
        $(this).text(getElegantDate($(this).attr('data-elegant-date')))
    })
}

function getElegantDate(dateStr) {

    var publishTime = Date.parse(dateStr.replace(/-/gi, "/")) / 1000,
        d_seconds,
        d_minutes,
        d_hours,
        d_days,
        timeNow = parseInt(new Date().getTime() / 1000),
        d,

        date = new Date(publishTime * 1000),
        Y = date.getFullYear(),
        M = date.getMonth() + 1,
        D = date.getDate(),
        H = date.getHours(),
        m = date.getMinutes(),
        s = date.getSeconds();

    //小于10的在前面补0
    if (M < 10) {
        M = '0' + M;
    }
    if (D < 10) {
        D = '0' + D;
    }
    if (H < 10) {
        H = '0' + H;
    }
    if (m < 10) {
        m = '0' + m;
    }
    if (s < 10) {
        s = '0' + s;
    }

    d = timeNow - publishTime;
    d_days = parseInt(d / 86400);
    d_hours = parseInt(d / 3600);
    d_minutes = parseInt(d / 60);
    d_seconds = parseInt(d);

    if (d_days > 0 && d_days < 3) {
        return d_days + '天前';
    } else if (d_days <= 0 && d_hours > 0) {
        return d_hours + '小时前';
    } else if (d_hours <= 0 && d_minutes > 0) {
        return d_minutes + '分钟前';
    } else if (d_seconds < 60) {
        if (d_seconds <= 0) {
            return '刚刚发表';
        } else {
            return d_seconds + '秒前';
        }
    } else if (d_days >= 3 && d_days < 30) {
        return M + '-' + D + ' ' + H + ':' + m;
    } else if (d_days >= 30) {
        return Y + '-' + M + '-' + D + ' ' + H + ':' + m;
    }
}


//comment

function init() {

    if (typeof hljs != "undefined") {
        hljs.initHighlightingOnLoad();
    }

    $('#commentForm').on('submit', function () {
        $(this).ajaxSubmit({
            type: "post",
            success: function (data) {
                if (data.state == "ok") {
                    doRenderComment(data);
                    $('#content').val('');
                } else {
                    doRenderError(data);
                }
                $('#captcha').val('');
                $('#captchaimg').attr('src',jpress.cpath+'/commons/captcha?d='+Math.random());
            },
            error: function () {
                alert("网络错误，请稍后重试");
            }
        });
        return false;
    });
}



function doRenderComment(data) {
    if (data.code == 0) {
        var tmpl = $.templates("#commentTmpl");
        var html = tmpl.render(data);
        var childs = $("#comment-pan  > div:first-child");
        if(childs.length == 0){
            $("#comment-pan").append(html);
        }else{
            childs.before(html);
        }
    } else {
        alert('评论发布成功，管理审核后即可正常显示。')
    }
}

function doRenderError(data){
    alert('评论失败：' + data.message);
    if (data.errorCode == 9) {
        location.href = jpress.cpath + '/user/login';
    }
}

$('body').on('click', '.toReply', function () {
    $('#pid').val($(this).attr('data-cid'));
    $('#content').val('回复 @' + $(this).attr('data-author') + " ：");
    $('#content').focus();
});


function initCommentEdtior(editor, height) {

    CKEDITOR.config.toolbar =
        [
            ['Bold', 'Italic', 'Underline', 'Strike', 'RemoveFormat'],
            ['Blockquote', 'CodeSnippet', 'Image', 'Table', 'HorizontalRule'],
            ['Link', 'Unlink'],
            ['Outdent', 'Indent'],
            ['NumberedList', 'BulletedList'],
            ['JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock']
        ];

    CKEDITOR.config.disallowedContent = 'img{width,height};img[width,height]';

    var bastPath = typeof jpress == "undefined" ? '' : jpress.cpath;

    return CKEDITOR.replace(editor, {
        autoUpdateElement: true,
        removePlugins: 'easyimage,cloudservices',
        extraPlugins: 'codesnippet,uploadimage,flash,image',
        codeSnippet_theme: 'monokai_sublime',
        height: height,
        uploadUrl: bastPath + '/commons/ckeditor/upload',
        imageUploadUrl: bastPath + '/commons/ckeditor/upload',
        filebrowserUploadUrl: bastPath + '/commons/ckeditor/upload',
        language: 'zh-cn'

    });
}