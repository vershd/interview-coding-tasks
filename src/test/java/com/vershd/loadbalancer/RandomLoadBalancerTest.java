package com.vershd;

import com.vershd.loadbalancer.RandomLoadBalancer;
import com.vershd.loadbalancer.RegistrationService;
import com.vershd.loadbalancer.exceptions.LoadBalancerIsEmptyException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.fail;

public class RandomLoadBalancerTest {

    private RegistrationService registrationService = new RegistrationService();
    private RandomLoadBalancer randomLoadBalancer = new RandomLoadBalancer(registrationService);

    @Test
    public void testGetRandomAddress_WhenAddressesAreRegistered_ShouldReturnExistingAddress() {
        for (int i = 0; i < 5; i++) {
            registrationService.register("https://google.com" + i);
        }


        var results = new HashSet<>();
        for (int i = 0; i < 20; i++) {
            results.add(randomLoadBalancer.get());
        }

        Assertions.assertTrue(results.size() > 1);

    }

    @Test
    public void testGetAddress_whenNoAddressesExist_shouldThrow_LoadBalancerIsEmptyException() {
        try {
            randomLoadBalancer.get();
        } catch (LoadBalancerIsEmptyException lbe) {
            System.out.println("We're good");
            return;
        }

        fail();
    }
}
