$(document).on('click','.label',function(e) {
	e.stopPropagation(); //阻止，删除会出现事件穿透
	if ($(this).hasClass('on')) {
		$(this).removeClass('on')
	} else{
		$(this).addClass('on')
	}
})
$(document).click(function() {
		$('.label').removeClass('on')
});
	//单选行/不全选
$(document).on('click','input[name="abox"]', function(data) {
		
		//选中的行数与总行数的对比
		if($("input[name='abox']:checked").length == $("input[name='abox']").length) {
			$("input[name='checkall']").prop("checked", true);
			//alert($("input[name='abox']:checked").length)
		} else {
			$("input[name='checkall']").prop("checked", false);
		}
	})
	//全选
$(document).on('click',"input[name='checkall']", function(data) {
		var a = $(this).is(":checked");
		console.log(a)
		if(a == true) {
			$("input[name='abox']").prop("checked", true);
			$("input[name='checkall']").prop("checked", true);
		} else {
			$("input[name='abox']").prop("checked", false);
			$("input[name='checkall']").prop("checked", false);
		}
	});


	
