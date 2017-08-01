package com.jpmorgan.tradelogger.message;

import com.jpmorgan.tradelogger.core.MessageReceiver;
import com.jpmorgan.tradelogger.core.Sale;

/**
 * A message that instructs a repository to record a number of same sales
 * 
 * @author nukic
 *
 */
public class BatchSaleMessage extends Message {
	private int occurences;

	public BatchSaleMessage(Sale sale, int occurences, MessageReceiver processor) {
		super(sale, processor);
		this.occurences = occurences;
	}

	public long getOccurences() {
		return occurences;
	}

	public void setOccurences(int occurences) {
		this.occurences = occurences;
	}

	@Override
	public String toString() {
		return "BATCH SALE MSG: [" + sale + "; occurences = " + occurences + "]";
	}

	@Override
	public void process() {
		repo.addBatch(sale, occurences);
	}

}
