package com.revature.project2.controllers;

import com.revature.project2.models.DTOs.EnvelopeDTO;
import com.revature.project2.models.DTOs.TransferFundDTO;
import com.revature.project2.models.Transaction;
import com.revature.project2.services.EnvelopeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/envelopes")
public class EnvelopeController {
    private final EnvelopeService envelopeService;

    public EnvelopeController(EnvelopeService envelopeService) {
        this.envelopeService = envelopeService;
    }

    @PostMapping
    public ResponseEntity<?> createEnvelope(@RequestBody EnvelopeDTO envelopeDTO) {
        return envelopeService.createEnvelope(envelopeDTO);
    }

    @GetMapping
    public ResponseEntity<?> getEnvelopes() {
        return envelopeService.getAllEnvelopes();
    }

    @GetMapping("/{envelopeId}")
    public ResponseEntity<?> getEnvelopeById(@PathVariable Integer envelopeId) {
        return envelopeService.getEnvelopeById(envelopeId);
    }

    @GetMapping("user/{userId}")
    public ResponseEntity<?> getEnvelopeByUserId(@PathVariable Integer userId) {
        return envelopeService.getEnvelopeByUserId(userId);
    }

    @DeleteMapping("/{envelopeId}")
    public ResponseEntity<?> deleteEnvelope(@PathVariable Integer envelopeId) {
        return envelopeService.deleteEnvelope(envelopeId);
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transferEnvelope(@RequestBody TransferFundDTO transferFundDTO) {
        return envelopeService.transferEnvelope(transferFundDTO);
    }

    @PostMapping("/allocate/{envelopeId}")
    public ResponseEntity<?> allocateMoney(@PathVariable Integer envelopeId, @RequestBody Transaction transaction) {
        return envelopeService.allocateMoney(envelopeId, transaction);
    }

    @PostMapping("/spend/{envelopeId}")
    public ResponseEntity<?> spendMoney(@PathVariable Integer envelopeId, @RequestBody Transaction transaction) {
        return envelopeService.spendMoney(envelopeId, transaction);
    }
}
