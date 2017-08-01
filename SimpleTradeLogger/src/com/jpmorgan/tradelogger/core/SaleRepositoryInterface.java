package com.jpmorgan.tradelogger.core;

import java.math.BigDecimal;

import com.jpmorgan.tradelogger.message.AdjustmentOperation;

/**
 * This is an interface towards a repository where all sales are stored. In our
 * case {@code MessageReceiver} is the only class implementing this interface.
 * 
 * @author nukic
 *
 */
public interface SaleRepositoryInterface {
	/**
	 * Adds a sale to repository.
	 * 
	 * @param sale
	 *            A Sale instance to be added
	 */
	void add(Sale sale);

	/**
	 * Adds a number of copies of this sale to a repository.
	 * 
	 * @param sale
	 *            A Sale prototype to be added
	 * @param occurences
	 *            A number of copies to be made
	 */
	void addBatch(Sale sale, int occurences);

	/**
	 * Adds a single sale and adjusts a value of all existing sales, including
	 * the one that is being added.
	 * 
	 * @param sale
	 *            A Sale to be added
	 * @param operation
	 *            An arithmetic operation to be performed on sale values
	 * @param value
	 *            A value to be used with operation when adjusting sale values
	 */
	void addAndAdjust(Sale sale, AdjustmentOperation operation, BigDecimal value);

}
