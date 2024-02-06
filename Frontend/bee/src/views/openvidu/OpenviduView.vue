<script setup>
import axios from "axios";
import { OpenVidu } from "openvidu-browser";
var session;
var OV = new OpenVidu();

const API_SERVER_URL = import.meta.env.VITE_API_SERVER_URL;
let sessionId = "";
let connectId = "";

// 세션 연결 - 방송 참여자
// ssesionID = opensession 하면 전역변수로 생김
const subscribeStream = async (role = "SUBSCRIBER") => {
  let subscriber;
  if (!session) {
    session = OV.initSession();
  }

  try {
    const response = await axios.post(
      `${API_SERVER_URL}openvidu/api/sessions/CUSTOM_SESSION_ID2/connection`,
      {
        type: "WEBRTC",
        data: "My Server Data",
        record: true,
        role: "SUBSCRIBER",
        kurentoOptions: {
          videoMaxRecvBandwidth: 1000,
          videoMinRecvBandwidth: 300,
          videoMaxSendBandwidth: 1000,
          videoMinSendBandwidth: 300,
          allowedFilters: ["GStreamerFilter", "ZBarFilter"],
        },
      }
    );

    console.log(response.data);
    // 커넥트 아이디, 토큰 - 내 연결에서 받아와야 함. sessionID = 퍼블리셔가 열어놓은 sessionID 글로벌 스코프로 선언되어 있음.
    connectId = response.data.connectionId;
    const token = response.data.connectionToken;

    console.log(connectId);
    console.log(token);

    // 새로운 스트림이 생성될 때 실행되는 이벤트 핸들러
    session.on("streamCreated", (event) => {
      // 새로운 스트림을 subscriber-video 엘리먼트에 구독
      const subscriber = session.subscribe(event.stream, "subscriber-video");
      console.log("새로운 스트림 구독 시작");
    });

    session
      .connect(token)
      .then(() => {
        console.log("1111111111111111111111111111111111111111111");
        console.log(response.data);
        console.log(connectId);
        console.log("11111111111111111111111111111111111111111", sessionId);
        console.log("세션 연결 성공, subscriber 연결 성공");
      })
      .catch((error) => {
        console.error("subscriber 연결 실패", error);
      });
  } catch (error) {
    console.error("세션 연결 실패", error);
  }
};
</script>

<template>
  <div class="outer">
    <div id="my-video">
      <h1>스트리머 측 화면</h1>
    </div>
    <div id="subscriber-video">
      <h1>방송 참여자 측 화면</h1>
    </div>
    <div class="buttons">
      <!-- 시청자로 커넥션 연습 -->
      <!-- <div class="subscribe"></div> -->
      <!-- <button @click="connectSession('SUBSCRIBER')">
        구독자로 세션 연결하기
      </button> -->
      <button @click="subscribeStream">구독자로 세션 연결</button>
    </div>
  </div>
</template>

<style scoped>
button {
  height: 100px;
  color: black;
  margin: 20px;
  font-size: 20px;
}
.outer {
  display: flex;
  flex-direction: column;
}

.buttons {
  display: flex;
  flex-direction: row;
}
</style>
