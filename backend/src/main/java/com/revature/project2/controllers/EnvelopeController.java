package com.revature.project2.controllers;

import com.revature.project2.models.DTOs.EnvelopeDTO;
import com.revature.project2.models.DTOs.TransferFundDTO;
import com.revature.project2.services.EnvelopeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/envelope")
public class EnvelopeController {
    private final EnvelopeService envelopeService;

    public EnvelopeController(EnvelopeService envelopeService) {
        this.envelopeService = envelopeService;
    }

    @PostMapping
    public ResponseEntity<?> createEnvelope(@RequestBody EnvelopeDTO envelopeDTO) {
        return envelopeService.createEnvelope(envelopeDTO);
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transferEnvelope(@RequestBody TransferFundDTO transferFundDTO) {
        return envelopeService.transferEnvelope(transferFundDTO);
    }

    @GetMapping
    public ResponseEntity<?> getEnvelopes() {
        return envelopeService.getAllEnvelopes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEnvelopeById(@PathVariable Integer id) {
        return envelopeService.getEnvelopeById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEnvelope(@PathVariable Integer id) {
        return envelopeService.deleteEnvelope(id);
    }
}
