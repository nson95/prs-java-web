package com.prs.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.prs.db.LineItemRepo;
import com.prs.db.RequestRepo;
import com.prs.model.LineItem;
import com.prs.model.Request;

@CrossOrigin
@RestController
@RequestMapping("/api/lineitems")
public class LineItemController {

	@Autowired
	private LineItemRepo lineItemRepo;
	@Autowired
	private RequestRepo requestRepo;

	@GetMapping("/")
	public List<LineItem> getAll() {
		return lineItemRepo.findAll();
	}

	@GetMapping("/{id}")
	public Optional<LineItem> getById(@PathVariable int id) {
		Optional<LineItem> li = lineItemRepo.findById(id);
		if (li.isPresent()) {
			return li;
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error, no lineitem for id " + id);
		}
	}

	@PostMapping("")
	public LineItem add(@RequestBody LineItem lineItem) {
		lineItemRepo.save(lineItem);
		recalculateLineItemTotal(lineItem.getRequest().getId());
		return lineItem;
	}

	@PutMapping("/{id}")
	public void update(@PathVariable int id, @RequestBody LineItem lineitem) {
		if (id != lineitem.getId()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error, id in path and body do not match");
		} else if (lineItemRepo.existsById(id)) {
			int reqId = lineItemRepo.findById(id).get().getRequest().getId();
			lineItemRepo.save(lineitem);
			recalculateLineItemTotal(reqId);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error, no lineitem for id " + id);
		}
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable int id) {
		if (lineItemRepo.existsById(id)) {
			int reqId = lineItemRepo.findById(id).get().getRequest().getId();
			lineItemRepo.deleteById(id);
			recalculateLineItemTotal(reqId);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error, no lineitem for id " + id);
		}
	}

	@GetMapping("/lines-for-req/{id}")
	public List<LineItem> getLinesForRequest(@PathVariable int id) {
		return lineItemRepo.findByRequestId(id);
	}

	private void recalculateLineItemTotal(int requestId) {
		List<LineItem> li = lineItemRepo.findByRequestId(requestId);
		Request request = requestRepo.findById(requestId).get();
		double total = 0.0;
		if (!request.equals(null)) {
			for (var l : li) {
				total += l.getQuantity() * l.getProduct().getPrice();
			}
			request.setTotal(total);
			requestRepo.save(request);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error, no request for id " + requestId);
		}
	}
}
