package de.bomc.poc.hrm.config.scheduling;

import org.springframework.boot.logging.LogLevel;
import org.springframework.util.ErrorHandler;

import de.bomc.poc.hrm.application.log.method.Loggable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HrmScheduledTaskExceptionHandler implements ErrorHandler {

	private static final String LOG_PREFIX = "HrmScheduledTaskExceptionHandler#";
	
	private volatile ErrorHandler existingErrorHandler;
	
	@Override
	@Loggable(result = false, params = true, value = LogLevel.DEBUG, time = true)
	public void handleError(final Throwable throwable) {
		
		if (throwable == null) {
			return;
		}
		
		// ______________________________________
		// DO SOMETHIN USEFULL WITH THE ERRORMESSAGE (e.g. notifying)
		// --------------------------------------
		log.error(LOG_PREFIX + "handleError - " + throwable.getMessage() + "\n", throwable);
		
        if (existingErrorHandler != null
                && !(existingErrorHandler instanceof HrmScheduledTaskExceptionHandler)) {
            existingErrorHandler.handleError(throwable);
        }
	}
	
    void setExistingErrorHandler(final ErrorHandler existingErrorHandler) {
        this.existingErrorHandler = existingErrorHandler;
    }

}
