package com.bohyun.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class OrderDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String productId;
	private Integer qty;
	private Integer unitPrice;
	private Integer totalPrice;
	private Integer viewNum;
	
	private String orderId;
	private String userId;
	
}
