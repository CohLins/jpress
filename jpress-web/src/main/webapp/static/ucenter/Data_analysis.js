layui.use(['form', 'laydate'], function() {
	var laydate = layui.laydate;
	var form = layui.form;
	var d = new Date();
	var dd = d.getFullYear() + " - " + (d.getMonth() + 1);
	//执行一个laydate实例
	laydate.render({
		elem: '#Date_receiving_DA',
		type: 'date',
		range: '~',
		format: 'yyyy-M-d',
		trigger: 'click',
		eventElem: '.icon_DA',
		done: function(value, date, endDate) {
			var type = $('.mune_type li.active').text();
			console.log(value + "-" + type); //得到日期生成的值，如：2017-08-18
			//console.log(date.year + "-" + date.month); //得到日期时间对象：{year: 2017, month: 8, date: 18, hours: 0, minutes: 0, seconds: 0}
			$('.mune_DA ul a').removeClass('active');
			datatlist_DA();
		},
		//改变月份时的回调
		change: function(value, date, endDate) {
			//console.log(value); //得到日期生成的值，如：2017-08-18
			//var data = date.year + "-" + date.month; //得到日期时间对象：{year: 2017, month: 8, date: 18, hours: 0, minutes: 0, seconds: 0}
			//$('.mune_lxc ul:first-child a').removeClass('active');
			//时间改变时发送时间以及设备类型请求数据

		}
	});
});
$(document).on('click', '.mune_DA ul li', function() {
	$('.mune_DA ul li').removeClass('active');
	$(this).addClass('active');
	var type = $('.mune_type li.active').text();
	datenum = $(this).text();
	console.log(type+ "-" + datenum);
	datatlist_DA();
	$('#Date_receiving_DA').val('');
})

$(document).on('click', '.mune_type li', function() {
	$('.mune_type li').removeClass('active');
	$(this).addClass('active');
	var type = $('.mune_type li.active').text();
	$('.mune_DA ul li').removeClass('active');
	$('.mune_DA ul ').children("a:eq(0)").find('li').addClass('active');
	console.log($('.mune_DA ul ').children("a:eq(0)").find('li').text())
	datatlist_DA();
	$('#Date_receiving_DA').val('');
})


datatlist_DA();
//function datatlist_DA(date,type){
function datatlist_DA() {
	layui.use(['laypage', 'layer', 'table'], function() {
		laypage = layui.laypage //分页
			, layer = layui.layer //弹层
			, table = layui.table //表格
		//执行一个 table 实例
		table.render({
			elem: '#Data_analysis_table',
			url: '/static/ucenter/da.json', //数据接口
			title: '用户表',
			page: { //支持传入 laypage 组件的所有参数（某些参数除外，如：jump/elem） - 详见文档
				layout: ['prev', 'page', 'next', 'count', 'skip'] //自定义分页布局
					//
					,
				curr: 1 //设定初始在第 5 页
					,
				limit: 7 //每页显示条数
					,
				groups: 4 //只显示 1 个连续页码
					,
				first: false //不显示首页
					,
				last: false //不显示尾页
			}
			//,defaultToolbar: ['filter','print','exports'] //这里在右边显示 
			//,toolbar: '#toolbar' //开启工具栏，此处显示默认图标，可以自定义模板，详见文档
			,
			cols: [
				[ 
					{
						field: 'name',
						title: '标题',
						width: '29.5%'
					}, {
						field: 'number',
						title: '阅读量',
						width: '14%'
					}, {
						field: 'price',
						title: '评论量',
						width: '14%'
					}, {
						field: 'status',
						title: '收藏量',
						width: '14%'
					}, {
						field: 'status',
						title: '点赞数',
						width: '14%'
					}, {
						field: 'status',
						title: '转发数',
						width: '14%'
					}
				]
			],
			response: {
				statusCode: 200 //重新规定成功的状态码为 200，table 组件默认为 0
			},
			//			done: function() {
			//				$('.layui-table tbody tr').eq(0).addClass('active');
			//			},
			parseData: function(res) { //将原始数据解析成 table 组件所规定的数据
				return {
					"code": res.status, //解析接口状态
					"msg": res.msg, //解析提示文本
					"count": res.total, //解析数据长度
					"data": res.data //解析数据列表
				};
			}
		});
	})
}