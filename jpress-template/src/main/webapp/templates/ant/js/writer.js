$(document).on('click', '.tabul_0 li', function() {
    $(this).parent().find('li').removeClass('active'); //移除当前选项卡的选中
    $(this).addClass('active'); //将选中效果添加到当前的被选项
    var divshow = $(this).attr('idx'); //获取组件id值
    $('.tabdiv_0 div').removeClass('active'); //移除所有显示的类
    var liconter = divshow + ' ul li'; //内容li
    console.log($(liconter).length) //判断长度，添加物内容时的效果
    if($(liconter).length == 0) {
        var uldiv = divshow + ' ul';
        var non = $('<li class="liempty">暂无内容</li>');
        $(uldiv).append(non);
    }
    $(divshow).addClass('active');

})
$(document).on('click', '.tabul_1 li', function() {
    $(this).parent().find('li').removeClass('active');
    $(this).addClass('active');
    var divshow = $(this).attr('idx');
    $('.tabdiv_1 div').removeClass('active');
    var liconter = divshow + ' ul li';
    console.log($(liconter).length)
    if($(liconter).length == 0) {
        var uldiv = divshow + ' ul';
        var non = $('<li class="liempty">暂无内容</li>');
        $(uldiv).append(non);
    }
    $(divshow).addClass('active');
})
$(document).on('click', '.user_head_click', function() {
    $('.tabul_1').css('display', 'none');
    $('.tabdiv_1').css('display', 'none');
    $('.tabul_0').css('display', 'block');
    $('.tabdiv_0').css('display', 'block');
})
$(document).on('click', '.statistics dd', function() {
    if($(this).find('p').text() == '关注') {
        $(".tabul_1 li").removeClass('active')
        $(".tabul_1 li[idx='#tab_4']").addClass('active');
        $('.tabdiv_1 div').removeClass('active');
        $('#tab_5').removeClass('active');
        $('#tab_4').addClass('active');
    } else {
        $(".tabul_1 li").removeClass('active')
        $(".tabul_1 li[idx='#tab_5']").addClass('active');
        $('.tabdiv_1 div').removeClass('active');
        $('#tab_4').removeClass('active');
        $('#tab_5').addClass('active');
    }
    $('.tabul_0').css('display', 'none');
    $('.tabdiv_0').css('display', 'none');
    $('.tabul_1').css('display', 'block');
    $('.tabdiv_1').css('display', 'block');
})
$(document).on('click', '.guanzhu', function() {

})
$(document).on("click", '.top', function(e) {
    $('body,html').animate({
        scrollTop: $('body').offset().top
    }, 100);
})
$(document).on('focus', '.wsearch .input-text', function() {
    $('.wsearch .input-group').css('border','1px solid #f00');
})
$(document).on('blur', '.wsearch .input-text', function() {
    $('.wsearch .input-group').css('border','1px solid #e8e8e8');
})
//$('.Cancel_collection').hide();
//		$(document).on('mouseenter', '.tab-pane li', function(e) {
//			e.stopPropagation(); //阻止，删除会出现事件穿透
//			$(this).find('.Cancel_collection').show();
//		})
//
//		$(document).on('mouseleave', '.tab-pane li', function(e) {
//			e.stopPropagation(); //阻止，删除会出现事件穿透
//			$(this).find('.Cancel_collection').hide();
//		})