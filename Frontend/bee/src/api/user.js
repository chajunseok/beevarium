import { localAxios } from "@/util/http-commons";

const local = localAxios();

// 로그인
const login = async (loginData, success, fail) => {
  await local.post(`/login`, loginData).then(success).catch(fail);
}

// 회원가입
const signUp = async (registerData, success, fail) => {
  await local.post(`/user/join`, registerData).then(success).catch(fail);
};
// 소셜 로그인
const socialLogin = async (coperation, code, success, fail) => {
  await local
    .get(`/oauth/callback/${coperation}?code=${code}`)
    .then(success)
    .catch(fail);
};

export { login, signUp, socialLogin };