package com.vershd;

import com.vershd.loadbalancer.RegistrationResult;
import com.vershd.loadbalancer.RegistrationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RegistrationServiceTest {

    private RegistrationService registrationService = new RegistrationService();

    @Test
    public void testValidURL_whenCapacityIsNotFull_ShouldRegisterOK() {
        var result = registrationService.register("https://amazon.com");
        Assertions.assertEquals(RegistrationResult.OK, result);
    }

    @Test
    public void testValidURL_whenCapacityIsFull_shouldNotRegister() {
        for (int i = 0; i < 10; i++) {
            var result = registrationService.register("https://amazon.com" + i);
            Assertions.assertEquals(RegistrationResult.OK, result);
        }

        var result = registrationService.register("https://google.com");
        Assertions.assertEquals(RegistrationResult.CAPACITY_FULL, result);
    }

    @Test
    public void testMalformedURL_shouldNotRegister() {
        var result = registrationService.register("httpd://amazon.com");
        Assertions.assertEquals(RegistrationResult.MALFORMED_URL, result);
    }

    @Test
    public void testRegisterSameURL_shouldIgnore() {
        registrationService.register("https://amazon.com");
        var result = registrationService.register("https://amazon.com");
        Assertions.assertEquals(RegistrationResult.DUPLICATE, result);
    }
}
