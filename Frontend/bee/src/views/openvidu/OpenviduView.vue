<script setup>
import axios from "axios";
import { OpenVidu } from "openvidu-browser";
var session; //전역 스코프에서 선언
var OV = new OpenVidu();
var mainstreamer;

const API_SERVER_URL = import.meta.env.VITE_API_SERVER_URL;
let sessionId = "";
let connectId = "";

// 세션 연결 (connection) - 방송 만든사람
const connectSession = async (role = "PUBLISHER") => {
  try {
    const response = await axios.post(
      `${API_SERVER_URL}openvidu/api/sessions/CUSTOM_SESSION_ID/connection`,
      {
        type: "WEBRTC",
        data: "My Server Data",
        record: true,
        role: "PUBLISHER",
        kurentoOptions: {
          videoMaxRecvBandwidth: 1000,
          videoMinRecvBandwidth: 300,
          videoMaxSendBandwidth: 1000,
          videoMinSendBandwidth: 300,
          allowedFilters: ["GStreamerFilter", "ZBarFilter"],
        },
      }
    );

    connectId = response.data.connectionId;
    console.log("세션 connection", response.data);
    const token = response.data.connectionToken;
    //여기서 client 단 세션 join (with token)
    session
      .connect(token)
      .then(() => {
        console.log("클라이언트측 세션 연결 성공");
        var publisher = OV.initPublisher("my-video", {
          videoSource: "screen", //카메라 X, 화면 공유 설정
        });
        mainstreamer = publisher;
        session
          .publish(publisher)
          .then(() => {
            console.log("화면 공유 스트림 발생 성공");
            //여기서 전역변수에 저장
          })
          .catch((error) => {
            console.error("화면 공유 스트림 발행 실패", error);
          });
      })
      .catch((error) => {
        console.error("클라이언트측 세션 연결 실패", error);
      });
  } catch (error) {
    console.error("Error", error);
  }
};
// 세션 연결 - 방송 참여자
// ssesionID = opensession 하면 전역변수로 생김
const subscribeStream = async (role = "SUBSCRIBER") => {
  let subscriber;
  try {
    const response = await axios.post(
      `${API_SERVER_URL}openvidu/api/sessions/CUSTOM_SESSION_ID1/connection`,
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
    console.log(connectId);
    //커넥트 아이디, 토큰 - 내 연결에서 받아와야 함. sessionID = 퍼블리셔갸 열어놓은 sessionID 글로벌 스코프로 선언되어 있음.
    connectId = response.data.connectionId;
    // console.log("세션 connection", response.data)
    const token = response.data.connectionToken;
    session.on("streamCreated", (event) => {
      subscriber = session.subscribe(event.stream, "subscriber-video");
    });
    session
      .connect(token)
      .then(() => {
        console.log(response.data);
        console.log(connectId);
        console.log("새션 연결 성공, subscriber 연결 성공");
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
