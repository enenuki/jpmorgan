package com.jpmorgan.tradelogger.message;

import com.jpmorgan.tradelogger.core.Sale;
import com.jpmorgan.tradelogger.core.SaleRepositoryInterface;

/**
 * An abstract supertype for all types of messages.
 * <p>
 * TODO: Since message processing might optionally alter other sales of same
 * type, maybe we should consider removing it from here?
 * 
 * @author nukic
 *
 */
public abstract class Message {
	SaleRepositoryInterface repo;
	protected Sale sale;

	public Message(Sale sale, SaleRepositoryInterface repo) {
		this.sale = sale;
		this.repo = repo;
	}

	public Sale getSale() {
		return sale;
	}

	public void setSale(Sale sale) {
		this.sale = sale;
	}

	public SaleRepositoryInterface getRepo() {
		return repo;
	}

	public void setRepo(SaleRepositoryInterface repo) {
		this.repo = repo;
	}

	public abstract void process();

}
