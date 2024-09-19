package com.shubham.UserServer.controller;

import com.shubham.UserServer.model.Message;
import com.shubham.UserServer.requestEntity.MessageRequest;
import com.shubham.UserServer.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    // facility endpoints
    @GetMapping("/getALlFacility")
    public ResponseEntity<List<Object>> getAllFacilities(){
        return userService.getAllFacilities();
    }

    @GetMapping("/availFacility")
    public ResponseEntity<Object> availFacility(@RequestParam("name") String name) throws Exception {
        return userService.availFacility(name);
    }

//    @GetMapping("/getGrantedFacilities")
//    public ResponseEntity<List<Object>> getAllUsingFacilities(){
//        // get all using facilities of login user
//    }


    // room endpoints
    @GetMapping("/getAvailableRooms")
    public ResponseEntity<List<Object>> getAvailableRooms(){
        return userService.getAvailableRooms();
    }


    // user endpoints
    @GetMapping("/applyToLeave")
    public ResponseEntity<Object> applyToLeave() throws Exception {
        return userService.applyToLeave();
    }


    // message endpoints
    @PostMapping("/messageToAdmin")
    public ResponseEntity<Object> messageToAdmin(@Valid @RequestBody MessageRequest messageRequest){
        return userService.messageToAdmin(messageRequest);
    }

    @GetMapping("/getMessage")
    public ResponseEntity<List<Object>> getMessage(@RequestParam("page") Integer page, @RequestParam("size") Integer size){
        return userService.getMessage(page, size);
    }


    // Txn endpoints
//    @GetMapping("/getTxn")
//    public ResponseEntity<List<Object>> getTxnByFilter(
//            @RequestParam("filterType") String filterType,
//            @RequestParam("operator") String operator,
//            @RequestParam("value") String value
//    ){
//              // for current user
//    }

//    @PostMapping("/paymentOnlineRequest")
//    public ResponseEntity<Object> payRentOnlineRequest(){
//        // should have body
//        // return payment request
//    }

    // post mapping or get mapping depend on payment gateway callback api method
//    public ResponseEntity<Object> paymentCallback(){
//        // should have body or request param
//        // it should be handle callback
//        // 2way communication, use websocket
//    }
}