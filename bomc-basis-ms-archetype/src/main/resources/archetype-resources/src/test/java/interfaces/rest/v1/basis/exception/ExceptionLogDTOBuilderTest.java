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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.LocalDate;
import java.util.UUID;

import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;

import de.bomc.poc.exception.core.ErrorCode;

import ${package}.CategoryBasisUnitTest;
import ${package}.interfaces.rest.v1.basis.exception.dto.ExceptionLogDTO;

/**
 * Tests the {@link ExceptionLogDTO}${symbol_pound}Builder.
 * 
 * <pre>
 *     mvn clean install -Dtest=ExceptionLogDTOBuilderTest
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 07.12.2018
 */
@Category(CategoryBasisUnitTest.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExceptionLogDTOBuilderTest {

    private static final String LOG_PREFIX = "ExceptionLogDTOBuilderTest${symbol_pound}";
    private static final Logger LOGGER = Logger.getLogger(ExceptionLogDTOBuilderTest.class);
    // _______________________________________________
    // Test constants
    // -----------------------------------------------
    private static final String CATEGORY = ErrorCode.Category.ERROR.name();
    private static final String EXCEPTION_UUID = UUID.randomUUID().toString();
    private static final String RESPONSE_STATUS = Status.ACCEPTED.name();
    private static final LocalDate CREATE_DATE = LocalDate.now();
    private static final String SHORT_ERROR_CODE_DESCRIPTION = "Short error description";

    @Test
    public void test010_builder_pass() {
        LOGGER.debug(LOG_PREFIX + "test010_builder_pass");

        final ExceptionLogDTO exceptionLogDTO = ExceptionLogDTO.category(CATEGORY).exceptionUuid(EXCEPTION_UUID).responseStatus(RESPONSE_STATUS)
                .createDate(CREATE_DATE).shortErrorCodeDescription(SHORT_ERROR_CODE_DESCRIPTION).build();
        
        assertThat(exceptionLogDTO.getCategory(), equalTo(CATEGORY));
        assertThat(exceptionLogDTO.getCreateDate(), equalTo(CREATE_DATE));
        assertThat(exceptionLogDTO.getExceptionUuid(), equalTo(EXCEPTION_UUID));
        assertThat(exceptionLogDTO.getResponseStatus(), equalTo(RESPONSE_STATUS));
        assertThat(exceptionLogDTO.getShortErrorCodeDescription(), equalTo(SHORT_ERROR_CODE_DESCRIPTION));
    }

    @Test
    public void test020_builderGetter_pass() {
        LOGGER.debug(LOG_PREFIX + "test020_builderGetter_pass");

        final ExceptionLogDTO exceptionLogDTO = new ExceptionLogDTO();
        exceptionLogDTO.setCategory(CATEGORY);
        exceptionLogDTO.setCreateDate(CREATE_DATE);
        exceptionLogDTO.setExceptionUuid(EXCEPTION_UUID);
        exceptionLogDTO.setResponseStatus(RESPONSE_STATUS);
        exceptionLogDTO.setShortErrorCodeDescription(SHORT_ERROR_CODE_DESCRIPTION);
        
        assertThat(exceptionLogDTO.getCategory(), equalTo(CATEGORY));
        assertThat(exceptionLogDTO.getCreateDate(), equalTo(CREATE_DATE));
        assertThat(exceptionLogDTO.getExceptionUuid(), equalTo(EXCEPTION_UUID));
        assertThat(exceptionLogDTO.getResponseStatus(), equalTo(RESPONSE_STATUS));
        assertThat(exceptionLogDTO.getShortErrorCodeDescription(), equalTo(SHORT_ERROR_CODE_DESCRIPTION));
    }
}
