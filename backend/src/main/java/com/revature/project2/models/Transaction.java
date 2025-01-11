package com.revature.project2.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int transactionId;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String transactionDescription;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "envelopeId")
    private Envelope envelope;
    @Column(nullable = false)
    private LocalDateTime datetime;
    @Column(nullable = false)
    private String category;
    @Column(nullable = false)
    private double transactionAmount;
    @OneToMany(mappedBy = "transaction", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<EnvelopeHistory> envelopeHistories;

    private boolean audited = false;

    public Transaction() {
    }

    public Transaction(int transactionId, String title, String transactionDescription, Envelope envelope, LocalDateTime datetime, String category, double transactionAmount) {
        this.transactionId = transactionId;
        this.title = title;
        this.transactionDescription = transactionDescription;
        this.envelope = envelope;
        this.datetime = datetime;
        this.category = category;
        this.transactionAmount = transactionAmount;
    }

    public Transaction(int transactionId, String title, String transactionDescription, Envelope envelope, LocalDateTime datetime, String category, double transactionAmount, List<EnvelopeHistory> envelopeHistories, boolean audited) {
        this.transactionId = transactionId;
        this.title = title;
        this.transactionDescription = transactionDescription;
        this.envelope = envelope;
        this.datetime = datetime;
        this.category = category;
        this.transactionAmount = transactionAmount;
        this.envelopeHistories = envelopeHistories;
        this.audited = audited;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTransactionDescription() {
        return transactionDescription;
    }

    public void setTransactionDescription(String transactionDescription) {
        this.transactionDescription = transactionDescription;
    }

    public Envelope getEnvelope() {
        return envelope;
    }

    public void setEnvelope(Envelope envelope) {
        this.envelope = envelope;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public List<EnvelopeHistory> getEnvelopeHistories() {
        return envelopeHistories;
    }

    public void setEnvelopeHistories(List<EnvelopeHistory> envelopeHistories) {
        this.envelopeHistories = envelopeHistories;
    }

    public boolean isAudited() {
        return audited;
    }

    public void setAudited(boolean audited) {
        this.audited = audited;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", title='" + title + '\'' +
                ", transactionDescription='" + transactionDescription + '\'' +
                ", envelopeId=" + envelope +
                ", datetime=" + datetime +
                ", category='" + category + '\'' +
                ", transactionAmount=" + transactionAmount +
                '}';
    }
}
