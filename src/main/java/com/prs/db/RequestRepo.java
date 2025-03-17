package com.prs.db;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.prs.model.Request;

public interface RequestRepo extends JpaRepository<Request, Integer>{
	default Optional<String> findTopRequestNumber() {
        return findAll().stream()
          .map(Request::getRequestNumber)
          .max(Comparator.naturalOrder());
};
	@Query("select r from Request r where r.status = ?1")
	List<Request> findByStatus(String status);
}
