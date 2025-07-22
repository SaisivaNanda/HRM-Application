package com.hrms.project.controller;

import com.hrms.project.entity.*;
import com.hrms.project.payload.*;
import com.hrms.project.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class IdentityDetailsController {

    @Autowired
    private AadhaarServiceImpl aadhaarServiceImpl;

    @Autowired
    private PanServiceImpl panServiceImpl;

    @Autowired
    private DrivingLicenseServiceImpl drivingLicenseServiceImpl;

    @Autowired
    private PassportDetailsServiceImpl passportDetailsImpl;


    @Autowired
    private VoterIdServiceImpl  voterIdServiceImpl;
@PostMapping("/aadhaar/{employeeId}")
    public ResponseEntity<AadhaarCardDetails> save(@PathVariable String employeeId, @RequestPart(value = "aadhaarImage", required = false) MultipartFile aadhaarImage,
                                                   @RequestPart AadhaarCardDetails aadhaarCardDetails) throws IOException {

    return new ResponseEntity<>(aadhaarServiceImpl.createAadhaar(employeeId,aadhaarImage,aadhaarCardDetails), HttpStatus.CREATED);
}

    @GetMapping("/{employeeId}/aadhaar")
    public ResponseEntity<AadhaarDTO>getAadhaar(@PathVariable String employeeId){
        AadhaarDTO aadhaarDTO=aadhaarServiceImpl.getAadhaarByEmployeeId(employeeId);
        return new ResponseEntity<>(aadhaarDTO, HttpStatus.OK);

    }
    @PutMapping("/{employeeId}/aadhaar")
    public ResponseEntity<AadhaarCardDetails>updateAadhaar(@PathVariable String employeeId,
                                                           @RequestPart(value="aadhaarImage", required = false) MultipartFile aadhaarImage,
                                                           @RequestPart AadhaarCardDetails aadhaarDTO) throws IOException{
        AadhaarCardDetails dto = aadhaarServiceImpl.updateAadhaar(employeeId,aadhaarImage,aadhaarDTO);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }



@PostMapping("/pan/{employeeId}")
public ResponseEntity<PanDTO> savePan(@PathVariable String employeeId,
                                      @RequestPart(value="panImage", required = false) MultipartFile panImage,
                                      @RequestPart PanDetails panDetails) throws IOException {
    return new ResponseEntity<>(panServiceImpl.createPan(employeeId,panImage,panDetails),HttpStatus.CREATED);
}

@GetMapping("/{employeeId}/pan")
public ResponseEntity<PanDTO> getPanDetails(@PathVariable String employeeId){

    return new ResponseEntity<>(panServiceImpl.getPanDetails(employeeId),HttpStatus.OK);

}

@PutMapping("/{employeeId}/pan")
public ResponseEntity<PanDetails> updatePanDetails(@PathVariable String employeeId,
                                               @RequestPart(value="panImage", required = false) MultipartFile panImage,
                                               @RequestPart PanDetails panDetails) throws IOException {
    return new ResponseEntity<>(panServiceImpl.UpdatePanDetails(employeeId,panImage,panDetails),HttpStatus.CREATED);
}




@PostMapping("/driving/license/{employeeId}")
    public ResponseEntity<DrivingLicense> saveLicense(@PathVariable String employeeId,
                                                         @RequestPart(value="licenseImage", required = false) MultipartFile licenseImage,
                                                  @RequestPart DrivingLicense drivingLicense) throws IOException {
    return new ResponseEntity<>(drivingLicenseServiceImpl.createDrivingLicense(employeeId,licenseImage,drivingLicense),HttpStatus.CREATED);

}
@PutMapping("/{employeeId}/driving")
public ResponseEntity<DrivingLicense> updateDrivingLicense(@PathVariable String employeeId,
                                                              @RequestPart(value="licenseImage", required = false) MultipartFile licenseImage,
                                                             @RequestPart DrivingLicense drivingLicense){
    return new ResponseEntity<>(drivingLicenseServiceImpl.updatedrivingDetails(employeeId,licenseImage,drivingLicense),HttpStatus.CREATED);
}

@GetMapping("/{employeeId}/driving")
public ResponseEntity<DrivingLicenseDTO> getDrivingLicense(@PathVariable String employeeId){

    return new ResponseEntity<>(drivingLicenseServiceImpl.getDrivingDetails(employeeId),HttpStatus.OK);
}




@PostMapping("/passport/details/{employeeId}")
public ResponseEntity<PassportDetails> savePassportDetails(@PathVariable String employeeId,
                                                              @RequestPart(value="passportImage", required = false) MultipartFile passportImage,
                                                              @RequestPart PassportDetails passportDetails) throws IOException {
    return new ResponseEntity<>(passportDetailsImpl.createPassport(employeeId,passportImage,passportDetails),HttpStatus.CREATED);
}

@GetMapping("/{employeeId}/passport")
    public ResponseEntity<PassportDetailsDTO> getPassportDetails(@PathVariable String employeeId){
    return  new ResponseEntity<>(passportDetailsImpl.getPassportDetails(employeeId),HttpStatus.OK);
}
@PutMapping("/{employeeId}/passport")
    public ResponseEntity<PassportDetails> updatePassportDetails(@PathVariable String employeeId,
                                                                    @RequestPart(value="passportImage", required = false) MultipartFile passportImage,
                                                                    @RequestPart PassportDetails passportDetails) throws IOException {
    return new ResponseEntity<>(passportDetailsImpl.updatePasswordDetails(employeeId,passportImage,passportDetails),HttpStatus.CREATED);
}

    @PostMapping("/{employeeId}/voter")
    public ResponseEntity<VoterDetails>addVoter(@PathVariable String employeeId,
                                            @RequestPart(value="voterImage",required=false) MultipartFile voterImage,
                                            @RequestPart VoterDetails voterDetails)throws IOException {
        VoterDetails voter=voterIdServiceImpl.createVoter(employeeId,voterImage,voterDetails);
        return new ResponseEntity<>(voter,HttpStatus.CREATED);
    }

    @GetMapping("/{employeeId}/voter")
    public ResponseEntity<VoterDTO>getVoter(@PathVariable String employeeId){
        VoterDTO voterDto=voterIdServiceImpl.getVoterByEmployee(employeeId);
        return new ResponseEntity<>(voterDto,HttpStatus.OK);
    }

    @PutMapping("/{employeeId}/voter")
    public ResponseEntity<VoterDetails>updateVoter(@PathVariable String employeeId,
                                               @RequestPart(value="voterImage",required=false) MultipartFile voterImage,
                                               @RequestPart VoterDetails voterDetails) throws IOException {
        VoterDetails voter=voterIdServiceImpl.updateVoter(employeeId,voterImage,voterDetails);
        return new ResponseEntity<>(voter,HttpStatus.OK);
    }
}


