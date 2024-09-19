package com.shubham.UserServer.schedules;

import com.shubham.UserServer.model.MonthyRent;
import com.shubham.UserServer.model.Txn;
import com.shubham.UserServer.model.TxnStatus;
import com.shubham.UserServer.model.User;
import com.shubham.UserServer.repository.MonthlyRentRepository;
import com.shubham.UserServer.repository.TxnRepository;
import com.shubham.UserServer.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class AddMonth {
    @Autowired
    private MonthlyRentRepository monthlyRentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TxnRepository txnRepository;

    @Value("${application.monthly.Rent}")
    private Integer rent;

    @Scheduled(cron = "0 0 0 1 * *")
    @Transactional
    public void addMonthInDb(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM.yyyy");
        String monthYear = sdf.format(date);

        System.out.println(monthYear);

        MonthyRent monthyRent = MonthyRent.builder()
                .monthYear(monthYear)
                .amount(rent)
                .build();
        monthyRent = monthlyRentRepository.save(monthyRent);

        List<User> userList = userRepository.findAll();
        for(User user : userList){
            Txn txn = Txn.builder()
                    .amount(rent + user.getRoom().getExtraChargePerMonth())
                    .user(user)
                    .month(monthyRent)
                    .txnId(UUID.randomUUID().toString())
                    .status(TxnStatus.PENDING)
                    .build();

            txnRepository.save(txn);
        }
    }
}