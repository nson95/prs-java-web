package com.prs.db;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prs.model.User;
import java.util.List;


public interface UserRepo extends JpaRepository<User, Integer>{
	User findByUserNameEquals(String userName);
}
