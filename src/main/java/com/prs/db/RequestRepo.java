package com.prs.db;

import java.util.Comparator;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prs.model.Request;

public interface RequestRepo extends JpaRepository<Request, Integer>{
	default Optional<String> findTopRequestNumber() {
        return findAll().stream()
          .map(Request::getRequestNumber)
          .max(Comparator.naturalOrder());
};
}
