<script setup>
import axios from "axios";
import { onMounted, ref } from "vue";
import { OpenVidu } from "openvidu-browser";
import { useRouter } from "vue-router";

const API_SERVER_URL = import.meta.env.VITE_OPENVIDU_API_URL;
var OV = new OpenVidu();
var session = ref(null);
var sessionId = "";
let connectId = "";
let publisher = ref(null);
// 방송 상태를 관리하는 변수
let onAir = ref(false);

onMounted(async () => {
  await createSession();
});

// 세션 만들기
const createSession = async () => {
  try {
    // 성공적으로 통신시 클라이언트측 세션 초기화
    session.value = OV.initSession();
    console.log(session.value);
    //서버 만들기 post 요청
    const sessionResponse = await axios.post(
      `${API_SERVER_URL}openvidu/api/sessions`,
      {
        mediaMode: "ROUTED",
        recordingMode: "MANUAL",
        customSessionId: "CUSTOM_SESSION_ID1",
        forcedVideoCodec: "VP8",
        allowTranscoding: false,
        defaultRecordingProperties: {
          name: "MyRecording",
          hasAudio: true,
          hasVideo: true,
          outputMode: "COMPOSED",
          recordingLayout: "BEST_FIT",
          resolution: "1280x720",
          frameRate: 25,
          shmSize: 536870912,
          mediaNode: "media_openvidu.beevarium.site",
        },
      }
    );
    console.log("세션 생성됨", sessionResponse.data);
    sessionId = sessionResponse.data;
    //세션 열기 성공시, 자동으로 publisher로 연결
    await connectSession("PUBLISHER");
  } catch (error) {
    console.error("세션 생성 실패", error);
  }
};

// 방송 만든사람 세션 연결 (connection)
// createSssion 내에서 자동으로 호출됨
const connectSession = async () => {
  try {
    const response = await axios.post(
      `${API_SERVER_URL}openvidu/api/sessions/${sessionId}/connection`,
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
    //client 단 세션 join (with token)
    session.value
      .connect(token)
      .then(() => {
        console.log("클라이언트측 세션 연결 성공");
        // 스트리밍 전 미리보기만 띄우기
        publisher.value = OV.initPublisher("streamerscreen", {
          videoSource: "screen",
        });
      })
      .catch((error) => {
        console.error("클라이언트측 세션 연결 실패", error);
      });
  } catch (error) {
    console.error("Error", error);
  }
};

//방송 시작
const publishStream = async () => {
  try {
    session.value.publish(publisher.value);
    console.log("방송 시작");
    onAir.value = true;
    console.log(onAir.value);
  } catch (error) {
    console.error("방송 시작 실패", error);
  }
};

// 녹화 시작
const startRecording = async () => {
  try {
    const response = await axios.post(
      `${API_SERVER_URL}openvidu/api/recordings/start`,
      {
        // "id": "ses_YnDaGYNcd7",
        object: "recording",
        name: "MyRecording",
        outputMode: "COMPOSED",
        hasAudio: true,
        hasVideo: true,
        resolution: "1280x720",
        frameRate: 25,
        sessionId: "CUSTOM_SESSION_ID",
        mediaNode: "media_media.beevarium.site",
        size: 303072692,
        duration: 108000.234,
        url: `${API_SERVER_URL}openvidu/recordings/CUSTOM_SESSION_ID/MyRecording.mp4`,
        status: "ready",
      }
    );
  } catch (error) {
    console.log(error);
  }
};
</script>

<template>
  <div>
    <h1>Logic2</h1>
    <h2>방송 대쉬보드 화면</h2>
    <h1>{{ onAir ? "방송 중입니다" : "방송 중이 아닙니다." }}</h1>
    <div id="streamerscreen"></div>
    <button @click="publishStream">방송 시작</button>
    <button @click="startRecording">녹화 시작</button>
  </div>
</template>

<style scoped>
button {
  font-size: 30px;
  color: red;
  margin: 30px;
}
</style>
