package com.revature.project2.services;

import com.revature.project2.models.DTOs.EnvelopeDTO;
import com.revature.project2.models.DTOs.TransferFundDTO;
import com.revature.project2.models.Envelope;
import com.revature.project2.models.EnvelopeHistory;
import com.revature.project2.models.Transaction;
import com.revature.project2.models.User;
import com.revature.project2.repositories.EnvelopeRepository;
import com.revature.project2.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EnvelopeService {
    private final Logger logger = LoggerFactory.getLogger(EnvelopeService.class);
    private final EnvelopeRepository envelopeRepository;
    private final UserRepository userRepository;
    private final TransactionService transactionService;
    private final EnvelopeHistoryService envelopeHistoryService;

    public EnvelopeService(EnvelopeRepository envelopeRepository, UserRepository userRepository,
                           TransactionService transactionService, EnvelopeHistoryService envelopeHistoryService) {
        this.envelopeRepository = envelopeRepository;
        this.userRepository = userRepository;
        this.transactionService = transactionService;
        this.envelopeHistoryService = envelopeHistoryService;
    }

    public ResponseEntity<?> createEnvelope(EnvelopeDTO envelopeDTO) {
        logger.info("Creating envelope: {}", envelopeDTO);
        if (envelopeDTO.envelopeDescription().isEmpty() || envelopeDTO.balance() == null ||
                envelopeDTO.maxLimit() == null || envelopeDTO.userId() == null) {
            return ResponseEntity.badRequest().body("Envelope fields cannot be null");
        }
        if (envelopeDTO.balance() < 0 || envelopeDTO.maxLimit() < 0) {
            return ResponseEntity.badRequest().body("Amount and max limit cannot be negative");
        }
        Optional<User> user = userRepository.findById(envelopeDTO.userId());
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("User does not exist");
        }

        Envelope envelope = new Envelope();
        envelope.setEnvelopeDescription(envelopeDTO.envelopeDescription());
        envelope.setBalance(envelopeDTO.balance());
        envelope.setMaxLimit(envelopeDTO.maxLimit());
        envelope.setUser(user.get());

        logger.info("Creating envelope: " + envelope);

        Envelope savedEnvelope = envelopeRepository.save(envelope);
        savedEnvelope.getUser().setPassword(null);

        return ResponseEntity.ok(savedEnvelope);
    }

    public ResponseEntity<?> getEnvelopeById(Integer id) {
        logger.info("Retrieving envelope with id: {}", id);
        Optional<Envelope> envelope = envelopeRepository.findById(id);
        if (envelope.isEmpty()) {
            throw new RuntimeException("Envelope not found with id: " + id);
        }
        envelope.get().getUser().setPassword(null);

        return ResponseEntity.ok(envelope.get());
    }

    public ResponseEntity<?> getAllEnvelopes() {
        List<Envelope> envelopes = envelopeRepository.findAll();
        logger.info("Retrieving all envelopes, Envelope count: {}", envelopes.size());
        envelopes.forEach(envelope -> envelope.getUser().setPassword(null));
        return ResponseEntity.ok(envelopes);
    }

    public ResponseEntity<?> deleteEnvelope(Integer id) {
        logger.info("Deleting envelope with id: {}", id);
        Optional<Envelope> envelope = envelopeRepository.findById(id);
        if (envelope.isEmpty()) {
            throw new RuntimeException("Envelope not found with id: " + id);
        }
        envelopeRepository.delete(envelope.get());
        return ResponseEntity.ok("Envelope deleted successfully");
    }

    @Transactional
    public ResponseEntity<?> transferEnvelope(TransferFundDTO transferFundDTO) {
        logger.info("Transferring amount from envelope with id: {} to envelope with id: {}",
                transferFundDTO.fromId(), transferFundDTO.toId());
        Optional<Envelope> fromEnvelope = envelopeRepository.findById(transferFundDTO.fromId());
        Optional<Envelope> toEnvelope = envelopeRepository.findById(transferFundDTO.toId());
        if (fromEnvelope.isEmpty() || toEnvelope.isEmpty()) {
            throw new RuntimeException("Envelope not found");
        }
        if (transferFundDTO.amount() <= 0) {
            throw new RuntimeException("Amount must be greater than 0");
        }
        if (fromEnvelope.get().getBalance() < transferFundDTO.amount()) {
            throw new RuntimeException("Insufficient funds in envelope with id: " + transferFundDTO.fromId());
        }

        if (toEnvelope.get().getBalance() + transferFundDTO.amount() > toEnvelope.get().getMaxLimit()) {
            throw new RuntimeException("Amount exceeds max limit of envelope with id: " + transferFundDTO.toId());
        }

        fromEnvelope.get().setBalance(fromEnvelope.get().getBalance() - transferFundDTO.amount());
        toEnvelope.get().setBalance(toEnvelope.get().getBalance() + transferFundDTO.amount());

        envelopeRepository.save(fromEnvelope.get());
        envelopeRepository.save(toEnvelope.get());

        Transaction fromTransaction = new Transaction();
        fromTransaction.setTitle(transferFundDTO.transactionTitle());
        fromTransaction.setTransactionDescription(transferFundDTO.transactionDescription());
        fromTransaction.setEnvelope(fromEnvelope.get());
        fromTransaction.setDatetime(LocalDateTime.now());
        fromTransaction.setCategory("Envelope Fund Transfer");

        // Make the transaction amount negative to indicate spending on frontend
        fromTransaction.setTransactionAmount(-transferFundDTO.amount());

        fromTransaction.setEnvelope(fromEnvelope.get());
        transactionService.createTransaction(fromTransaction);

        Transaction toTransaction = new Transaction();
        toTransaction.setTitle(transferFundDTO.transactionTitle());
        toTransaction.setTransactionDescription(transferFundDTO.transactionDescription());
        toTransaction.setEnvelope(fromEnvelope.get());
        toTransaction.setDatetime(LocalDateTime.now());
        toTransaction.setCategory("Envelope Fund Transfer");
        toTransaction.setTransactionAmount(transferFundDTO.amount());
        toTransaction.setEnvelope(toEnvelope.get());
        transactionService.createTransaction(toTransaction);

        EnvelopeHistory fromEnvelopeHistory = new EnvelopeHistory();
        fromEnvelopeHistory.setEnvelope(fromEnvelope.get());
        fromEnvelopeHistory.setEnvelopeAmount(transferFundDTO.amount());
        fromEnvelopeHistory.setTransaction(fromTransaction);
        envelopeHistoryService.createEnvelopeHistory(fromEnvelopeHistory);

        EnvelopeHistory toEnvelopeHistory = new EnvelopeHistory();
        toEnvelopeHistory.setEnvelope(toEnvelope.get());
        toEnvelopeHistory.setEnvelopeAmount(transferFundDTO.amount());
        toEnvelopeHistory.setTransaction(toTransaction);
        envelopeHistoryService.createEnvelopeHistory(toEnvelopeHistory);

        return ResponseEntity.ok("Amount transferred successfully");
    }

    @Transactional
    public ResponseEntity<?> allocateMoney(Integer envelopeId, Transaction transaction) {
        logger.info("Allocating amount to envelope with id: {}", envelopeId);
        Optional<Envelope> envelope = envelopeRepository.findById(envelopeId);
        if (envelope.isEmpty()) {
            throw new RuntimeException("Envelope not found with id: " + envelopeId);
        }
        if (transaction.getTransactionAmount() <= 0) {
            throw new RuntimeException("Amount must be greater than 0");
        }
        if (envelope.get().getBalance() + transaction.getTransactionAmount() > envelope.get().getMaxLimit()) {
            throw new RuntimeException("Amount exceeds max limit of envelope with id: " + envelopeId);
        }

        envelope.get().setBalance(envelope.get().getBalance() + transaction.getTransactionAmount());
        envelopeRepository.save(envelope.get());

        transaction.setEnvelope(envelope.get());
        transaction.setDatetime(LocalDateTime.now());
        transaction.setCategory(transaction.getCategory());
        Transaction savedTransaction = transactionService.createTransaction(transaction);

        EnvelopeHistory currentEnvelopeHistory = new EnvelopeHistory();
        currentEnvelopeHistory.setEnvelope(envelope.get());
        currentEnvelopeHistory.setEnvelopeAmount(envelope.get().getBalance());
        currentEnvelopeHistory.setTransaction(savedTransaction);
        envelopeHistoryService.createEnvelopeHistory(currentEnvelopeHistory);

        return ResponseEntity.ok(savedTransaction);
    }

    @Transactional
    public ResponseEntity<?> spendMoney(Integer envelopeId, Transaction transaction) {
        logger.info("Spending amount from envelope with id: {}", envelopeId);
        Optional<Envelope> envelope = envelopeRepository.findById(envelopeId);
        if (envelope.isEmpty()) {
            throw new RuntimeException("Envelope not found with id: " + envelopeId);
        }
        if (transaction.getTransactionAmount() <= 0) {
            throw new RuntimeException("Amount must be greater than 0");
        }
        if (envelope.get().getBalance() < transaction.getTransactionAmount()) {
            throw new RuntimeException("Insufficient funds in envelope with id: " + envelopeId);
        }

        envelope.get().setBalance(envelope.get().getBalance() - transaction.getTransactionAmount());
        envelopeRepository.save(envelope.get());

        transaction.setEnvelope(envelope.get());
        transaction.setDatetime(LocalDateTime.now());
        transaction.setCategory(transaction.getCategory());
        // Make the transaction amount negative to indicate spending on frontend
        transaction.setTransactionAmount(-transaction.getTransactionAmount());
        Transaction savedTransaction = transactionService.createTransaction(transaction);

        EnvelopeHistory currentEnvelopeHistory = new EnvelopeHistory();
        currentEnvelopeHistory.setEnvelope(envelope.get());
        currentEnvelopeHistory.setEnvelopeAmount(envelope.get().getBalance());
        currentEnvelopeHistory.setTransaction(savedTransaction);
        envelopeHistoryService.createEnvelopeHistory(currentEnvelopeHistory);

        return ResponseEntity.ok(savedTransaction);
    }

    public ResponseEntity<?> getEnvelopeByUserId(Integer userId) {
        logger.info("Retrieving envelope by user id: {}", userId);
        //Check if user exists
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("User does not exist");
        }
        List<Envelope> envelopes = envelopeRepository.findByUser_UserId(userId);
        envelopes.forEach(envelope -> envelope.getUser().setPassword(null));
        return ResponseEntity.ok(envelopes);
    }
}
