package com.jpmorgan.tradelogger.message;

import java.math.BigDecimal;

import com.jpmorgan.tradelogger.core.MessageReceiver;
import com.jpmorgan.tradelogger.core.Sale;

/**
 * A message that instructs a repository to add a single additional sale and
 * adjust all existing sales (of the same product type).
 * 
 * @author nukic
 *
 */
public class AdjustmentSaleMessage extends Message {

	private AdjustmentOperation adjustmentOperation;
	private BigDecimal adjustmentValue;

	public AdjustmentSaleMessage(Sale sale, AdjustmentOperation adjustmentOperation, BigDecimal adjustmentValue,
			MessageReceiver processor) {
		super(sale, processor);
		this.adjustmentOperation = adjustmentOperation;
		this.adjustmentValue = adjustmentValue;
	}

	public AdjustmentOperation getAdjustmentOperation() {
		return adjustmentOperation;
	}

	public void setAdjustmentOperation(AdjustmentOperation adjustmentOperation) {
		this.adjustmentOperation = adjustmentOperation;
	}

	public BigDecimal getAdjustmentValue() {
		return adjustmentValue;
	}

	public void setAdjustmentValue(BigDecimal adjustmentValue) {
		this.adjustmentValue = adjustmentValue;
	}

	@Override
	public String toString() {
		return "ADJUSTMENT SALE MSG: [" + sale + "; operation = " + adjustmentOperation.name() + "; value = "
				+ adjustmentValue.setScale(2).toPlainString() + "]";
	}

	@Override
	public void process() {
		repo.addAndAdjust(sale, adjustmentOperation, adjustmentValue);
	}

}
