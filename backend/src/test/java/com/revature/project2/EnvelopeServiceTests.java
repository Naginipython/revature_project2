package com.revature.project2;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.revature.project2.models.DTOs.EnvelopeDTO;
import com.revature.project2.models.DTOs.TransferFundDTO;
import com.revature.project2.models.Envelope;
import com.revature.project2.models.EnvelopeHistory;
import com.revature.project2.models.Transaction;
import com.revature.project2.models.User;
import com.revature.project2.repositories.EnvelopeRepository;
import com.revature.project2.repositories.UserRepository;
import com.revature.project2.services.EnvelopeHistoryService;
import com.revature.project2.services.EnvelopeService;
import com.revature.project2.services.TransactionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class EnvelopeServiceTests {
    private EnvelopeRepository envelopeRepository;
    private UserRepository userRepository;
    private TransactionService transactionService;
    private EnvelopeHistoryService envelopeHistoryService;
    private EnvelopeService envelopeService;

    @BeforeEach
    public void loadContext(){
        envelopeRepository = Mockito.mock(EnvelopeRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        transactionService = Mockito.mock(TransactionService.class);
        envelopeHistoryService = Mockito.mock(EnvelopeHistoryService.class);

        envelopeService = new EnvelopeService(envelopeRepository, userRepository, transactionService,envelopeHistoryService);
    }

    @Test
    public void test_createEnvelope_valid(){
        EnvelopeDTO envelopeDTO = new EnvelopeDTO(100, "100", 100.0, 100.0);
        User user = new User();
        user.setUserId(100);
        when(userRepository.findById(100)).thenReturn(Optional.of(user));

        ArgumentCaptor<Envelope> envelopeArgumentCaptor = ArgumentCaptor.forClass(Envelope.class);

        Envelope outEnvelope = new Envelope();
        outEnvelope.setEnvelopeDescription("Saved Envelope");
        outEnvelope.setUser(user);
        when(envelopeRepository.save(envelopeArgumentCaptor.capture())).thenReturn(outEnvelope);

        ResponseEntity<?> r = envelopeService.createEnvelope(envelopeDTO);

        Assertions.assertEquals(HttpStatus.OK, r.getStatusCode());

        Envelope responseEnveleope = (Envelope) r.getBody();
        Envelope savedEnvelope = envelopeArgumentCaptor.getValue();

        Assertions.assertTrue("Saved Envelope".equals(responseEnveleope.getEnvelopeDescription()));
        Assertions.assertEquals(100, responseEnveleope.getUser().getUserId());

        Assertions.assertEquals(100.0, savedEnvelope.getBalance());
        Assertions.assertEquals(100.0, savedEnvelope.getMaxLimit());
        Assertions.assertEquals("100", savedEnvelope.getEnvelopeDescription());
        Assertions.assertEquals(100, savedEnvelope.getUser().getUserId());
    }

    @ParameterizedTest
    @CsvSource({"0,100,100.0,100.0", "100, ,100.0,100.0", "100,100,-1.0,100.0", "100,100,100.0,-1.0"})
    public void test_createEnvelope_invalid(int userId, String desc, double balance, double maxLimit) {
        if (desc==null){
            desc="";
        }
        EnvelopeDTO envelopeDTO = new EnvelopeDTO(userId, desc, balance, maxLimit);
        User user = new User();
        user.setUserId(100);
        when(userRepository.findById(100)).thenReturn(Optional.of(user));
        when(userRepository.findById(0)).thenReturn(Optional.empty());
        ResponseEntity<?> r = envelopeService.createEnvelope(envelopeDTO);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, r.getStatusCode());
        verifyNoInteractions(envelopeRepository);

    }



    @Test
    public void test_getEnvelopeById(){
        User user = new User();
        user.setUserId(100);
        Envelope envelope = new Envelope(100, user, "100", 100.0, 100.0);

        when(envelopeRepository.findById(100)).thenReturn(Optional.of(envelope));
        when(envelopeRepository.findById(0)).thenReturn(Optional.empty());
        ResponseEntity<?> r1 = envelopeService.getEnvelopeById(100);
        Envelope returnEnvelope =  (Envelope) r1.getBody();

        Assertions.assertEquals(100.0, returnEnvelope.getBalance());
        Assertions.assertEquals(100.0, returnEnvelope.getMaxLimit());
        Assertions.assertEquals("100", returnEnvelope.getEnvelopeDescription());
        Assertions.assertEquals(100, returnEnvelope.getUser().getUserId());

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, ()->envelopeService.getEnvelopeById(0));
    }
    @Test
    public void test_getAllEnvelopes(){
        List<Envelope> envList = new ArrayList<>();

        User user = new User();
        user.setUserId(100);
        Envelope envelope = new Envelope(100, user, "100", 100.0, 100.0);

        envList.add(envelope);
        when(envelopeRepository.findAll()).thenReturn(envList);
        ResponseEntity<?> r = envelopeService.getAllEnvelopes();
        Assertions.assertEquals(HttpStatus.OK, r.getStatusCode());
        List<Envelope> outList = (List<Envelope>) r.getBody();
        Assertions.assertEquals(1, outList.size());


        Assertions.assertEquals(100.0, outList.get(0).getBalance());
        Assertions.assertEquals(100.0, outList.get(0).getMaxLimit());
        Assertions.assertEquals("100", outList.get(0).getEnvelopeDescription());
        Assertions.assertEquals(100, outList.get(0).getUser().getUserId());


    }

    @Test
    public void test_deleteEnvelope(){
        User user = new User();
        user.setUserId(100);
        Envelope envelope = new Envelope(100, user, "100", 100.0, 100.0);

        when(envelopeRepository.findById(100)).thenReturn(Optional.of(envelope));
        when(envelopeRepository.findById(0)).thenReturn(Optional.empty());
        ArgumentCaptor<Envelope> envelopeArgumentCaptor = ArgumentCaptor.forClass(Envelope.class);
        doNothing().when(envelopeRepository).delete(envelopeArgumentCaptor.capture());

        ResponseEntity<?> r = envelopeService.deleteEnvelope(100);
        Assertions.assertEquals(HttpStatus.OK, r.getStatusCode());
        Envelope deletedEnvelope = envelopeArgumentCaptor.getValue();

        Assertions.assertEquals(100.0, deletedEnvelope.getBalance());
        Assertions.assertEquals(100.0, deletedEnvelope.getMaxLimit());
        Assertions.assertEquals("100", deletedEnvelope.getEnvelopeDescription());
        Assertions.assertEquals(100, deletedEnvelope.getUser().getUserId());

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, ()->envelopeService.getEnvelopeById(0));

    }

    /*
    TODO:
    each of the THREE remaining methods has valid and invalid
    valid, create two reasonable envelopes and transfer
    for each service used, just use an argument capture
    invalid, test without csv the "bad cases"

     */
    @Test
    public void test_transferEnvelope_valid(){
        User user = new User();
        user.setUserId(100);
        Envelope envelopeFrom = new Envelope(100, user, "100", 100.0, 200.0);
        Envelope envelopeTo = new Envelope(200, user, "100", 100.0, 200.0);
        TransferFundDTO transferFundDTO = new TransferFundDTO(100,200, "TransactionTitle", "TransactionDesc", 100.0);

        when(envelopeRepository.findById(0)).thenReturn(Optional.empty());
        when(envelopeRepository.findById(100)).thenReturn(Optional.of(envelopeFrom));
        when(envelopeRepository.findById(200)).thenReturn(Optional.of(envelopeTo));

        ArgumentCaptor<Envelope> envelopeArgumentCaptor = ArgumentCaptor.forClass(Envelope.class);
        ArgumentCaptor<EnvelopeHistory> envelopeHistoryArgumentCaptor = ArgumentCaptor.forClass(EnvelopeHistory.class);
        ArgumentCaptor<Transaction> transactionArgumentCaptor = ArgumentCaptor.forClass(Transaction.class);

        envelopeService.transferEnvelope(transferFundDTO);


        Mockito.verify(envelopeRepository, Mockito.times(2)).save(envelopeArgumentCaptor.capture());
        List<Envelope> envelopeList = envelopeArgumentCaptor.getAllValues();

        if ((envelopeList.get(0).getEnvelopeId()==200)&&(envelopeList.get(1).getEnvelopeId()==100)){
            Collections.swap(envelopeList,0,1);
        }
        if (!(envelopeList.get(0).getEnvelopeId()==100)||!(envelopeList.get(1).getEnvelopeId()==200)){
            //transactions failed to be created
            Assertions.assertFalse(true);
        }
        Envelope envFrom = envelopeList.get(0);
        Envelope envTo = envelopeList.get(1);
        Assertions.assertEquals(0.0, envFrom.getBalance());
        Assertions.assertEquals(200.0, envTo.getBalance());


        Mockito.verify(transactionService, Mockito.times(2)).createTransaction(transactionArgumentCaptor.capture());
        List<Transaction> transactionList = transactionArgumentCaptor.getAllValues();

        if ((transactionList.get(0).getEnvelope().getEnvelopeId()==200)&&(transactionList.get(1).getEnvelope().getEnvelopeId()==100)){
            Collections.swap(transactionList,0,1);
        }
        if (!(transactionList.get(0).getEnvelope().getEnvelopeId()==100)||!(transactionList.get(1).getEnvelope().getEnvelopeId()==200)){
            //transactions failed to be created
            Assertions.assertFalse(true);
        }
        Transaction transTo = transactionList.get(0);
        Transaction transFrom = transactionList.get(1);
        Assertions.assertEquals(-100.0, transTo.getTransactionAmount());
        Assertions.assertTrue("TransactionTitle".equals(transTo.getTitle()));
        Assertions.assertTrue("TransactionDesc".equals(transTo.getTransactionDescription()));
        Assertions.assertEquals(100.0, transFrom.getTransactionAmount());
        Assertions.assertTrue("TransactionTitle".equals(transFrom.getTitle()));
        Assertions.assertTrue("TransactionDesc".equals(transFrom.getTransactionDescription()));


        Mockito.verify(envelopeHistoryService, Mockito.times(2)).createEnvelopeHistory(envelopeHistoryArgumentCaptor.capture());
        List<EnvelopeHistory> envelopeHistoryList = envelopeHistoryArgumentCaptor.getAllValues();

        if ((envelopeHistoryList.get(0).getEnvelope().getEnvelopeId()==200)&&(envelopeHistoryList.get(1).getEnvelope().getEnvelopeId()==100)){
            Collections.swap(envelopeList,0,1);
        }
        if (!(envelopeHistoryList.get(0).getEnvelope().getEnvelopeId()==100)||!(envelopeHistoryList.get(1).getEnvelope().getEnvelopeId()==200)){
            //transactions failed to be created
            Assertions.assertFalse(true);
        }
    }

    @Test
    public void test_transferEnvelope_invalid_noFrom(){

        User user = new User();
        user.setUserId(100);
        Envelope envelopeFrom = new Envelope(100, user, "100", 100.0, 200.0);
        Envelope envelopeTo = new Envelope(200, user, "100", 100.0, 200.0);
        TransferFundDTO transferFundDTO = new TransferFundDTO(0,200, "TransactionTitle", "TransactionDesc", 100.0);

        when(envelopeRepository.findById(0)).thenReturn(Optional.empty());
        when(envelopeRepository.findById(100)).thenReturn(Optional.of(envelopeFrom));
        when(envelopeRepository.findById(200)).thenReturn(Optional.of(envelopeTo));

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, ()->envelopeService.transferEnvelope(transferFundDTO));
        Mockito.verify(transactionService, never()).createTransaction(Mockito.any(Transaction.class));
        Mockito.verify(envelopeRepository, never()).save(Mockito.any(Envelope.class));
        Mockito.verify(envelopeHistoryService, never()).createEnvelopeHistory(Mockito.any(EnvelopeHistory.class));
    }


    @Test
    public void test_transferEnvelope_invalid_noTo(){

        User user = new User();
        user.setUserId(100);
        Envelope envelopeFrom = new Envelope(100, user, "100", 100.0, 200.0);
        Envelope envelopeTo = new Envelope(200, user, "100", 100.0, 200.0);
        TransferFundDTO transferFundDTO = new TransferFundDTO(100,0, "TransactionTitle", "TransactionDesc", 100.0);

        when(envelopeRepository.findById(0)).thenReturn(Optional.empty());
        when(envelopeRepository.findById(100)).thenReturn(Optional.of(envelopeFrom));
        when(envelopeRepository.findById(200)).thenReturn(Optional.of(envelopeTo));

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, ()->envelopeService.transferEnvelope(transferFundDTO));
        Mockito.verify(transactionService, never()).createTransaction(Mockito.any(Transaction.class));
        Mockito.verify(envelopeRepository, never()).save(Mockito.any(Envelope.class));
        Mockito.verify(envelopeHistoryService, never()).createEnvelopeHistory(Mockito.any(EnvelopeHistory.class));
    }


    @Test
    public void test_transferEnvelope_invalid_notEnoughBalance(){
        User user = new User();
        user.setUserId(100);
        Envelope envelopeFrom = new Envelope(100, user, "100", 100.0, 200.0);
        Envelope envelopeTo = new Envelope(200, user, "100", 100.0, 300.0);
        TransferFundDTO transferFundDTO = new TransferFundDTO(0,200, "TransactionTitle", "TransactionDesc", 200.0);

        when(envelopeRepository.findById(0)).thenReturn(Optional.empty());
        when(envelopeRepository.findById(100)).thenReturn(Optional.of(envelopeFrom));
        when(envelopeRepository.findById(200)).thenReturn(Optional.of(envelopeTo));

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, ()->envelopeService.transferEnvelope(transferFundDTO));
        Mockito.verify(transactionService, never()).createTransaction(Mockito.any(Transaction.class));
        Mockito.verify(envelopeRepository, never()).save(Mockito.any(Envelope.class));
        Mockito.verify(envelopeHistoryService, never()).createEnvelopeHistory(Mockito.any(EnvelopeHistory.class));
    }


    @Test
    public void test_transferEnvelope_invalid_maxLimLow(){
        User user = new User();
        user.setUserId(100);
        Envelope envelopeFrom = new Envelope(100, user, "100", 100.0, 200.0);
        Envelope envelopeTo = new Envelope(200, user, "100", 100.0, 100.0);
        TransferFundDTO transferFundDTO = new TransferFundDTO(100,200, "TransactionTitle", "TransactionDesc", 100.0);

        when(envelopeRepository.findById(0)).thenReturn(Optional.empty());
        when(envelopeRepository.findById(100)).thenReturn(Optional.of(envelopeFrom));
        when(envelopeRepository.findById(200)).thenReturn(Optional.of(envelopeTo));

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, ()->envelopeService.transferEnvelope(transferFundDTO));
        Mockito.verify(transactionService, never()).createTransaction(Mockito.any(Transaction.class));
        Mockito.verify(envelopeRepository, never()).save(Mockito.any(Envelope.class));
        Mockito.verify(envelopeHistoryService, never()).createEnvelopeHistory(Mockito.any(EnvelopeHistory.class));
    }

    @Test
    public void test_allocateMoney_valid(){
        User user = new User();
        user.setUserId(100);
        Envelope envelope = new Envelope(100, user, "100", 100.0, 200.0);

        Envelope outEnvelope = new Envelope();
        outEnvelope.setUser(user);
        outEnvelope.setEnvelopeDescription("saveEnvelope");

        Transaction transaction = new Transaction();
        transaction.setTransactionAmount(100.0);
        transaction.setCategory("Bills");
        transaction.setTitle("allocateTransaction");

        Transaction saveTransaction = new Transaction();
        saveTransaction.setTitle("saveTransaction");

        when(envelopeRepository.findById(100)).thenReturn(Optional.of(envelope));

        ArgumentCaptor<Envelope> envelopeArgumentCaptor = ArgumentCaptor.forClass(Envelope.class);
        ArgumentCaptor<Transaction> transactionArgumentCaptor = ArgumentCaptor.forClass(Transaction.class);

        when(envelopeRepository.save(envelopeArgumentCaptor.capture())).thenReturn(outEnvelope);
        when(transactionService.createTransaction(transactionArgumentCaptor.capture())).thenReturn(saveTransaction);

        ResponseEntity r = envelopeService.allocateMoney(100, transaction);

        Assertions.assertEquals(HttpStatus.OK, r.getStatusCode());
        Envelope saveEnvelope = envelopeArgumentCaptor.getValue();
        Transaction createTransaction = transactionArgumentCaptor.getValue();
        Transaction returnTransaction = (Transaction) r.getBody();
        Assertions.assertEquals(100, saveEnvelope.getEnvelopeId());
        Assertions.assertEquals(200.0, saveEnvelope.getBalance());
        Assertions.assertEquals(100.0, createTransaction.getTransactionAmount());
        Assertions.assertTrue("saveTransaction".equals(returnTransaction.getTitle()));
    }

    @Test
    public void test_allocateMoney_invalid_noUser(){
        User user = new User();
        user.setUserId(100);
        Envelope envelope = new Envelope(100, user, "100", 100.0, 200.0);

        Transaction transaction = new Transaction();
        transaction.setTransactionAmount(100.0);
        transaction.setCategory("Bills");
        transaction.setTitle("allocateTransaction");

        when(envelopeRepository.findById(0)).thenReturn(Optional.empty());
        when(envelopeRepository.findById(100)).thenReturn(Optional.of(envelope));

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, ()->envelopeService.allocateMoney(0, transaction));

        verify(envelopeRepository, never()).save(Mockito.any(Envelope.class));
        verify(transactionService, never()).createTransaction(Mockito.any(Transaction.class));
    }

    @Test
    public void test_allocateMoney_invalid_maxLimTooLow(){
        User user = new User();
        user.setUserId(100);
        Envelope envelope = new Envelope(100, user, "100", 100.0, 200.0);

        Transaction transaction = new Transaction();
        transaction.setTransactionAmount(10000000000.0);
        transaction.setCategory("Bills");
        transaction.setTitle("allocateTransaction");

        when(envelopeRepository.findById(0)).thenReturn(Optional.empty());
        when(envelopeRepository.findById(100)).thenReturn(Optional.of(envelope));

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, ()->envelopeService.allocateMoney(100, transaction));

        verify(envelopeRepository, never()).save(Mockito.any(Envelope.class));
        verify(transactionService, never()).createTransaction(Mockito.any(Transaction.class));
    }

    @Test
    public void test_allocateMoney_invalid_noTransfer(){
        User user = new User();
        user.setUserId(100);
        Envelope envelope = new Envelope(100, user, "100", 100.0, 200.0);

        Transaction transaction = new Transaction();
        transaction.setTransactionAmount(0.0);
        transaction.setCategory("Bills");
        transaction.setTitle("allocateTransaction");

        when(envelopeRepository.findById(0)).thenReturn(Optional.empty());
        when(envelopeRepository.findById(100)).thenReturn(Optional.of(envelope));

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, ()->envelopeService.allocateMoney(100, transaction));

        verify(envelopeRepository, never()).save(Mockito.any(Envelope.class));
        verify(transactionService, never()).createTransaction(Mockito.any(Transaction.class));
    }

    @Test
    public void test_spendMoney_valid(){
        User user = new User();
        user.setUserId(100);
        Envelope envelope = new Envelope(100, user, "100", 100.0, 200.0);

        Envelope outEnvelope = new Envelope();
        outEnvelope.setUser(user);
        outEnvelope.setEnvelopeDescription("saveEnvelope");

        Transaction transaction = new Transaction();
        transaction.setTransactionAmount(100.0);
        transaction.setCategory("Bills");
        transaction.setTitle("allocateTransaction");

        Transaction saveTransaction = new Transaction();
        saveTransaction.setTitle("saveTransaction");

        when(envelopeRepository.findById(100)).thenReturn(Optional.of(envelope));

        ArgumentCaptor<Envelope> envelopeArgumentCaptor = ArgumentCaptor.forClass(Envelope.class);
        ArgumentCaptor<Transaction> transactionArgumentCaptor = ArgumentCaptor.forClass(Transaction.class);

        when(envelopeRepository.save(envelopeArgumentCaptor.capture())).thenReturn(outEnvelope);
        when(transactionService.createTransaction(transactionArgumentCaptor.capture())).thenReturn(saveTransaction);

        ResponseEntity r = envelopeService.spendMoney(100, transaction);

        Assertions.assertEquals(HttpStatus.OK, r.getStatusCode());
        Envelope saveEnvelope = envelopeArgumentCaptor.getValue();
        Transaction createTransaction = transactionArgumentCaptor.getValue();
        Transaction returnTransaction = (Transaction) r.getBody();
        Assertions.assertEquals(100, saveEnvelope.getEnvelopeId());
        Assertions.assertEquals(0.0, saveEnvelope.getBalance());
        Assertions.assertEquals(-100.0, createTransaction.getTransactionAmount());
        Assertions.assertTrue("saveTransaction".equals(returnTransaction.getTitle()));
    }

    @Test
    public void test_spendMoney_invalid_noUser(){
        User user = new User();
        user.setUserId(100);
        Envelope envelope = new Envelope(100, user, "100", 100.0, 200.0);

        Transaction transaction = new Transaction();
        transaction.setTransactionAmount(100.0);
        transaction.setCategory("Bills");
        transaction.setTitle("allocateTransaction");

        when(envelopeRepository.findById(0)).thenReturn(Optional.empty());
        when(envelopeRepository.findById(100)).thenReturn(Optional.of(envelope));

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, ()->envelopeService.spendMoney(0, transaction));

        verify(envelopeRepository, never()).save(Mockito.any(Envelope.class));
        verify(transactionService, never()).createTransaction(Mockito.any(Transaction.class));
    }

    @Test
    public void test_spendMoney_invalid_BalanceTooLow(){
        User user = new User();
        user.setUserId(100);
        Envelope envelope = new Envelope(100, user, "100", 100.0, 200.0);

        Transaction transaction = new Transaction();
        transaction.setTransactionAmount(10000000000.0);
        transaction.setCategory("Bills");
        transaction.setTitle("allocateTransaction");

        when(envelopeRepository.findById(0)).thenReturn(Optional.empty());
        when(envelopeRepository.findById(100)).thenReturn(Optional.of(envelope));

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, ()->envelopeService.allocateMoney(100, transaction));

        verify(envelopeRepository, never()).save(Mockito.any(Envelope.class));
        verify(transactionService, never()).createTransaction(Mockito.any(Transaction.class));
    }

    @Test
    public void test_spendMoney_invalid_noTransfer(){
        User user = new User();
        user.setUserId(100);
        Envelope envelope = new Envelope(100, user, "100", 100.0, 200.0);

        Transaction transaction = new Transaction();
        transaction.setTransactionAmount(0.0);
        transaction.setCategory("Bills");
        transaction.setTitle("allocateTransaction");

        when(envelopeRepository.findById(0)).thenReturn(Optional.empty());
        when(envelopeRepository.findById(100)).thenReturn(Optional.of(envelope));

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, ()->envelopeService.allocateMoney(100, transaction));

        verify(envelopeRepository, never()).save(Mockito.any(Envelope.class));
        verify(transactionService, never()).createTransaction(Mockito.any(Transaction.class));
    }


}
