package com.vershd.loadbalancer;

import com.vershd.loadbalancer.exceptions.LoadBalancerIsEmptyException;

import java.util.Random;

public class RandomLoadBalancer implements LoadBalancer {

    private final RegistrationService registrationService;

    public RandomLoadBalancer(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @Override
    public String get() {
        var addresses = registrationService.getAddresses();

        if (addresses == null || addresses.isEmpty()) {
            throw new LoadBalancerIsEmptyException();
        }

        Random random = new Random();
        int randomInt = random.nextInt(addresses.size());

        return addresses.get(randomInt);
    }
}
