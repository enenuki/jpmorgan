package com.jpmorgan.tradelogger.core;

import java.math.BigDecimal;

/**
 * An instance of this class represents a sale. Value may be sensitive to
 * precision, so we are using BigDecimal for it.
 * <p>
 * TODO: Maybe we should add number of pieces in this class?
 * 
 * @author nukic
 *
 */
public class Sale {
	private BigDecimal value;
	private String productType;

	public Sale(BigDecimal value, String productType) {
		this.value = value;
		this.productType = productType;
	}

	public Sale(Sale copy) {
		this.value = copy.value;
		this.productType = copy.productType;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	@Override
	public String toString() {
		return "SALE: [product = " + this.productType + "; value = " + value.setScale(2).toPlainString() + "]";
	}

}
