package com.royasoftware.service;

import com.royasoftware.model.Account;

import java.util.Collection;

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
