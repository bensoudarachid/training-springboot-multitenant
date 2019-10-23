package com.royasoftware.school.service;

import com.royasoftware.school.model.Account;
import com.royasoftware.school.model.Role;
import com.royasoftware.school.repository.AccountRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class AccountServiceBean implements AccountService {
    /**
     * The Logger for this class.
     */
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * The Spring Data repository for Account entities.
     */
    @Autowired(required=false)
    private AccountRepository accountRepository;

    /**
     * The Spring Data repository for Role entities
     */
    @Autowired(required=false)
    private RoleService roleService;

    /**
     * Find and return all accounts
     * @return collection of all accounts
     */
    @Override
    public Collection<Account> findAll() {
        Collection<Account> accounts = accountRepository.findAll();
        return accounts;
    }

    /**
     * Find user by username
     * @param username the username of the user
     * @return the user account
     */
    @Override
    public Account findByUsername(String username) {
        Account account = accountRepository.findByUsername(username);
        return account;
    }

    /**
     * Find user by username
     * @param username the username of the user
     * @return the user account
     */
    @Override
    public Account findByUserid(Long userid) {
        Account account = accountRepository.findByUserid(userid);
        return account;
    }

    /**
     * Create a new user as simple user. Find the simple user role from the database
     * add assign to the many to many collection
     * @param account - new Account of user
     * @return - the created account
     * @throws Exception 
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public Account createNewAccount(Account account) {
    	System.out.println("new BCryptPasswordEncoder().encode");
        // Add the simple user role
        Role role = roleService.findByCode("ROLE_USER");
        Set<Role> roles = new HashSet<>();
        roles.add(role);

        // Validate the password
        if (account.getPassword().length() < 8){
            throw new PersistenceException("password should be greater than 8 characters");
        }

        // Encode the password
        account.setPassword(new BCryptPasswordEncoder().encode(account.getPassword()));

        // Create the role
        account.setRoles(roles);
        
		Integer rand = new Integer(new java.util.Random().nextInt());
		System.out.println("AuthenticationCoontroller, Reg code of "+rand);
		rand = rand<0?(-rand):rand;
		System.out.println("AuthenticationCoontroller, Reg code of "+rand);
		account.setRegistrationId(rand);
		account.setRegistrationDate(new Date());
		account.setEnabled(false);
        account = accountRepository.save(account);
//        account.setPassword("");
        return account;
    }
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public Account saveAccount(Account account) {
        Account acc = accountRepository.save(account);
        return acc;
    }

}
