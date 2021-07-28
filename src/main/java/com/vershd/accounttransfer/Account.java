package com.vershd.accounttransfer;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Account implements Comparable<Account> {

    private int id;

    private BigInteger amount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigInteger getAmount() {
        return amount;
    }

    public void setAmount(BigInteger amount) {
        this.amount = amount;
    }

    @Override
    public int compareTo(Account o) {
        if (this.id < o.id) {
            return -1;
        } else if (this.id > o.id) {
            return 1;
        }

        return 0;
    }
}