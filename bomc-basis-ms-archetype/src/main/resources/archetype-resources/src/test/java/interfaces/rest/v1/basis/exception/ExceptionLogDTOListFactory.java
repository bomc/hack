#set($symbol_pound='#')
#set($symbol_dollar='$')
#set($symbol_escape='\' )
/**
 * Project: bomc-onion-architecture
 * <pre>
 *
 * Last change:
 *
 *  by: ${symbol_dollar}Author: bomc ${symbol_dollar}
 *
 *  date: ${symbol_dollar}Date: ${symbol_dollar}
 *
 *  revision: ${symbol_dollar}Revision: ${symbol_dollar}
 *
 * </pre>
 */
package ${package}.interfaces.rest.v1.basis.exception;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import ${package}.interfaces.rest.v1.basis.exception.dto.ExceptionLogDTO;

/**
 * A factory that creates a {@link ExceptionLogDTO} list for test cases.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 11.12.2018
 */
public final class ExceptionLogDTOListFactory {

    public static final Logger LOGGER = Logger.getLogger(ExceptionLogDTOListFactory.class);
    public static final String LOG_PREFIX = "ExceptionLogDTOListFactory${symbol_pound}";
    // _______________________________________________
    // Member variables
    // -----------------------------------------------
    public static final int YEAR = 2018;
    public static final int MONTH = 11;
    public static final int CREATE_DAY_OF_MONTH = 4;
    public static final int MODIFY_DAY_OF_MONTH = 11;
    public static final String CATEGORY = "category";
    public static final LocalDate CREATE_DATE = LocalDate.of(YEAR, MONTH, CREATE_DAY_OF_MONTH);
    public static final LocalDate MODIFY_DATE = LocalDate.of(YEAR, MONTH, MODIFY_DAY_OF_MONTH);
    public static final String CREATE_USER = "createUser";
    public static final String MODIFY_USER = "modifyUser";
    public static final String EXCEPTION_UUID = "exceptionUuid";
    public static final Long ID = 42L;
    public static final String RESPONSE_STATUS = Status.ACCEPTED.name();
    public static final String SHORT_ERROR_CODE_DESCRIPTION = "Describes an error";
    
    /**
     * Prevents instantiation.
     */
    private ExceptionLogDTOListFactory() {
        //
    }

    /**
     * Creates the given number of ExceptionLogDTOs.
     * 
     * @param countOfStoredDtos
     *            the given count.
     * @return the given number of ExceptionLogDTOs as list.
     */
    public static List<ExceptionLogDTO> createExceptionLogDTOList(final int countOfStoredEntites) {
        LOGGER.debug(LOG_PREFIX + "createExceptionLogDTOList${symbol_pound}createExceptionLogDTOList");

        final List<ExceptionLogDTO> list = new ArrayList<ExceptionLogDTO>();

        for (int i = 0; i < countOfStoredEntites; i++) {
            final ExceptionLogDTO exceptionLogDTO = new ExceptionLogDTO();
            exceptionLogDTO.setCategory(CATEGORY);
            exceptionLogDTO.setCreateDate(CREATE_DATE);
            exceptionLogDTO.setExceptionUuid(EXCEPTION_UUID + i);
            exceptionLogDTO.setResponseStatus(RESPONSE_STATUS);
            exceptionLogDTO.setShortErrorCodeDescription(SHORT_ERROR_CODE_DESCRIPTION);

            list.add(exceptionLogDTO);
        }

        return list;
    }
}
