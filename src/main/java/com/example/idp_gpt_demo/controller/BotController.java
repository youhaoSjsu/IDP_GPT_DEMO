package com.example.idp_gpt_demo.controller;

import com.example.idp_gpt_demo.dto.ChatGptRequest;
import com.example.idp_gpt_demo.dto.ChatGptRespons;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/bot")
public class BotController {
    @Value("${openai.model}")
    private String model;
    @Value(("${openai.api.url}"))
    private String apiUrl;
    @Value("${openai.image.url}")
    private String imageUrl;
    @Value("${openai.api.key}")
    private String openaiApiKey;

    @GetMapping("/main")
    public ModelAndView main()
    {




        //image("a yellow cat",1);

        ModelAndView mv = new ModelAndView("main.html");


        //this template can only take array

        return mv;

    }
//    @GetMapping(value = "/images", produces = MediaType.APPLICATION_JSON_VALUE)
//    public List<String> getImages() {
//        List<String> images = new ArrayList<>();
//
//        // Add base64 encoded images to the list
//        images.add("data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEASABIAAD...");
//        images.add("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAMg...");
//        // Add more images as needed
//
//        return images;
//    }
    @Autowired
    private RestTemplate template;
    @GetMapping("/chat")
    public String chat(@RequestParam("prompt")String prompt)
    {
        ChatGptRequest request = new ChatGptRequest(model,prompt);
        ChatGptRespons respons = template.postForObject(apiUrl,request,ChatGptRespons.class);
        //we need the string respons,
        //get choices is a list of choices, and get the first one, then take the content;
        return  respons.getChoices().get(0).getMessage().getContent();
    }
//=========image generator:=================================

    @GetMapping(value = "/images", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> image(@RequestParam("prompt")String prompt, @RequestParam("n")int n)
    {
        if(n ==0)
        {
            n =1;
        }
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + openaiApiKey);
        // We are including only some of the parameters to the json request
        String requestJson = "{\"prompt\":\"" + prompt + "\",\"n\":" + n + "}";
        HttpEntity< String > request = new HttpEntity < > (requestJson, headers);
        ResponseEntity< String > response = restTemplate.postForEntity(imageUrl, request, String.class);
        List<String> result = new ArrayList<>();
        result = extractAndConvertImages(response.getBody());
        if(result.size() == n)
        {
            System.out.println(result.get(0));
            return result;
        }
        return  result;
    }



    public List<String> extractAndConvertImages(String responseBody) {
        List<String> base64Images = new ArrayList<>();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode dataNode = root.get("data");

            if (dataNode != null && dataNode.isArray()) {
                for (JsonNode item : dataNode) {
                    JsonNode urlNode = item.get("url");

                    if (urlNode != null && urlNode.isTextual()) {
                        String imageUrl = urlNode.asText();

                        // Download the image from the URL
                        byte[] imageBytes = downloadImage(imageUrl);

                        // Convert the image bytes to base64
                        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

                        base64Images.add(base64Image);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle any potential IO exception
        }

        return base64Images;
    }

    private byte[] downloadImage(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        Path tempFilePath = Files.createTempFile("temp", ".img");

        // Download the image from the URL
        Files.copy(url.openStream(), tempFilePath, StandardCopyOption.REPLACE_EXISTING);

        // Read the downloaded image as bytes
        byte[] imageBytes = Files.readAllBytes(tempFilePath);

        // Delete the temporary file
        Files.deleteIfExists(tempFilePath);

        return imageBytes;
    }


}
