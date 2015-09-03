package com.weshaka.ole.exceptions;

public class BeaconMacIdNotValidException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -2867245329728743075L;

    public BeaconMacIdNotValidException(String macId) {
        super("Beacon macId '" + macId + "' is not valid.");
    }
}

