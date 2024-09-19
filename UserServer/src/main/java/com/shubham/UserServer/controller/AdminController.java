package com.shubham.UserServer.controller;

import com.shubham.UserServer.model.Facilities;
import com.shubham.UserServer.model.Message;
import com.shubham.UserServer.repository.FacilityRepository;
import com.shubham.UserServer.requestEntity.AddFacility;
import com.shubham.UserServer.requestEntity.AddRoom;
import com.shubham.UserServer.requestEntity.MessageRequest;
import com.shubham.UserServer.requestEntity.SettleRentRequest;
import com.shubham.UserServer.service.AdminService;
import jakarta.validation.Valid;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @PostMapping("/addFacility")
    public ResponseEntity<Object> addFacility(@Valid @RequestBody AddFacility addFacility){
        return adminService.addFacility(addFacility);
    }

    @PostMapping("/addRoom")
    public ResponseEntity<Object> addRoomToList(@Valid @RequestBody AddRoom addRoom){
        return adminService.addRoomToList(addRoom);
    }

    @PostMapping("/settleRent")
    public ResponseEntity<Object> settleRentTxn(@Valid @RequestBody SettleRentRequest settlerent) throws Exception {
        return adminService.settleRentTxn(settlerent);
    }

    @GetMapping("/getUserList")
    public ResponseEntity<List<Object>> getUserList(@RequestParam("page") Integer page, @RequestParam("size") Integer size){
        return adminService.getAllUsers(page, size);
    }

    @PostMapping("/sendMessageToEveryOne")
    public ResponseEntity<Object> sendMessageToEveryOne(@RequestBody MessageRequest messageRequest) throws Exception {
        return adminService.sendMessageToEveryOne(messageRequest);
    }

    @GetMapping("/getMessage")
    public ResponseEntity<List<Object>> getMessage(@RequestParam("page") Integer page, @RequestParam("size") Integer size){
        return adminService.getMessage(page, size);
    }

//    @GetMapping("/getFacilityRequest")
//    public ResponseEntity<List<Object>> getFacilityRequest(){
//
//    }

//    @GetMapping("/grantFacilityRequest")
//    public ResponseEntity<String> grantFacilityRequest(){
//
//    }

//    @GetMapping("/getFacilityByFilter")
//    public ResponseEntity<List<Object>> getFacilityUsersByFilter(
//            @RequestParam("filterType") String filterType,
//            @RequestParam("operator") String operator,
//            @RequestParam("value") String value
//    ){
//
//    }

//    @GetMapping("/getSubscriptionRequest")
//    public ResponseEntity<List<Object>> getSubscriptionRequest(){
//
//    }

//     // username of the subscriber user
//    @GetMapping("/grantSubscriptionRequest")
//    public ResponseEntity<String> grantSubscriptionRequest(@RequestParam("username") String username){
//
//    }

//    @GetMapping("/getTxn")
//    public ResponseEntity<List<Object>> getTxnByFilter(
//            @RequestParam("filterType") String filterType,
//            @RequestParam("operator") String operator,
//            @RequestParam("value") String value
//    ){
//          // for all user
//    }

//    @GetMapping("/getRoomStatus")
//    public ResponseEntity<Object> getRoomStatus(@RequestParam("roomNo") Integer roomNo){
//        // if roomNo is null return info about all room
//        // else return info about particular room
//    }
}
