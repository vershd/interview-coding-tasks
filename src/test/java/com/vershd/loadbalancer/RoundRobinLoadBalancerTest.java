package com.vershd;

import com.vershd.loadbalancer.RegistrationService;
import com.vershd.loadbalancer.RoundRobinLoadBalancer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.stream.IntStream;

public class RoundRobinLoadBalancerTest {

    private RegistrationService registrationService = new RegistrationService();
    private RoundRobinLoadBalancer loadBalancer = new RoundRobinLoadBalancer(registrationService);

    @Test
    public void testMultipleGets_whenAddressesWereRegistered_ShouldReturnAllOfThem() {
        int registrationAttempts = 2;
        for (int i = 0; i < registrationAttempts; i++) {
            registrationService.register("https://google.com" + i);
        }

        var result = loadBalancer.get();
        Assertions.assertEquals("https://google.com0", result);

        result = loadBalancer.get();
        Assertions.assertEquals("https://google.com1", result);

        result = loadBalancer.get();
        Assertions.assertEquals("https://google.com0", result);
    }

    @Test
    public void testConcurrentMultipleRequests_whenAddressesWereRegistered_ShouldReturnSequentially() {
        IntStream.range(0, 10).forEach(i -> registrationService.register("https://google.com" + i));

        var result = new HashSet<String>();
        IntStream.range(0, 10)
                .parallel()
                .forEach(i -> {
                    var address = loadBalancer.get();
                    result.add(address);
                });

        Assertions.assertEquals(10, result.size());

        System.out.println(result);
    }
}
