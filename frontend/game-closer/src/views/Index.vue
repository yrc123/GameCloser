<template>
  <div class="content">
		<div class="a" style="display: flex;">
			<div class="select">
				<el-select v-model="value" placeholder="请选择主机">
					<el-option
						v-for="item in hostNameList"
						:key="item.value"
						:label="item.hostname"
						:value="item.hostname">
					</el-option>
				</el-select>
			</div>
			<div class="process">
				<el-input v-model="processName" placeholder="请输入进程名"></el-input>
			</div>
			<div class="usually-process">
				<el-select v-model="processName" placeholder="请选择常用进程">
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
		<el-button type="primary" @click="getResult" style="margin-top: 20px;">获取结果</el-button>

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
				usuallyProcess:[{
					'processName':'Tim与QQ',
					'label':'QQ',
					'value':0
				},{
					'processName':'YuanShen',
					'label':'原神',
					'value':1
				},{
					'processName':'UUVoice',
					'label':'UU语音',
					'value':2
				},
				],
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
			axios.get('http://127.0.0.1:8083/api/devices')
			.then(function(response){
				console.log(response.data.data.deviceList)
				let arr=response.data.data.deviceList;
				for (var i = 0; i < arr.length; i++) {
					arr[i].value=i;
				}
				_this.hostNameList=arr;
			});
		},
		send(){
			console.log('发送请求');
			console.log(this.$data.value);
			let data = {
				"hostname":this.$data.value,
				"gameName":this.$data.processName
			}
			axios.post('http://127.0.0.1:8083/api/devices/device/'+data.hostname,data)
			.then(function(response){
				console.log(response.data)
				let res = response.data;

			});
		},
		getResult(){
			console.log('获取结果'); 
			axios.get('http://127.0.0.1:8083/api/devices/result')
			.then(function(response){
				console.log(response.data.data.resultList)
				let arr=response.data.data.resultList;
				for (var i = 0; i < arr.length; i++) {
					arr[i].value=i;
				}
				// _this.hostNameList=arr;
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
	}
	.send{
		margin-top: 20px;
	}
	.process{
		margin-left: 20px;
		margin-right: 20px;
	}
</style>