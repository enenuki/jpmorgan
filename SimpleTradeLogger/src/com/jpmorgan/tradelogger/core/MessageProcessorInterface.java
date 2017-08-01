package com.jpmorgan.tradelogger.core;

import com.jpmorgan.tradelogger.message.Message;

/**
 * Any class implementing this interface will be considered as message
 * processor. For now we have only {@code MessageReceiver} class. This is a main
 * entry to our message processing system.
 * 
 * @author nukic
 *
 */
public interface MessageProcessorInterface {

	/**
	 * Initiates processing of a message.
	 * 
	 * @param message
	 *            Message to be processed
	 * @return
	 */
	boolean process(Message message);

}
