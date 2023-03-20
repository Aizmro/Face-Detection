package com.pecokie.controller;

import com.pecokie.application.AlgorithmAppService;
import com.pecokie.param.AlgorithmView;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class AlgorithmResource {
    private final AlgorithmAppService algorithmAppService;

    public AlgorithmResource(AlgorithmAppService algorithmAppService) {
        this.algorithmAppService = algorithmAppService;
    }


    @PostMapping("/resource/process")
    public AlgorithmView process(@RequestParam("image") MultipartFile image, String roi) throws IOException {
        return algorithmAppService.process(image.getBytes(), roi);
    }
}