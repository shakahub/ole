package com.weshaka.ole.exceptions;

public class BeaconNotFoundException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -7722707590855568058L;

    public BeaconNotFoundException(String macId) {
        super("Could not find beacon '" + macId + "'.");
    }
}
