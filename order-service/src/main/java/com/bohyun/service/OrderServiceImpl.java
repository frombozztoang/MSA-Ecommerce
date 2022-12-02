package com.bohyun.service;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bohyun.dto.OrderDto;
import com.bohyun.jpa.OrderEntity;
import com.bohyun.jpa.OrderRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService{

	private static OrderDto updateView;
	OrderRepository orderRepository;
	
	@Autowired
	public OrderServiceImpl(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	/* Views Counting */
	@Transactional
	public OrderDto updateView(Long id) {
		return OrderServiceImpl.updateView;
	}
	@Override
	public OrderDto createOrder(OrderDto orderDto) {
		orderDto.setOrderId(UUID.randomUUID().toString());
		orderDto.setTotalPrice(orderDto.getUnitPrice()*orderDto.getQty());
		orderDto.setViewNum(orderDto.getViewNum());
		
		// 데이터 베이스에 저장하기 위해 OrderEntity가 필요함.
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		OrderEntity orderEntity = mapper.map(orderDto, OrderEntity.class);
		
		
		// insert into database
		orderRepository.save(orderEntity);
		OrderDto returnOrderDto = mapper.map(orderEntity, OrderDto.class);

		return returnOrderDto;
		
	}

	@Override
	public OrderDto getViewNum(OrderDto orderDto) {
		return null;
	}




	@Override
	public OrderDto getOrderByOrderId(String orderId) {
		OrderEntity orderEntity = orderRepository.findByOrderId(orderId);
		OrderDto orderDto = new ModelMapper().map(orderEntity, OrderDto.class);
		
		return orderDto;
	}

	
	@Override
	public Iterable<OrderEntity> getOrdersByUserId(String userId) {
		return orderRepository.findByUserId(userId);
	}
}
