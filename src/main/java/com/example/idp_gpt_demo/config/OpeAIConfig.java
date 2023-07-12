package com.example.idp_gpt_demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OpeAIConfig {

    @Value("${openai.api.key}")
    private String openaiApiKey;
    @Bean
    public RestTemplate template(){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer "+openaiApiKey);
            return execution.execute(request,body);

        }));

        return restTemplate;
    }


//    public RestTemplate imageTemplate()
//    {
//
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("Authorization", "Bearer " + openaiApiKey);
//        // We are including only some of the parameters to the json request
//        String requestJson = "{\"prompt\":\"" + prompt + "\",\"n\":" + n + "}";
//        HttpEntity< String > request = new HttpEntity < > (requestJson, headers);
//        ResponseEntity< String > response = restTemplate.postForEntity(OPENAI_URL, request, String.class);
//        return response.getBody();
//
//
//
//    }
}
