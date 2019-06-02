package de.bomc.poc.exception.core;

/**
 * A interface that describes the error code.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: 6790 $ $Author: tzdbmm $ $Date: 2016-07-19 09:06:34 +0200 (Di, 19 Jul 2016) $
 * @since 07.07.2016
 */
public interface ErrorCode {

    /**
     * @return a short error decription.
     */
    String getShortErrorCodeDescription();
}
