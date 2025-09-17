package com.project.Ipubly.Controller;


import com.project.Ipubly.Model.DTO.PromptRequestDTO;
import com.project.Ipubly.Model.DTO.PromptResponseDTO;
import com.project.Ipubly.Services.GenareteAITextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/generateAI")
public class GenerateAITextController {

    @Autowired
    private GenareteAITextService genareteAITextService;

    @PostMapping("/text")
    public PromptResponseDTO getText(@RequestBody PromptRequestDTO dto) {
        String result = genareteAITextService.generateText(dto);
        PromptResponseDTO response = new PromptResponseDTO();
        response.setStatus("success");
        response.setMessage("Text generated success");
        response.setResult(result);
        return response;
    }

    @PostMapping("/image")
    public PromptResponseDTO getImage(@RequestBody PromptRequestDTO dto) {
        String result = genareteAITextService.generateImage(dto);
        PromptResponseDTO response = new PromptResponseDTO();
        response.setStatus("success");
        response.setMessage("Imaged generated success");
        response.setResult(result);
        return response;
    }
}


