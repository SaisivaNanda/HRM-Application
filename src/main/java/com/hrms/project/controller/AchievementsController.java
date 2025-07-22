package com.hrms.project.controller;

import com.hrms.project.entity.Achievements;
import com.hrms.project.service.AchievementsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class AchievementsController {

    @Autowired
    private AchievementsServiceImpl achievementsServiceImpl;

    @PostMapping("/achievements/{employeeId}")
    public ResponseEntity<Achievements> addAchievements(@PathVariable String employeeId,
                                                            @RequestPart(value="image",required = false) MultipartFile image,
                                                        @RequestPart Achievements achievements) throws IOException {

        return new ResponseEntity<Achievements>(achievementsServiceImpl.addAchievements(employeeId,image,achievements), HttpStatus.CREATED);



    }


}
