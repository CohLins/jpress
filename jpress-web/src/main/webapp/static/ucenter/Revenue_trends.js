layui.use(['form', 'laydate'], function() {
	var laydate = layui.laydate;
	var form = layui.form;
	var d = new Date();
	var dd = d.getFullYear() + " - " + (d.getMonth() + 1);
	//执行一个laydate实例
	laydate.render({
		elem: '#Date_receiving',
		type: 'date',
		range: '~',
		format: 'yyyy-M-d',
		trigger: 'click',
		eventElem: '.icon_lxc',
		done: function(value, date, endDate) {
			var type = $('.mune_lxc ul:last-child a.active').text();
			console.log(value + "-" + type); //得到日期生成的值，如：2017-08-18
			//console.log(date.year + "-" + date.month); //得到日期时间对象：{year: 2017, month: 8, date: 18, hours: 0, minutes: 0, seconds: 0}
			$('.mune_lxc ul:first-child a').removeClass('active');
			if(type == '趋势图') {
				datachart();
			} else {
				datatlist();
			}
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
$(document).on('click', '.mune_lxc ul:first-child a', function() {
	$('.mune_lxc ul:first-child a').removeClass('active');
	$(this).addClass('active');
	var type = $('.mune_lxc ul:last-child a.active').text();
	datenum = $(this).text();
	console.log(type + "-" + datenum);
	if(type == '趋势图') {
		datachart();
	} else {
		datatlist();
	}
	$('#Date_receiving').val('');
})
$(document).on('click', '.mune_lxc ul:last-child a', function() {
	var datenum = $('.mune_lxc ul:first-child a.active').text();
	type = $(this).text();
	console.log(type + "-" + datenum)
	if($(this).text() == '趋势图') {
		$('#Data_list').removeClass('active');
		$('#Trend_chart').addClass('active');
		datachart();
	} else {
		$('#Data_list').addClass('active');
		$('#Trend_chart').removeClass('active');
		datatlist();
	}
	$('.mune_lxc ul:last-child a').removeClass('active');
	$(this).addClass('active');

})

datachart();
//function datachart(date,type){
function datachart() {
	Highcharts.setOptions({
		lang: {

			　
			printChart: "打印图表",
			downloadJPEG: "下载JPEG 图片",
			downloadPDF: "下载PDF文档",
			downloadPNG: "下载PNG 图片",
			downloadSVG: "下载SVG 矢量图",
			exportButtonTitle: "导出图片"
		}
	});
	var i = 2
	var chart = Highcharts.chart('Trend_chart', {
		title: {
			text: null
		},
		subtitle: {
			text: null
		},
		yAxis: {
			title: {
				text: null
			}
		},
		legend: {
			layout: 'vertical',
			align: 'right',
			verticalAlign: 'middle'
		},
		plotOptions: {
			series: {
				label: {
					connectorAllowed: false
				},
				//						pointStart: 2010
				dateTimeLabelFormats: {
					week: '%Y-%m-%d'
				}
			}
		},
		xAxis: {
			categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jundwdwadawdwdwa', 'Jul'],
			tickInterval: i
		},
		series: [{
			name: '视频收益',
			data: [34, 3, 77, 58, 31, 31, 133]
		}, {
			name: '文章收益',
			data: [16, 64, 42, 51, 490, 282, 121]
		}, {
			name: '帖子收益',
			data: [44, 22, 5, 71, 185, 377, 147]
		}, {
			name: '其他收益',
			data: [null, null, 88, 169, 112, 452, 400]
		}],
		responsive: {
			rules: [{
				condition: {
					maxWidth: 500
				},
				chartOptions: {
					legend: {
						layout: 'horizontal',
						align: 'center',
						verticalAlign: 'bottom'
					}
				}
			}]
		}
	});
}
//function datatlist(date,type){
function datatlist() {
	layui.use(['laypage', 'layer', 'table'], function() {
		laypage = layui.laypage //分页
			, layer = layui.layer //弹层
			, table = layui.table //表格
		//执行一个 table 实例
		table.render({
			elem: '#Trend_data',
			url: '/static/ucenter/Purchase_order.json', //数据接口
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
				[ //表头
					//								{
					//									type: 'checkbox',
					//									fixed: 'left'
					//								}, 
					{
						field: 'name',
						title: '提现日期',
						width: '25%'
					}, {
						field: 'number',
						title: '收款方式',
						width: '25%'
					}, {
						field: 'price',
						title: '提现金额',
						width: '25%'
					}, {
						field: 'status',
						title: '打款状态',
						width: '25%'
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
		//监听行工具事件
		table.on('tool(test)', function(obj) { //注：tool 是工具条事件名，test 是 table 原始容器的属性 lay-filter="对应的值"
			var data = obj.data //获得当前行数据
				,
				layEvent = obj.event; //获得 lay-event 对应的值
			if(layEvent === 'edit') {
				layer.msg(22)
			}
		});
	})
}