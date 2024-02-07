<script setup>
// 페이지 구성 : 방송하기 버튼, 썸네일
// 방송하기 버튼 클릭 -> 세션 열림 -> 여기서 나온 sessionID를 대쉬보드로 넘겨줘야 함 -> 방송자로 connect
import { OpenVidu } from 'openvidu-browser'
import { useRouter } from 'vue-router'
const OV = new OpenVidu() 
//openvidu api 요청 url 
const API_SERVER_URL = import.meta.env.VITE_API_SERVER_URL 
const router = useRouter()


const moveTologic2 = () => {
  router.push({ name: 'OpenviduLogic2'})
  
}
</script>

<template>
  <div>
    <h1>page 1 : 방송하기(대쉬보드 이동) 버튼, 썸네일</h1>
    <button @click="moveTologic2"> 대쉬보드 이동 </button>
    
  </div>
</template>

<style scoped>
button{
  font-size: 30px;
  color: red
}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   
</style>
