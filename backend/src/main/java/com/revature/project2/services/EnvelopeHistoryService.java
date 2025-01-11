package com.revature.project2.services;

import com.revature.project2.models.EnvelopeHistory;
import com.revature.project2.repositories.EnvelopeHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnvelopeHistoryService {
    private final EnvelopeHistoryRepository envelopeHistoryRepository;
    Logger logger = LoggerFactory.getLogger(EnvelopeHistoryService.class);

    public EnvelopeHistoryService(EnvelopeHistoryRepository envelopeHistoryRepository) {
        this.envelopeHistoryRepository = envelopeHistoryRepository;
    }

    public ResponseEntity<?> getAllEnvelopeHistory() {
        logger.info("Retrieving all envelope history");
        List<EnvelopeHistory> envelopeHistories = envelopeHistoryRepository.findAll();
        return ResponseEntity.ok(envelopeHistories);
    }

    public ResponseEntity<?> getEnvelopeHistoryByEnvelopeId(Integer envelopeId) {
        logger.info("Retrieving envelope history by id: " + envelopeId);
        List<EnvelopeHistory> envelopeHistory = envelopeHistoryRepository.findByEnvelope_EnvelopeId(envelopeId);
        logger.info("Retrieved envelope history with id: " + envelopeHistory);
        return ResponseEntity.ok(envelopeHistory);
    }

    public ResponseEntity<?> createEnvelopeHistory(EnvelopeHistory envelopeHistory) {
        logger.info("Creating envelope history: " + envelopeHistory);
        EnvelopeHistory savedEnvelopeHistory = envelopeHistoryRepository.save(envelopeHistory);
        return ResponseEntity.ok(savedEnvelopeHistory);
    }
}
