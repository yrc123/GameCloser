// post请求

import axios from 'axios';
function postRequest(url, data = {}, method = 'post') {
    return new Promise((resolve, reject) => {
        axios({
						url: url,
						method: method,
						headers: {
								'Content-Type': 'application/json'
						},
                data: data
            })
            .then(res => {
                //成功
                resolve(res.data)
            })
            .catch(res => {
                //失败
                reject(res)
            })

    })
}


export default {
    postRequest: postRequest
}