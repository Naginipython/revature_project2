package com.revature.project2.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Entity
@Table(name = "envelopes")
public class Envelope {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int envelopeId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId")
    private User user;
    @Column(nullable = false)
    private String envelopeDescription;
    @Column(nullable = false)
    private double balance;
    @Column(nullable = false)
    private double maxLimit;
    @OneToMany(mappedBy = "envelope", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Transaction> transactions;
    @OneToMany(mappedBy = "envelope", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<EnvelopeHistory> envelopeHistories;

    public Envelope() {
    }

    public Envelope(int envelopeId, User user, String envelopeDescription, double balance, double maxLimit) {
        this.envelopeId = envelopeId;
        this.user = user;
        this.envelopeDescription = envelopeDescription;
        this.balance = balance;
        this.maxLimit = maxLimit;
    }

    @PreRemove
    private void removeFromUser(){
        this.user.getEnvelopes().remove(this);
    }

    public int getEnvelopeId() {
        return envelopeId;
    }

    public void setEnvelopeId(int envelopeId) {
        this.envelopeId = envelopeId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getEnvelopeDescription() {
        return envelopeDescription;
    }

    public void setEnvelopeDescription(String envelopeDescription) {
        this.envelopeDescription = envelopeDescription;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getMaxLimit() {
        return maxLimit;
    }

    public void setMaxLimit(double maxLimit) {
        this.maxLimit = maxLimit;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public List<EnvelopeHistory> getEnvelopeHistories() {
        return envelopeHistories;
    }

    public void setEnvelopeHistories(List<EnvelopeHistory> envelopeHistories) {
        this.envelopeHistories = envelopeHistories;
    }

    @Override
    public String toString() {
        return "Envelope{" +
                "envelopeId=" + envelopeId +
                ", user=" + user +
                ", envelopeDescription='" + envelopeDescription + '\'' +
                ", balance=" + balance +
                ", max=" + maxLimit +
                '}';
    }
}