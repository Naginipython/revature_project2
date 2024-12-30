package com.revature.project2.services;

import com.revature.project2.models.DTOs.EnvelopeDTO;
import com.revature.project2.models.DTOs.TransferFundDTO;
import com.revature.project2.models.Envelope;
import com.revature.project2.models.User;
import com.revature.project2.repositories.EnvelopeRepository;
import com.revature.project2.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EnvelopeService {
    private final Logger logger = LoggerFactory.getLogger(EnvelopeService.class);
    private final EnvelopeRepository envelopeRepository;
    private final UserRepository userRepository;

    public EnvelopeService(EnvelopeRepository envelopeRepository, UserRepository userRepository) {
        this.envelopeRepository = envelopeRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> createEnvelope(EnvelopeDTO envelopeDTO) {
        if (envelopeDTO.envelopeDescription().isEmpty() || envelopeDTO.amount() == null ||
                envelopeDTO.maxLimit() == null || envelopeDTO.userId() == null) {
            return ResponseEntity.badRequest().body("Envelope fields cannot be null");
        }
        if (envelopeDTO.amount() < 0 || envelopeDTO.maxLimit() < 0) {
            return ResponseEntity.badRequest().body("Amount and max limit cannot be negative");
        }
        Optional<User> user = userRepository.findById(envelopeDTO.userId());
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("User does not exist");
        }

        Envelope envelope = new Envelope();
        envelope.setEnvelopeDescription(envelopeDTO.envelopeDescription());
        envelope.setAmount(envelopeDTO.amount());
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
        if (fromEnvelope.get().getAmount() < transferFundDTO.amount()) {
            throw new RuntimeException("Insufficient funds in envelope with id: " + transferFundDTO.fromId());
        }

        if (toEnvelope.get().getAmount() + transferFundDTO.amount() > toEnvelope.get().getMaxLimit()) {
            throw new RuntimeException("Amount exceeds max limit of envelope with id: " + transferFundDTO.toId());
        }

        fromEnvelope.get().setAmount(fromEnvelope.get().getAmount() - transferFundDTO.amount());
        toEnvelope.get().setAmount(toEnvelope.get().getAmount() + transferFundDTO.amount());

        envelopeRepository.save(fromEnvelope.get());
        envelopeRepository.save(toEnvelope.get());

        return ResponseEntity.ok("Amount transferred successfully");
    }
}