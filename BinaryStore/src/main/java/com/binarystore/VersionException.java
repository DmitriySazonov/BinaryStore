package com.binarystore;

public class VersionException extends Exception {

    public VersionException(final String message) {
        super(message);
    }

    public VersionException(
            final int expectedVersion,
            final int actualVersion
    ) {
        super("Expected version " + expectedVersion + " but was found " + actualVersion);
    }
}
