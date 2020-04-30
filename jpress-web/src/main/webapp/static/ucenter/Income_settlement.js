layui.use(['laypage', 'layer', 'table'], function() {
		laypage = layui.laypage //分页
			, layer = layui.layer //弹层
			, table = layui.table //表格
		//执行一个 table 实例
		table.render({
			elem: '#demo',
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
						width:'25%'
					}, {
						field: 'number',
						title: '收款方式',
						width:'25%'
					}, {
						field: 'price',
						title: '提现金额',
						width:'25%'
					}, {
						field: 'status',
						title: '打款状态',
						width:'25%'
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