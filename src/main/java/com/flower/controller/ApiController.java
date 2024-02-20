package com.flower.controller;

import com.flower.dto.TodaySearchDto;
import com.flower.service.ApiService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;


@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {
    private final ApiService apiService;

    @PostMapping(value = "/mail")
    public @ResponseBody String mailSend(@RequestBody Map<String, Object> map) throws MessagingException {
        int number =0;
        try {
            number = apiService.sendMail((String) map.get("mail"));
        }
        catch (MailSendException e){
            return null;
        }
        String num = ""+number;
        return num;
    }

    @GetMapping(value = "/map")
    public String map(){
        return "info/map";
    }

    @GetMapping({"/today-flower","/today-flower/{fMonth}/{fDay}"})
    public String getTodayFlowerInfo(Model model, @PathVariable("fMonth") Optional<Integer> fMonth,
                                     @PathVariable("fDay") Optional<Integer> fDay) {
        String flowerInfo = apiService.getTodayFlowerInfo(fMonth,fDay);
        model.addAttribute("flowerInfo", flowerInfo);
        return "info/today";
    }

    @GetMapping("/todaySearch")
    public String todaySearch(){
        return "info/todaySearch";
    }

    @PostMapping("/todaySearch")
    @ResponseBody
    public String todaySearch(@RequestBody TodaySearchDto todaySearchDto){
        return apiService.getTodayFlowerList(todaySearchDto);
    }

    @GetMapping("/identify-flower")
    public String kind(){
        return "info/kind";
    }

    @PostMapping("/identify-flower")
    public ResponseEntity<String> identifyFlower(@RequestParam("imageFile") MultipartFile imageFile) {
        String result = apiService.identifyPlant(imageFile);
        if (result != null) {
            System.out.println("=="+result.toString());
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to identify flower.");
        }
    }
}
