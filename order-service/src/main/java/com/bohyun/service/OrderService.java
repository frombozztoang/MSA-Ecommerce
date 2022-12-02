package com.bohyun.service;

import com.bohyun.dto.OrderDto;
import com.bohyun.jpa.OrderEntity;

public interface OrderService {
	
	OrderDto createOrder(OrderDto orderDto);

	 OrderDto updateView(Long orderDto);

	OrderDto getViewNum(OrderDto orderDto);

	static OrderDto getOrderByOrderId(String orderId);

	OrderDto getOrderByOrderId(String orderId);

	Iterable<OrderEntity> getOrdersByUserId(String userId);
}
