package com.ticketml.controller;

import com.ticketml.response.Response;
import com.ticketml.services.S3Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1")
public class S3Controller {

    private final S3Service s3Service;

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/upload")
    public Response upload(@RequestParam("file") MultipartFile file) {
        return new Response(s3Service.uploadFile(file));

    }
}

