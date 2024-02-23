package com.example.demo.repo;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.User;

@Repository
@Transactional
public interface UsersRepo extends JpaRepository<User, Integer> {

	User findByUsername(String username);

}
