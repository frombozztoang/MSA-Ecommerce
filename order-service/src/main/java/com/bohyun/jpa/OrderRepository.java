package com.bohyun.jpa;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<OrderEntity, Long>{
	
	OrderEntity findByOrderId(String OrderId);
	@Modifying
	@Query("update OrderEntity p set p.view = p.view + 1 where p.id = :id")
	int updateView(Long id);
	Iterable<OrderEntity> findByUserId(String userId);
	
}
