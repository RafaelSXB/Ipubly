package com.project.Ipubly.Controller;


import org.springframework.web.bind.annotation.RestController;


import org.springframework.web.bind.annotation.GetMapping;



@RestController
public class mainController {

   

    @GetMapping("/")
    public String getMain() {
      

        return "oi";
    }
    
    
    }


