<script setup>
// 페이지 구성 : 방송하기 버튼, 썸네일
// 방송하기 버튼 클릭 -> 세션 열림 -> 여기서 나온 sessionID를 대쉬보드로 넘겨줘야 함 -> 방송자로 connect
import axios from 'axios'
import { OpenVidu } from 'openvidu-browser'
//openvidu api 요청 url 
const OV = new OpenVidu() 
const API_SERVER_URL = import.meta.env.VITE_API_SERVER_URL 
var sessionId = ''
let connectId = ''
var session = ''

const createSession = async () => {
  try {
    // 성공적으로 통신시 클라이언트측 세션 초기화
    session = OV.initSession()
    console.log(session)
    const response = await axios.post(`${API_SERVER_URL}openvidu/api/sessions`, {
      "mediaMode": "ROUTED",
      "recordingMode": "MANUAL",
      "customSessionId": "CUSTOM_SESSION_ID",
      "forcedVideoCodec": "VP8",
      "allowTranscoding": false,
      "defaultRecordingProperties": {
          "name": "MyRecording",
          "hasAudio": true,
          "hasVideo": true,
          "outputMode": "COMPOSED",
          "recordingLayout": "BEST_FIT",
          "resolution": "1280x720",
          "frameRate": 25,
          "shmSize": 536870912,
       },
    })
    console.log('세션 생성됨', response.data)
    sessionId = response.data
  
    //세션 열기 성공시, 자동으로 publisher로 연결
    await connectSession("PUBLISHER")

  }
  catch (error) {
    console.error('Error', error)
  }
}
// 세션 닫기
const closeSession = async () => {
  try {
    await axios.delete(`${API_SERVER_URL}openvidu/api/sessions/${sessionId}`)
    console.log("세션 닫힘")
  }
  catch (error) {
    console.error("Error",error)

  }
}
// 세션 연결 (connection) - 방송 만든사람
const connectSession = async (role="PUBLISHER") => {
  try {
    const response = await axios.post(`${API_SERVER_URL}openvidu/api/sessions/${sessionId}/connection`, {
      "type": "WEBRTC",
      "data": "My Server Data",
      "record": true,
      "role": "PUBLISHER",
      "kurentoOptions": {
        "videoMaxRecvBandwidth": 1000,
        "videoMinRecvBandwidth": 300,
        "videoMaxSendBandwidth": 1000,
        "videoMinSendBandwidth": 300,
        "allowedFilters": ["GStreamerFilter", "ZBarFilter"]
      }
    })

    connectId = response.data.connectionId
    console.log("세션 connection", response.data)
    const token = response.data.connectionToken
    //여기서 client 단 세션 join (with token)
    session.connect(token)
      .then(() => {
        console.log("클라이언트측 세션 연결 성공")
        var publisher = OV.initPublisher('my-video', {
          videoSource: "screen" //카메라 X, 화면 공유 설정
        })
        mainstreamer = publisher
        session.publish(publisher).then(() => {
          console.log("화면 공유 스트림 발생 성공")
          //여기서 전역변수에 저장 
        }).catch((error) => {
          console.error("화면 공유 스트림 발행 실패",error)
        })
        
      })
      .catch(error => {
        console.error("클라이언트측 세션 연결 실패",error)
      })
  

  }
    catch (error) {
      console.error("Error",error)
    }
}
</script>

<template>
  <div>
    <h1>page 1 : 방송하기(대쉬보드 이동) 버튼, 썸네일</h1>
    <button @click="createSession"> 방송하기 </button>
    
  </div>
</template>

<style scoped>
button{
  font-size: 30px;
  color: red
}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   
</style>
