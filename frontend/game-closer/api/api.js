import requestAll from './request.js';

const apiUrl = 'http://127.0.0.1:8083'
const webUrl = '域名'
const baseUrl = apiUrl + '?act='

const api= {
    
    //获取url补充静态资源
    getUrl(){
        return webUrl;
    },

		getSocketList(){
			return requestAll.postRequest(apiUrl,{},'get');
		},
  //获取轮播图
  // getBanner(session,language){
  //     let data = {
  //         session,
  //         language
  //     }
  //     return requestAll.postRequest(apiUrl + 'portal/index/top_banner', data);
  // },
}
 
export default api
