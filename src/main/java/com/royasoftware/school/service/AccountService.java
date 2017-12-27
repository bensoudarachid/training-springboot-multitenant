package com.royasoftware.school.service;

import java.util.Collection;

import com.royasoftware.school.model.Account;

/**
 * Created by christospapidas on 24012016--.
 */
public interface AccountService {

    Collection<Account> findAll();

    Account findByUserid(Long userid);
    
    Account findByUsername(String userename);

    Account createNewAccount(Account account);
    
    Account saveAccount(Account account);

}
