package com.revature.project2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.project2.models.EnvelopeHistory;
import com.revature.project2.repositories.EnvelopeHistoryRepository;
import com.revature.project2.repositories.EnvelopeRepository;
import com.revature.project2.repositories.TransactionRepository;
import com.revature.project2.repositories.UserRepository;
import com.revature.project2.services.AuthenticationService;
import com.revature.project2.services.EnvelopeHistoryService;
import com.revature.project2.services.EnvelopeService;
import org.apache.catalina.core.ApplicationContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class EnvelopeHistoryServiceTests {
    private EnvelopeHistoryRepository envelopeHistoryRepository;
    private EnvelopeHistoryService envelopeHistoryService;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void contextLoads() {
        envelopeHistoryRepository = Mockito.mock(EnvelopeHistoryRepository.class);
        envelopeHistoryService = new EnvelopeHistoryService(envelopeHistoryRepository);
    }

    @Test
    void test_getAllEnvelopeHistory(){
        List<EnvelopeHistory> mockedList = new ArrayList<>();
        EnvelopeHistory envelopeHistory = new EnvelopeHistory();
        mockedList.add(new EnvelopeHistory());
        when(envelopeHistoryRepository.findAll()).thenReturn(mockedList);
        ResponseEntity<?> responseOutput = envelopeHistoryService.getAllEnvelopeHistory();
        assertEquals(HttpStatus.OK, responseOutput.getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, responseOutput.getStatusCode());

        List<EnvelopeHistory> outputResponse = (List<EnvelopeHistory>) responseOutput.getBody();
        Assertions.assertEquals(1, outputResponse.size());
        Assertions.assertTrue(envelopeHistory.toString().equals(outputResponse.get(0).toString()));

        Mockito.verify(envelopeHistoryRepository).findAll();
        Mockito.verifyNoMoreInteractions(envelopeHistoryRepository);
    }

    @Test
    void test_getEnvelopeHistoryByEnvelopeId_idFound(){
        List<EnvelopeHistory> mockedList = new ArrayList<>();
        EnvelopeHistory envelopeHistory = new EnvelopeHistory();
        mockedList.add(new EnvelopeHistory());
        when(envelopeHistoryRepository.findByEnvelope_EnvelopeId(0)).thenReturn(mockedList);
        ResponseEntity<?> responseOutput = envelopeHistoryService.getEnvelopeHistoryByEnvelopeId(0);
        Assertions.assertEquals(HttpStatus.OK, responseOutput.getStatusCode());

        List<EnvelopeHistory> outputResponse = (List<EnvelopeHistory>) responseOutput.getBody();
        Assertions.assertEquals(1, outputResponse.size());
        Assertions.assertTrue(envelopeHistory.toString().equals(outputResponse.get(0).toString()));
    }

    //@Test
    void test_getEnvelopeHistoryByEnvelopeId_idNotFound(){
        List<EnvelopeHistory> mockedList = new ArrayList<>();

        when(envelopeHistoryRepository.findByEnvelope_EnvelopeId(0)).thenReturn(mockedList);
        ResponseEntity<?> responseOutput = envelopeHistoryService.getEnvelopeHistoryByEnvelopeId(0);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseOutput.getStatusCode());

        String outputResponse = (String) responseOutput.getBody();

        Assertions.assertTrue(outputResponse.startsWith("Envelope history with Envelope id"));
        Assertions.assertTrue(outputResponse.endsWith("does not exist"));
    }

    @Test
    void test_createEnvelopeHistory(){
        EnvelopeHistory envelopeHistory = new EnvelopeHistory();
        when(envelopeHistoryRepository.save(envelopeHistory)).thenReturn(envelopeHistory);
        ResponseEntity<?> responseOutput = envelopeHistoryService.createEnvelopeHistory(envelopeHistory);
        Assertions.assertEquals(HttpStatus.OK, responseOutput.getStatusCode());
        EnvelopeHistory outputEnvelopeHistory = (EnvelopeHistory) responseOutput.getBody();
        Assertions.assertTrue(outputEnvelopeHistory.toString().equals(envelopeHistory.toString()));
    }
}
