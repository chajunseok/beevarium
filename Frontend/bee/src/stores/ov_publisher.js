import { ref } from "vue";
import { defineStore } from "pinia";
import axios from "axios";
import { OpenVidu } from "openvidu-browser";

export const useOVPStore = defineStore(
  "OVPStore",
  () => {
    var session;
    var OV = new OpenVidu();
    var mainstreamer;

    axios.defaults.headers.post["Content-Type"] = "application/json";

    const API_SERVER_URL = import.meta.env.VITE_OPENVIDU_API_URL;
    let sessionId = "";
    let connectId = "";

    const openSession = async () => {
      try {
        // 성공적으로 통신시 클라이언트측 세션 초기화
        session = OV.initSession();
        const response = await axios.post(
          `${API_SERVER_URL}openvidu/api/sessions`,
          {
            mediaMode: "ROUTED",
            recordingMode: "MANUAL",
            customSessionId: "CUSTOM_SESSION_ID2",
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
            },
          }
        );
        console.log("세션 생성됨", response.data);
        sessionId = response.data;
        // 세션 열기 성공시, 자동으로 publisher로 연결
        await connectSession("PUBLISHER");
      } catch (error) {
        console.error("Error!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11", error);
      }
    };

    const connectSession = async (role = "PUBLISHER") => {
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

        // 클라이언트 단 세션 join (with token)
        session
          .connect(token)
          .then(() => {
            console.log("클라이언트측 세션 연결 성공");

            // 퍼블리셔의 카메라 및 화면 공유 설정
            var publisher = OV.initPublisher("my-video", {
              videoSource: "screen",
              resolution: "1280x720",
              // 카메라와 화면 공유 설정
            });
            mainstreamer = publisher;

            session
              .publish(publisher)
              .then(() => {
                console.log("화면 및 카메라 공유 스트림 발생 성공", sessionId);
              })
              .catch((error) => {
                console.error("화면 및 카메라 공유 스트림 발행 실패", error);
              });
          })
          .catch((error) => {
            console.error("클라이언트측 세션 연결 실패", error);
          });
      } catch (error) {
        console.error("Error", error);
      }
    };

    // 세션 닫기
    const closeSession = async () => {
      try {
        await axios.delete(
          `${API_SERVER_URL}openvidu/api/sessions/${sessionId}`
        );
        console.log("세션 닫힘");
        //클라이언트측 세션 닫기 -> 필요없나??
      } catch (error) {
        console.error("Error", error);
      }
    };

    return { openSession, connectSession, closeSession, sessionId, connectId };
  },
  {
    persist: true,
  }
);