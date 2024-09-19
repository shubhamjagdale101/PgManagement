package com.shubham.UserServer.service;

import com.shubham.UserServer.configuration.SecurityConfig;
import com.shubham.UserServer.model.*;
import com.shubham.UserServer.repository.*;
import com.shubham.UserServer.requestEntity.AddFacility;
import com.shubham.UserServer.requestEntity.AddRoom;
import com.shubham.UserServer.requestEntity.MessageRequest;
import com.shubham.UserServer.requestEntity.SettleRentRequest;
import com.shubham.UserServer.responseEntity.FacilityResponse;
import com.shubham.UserServer.responseEntity.Response;
import com.shubham.UserServer.responseEntity.SubscriptionUser;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AdminService {
    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);
    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TxnRepository txnRepository;

    @Autowired
    private MonthlyRentRepository monthlyRentRepository;

    @Autowired
    private MessageRepository messageRepository;

    public ResponseEntity<Object> addFacility(AddFacility addFacility){
        Facilities facility = Facilities.builder()
                .name(addFacility.getName())
                .perMonthCharge(addFacility.getPerMonthCharge())
                .build();
        facility = facilityRepository.save(facility);

        FacilityResponse facilityResponse = modelMapper.map(facility, FacilityResponse.class);
        return new ResponseEntity<>(facilityResponse, HttpStatus.CREATED);
    }

    public ResponseEntity<Object> addRoomToList(AddRoom addRoom){
        Room room = Room.builder()
                .roomNo(addRoom.getRoomNo())
                .availableSeats(addRoom.getAvailableSeats())
                .extraChargePerMonth(addRoom.getExtraChargePerMonth())
                .build();
        room = roomRepository.save(room);

        Response res = Response.builder()
                .message("room added successfully")
                .build();
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<Object> settleRentTxn(@Valid @RequestBody SettleRentRequest settlerent) throws Exception {
        User user = userRepository.findByUserName(settlerent.getUsername());
        if(user == null) throw new Exception("user not found");

        MonthyRent monthyRent = monthlyRentRepository.findByMonthYear(settlerent.getMonthYear());
        if(monthyRent == null) throw new Exception("month not found");

        Txn txn = txnRepository.findTxn(user, monthyRent);
        if(txn == null || txn.getStatus() == TxnStatus.COMPLETED) throw new Exception("can not do rent settlement");

        txn.setStatus(TxnStatus.COMPLETED);
        txnRepository.save(txn);

        Response res = Response.builder()
                .message("rent settlement done")
                .build();
        return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
    }

    public ResponseEntity<List<Object>> getAllUsers(Integer page, Integer size){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "creationDate", "updationDate"));
        Page<User> userPage = userRepository.findAll(pageable);

        List<User> userList = userPage.getContent();
        List<Object> userListResponse = new ArrayList<>();

        for(User user : userList){
            SubscriptionUser subUser = SubscriptionUser.builder()
                    .panId(user.getPanId())
                    .phNo(user.getPhNo())
                    .userName(user.getUsername())
                    .lastName(user.getLastName())
                    .firstName(user.getFirstName())
                    .adharId(user.getAdharId())
                    .email(user.getEmail())
                    .status(user.getStatus())
                    .build();
            userListResponse.add(subUser);
        }

        return new ResponseEntity<>(userListResponse, HttpStatus.OK);
    }

    public ResponseEntity<Object> sendMessageToEveryOne(MessageRequest messageRequest) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = ((Jwt) auth.getPrincipal()).getSubject();

        User user = userRepository.findByUserName(username);
        if(user == null) throw new Exception("sender not found");

        Message message = Message.builder()
                .type(MessageType.ADMIN_TO_ALL_USERS)
                .sender(user)
                .message(messageRequest.getMessage())
                .build();
        messageRepository.save(message);

        Response res = Response.builder()
                .message("message sent to every one")
                .build();
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    public ResponseEntity<List<Object>> getMessage(Integer page, Integer size){
        Pageable pageable = PageRequest.of(page, size);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = ((Jwt) auth.getPrincipal()).getSubject();

        User user = userRepository.findByUserName(username);

        Page<Message> pageData = messageRepository.getMessage(MessageType.USER_TO_ADMIN, user, pageable);
        List<Message> messageList = pageData.getContent();
        List<Object> messageListRes = new ArrayList<>();

        for(Message msg : messageList){
            MessageRequest msgRes = MessageRequest.builder()
                    .Message(msg.getMessage())
                    .sender(msg.getSender().getUsername())
                    .type(msg.getType())
                    .build();
            messageListRes.add(msgRes);
        }
        return new ResponseEntity<>(messageListRes, HttpStatus.OK);
    }
}
