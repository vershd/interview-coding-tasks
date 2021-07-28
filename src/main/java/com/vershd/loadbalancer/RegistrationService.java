package com.vershd.loadbalancer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RegistrationService {
    
    public List<String> getAddresses() {
        return addresses;
    }

    private List<String> addresses = new ArrayList<>();

    public RegistrationResult register(String address) {
        if (addresses.size() == 10) {
            return RegistrationResult.CAPACITY_FULL;
        }

        if (!isValidAddress(address)) {
            return RegistrationResult.MALFORMED_URL;
        }

        if (addresses.contains(address)) {
            return RegistrationResult.DUPLICATE;
        }

        addresses.add(address);

        return RegistrationResult.OK;
    }

    private boolean isValidAddress(String address) {
        try {
            URL url = new URL(address);
        } catch (MalformedURLException e) {
            return false;
        }

        return true;
    }
}
