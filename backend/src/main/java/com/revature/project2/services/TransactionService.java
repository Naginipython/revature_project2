package com.revature.project2.services;

import com.revature.project2.exceptions.BusinessException;
import com.revature.project2.models.DTOs.TransactionDTO;
import com.revature.project2.models.Transaction;
import com.revature.project2.models.mappers.TransactionDTOMapper;
import com.revature.project2.repositories.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionDTOMapper transactionDTOMapper;

    public TransactionService(TransactionRepository transactionRepository, TransactionDTOMapper transactionDTOMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionDTOMapper = transactionDTOMapper;
    }

    public Transaction updateTransactionTitle(Integer id, String newTitle) {
        logger.info("Updating transaction title for transaction with id: " + id);
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        transaction.setTitle(newTitle);
        return transactionRepository.save(transaction);
    }

    public Transaction updateTransactionDescription(Integer id, String newDescription) {
        logger.info("Updating transaction description for transaction with id: " + id);
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        transaction.setTransactionDescription(newDescription);
        return transactionRepository.save(transaction);
    }

    public Transaction createTransaction(Transaction transaction) {
        logger.info("Creating transaction: " + transaction);
        if (transaction.getTitle() == null || transaction.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Transaction title cannot be null or empty");
        }
        if (transaction.getTransactionDescription() == null || transaction.getTransactionDescription().isEmpty()) {
            throw new IllegalArgumentException("Transaction description cannot be null or empty");
        }
        //Additional business rule checks can be added here

        try {
            return transactionRepository.save(transaction);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save transaction: " + e.getMessage(), e);
        }
    }

    public List<Transaction> getAllTransactions() {
        logger.info("Retrieving all transactions");
        return transactionRepository.findAll();
    }

    public Transaction updateTransactionCategory(Integer id, String newCategory) {
        logger.info("Updating transaction Category for transaction with id: " + id);
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(654, "Transaction not found"));
        if (newCategory == null || newCategory.isEmpty()) {
            throw new BusinessException(607,"Transaction category cannot be null or empty");
        }
        transaction.setCategory(newCategory);
        return transactionRepository.save(transaction);
    }

    public ResponseEntity<?> getTransactionsByEnvelopeId(Integer envelopeId) {
        logger.info("Retrieving transaction by envelope id: " + envelopeId);
        List<Transaction> transactions = transactionRepository.findByEnvelope_EnvelopeId(envelopeId);
        if (transactions.isEmpty()) {
            return ResponseEntity.badRequest().body("Transaction with Envelope id " + envelopeId + " does not exist");
        } else {
            return ResponseEntity.ok(transactions);
        }
    }

}