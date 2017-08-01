package com.jpmorgan.tradelogger.message;

import com.jpmorgan.tradelogger.core.MessageReceiver;
import com.jpmorgan.tradelogger.core.Sale;

/**
 * A message type that instructs a repository to record a simple sale.
 * @author nukic
 *
 */
public class SimpleSaleMessage extends Message {

	public SimpleSaleMessage(Sale sale, MessageReceiver processor) {
		super(sale, processor);
	}

	@Override
	public String toString() {
		return "SIMPLE SALE MSG: " + sale;
	}

	@Override
	public void process() {
		repo.add(sale);
	}

}
