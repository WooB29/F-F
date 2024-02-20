package com.flower.service;

import com.flower.dto.TodaySearchDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import java.net.URLEncoder;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ApiService {
    // mail
    private final JavaMailSender javaMailSender;

    @Value("${KAKAO_MAP_URL}")
    private String KAKAO_MAP_URL;
    @Value("${KAKAO_MAP_KEY}")
    private String KAKAO_MAP_KEY;

    @Value("${spring.mail.username}")
    private String senderEmail;
    @Value("${ACCOUNT_SID}")
    private String ACCOUNT_SID;
    @Value("${AUTH_TOKEN}")
    private String AUTH_TOKEN;

    // plant.id
    @Value("${PLANT_ID_API_URL}")
    private String PLANT_ID_API_URL;
    @Value("${plantApiKey}")
    private String plantApiKey;

    // today_flower
    @Value("${TODAY_FLOWER_API_URL}")
    private String TODAY_FLOWER_API_URL;
    @Value("${todayFlowerApiKey}")
    private String todayFlowerApiKey;
    @Value("${TODAY_FLOWER_LIST_API_URL}")
    private String TODAY_FLOWER_LIST_API_URL;

    private final RestTemplate restTemplate = new RestTemplate();

    private int createNumber(){
        int number;
        return number = (int)(Math.random() * (90000)) + 100000;
    }

    private MimeMessage createMail(String mail, int number) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, mail);
        message.setSubject("이메일 인증");
        String body = "";
        body += "<h3>"+"요청하신 인증 번호입니다."+"</h3>";
        body += "<h1>"+number+"</h1>";
        message.setText(body, "UTF-8", "html");

        return message;
    }

    public int sendMail(String mail) throws MessagingException {
        int number = createNumber();
        MimeMessage message = createMail(mail, number);
        javaMailSender.send(message);
        return number;
    }

    public String getKAKAO_MAP(){
        StringBuilder sb;
        try {
            StringBuilder urlBuilder = new StringBuilder(KAKAO_MAP_URL);
            urlBuilder.append("?"+URLEncoder.encode("appkey","UTF-8")+"="+KAKAO_MAP_KEY);
            urlBuilder.append("&"+URLEncoder.encode("libraries","UTF-8")+"="+"services");
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            System.out.println("Response code: " + conn.getResponseCode());
            BufferedReader rd;
            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    public String getTodayFlowerInfo(Optional<Integer> fMonth, Optional<Integer> fDay) {
        StringBuilder sb;
        try{
            StringBuilder urlBuilder = new StringBuilder(TODAY_FLOWER_API_URL);
            urlBuilder.append("?"+URLEncoder.encode("serviceKey","UTF-8")+"="+todayFlowerApiKey);
            if (fMonth.isPresent() && fDay.isPresent()){
                urlBuilder.append("&" + URLEncoder.encode("fMonth","UTF-8") + "=" +fMonth.get());
                urlBuilder.append("&" + URLEncoder.encode("fDay","UTF-8") + "=" + fDay.get());
            }
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            System.out.println("Response code: " + conn.getResponseCode());
            BufferedReader rd;
            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();
        }
         catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    public String getTodayFlowerList(TodaySearchDto todaySearchDto) {
        StringBuilder sb;
        try{
            StringBuilder urlBuilder = new StringBuilder(TODAY_FLOWER_LIST_API_URL);
            urlBuilder.append("?"+URLEncoder.encode("serviceKey","UTF-8")+"="+todayFlowerApiKey);
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + todaySearchDto.getPageNo());
            urlBuilder.append("&" + URLEncoder.encode("fMonth","UTF-8") + "=" + todaySearchDto.getMonth());
            urlBuilder.append("&" + URLEncoder.encode("fDay","UTF-8") + "=" + todaySearchDto.getDay());
            urlBuilder.append("&" + URLEncoder.encode("searchType","UTF-8") + "=" + todaySearchDto.getSearchType());
            urlBuilder.append("&" + URLEncoder.encode("searchWord","UTF-8") + "=" + URLEncoder.encode(todaySearchDto.getSearchWord(), "UTF-8"));
            System.out.println(urlBuilder);
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            System.out.println("Response code: " + conn.getResponseCode());
            BufferedReader rd;
            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }


    public String identifyPlant(MultipartFile imageFile) {
        if (!imageFile.isEmpty()){
            try {
                byte[] bytes = imageFile.getBytes();
                HttpClient httpclient = HttpClients.createDefault();
                HttpPost httpPost = new HttpPost(PLANT_ID_API_URL);
                httpPost.setHeader("Api-Key", plantApiKey);

                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.addBinaryBody("image1", bytes, ContentType.DEFAULT_BINARY, imageFile.getOriginalFilename());

                builder.addTextBody("latitude", "49.207");
                builder.addTextBody("longitude", "16.608");
                builder.addTextBody("similar_images", "true");

                HttpEntity multipart = builder.build();
                httpPost.setEntity(multipart);

                HttpResponse response = httpclient.execute(httpPost);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200 || statusCode == 201) {
                    HttpEntity responseEntity = response.getEntity();
                    String jsonResponse = EntityUtils.toString(responseEntity);
                    return jsonResponse;
                } else {
                    System.err.println("API 요청이 실패했습니다. 상태 코드: " + statusCode);
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        else{
            return null;
        }
    }
}
