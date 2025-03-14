package com.prs.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.prs.db.UserRepo;
import com.prs.model.User;
import com.prs.model.UserLoginDTO;

@CrossOrigin
@RestController
@RequestMapping("/api/users")
public class UserController {
	
	@Autowired
	private UserRepo userRepo;
	
	@GetMapping("/")
	public List<User> getAll() {
		return userRepo.findAll();
	}
	
	@GetMapping("/{id}")
	public Optional<User> getById(@PathVariable int id) {
		Optional<User> u = userRepo.findById(id);
		if (u.isPresent()) {
			return u;
		}
		else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user found for id. Id :" +id);
		}
	}
	
	@PostMapping("")
	public User add(@RequestBody User user) {
		return userRepo.save(user);
	}
	
	@PutMapping("/{id}") 
	public void update(@PathVariable int id, @RequestBody User user) {
		if (id != user.getId()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error, id mismatch per URL");
		}
		else if (userRepo.existsById(id)) {
			userRepo.save(user);
		}
		else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error no id found for id "+id);
		}
	}
	
	@DeleteMapping("/{id}")
	public void delete(@PathVariable int id) {
		if (userRepo.existsById(id)) {
			userRepo.deleteById(id);
		}
		else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user found for id "+id);
		}
	}
	@PostMapping("/login")
	public User login(@RequestBody UserLoginDTO log) {
		User u = userRepo.findByUserNameEquals(log.getUserName());
		if (u == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error, no user found with credentials.");
		}
		else if (!u.getPassword().equals(log.getPassword()) ) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect username/password combination.");
		}
		else {
			return u;
		}
	}
} 
