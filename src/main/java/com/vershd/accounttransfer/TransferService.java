package com.vershd.accounttransfer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.locks.ReentrantLock;

public class TransferService {

    public synchronized void transferSync(Account from, Account to, BigInteger amount) {
        from.setAmount(from.getAmount().subtract(amount));
        to.setAmount(to.getAmount().add(amount));
    }

    private ReentrantLock bankLock = new ReentrantLock();

    public void transferLocks(Account from, Account to, BigInteger amount) {
        bankLock.lock();
        try {
            from.setAmount(from.getAmount().subtract(amount));
            to.setAmount(to.getAmount().add(amount));
        } finally {
            bankLock.unlock();
        }
    }

    public void transferManualSync(Account from, Account to, BigInteger amount) {
        //have to obtain locks in the same order every time
        Account fromLock = from.compareTo(to) <= 0 ? from : to;
        Account toLock = from.compareTo(to) <= 0 ? to : from;
        synchronized (fromLock) {
            synchronized (toLock) {
                try {
                    from.setAmount(from.getAmount().subtract(amount));
                    Thread.sleep(10);
                    to.setAmount(to.getAmount().add(amount));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
