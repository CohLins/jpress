/*选择图片--由事件触发另一个事件*/
$(document).on('click','.m_image',function() {
	$('input[name="m_image"]').click();	
})
 /*选择图片并回显、获取base64编码*/
$('input[name="m_image"]').change(function(e) {
	 var name = $(this).val();
	 var i = name.split("\\").length;
	 console.log(name.split("\\")[i-1].split(".")[0]);//获取上传的文件名
	 var fileobj=e.target.files[0];
	 console.log(fileobj);
	  var reader = new FileReader();
      reader.readAsDataURL(e.target.files[0]); // 读出 base64
      reader.onloadend = function () {
            // 图片的 base64 格式, 可以直接当成 img 的 src 属性值        
           var dataURL = reader.result;//base64
           // 显示图片
           $('.one-photo img').attr('src',dataURL);
           $('.photo-preview').css('display','block');
      };
      
})

$(document).on('click','.delimg',function() {
	$('input[name="m_image"]').val("");
	$('.one-photo img').attr('src',"");
	$('.photo-preview').css('display','none');
})


$(document).on('click','.m_delete',function() {
	var msgid = $(this).attr('msg-id');
	console.log(msgid);
			//	$.ajax({
			//		type: "get",
			//		url: "",
			//		data: {
			//			
			//		},
			//		//async: false,
			//		dataType: 'json',
			//		success: function(data) {
			//			
			//		}
			//	});
})

/*删除移入移除效果*/
$(document).on('mouseenter', '.chat', function(e) {
	$(this).find('.operations').show();
})
$(document).on('mouseleave', '.chat', function(e) {
	$(this).find('.operations').hide();
})
