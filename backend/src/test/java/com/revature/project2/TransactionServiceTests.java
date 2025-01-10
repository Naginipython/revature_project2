package com.revature.project2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.project2.exceptions.BusinessException;
import com.revature.project2.models.DTOs.TransactionDTO;
import com.revature.project2.models.EnvelopeHistory;
import com.revature.project2.models.Transaction;
import com.revature.project2.models.mappers.TransactionDTOMapper;
import com.revature.project2.repositories.TransactionRepository;
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
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TransactionServiceTests {
    private TransactionRepository transactionRepository;
    private TransactionService transactionService;

    @BeforeEach
    void contextLoads(){
        transactionRepository = Mockito.mock(TransactionRepository.class);
        transactionService = new TransactionService(transactionRepository, new TransactionDTOMapper());
    }

    @Test
    void test_updateTransactionTitle(){
        Transaction transaction = new Transaction();
        transaction.setTransactionAmount(100);
        Transaction updateTransaction = new Transaction();
        updateTransaction.setTransactionAmount(100);
        updateTransaction.setTitle("newTitle");
        ArgumentCaptor<Transaction> transactionCapture = ArgumentCaptor.forClass(Transaction.class);
        when(transactionRepository.findById(0)).thenReturn(Optional.of(transaction));
        when(transactionRepository.findById(1)).thenReturn(Optional.empty());
        when(transactionRepository.save(transactionCapture.capture())).thenReturn(updateTransaction);
        //NOTE: incorrect output, but 1.) we just want to see what this saved 2.) returning getcapture output errors
        Transaction responseTransaction = transactionService.updateTransactionTitle(0, "newTitle");
        Transaction savedTransaction = transactionCapture.getValue();
        Assertions.assertTrue(responseTransaction.getTitle().equals("newTitle"));
        Assertions.assertTrue(responseTransaction.getTransactionAmount()==100);

        Assertions.assertTrue(savedTransaction.getTitle().equals("newTitle"));
        Assertions.assertTrue(savedTransaction.getTransactionAmount()==100);

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, ()->transactionService.updateTransactionTitle(1, "newTitle"));
    }

    @Test
    void test_updateTransactionDescription(){
        Transaction transaction = new Transaction();
        transaction.setTransactionAmount(100);
        Transaction updateTransaction = new Transaction();
        updateTransaction.setTransactionAmount(100);
        updateTransaction.setTransactionDescription("Desc");
        ArgumentCaptor<Transaction> transactionCapture = ArgumentCaptor.forClass(Transaction.class);
        when(transactionRepository.findById(0)).thenReturn(Optional.of(transaction));
        when(transactionRepository.findById(1)).thenReturn(Optional.empty());
        when(transactionRepository.save(transactionCapture.capture())).thenReturn(updateTransaction);
        //NOTE: incorrect output, but 1.) we just want to see what this saved 2.) returning getcapture output errors
        Transaction responseTransaction = transactionService.updateTransactionDescription(0, "Desc");
        Transaction savedTransaction = transactionCapture.getValue();
        Assertions.assertTrue(responseTransaction.getTransactionDescription().equals("Desc"));
        Assertions.assertTrue(responseTransaction.getTransactionAmount()==100);

        Assertions.assertTrue(savedTransaction.getTransactionDescription().equals("Desc"));
        Assertions.assertTrue(savedTransaction.getTransactionAmount()==100);

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, ()->transactionService.updateTransactionDescription(1, "newTitle"));
    }

    @Test
    void test_createTransaction_valid(){
        Transaction transaction = new Transaction();
        transaction.setTitle("Title");
        transaction.setTransactionDescription("Desc");
        ArgumentCaptor<Transaction> transactionCapture = ArgumentCaptor.forClass(Transaction.class);
        when(transactionRepository.save(transactionCapture.capture())).thenReturn(transaction);
        Transaction responseTransaction = transactionService.createTransaction(transaction);
        Transaction savedTransaction = transactionCapture.getValue();
        Assertions.assertTrue(responseTransaction.getTransactionDescription().equals("Desc"));
        Assertions.assertTrue(responseTransaction.getTitle().equals("Title"));
        Assertions.assertTrue(savedTransaction.getTransactionDescription().equals("Desc"));
        Assertions.assertTrue(savedTransaction.getTitle().equals("Title"));

    }

    @ParameterizedTest
    @CsvSource({",Desc","Title,"})
    void  test_createTransaction_invalid(String title, String desc){
        Transaction transaction = new Transaction();
        transaction.setTitle(title);
        transaction.setTransactionDescription(desc);

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, ()->transactionService.createTransaction(transaction));

    }

    @Test
    void test_getAllTransactions(){
        List<Transaction> tList = new ArrayList<>();
        Transaction transaction = new Transaction();
        transaction.setTitle("Title");
        transaction.setTransactionDescription("Desc");
        tList.add(transaction);
        when(transactionRepository.findAll()).thenReturn(tList);
        List<Transaction> responseList = transactionService.getAllTransactions();
        Assertions.assertEquals(1, responseList.size());
        Assertions.assertTrue("Desc".equals(responseList.get(0).getTransactionDescription()));
        Assertions.assertTrue("Title".equals(responseList.get(0).getTitle()));
    }

    // @Test
    // void test_updateTransactionCategory(){
    //     TransactionDTO updateTransaction = new TransactionDTO();
    //     updateTransaction.setCategory("Bills");
    //     Transaction transaction = new Transaction();
    //     transaction.setEnvelopeHistories(new ArrayList<EnvelopeHistory>());
    //     transaction.setTitle("Title");
    //     transaction.setTransactionDescription("Desc");
    //     Transaction outTransaction = new Transaction();
    //     outTransaction.setEnvelopeHistories(new ArrayList<EnvelopeHistory>());
    //     outTransaction.setTitle("Title");
    //     outTransaction.setTransactionDescription("Desc");
    //     outTransaction.setCategory("Bills");
    //     ArgumentCaptor<Transaction> transactionArgumentCaptor = ArgumentCaptor.forClass(Transaction.class);
    //     when(transactionRepository.findById(0)).thenReturn(Optional.of(transaction));
    //     when(transactionRepository.findById(1)).thenReturn(Optional.empty());
    //     when(transactionRepository.save(transactionArgumentCaptor.capture())).thenReturn(outTransaction);
    //     TransactionDTO responseTransactionDTO =  transactionService.updateTransactionCategory(0, updateTransaction);
    //     Transaction savedTransaction = transactionArgumentCaptor.getValue();
    //     Assertions.assertTrue(responseTransactionDTO.getCategory().equals("Bills"));
    //     Assertions.assertTrue(responseTransactionDTO.getTransactionDescription().equals("Desc"));
    //     Assertions.assertTrue(responseTransactionDTO.getTitle().equals("Title"));

    //     Assertions.assertTrue(savedTransaction.getCategory().equals("Bills"));
    //     Assertions.assertTrue(savedTransaction.getTransactionDescription().equals("Desc"));
    //     Assertions.assertTrue(savedTransaction.getTitle().equals("Title"));

    //     BusinessException ex1 = Assertions.assertThrows(BusinessException.class,  ()->transactionService.updateTransactionCategory(1, updateTransaction));
    //     updateTransaction.setCategory("");
    //     BusinessException ex2 = Assertions.assertThrows(BusinessException.class,  ()->transactionService.updateTransactionCategory(0, updateTransaction));
    // }

    @Test
    void test_getTransactionsByEnvelopeId(){
        List<Transaction> tList = new ArrayList<>();
        Transaction transaction = new Transaction();
        transaction.setTitle("Title");
        transaction.setTransactionDescription("Desc");
        tList.add(transaction);
        when(transactionRepository.findByEnvelope_EnvelopeId(0)).thenReturn(tList);
        when(transactionRepository.findByEnvelope_EnvelopeId(1)).thenReturn(new ArrayList<>());

        ResponseEntity<?> r1 = transactionService.getTransactionsByEnvelopeId(0);
        Assertions.assertEquals(HttpStatus.OK, r1.getStatusCode());
        List<Transaction> responseList = (List<Transaction>) r1.getBody();
        Assertions.assertEquals(1, responseList.size());
        Assertions.assertTrue("Desc".equals(responseList.get(0).getTransactionDescription()));
        Assertions.assertTrue("Title".equals(responseList.get(0).getTitle()));

        ResponseEntity<?> r2 = transactionService.getTransactionsByEnvelopeId(1);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, r2.getStatusCode());

    }


}
