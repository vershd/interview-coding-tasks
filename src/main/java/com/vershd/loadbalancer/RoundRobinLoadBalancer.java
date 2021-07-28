package com.vershd.loadbalancer;

import com.vershd.loadbalancer.exceptions.LoadBalancerIsEmptyException;

import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalancer implements LoadBalancer {

    private final RegistrationService registrationService;
    private AtomicInteger counter = new AtomicInteger(0);

    public RoundRobinLoadBalancer(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @Override
    public String get() {

        var addresses = registrationService.getAddresses();

        if (addresses == null || addresses.isEmpty()) {
            throw new LoadBalancerIsEmptyException();
        }

        int value = counter.get();
        if (value >= addresses.size()) {
            counter.compareAndSet(value, 0);
        }

        return addresses.get(counter.getAndIncrement());
    }
}
