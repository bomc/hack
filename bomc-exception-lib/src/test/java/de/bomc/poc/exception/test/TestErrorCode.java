package de.bomc.poc.exception.test;

import de.bomc.poc.exception.core.ErrorCode;

/**
 * <pre>
 * Describes the error codes for domain test.
 * This class must be implemented by all domains in their own project to set a unified error code and description.
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 */
public enum TestErrorCode implements ErrorCode {
    TEST_00101("Invalid arguments"),
    TEST_00102("Invalid parameters");
    private final String shortErrorCodeDescription;

    /**
     * Creates a new instance of <code>TestErrorCode</code>.
     * @param shortErrorCodeDescription the a short error code description.
     */
    TestErrorCode(final String shortErrorCodeDescription) {
        this.shortErrorCodeDescription = shortErrorCodeDescription;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getShortErrorCodeDescription() {
        return this.shortErrorCodeDescription;
    }
}
