package com.salesmanager.core.business.services.payments;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.payments.TransactionRepository;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.model.order.Order;
import com.salesmanager.core.model.payments.Transaction;
import com.salesmanager.core.model.payments.TransactionType;



@Service("transactionService")
public class TransactionServiceImpl  extends SalesManagerEntityServiceImpl<Long, Transaction> implements TransactionService {
	

	private TransactionRepository transactionRepository;
	
	@Inject
	public TransactionServiceImpl(TransactionRepository transactionRepository) {
		super(transactionRepository);
		this.transactionRepository = transactionRepository;
	}
	
	@Override
	public void create(Transaction transaction) throws ServiceException {
		
		//parse JSON string
		String transactionDetails = transaction.toJSONString();
		if(!StringUtils.isBlank(transactionDetails)) {
			transaction.setDetails(transactionDetails);
		}
		
		super.create(transaction);
		
		
	}
	
	@Override
	public List<Transaction> listTransactions(Order order) throws ServiceException {
		
		List<Transaction> transactions = transactionRepository.findByOrder(order.getId());
		ObjectMapper mapper = new ObjectMapper();
		for(Transaction transaction : transactions) {
				if(!StringUtils.isBlank(transaction.getDetails())) {
					try {
						@SuppressWarnings("unchecked")
						Map<String,String> objects = mapper.readValue(transaction.getDetails(), Map.class);
						transaction.setTransactionDetails(objects);
					} catch (Exception e) {
						throw new ServiceException(e);
					}
				}
		}
		
		return transactions;
	}

	@Override
	public Transaction getCapturableTransaction(Order order)
			throws ServiceException {
		List<Transaction> transactions = transactionRepository.findByOrder(order.getId());
		ObjectMapper mapper = new ObjectMapper();
		Transaction capturable = null;
		for(Transaction transaction : transactions) {
			if(transaction.getTransactionType().name().equals(TransactionType.AUTHORIZE.name())) {
				if(!StringUtils.isBlank(transaction.getDetails())) {
					try {
						@SuppressWarnings("unchecked")
						Map<String,String> objects = mapper.readValue(transaction.getDetails(), Map.class);
						transaction.setTransactionDetails(objects);
						capturable = transaction;
					} catch (Exception e) {
						throw new ServiceException(e);
					}
				}
			}
			if(transaction.getTransactionType().name().equals(TransactionType.CAPTURE.name())) {
				break;
			}
			if(transaction.getTransactionType().name().equals(TransactionType.REFUND.name())) {
				break;
			}
		}
		
		return capturable;
	}
	
	@Override
	public Transaction getRefundableTransaction(Order order)
		throws ServiceException {
		List<Transaction> transactions = transactionRepository.findByOrder(order.getId());
		Map<String,Transaction> finalTransactions = new HashMap<String,Transaction>();
		Transaction finalTransaction = null;
		for(Transaction transaction : transactions) {
			//System.out.println("Transaction type " + transaction.getTransactionType().name());
			if(transaction.getTransactionType().name().equals(TransactionType.AUTHORIZECAPTURE.name())) {
				finalTransactions.put(TransactionType.AUTHORIZECAPTURE.name(),transaction);
				continue;
			}
			if(transaction.getTransactionType().name().equals(TransactionType.CAPTURE.name())) {
				finalTransactions.put(TransactionType.CAPTURE.name(),transaction);
				continue;
			}
			if(transaction.getTransactionType().name().equals(TransactionType.REFUND.name())) {
				//check transaction id
				Transaction previousRefund = finalTransactions.get(TransactionType.REFUND.name());
				if(previousRefund!=null) {
					Date previousDate = previousRefund.getTransactionDate();
					Date currentDate = transaction.getTransactionDate();
					if(previousDate.before(currentDate)) {
						finalTransactions.put(TransactionType.REFUND.name(),transaction);
						continue;
					}
				} else {
					finalTransactions.put(TransactionType.REFUND.name(),transaction);
					continue;
				}
			}
		}
		
		if(finalTransactions.containsKey(TransactionType.AUTHORIZECAPTURE.name())) {
			finalTransaction = finalTransactions.get(TransactionType.AUTHORIZECAPTURE.name());
		}
		
		if(finalTransactions.containsKey(TransactionType.CAPTURE.name())) {
			finalTransaction = finalTransactions.get(TransactionType.CAPTURE.name());
		}

		if(finalTransaction!=null && !StringUtils.isBlank(finalTransaction.getDetails())) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				@SuppressWarnings("unchecked")
				Map<String,String> objects = mapper.readValue(finalTransaction.getDetails(), Map.class);
				finalTransaction.setTransactionDetails(objects);
			} catch (Exception e) {
				throw new ServiceException(e);
			}
		}
		
		return finalTransaction;
	}

	@Override
	public List<Transaction> listTransactions(Date startDate, Date endDate) throws ServiceException {
		
		return transactionRepository.findByDates(startDate, endDate);
	}

}
