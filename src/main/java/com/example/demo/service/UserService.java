package com.example.demo.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.repo.UsersRepo;

@Service
@Transactional
public class UserService {

	@Autowired
	private UsersRepo usersRepo;

}
