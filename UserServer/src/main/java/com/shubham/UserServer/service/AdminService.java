package com.shubham.UserServer.service;

import com.shubham.UserServer.model.*;
import com.shubham.UserServer.repository.*;
import com.shubham.UserServer.requestEntity.AddFacility;
import com.shubham.UserServer.requestEntity.AddRoom;
import com.shubham.UserServer.requestEntity.MessageRequest;
import com.shubham.UserServer.requestEntity.SettleRentRequest;
import com.shubham.UserServer.responseEntity.FacilityResponse;
import com.shubham.UserServer.responseEntity.SubscriptionUser;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AdminService {
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

    public ResponseEntity<String> addRoomToList(AddRoom addRoom){
        Room room = Room.builder()
                .roomNo(addRoom.getRoomNo())
                .availableSeats(addRoom.getAvailableSeats())
                .extraChargePerMonth(addRoom.getExtraChargePerMonth())
                .location(addRoom.getLocation())
                .build();
        room = roomRepository.save(room);
        return new ResponseEntity<String>("room added successfully", HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<String> settleRentTxn(@Valid @RequestBody SettleRentRequest settlerent) throws Exception {
        User user = userRepository.findByUserName(settlerent.getUsername());
        if(user == null) throw new Exception("user not found");

        MonthyRent monthyRent = monthlyRentRepository.findByMonthYear(settlerent.getMonthYear());
        if(monthyRent == null) throw new Exception("month not found");

        Txn txn = txnRepository.findTxn(user, monthyRent);
        if(txn == null || txn.getStatus() == TxnStatus.COMPLETED) throw new Exception("can not do rent settlement");

        txn.setStatus(TxnStatus.COMPLETED);
        txnRepository.save(txn);

        return new ResponseEntity<>("rent settlement done", HttpStatus.ACCEPTED);
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

    public ResponseEntity<String> sendMessageToEveryOne(MessageRequest messageRequest) throws Exception {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user = userRepository.findByUserName(user.getUsername());
        if(user == null) throw new Exception("sender not found");

        Message message = Message.builder()
                .type(MessageType.ADMIN_TO_ALL_USERS)
                .sender(user)
                .message(messageRequest.getMessage())
                .build();
        messageRepository.save(message);

        return new ResponseEntity<>("message sent to every one", HttpStatus.CREATED);
    }

    public ResponseEntity<List<Object>> getMessage(Integer page, Integer size){
        Pageable pageable = PageRequest.of(page, size);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user = userRepository.findByUserName(user.getUsername());

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
