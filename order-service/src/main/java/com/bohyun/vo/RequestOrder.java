package com.bohyun.vo;

import lombok.Data;

@Data
public class RequestOrder {
	private String productId;
	private Integer qty;
	private Integer unitPrice;
}
