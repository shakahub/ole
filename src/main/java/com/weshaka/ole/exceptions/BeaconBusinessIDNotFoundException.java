package com.weshaka.ole.exceptions;

public class BeaconBusinessIDNotFoundException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = -5301989744859331337L;

    public BeaconBusinessIDNotFoundException(String macId) {
        super("Could not find beacon business ID for mac ID '" + macId + "'.");
    }
}
