package cn.mintimate.tool.sendMessageToWechat.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

public class sendMessageToWechat {
    // 微信接口获取
    final static String GET_ACCESS_TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";
    // 个人企业微信接口参数
    final static String SEND_MESSAGE_URL = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=";
    // 企业微信公司ID
    final static String WECOM_CID = "你的企业微信ID";
    // 企业微信公司密钥
    final static String WECOM_SECRET = "你的企业微信密钥";
    // 应用ID
    final static String AGENT_ID = "你的应用ID";
    // 发送用户，@all代表企业微信里所有人
    final static String TOUSER = "@all";


    // 发送失败
    final static int ERROR_CODE = 0;
    // 发送成功
    final static int SUCCESS_CODE = 0;
    // 发送状态
    static int STATUS_CODE = ERROR_CODE;


    private static String HttpRestClient(String url, HttpMethod method, JSONObject json) throws IOException {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10 * 1000);
        requestFactory.setReadTimeout(10 * 1000);
        RestTemplate client = new RestTemplate(requestFactory);
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json;charset=UTF-8");
        headers.setContentType(type);
        HttpEntity<String> requestEntity = new HttpEntity(json.toString(), headers);
        //  执行HTTP请求
        ResponseEntity<String> response = client.exchange(url, method, requestEntity, String.class);
        return response.getBody();
    }

    private static String getAccessToken() {
        HttpMethod method = HttpMethod.POST;
        JSONObject json = new JSONObject();
        json.put("corpid", WECOM_CID);
        json.put("corpsecret", WECOM_SECRET);
        String result = null;
        try {
            result = sendMessageToWechat.HttpRestClient(GET_ACCESS_TOKEN_URL, method, json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject obj = JSON.parseObject(result);
        //将Json字符串转化为Json对象
        String access_token = (String) obj.get("access_token");
        //从Json对象中提取键值为“status”的键值对，并将键值保存在“status”字符串中
        return access_token;
    }

    private static int sendMessage(String sendText, String access_token) {
        String url = SEND_MESSAGE_URL + access_token;
        HttpMethod method = HttpMethod.POST;
        JSONObject json = new JSONObject();
        JSONObject jsonText = new JSONObject();
        jsonText.put("content", sendText);
        json.put("touser", TOUSER);
        json.put("agentid", AGENT_ID);
        json.put("msgtype", "text");
        json.put("text", jsonText);
        //发送http请求并返回结果
        try {
            String result = sendMessageToWechat.HttpRestClient(url, method, json);
            JSONObject obj = JSON.parseObject(result);
            //将Json字符串转化为Json对象
            if ((int) obj.get("errcode") == 200) {
                //从Json对象中提取键值为“status”的键值对，并将键值保存在“status”字符串中
                STATUS_CODE = SUCCESS_CODE;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return STATUS_CODE;
    }

    public static int sendText(String Text) {
        if (Text == null) {
            return ERROR_CODE;
        }
        String access_token = getAccessToken();
        if (access_token == null || access_token.equals("")) {
            return ERROR_CODE;
        } else {
            sendMessage(Text, access_token);
        }
        return STATUS_CODE;
    }

    public static void main(String args[]) {
        sendMessageToWechat.sendText("Mintimate's Blog\n" +
                "只为与你分享~😄😄\n" +
                "=> Bilibili：https://space.bilibili.com/355567627\n"+
                "=> 腾讯云社区：https://cloud.tencent.com/developer/user/7704194\n"+
                "=====\n"+
                "<a href='https://www.mintimate.cn/about'>查看更多</a>");
    }
}