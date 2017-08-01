package com.jpmorgan.tradelogger.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import com.jpmorgan.tradelogger.message.AdjustmentOperation;
import com.jpmorgan.tradelogger.message.AdjustmentSaleMessage;
import com.jpmorgan.tradelogger.message.Message;

/**
 * This class represents receiver of messages about sales. Each MessageProcessor
 * instance can only process up to {@code QUEUE_SIZE} messages. Every 10th
 * message initiates an intermediate report. After queue is filled, no more
 * messages is accepted and final log is printed out to a console.
 * 
 * @author nukic
 *
 */
public class MessageReceiver implements MessageProcessorInterface, SaleRepositoryInterface {
	public static final int QUEUE_SIZE = 50;

	// At most QUEUE_SIZE messages, implies at most QUEUE_SIZE product types
	private Map<String, List<Sale>> product2SaleListMap = new HashMap<String, List<Sale>>(QUEUE_SIZE);
	// Optionally bounded queue will suite our needs
	private LinkedBlockingQueue<Message> msgQueue = new LinkedBlockingQueue<Message>(QUEUE_SIZE);

	@Override
	public boolean process(Message message) {
		// System.out.println("Processing a message. Remaining queue capacity: "
		// + msgQueue.remainingCapacity());
		boolean messageAccepted = msgQueue.offer(message);
		// If there is place in the queue...
		if (messageAccepted) {
			message.process();
			createLogsIfNeeded();
		} else {
			System.out.println("This receiver does not accept any more messages. Ignoring received message: "
					+ message.getSale().toString());
		}
		return messageAccepted;
	}

	@Override
	public void add(Sale sale) {
		if (!product2SaleListMap.containsKey(sale.getProductType())) {
			product2SaleListMap.put(sale.getProductType(), new ArrayList<Sale>());
		}
		product2SaleListMap.get(sale.getProductType()).add(new Sale(sale));
	}

	@Override
	public void addBatch(Sale sale, int occurences) {
		if (!product2SaleListMap.containsKey(sale.getProductType())) {
			product2SaleListMap.put(sale.getProductType(), new ArrayList<Sale>());
		}
		for (int i = 0; i < occurences; i++) {
			product2SaleListMap.get(sale.getProductType()).add(new Sale(sale));
		}
	}

	@Override
	public void addAndAdjust(Sale sale, AdjustmentOperation operation, BigDecimal value) {
		if (!product2SaleListMap.containsKey(sale.getProductType())) {
			product2SaleListMap.put(sale.getProductType(), new ArrayList<Sale>());
		}
		product2SaleListMap.get(sale.getProductType()).add(sale);
		adjustSalesValues(sale.getProductType(), operation, value);

	}

	/**
	 * Adjusts the value of all sales of this type, depending on the operation
	 * to be performed.
	 */
	private void adjustSalesValues(String productType, AdjustmentOperation operation, BigDecimal value) {
		switch (operation) {
		case MULTIPLY:
			product2SaleListMap.get(productType).stream().forEach(sale -> {
				sale.setValue(sale.getValue().multiply(value));
			});
			break;
		case DIVIDE:
			product2SaleListMap.get(productType).stream().forEach(sale -> {
				sale.setValue(sale.getValue().divide(value));
			});
			break;
		case ADD:
			product2SaleListMap.get(productType).stream().forEach(sale -> {
				sale.setValue(sale.getValue().add(value));
			});
			break;
		case SUBTRACT:
			product2SaleListMap.get(productType).stream().forEach(sale -> {
				sale.setValue(sale.getValue().subtract(value));
			});
			break;
		}
	}

	/**
	 * This method is called after message is processed to generate logs if it
	 * is needed. We generate report every 10 message. After 50 processed
	 * messages we generate final log and report
	 */
	private void createLogsIfNeeded() {
		int msgNumber = QUEUE_SIZE - msgQueue.remainingCapacity();
		// If this is last accepted message, we should create both logs...
		if (msgNumber == QUEUE_SIZE) {
			System.out.println("Received 50 messages. Creating report and final log...");
			createReport();
			createFinalLog();
		}
		// If this is 10th message, create intermediate log
		else if (msgNumber % 10 == 0 && msgNumber != 0) {
			System.out.println("Received " + msgNumber + " messages. Creating report...");
			createReport();
		} else {
			// System.out.println("No need to create logs.");
		}
	}

	/**
	 * Final log states that logger is pausing and specifies all adjustments
	 * that have been made to each sale type while the application was running.
	 */
	private void createFinalLog() {
		System.out.println("----------------------------------------------------------");
		System.out.println("FINAL LOG:");
		System.out.println("Simple trade logger is pausing. Adjustments made:");
		List<AdjustmentSaleMessage> aMsgList = msgQueue.stream().filter(msg -> {
			return msg instanceof AdjustmentSaleMessage;
		}).map(msg -> (AdjustmentSaleMessage) msg).collect(Collectors.toList());
		if (aMsgList.isEmpty()) {
			System.out.println("<NO ADJUSTMENTS MADE>");
		} else {
			aMsgList.stream().forEach(adjustMsg -> {
				System.out.println("Product " + adjustMsg.getSale().getProductType() + " adjusted by value "
						+ adjustMsg.getAdjustmentValue().toPlainString() + " using operation "
						+ adjustMsg.getAdjustmentOperation().name());
			});
		}
		System.out.println("\n----------------------------------------------------------");

	}

	
	
	/**
	 * This method prints the report to the standard output. Report is printed
	 * every 10 messages and specifies the number of sales of each product and
	 * their total value
	 */
	private void createReport() {
		System.out.println("----------------------------------------------------------");
		System.out.println("INTERMEDIARY REPORT:");
		product2SaleListMap.forEach((k, v) -> {
			BigDecimal totalProductValue = v.stream().map(sale -> sale.getValue()).reduce(BigDecimal.ZERO,
					BigDecimal::add);
			System.out.print("PRODUCT " + k + " (NUMBER OF SALES: " + v.size() + ") TOTAL VALUE: "
					+ totalProductValue.toPlainString());
		});
		System.out.println("\n----------------------------------------------------------");
	}

	public Map<String, List<Sale>> getProduct2SaleListMap() {
		return product2SaleListMap;
	}

	public void setProduct2SaleListMap(Map<String, List<Sale>> product2SaleListMap) {
		this.product2SaleListMap = product2SaleListMap;
	}

	public LinkedBlockingQueue<Message> getMsgQueue() {
		return msgQueue;
	}

	public void setMsgQueue(LinkedBlockingQueue<Message> msgQueue) {
		this.msgQueue = msgQueue;
	}
	
	

}
