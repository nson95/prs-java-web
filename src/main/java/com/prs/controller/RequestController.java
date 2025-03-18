package com.prs.controller;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.prs.db.RequestRepo;
import com.prs.db.UserRepo;
import com.prs.model.Request;
import com.prs.model.RequestDTO;
import com.prs.model.User;

@CrossOrigin
@RestController
@RequestMapping("/api/requests")
public class RequestController {

	@Autowired
	private RequestRepo requestRepo;
	
	@Autowired
	private UserRepo userRepo;

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
	public Request add(@RequestBody RequestDTO requestDTO) {
		User currentUser = userRepo.findById(requestDTO.getUserId()).get();
		Request r = new Request(currentUser, getRequestNumber(), requestDTO.getDescription(), requestDTO.getJustification(),
				requestDTO.getDateNeeded(), requestDTO.getDeliveryMode(),
				"NEW", 0.0, LocalDateTime.now(), null);
		requestRepo.save(r);
		return r;
	}

	@PutMapping("/{id}")
	public void update(@PathVariable int id, @RequestBody Request request) {
		Request r = requestRepo.findById(id).get();
		if (id != r.getId()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error, id input mismatch per URL id " + id);
		} else if (requestRepo.existsById(id)) {
			requestRepo.save(request);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error, no entry found for id " + id);
		}
	}

	@PutMapping("/approve/{id}")
	public Request approve(@PathVariable int id) {
		Request r = requestRepo.findById(id).get();
		if (id != r.getId()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error, id input mismatch per URL id " + id);
		} else if (requestRepo.existsById(id)) {
			r.setStatus("APPROVED");
			requestRepo.save(r);
			return r;
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error, no entry found for id " + id);
		}
	}

	@PutMapping("/reject/{id}")
	public Request reject(@PathVariable int id, @RequestBody String reasonForRejection) {
		Request r = requestRepo.findById(id).get();
		if (requestRepo.existsById(id)) {
			r.setReasonForRejection(reasonForRejection);
			r.setStatus("REJECTED");
			requestRepo.save(r);
			return r;
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error, no entry found for id " + id);
		}
	}

	@PutMapping("/submit-review/{id}")
	public Request submitForReview(@PathVariable int id) {
		Request r = requestRepo.findById(id).get();
		if (id != r.getId()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error, id input mismatch per URL id " + id);
		} else if (requestRepo.existsById(id)) {
			if (r.getTotal() < 50) {
				r.setStatus("APPROVED");
				requestRepo.save(r);
				return r;
			} else {
				r.setStatus("REVIEW");
				requestRepo.save(r);
				return r;
			}
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error, no entry found for id " + id);
		}
	}

	@GetMapping("/list-review/{id}")
	public List<Request> listForReview(@PathVariable int id) {
		List<Request> allRequests = requestRepo.findByStatus("REVIEW");
		List<Request> reqForReview = allRequests.stream()
											   .filter(request -> !(request.getUser().getId() == id))
											   .collect(Collectors.toList());
			if (allRequests.equals(null)) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error, bad request");
			}
			else if (!allRequests.isEmpty()) {
				return reqForReview;
			}
				else {
					throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No requests found for userId "+id);
				}
			}
		
	

	@DeleteMapping("/{id}")
	public void delete(@PathVariable int id) {
		if (requestRepo.existsById(id)) {
			requestRepo.deleteById(id);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error, no id found for entry " + id);
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
