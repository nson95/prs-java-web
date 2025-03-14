package com.prs.controller;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.prs.db.RequestRepo;
import com.prs.model.Request;

@CrossOrigin
@RestController
@RequestMapping("/api/requests")
public class RequestController {

	@Autowired
	private RequestRepo requestRepo;

	@GetMapping("/")
	public List<Request> getAll() {
		return requestRepo.findAll();
	}

	@GetMapping("/{id}")
	public Optional<Request> getById(@PathVariable int id) {
		Optional<Request> r = requestRepo.findById(id);
		if (r.isPresent()) {
			return r;
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error no requests found for id " + id);
		}
	}

	@PostMapping("")
	public Request add(@RequestBody Request request) {
		request.setRequestNumber(getRequestNumber());
		request.setSubmittedDate(LocalDateTime.now());
		return requestRepo.save(request);
	}

	@PutMapping("/{id}")
	public void update(@PathVariable int id, @RequestBody Request request) {
		Request r = requestRepo.findById(id).get();
		if (id != r.getId()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error, id input mismatch per URL id "+id);
		}
		else if (requestRepo.existsById(id)) {
			requestRepo.save(request);
		}
		else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error, no entry found for id "+id);
		}
	}
	
	@DeleteMapping("/{id}")
	public void delete(@PathVariable int id) {
		if (requestRepo.existsById(id)) {
			requestRepo.deleteById(id);
		}
		else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error, no id found for entry "+id);
		}
	}
	
	private String getRequestNumber() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
		DecimalFormat formatTheEnd = new DecimalFormat("0000");
		String requestNumber = "R";
		int maxReqNum = 0;
		List<Request> requests = requestRepo.findAll();
		LocalDate today = LocalDate.now();
		String formattedDate = formatter.format(today);
		for (var r : requests) {
			if (r.getId() > maxReqNum) {
				maxReqNum = r.getId();
			}
		}
		maxReqNum += 1;
		requestNumber += formattedDate;
		requestNumber += formatTheEnd.format(maxReqNum);

		return requestNumber;
	}
}
