<template>
  <div class="content">
		<div class="a" style="display: flex;flex-wrap:wrap;align-items: center;justify-content: center;">
			<div class="select">
				<el-select v-model="value" placeholder="请选择主机" style="width: 250px;">
					<el-option
						v-for="item in hostNameList"
						:key="item.value"
						:label="item.hostname +'   '+ item.createdTime"
						:value="item.hostname">
					</el-option>
				</el-select>
			</div>
			<div class="process">
				<el-input v-model="processName" placeholder="请输入进程名" style="width: 250px;"></el-input>
			</div>
			<div class="usually-process">
				<el-select v-model="processName" placeholder="请选择常用进程" style="width: 250px;">
					<el-option 
						v-for="item in usuallyProcess"
						:key="item.value"
						:label="item.label"
						:value="item.processName">
					</el-option>
				</el-select>
			</div>
		</div>
		<div class="send">
			
		</div>
		<el-button type="primary" @click="send">发送</el-button>
		<div class="result">
			<el-popover
				:open-delay="100"
			  placement="bottom"
			  width="400"
			  trigger="click">
			  <el-table :data="resultList" :height="200">
			    <el-table-column align="center" width="200" property="hostname" sortable label="主机"></el-table-column>
			    <el-table-column align="center" width="120" property="gameName" sortable label="进程"></el-table-column>
			    <el-table-column align="center" width="80" property="resultCode" sortable label="结果"></el-table-column>
			    <el-table-column align="center" width="200" property="createdTime" sortable label="创建时间"></el-table-column>
			  </el-table>
			  <el-button slot="reference" @click="getResult" style="margin-top: 20px;">获取结果</el-button>
			</el-popover>
		</div>
  </div>
</template>

<script>
// @ is an alias to /src

import axios from 'axios';
export default {
  name: 'Index',
		data() {
			return {
				hostNameList:[],
				value:'',
				key:'',
				processName:'',
				resultList:[],
				usuallyProcess:[],
			}
		},
  components: {

  },
	created(){
		this.init();
	},
	methods:{
		init(){
			var _this=this;
			axios.get('http://106.15.74.153:8083/api/devices')
			.then(function(response){
				if( response.data.errorCode!=0){
					_this.$message({
						message: '获取主机列表失败！',
						type: 'warning'
					});
				}
				let arr=response.data.data.deviceList;
				for (var i = 0; i < arr.length; i++) {
					arr[i].value=i;
				}
				_this.hostNameList=arr;
			});
			axios.get('http://picgo-fz.oss-cn-shanghai.aliyuncs.com/game-closer/usuallyProcess.json')
			.then(function(response){
				console.log(response.data)
				let usuallyProcessList=response.data.usuallyProcess;
				for (var i = 0; i < usuallyProcessList.length; i++) {
					usuallyProcessList[i].value=i;
				}
				_this.usuallyProcess=usuallyProcessList;
			});
		},
		send(){
			var _this=this;
			console.log('发送请求');
			console.log(this.$data.value);
			let data = {
				"hostname":this.$data.value,
				"gameName":this.$data.processName
			}
			console.log(data);
			if(data.hostname.length > 0){
				axios.post('http://106.15.74.153:8083/api/devices/device/'+data.hostname,data)
				.then(function(response){
					console.log(response.data)
					let res = response.data;
					if(res.errorCode!=0){
						_this.$message({
							message: res.message,
							type: 'warning'
						})
					}
				});
			}else{
				_this.$message({
					message: "主机不能为空",
					type: 'warning'
				})
			}
		},
		getResult(){
			var _this=this;
			axios.get('http://106.15.74.153:8083/api/devices/result')
			.then(function(response){
				if( response.data.errorCode!=0){
					_this.$message({
						message: '获取结果列表失败！',
						type: 'warning'
					});
				}
				_this.resultList=response.data.data.resultList;
				//模拟多数据
				// for (var i = 1; i < 20; i++) {
				// 	_this.resultList[i]=_this.resultList[0]
				// }
			});
		}
	},
}
</script>

<style type="text/css">
	.content{
		width: 100%;
		height: 100%;
		position: fixed;
		background: linear-gradient(to bottom, #BE93C5, #7BC6CC);
		display: flex;
		justify-content: center;
		align-items: center;
		flex-direction:column;
	}
	
	.select{
		margin: 10px 10px 20px;
	}
	.send{
		margin-top: 20px;
	}
	.process{
		margin: 10px 10px 20px;
	}
	.usually-process{
		margin: 10px 10px 20px;
	}
</style>