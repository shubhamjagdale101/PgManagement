package com.shubham.UserServer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shubham.UserServer.constants.UserCreatedConstant;
import com.shubham.UserServer.model.*;
import com.shubham.UserServer.repository.*;
import com.shubham.UserServer.requestEntity.MessageRequest;
import com.shubham.UserServer.requestEntity.SignUp;
import com.shubham.UserServer.responseEntity.FacilityResponse;
import com.shubham.UserServer.responseEntity.Response;
import com.shubham.UserServer.responseEntity.RoomResponse;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private TxnRepository txnRepository;

    @Autowired
    private UsersFacilitiesRepository usersFacilitiesRepository;

    @Autowired
    private MonthlyRentRepository monthlyRentRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private OtpStore otpStore;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUserName(username);
    }

    public User handleSignUp(SignUp req, String role) throws Exception {
        if(!otpStore.isVerified(req.getEmail())) {
            throw new Exception("email id not verified by otp");
        }

         User user = User.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .userName(req.getUserName())
                .adharId(req.getAdharId())
                .panId(req.getPanId())
                .email(req.getEmail())
                .phNo(req.getPhNo())
                .advancePaid(0)
                .authorities(role)
                .status(UserStatus.INACTIVE)
                .password(passwordEncoder.encode(req.getPassword()))
                .build();

         userRepository.save(user);
         otpStore.clearVerified(req.getEmail());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(UserCreatedConstant.ADHAR_NO, user.getAdharId());
        jsonObject.put(UserCreatedConstant.FIRST_NAME, user.getFirstName());
        jsonObject.put(UserCreatedConstant.LAST_NAME, user.getLastName());
        jsonObject.put(UserCreatedConstant.PHONE_NUMBER, user.getPhNo());
        jsonObject.put(UserCreatedConstant.PAN_NO, req.getPanId());
        jsonObject.put(UserCreatedConstant.USERNAME, req.getUserName());

//        kafkaTemplate.send(UserCreatedConstant.USER_CREATED, objectMapper.writeValueAsString(jsonObject.toString()));
        return user;
    }

    public ResponseEntity<Object> availFacility(String serviceName) throws Exception {
        Date date = new Date();
        if(date.getDay() > 10) throw  new Exception("you cant avail facility after 10th day of month");

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        user = userRepository.findByUserName(user.getUsername());
        if(user == null) throw new Exception("user not found");

        Facilities facility = facilityRepository.findByName(serviceName);
        if(facility == null) throw new Exception("facility not found");

        SimpleDateFormat sdf = new SimpleDateFormat("MM.yyyy");
        String monthYear = sdf.format(date);

        MonthyRent month = monthlyRentRepository.findByMonthYear(monthYear);
        if(month == null) throw new Exception("month in month's table not found");

        Txn txn = txnRepository.findTxn(user, month);
        if(txn != null && txn.getStatus() == TxnStatus.COMPLETED) throw new Exception("you already paid this month rent, now you can't add facilities for this month");

        UsersFacilities usersFacilities = usersFacilitiesRepository.findByUserAndFacility(user, facility);
        if(usersFacilities != null) {
            throw new Exception("facility is already in use");
        }

         usersFacilities = UsersFacilities.builder()
                .facility(facility)
                .user(user)
                .status(FacilityRequestStatus.PENDING)
                .facilityStatus(FacilityStatus.INACTIVE)
                .build();
        usersFacilitiesRepository.save(usersFacilities);

        Response res = Response.builder()
                .message("facility is get added to your profile")
                .build();
        return new ResponseEntity<Object>(res, HttpStatus.ACCEPTED);
    }

    public ResponseEntity<List<Object>> getAllFacilities(){
        List<Facilities> facilitiesList = facilityRepository.findAll();
        List<Object> listOfFacilities = new ArrayList<>();

        for(Facilities facilities : facilitiesList){
            listOfFacilities.add(modelMapper.map(facilities, FacilityResponse.class));
        }

        return new ResponseEntity<>(listOfFacilities, HttpStatus.ACCEPTED);
    }

    public ResponseEntity<List<Object>> getAvailableRooms(){
        List<Room> rooms = roomRepository.findAll();
        List<Object> roomList = new ArrayList<>();

        for(Room room : rooms){
            if(room.getAvailableSeats() > 0){
                RoomResponse roomResponse = modelMapper.map(room, RoomResponse.class);
                roomList.add(roomResponse);
            }
        }

        return new ResponseEntity<>(roomList, HttpStatus.ACCEPTED);
    }

    public ResponseEntity<Object> applyToLeave() throws Exception {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user = userRepository.findByUserName(user.getUsername());

        if(user.getStatus() == UserStatus.INACTIVE) return new ResponseEntity<>("you are not active user", HttpStatus.BAD_REQUEST);
        if(user.getStatus() == UserStatus.APPLIED_TO_LEAVE) return new ResponseEntity<>("you are already applied for to leave", HttpStatus.BAD_REQUEST);

        user.setStatus(UserStatus.APPLIED_TO_LEAVE);

        Response res = Response.builder()
                .message("Applied to Leave Pg.")
                .build();
        return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
    }

    public ResponseEntity<Object> messageToAdmin(MessageRequest messageRequest){
        if(messageRequest.getType() == MessageType.ADMIN_TO_USER) return new ResponseEntity<>("can't send", HttpStatus.BAD_REQUEST);

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user = userRepository.findByUserName(user.getUsername());

        Message message = Message.builder()
                .sender(user)
                .type(MessageType.USER_TO_ADMIN)
                .message(messageRequest.getMessage())
                .build();

        messageRepository.save(message);

        Response res = Response.builder()
                .message("Message sent successfully")
                .build();
        return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
    }

    public ResponseEntity<List<Object>> getMessage(Integer page, Integer size){
        Pageable pageable = PageRequest.of(page, size);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = ((Jwt) auth.getPrincipal()).getSubject();
        User user = userRepository.findByUserName(username);

        Page<Message> pageData = messageRepository.getMessage(MessageType.ADMIN_TO_ALL_USERS, user, pageable);
        List<Message> messageList = pageData.getContent();
        List<Object> messageListRes = new ArrayList<>();

        for(Message msg : messageList){
            MessageRequest msgRes = MessageRequest.builder()
                    .Message(msg.getMessage())
                    .sender(msg.getSender().getUsername())
                    .type(msg.getType())
                    .build();
            if(msg.getReceiver() != null) msgRes.setReceiver(msg.getReceiver().getUsername());
            messageListRes.add(msgRes);
        }
        return new ResponseEntity<>(messageListRes, HttpStatus.OK);
    }
}