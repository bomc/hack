/**
 * Project: MY_POC
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.test.api.unit.mapper;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.bomc.poc.api.mapper.LocalDateTimeMapper;

/**
 * A test for {@link LocalDateTimeMapper} with jaxb.
 * 
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LocalDateTimeMapperTest {

	private static final Logger LOGGER = Logger.getLogger(LocalDateTimeMapperTest.class);

	private final LocalDateTime localDateTime = LocalDateTime.of(2016, 1, 1, 12, 30, 30);
	private final String COMPARABLE_DATE = "2016-01-01 12:30:30";
	private LocalDateTimeMapper localDateTimeMapper;
	private ExecutableValidator executableValidator;

	@Before
	public void setup() {
		localDateTimeMapper = new LocalDateTimeMapper();

		final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		this.executableValidator = validator.forExecutables();
	}

	@Test
	public void test01_localDateMapperToString_Pass() {
		LOGGER.debug("test01_localDateMapperToString_Pass");

		final String dateAsString = localDateTimeMapper.asString(localDateTime);

		assertThat(COMPARABLE_DATE, is(equalTo(dateAsString)));
	}

	@Test
	public void test02_localDateMapperAsDate_Pass() {
		LOGGER.debug("test02_localDateMapperAsDate_Pass");

		final LocalDateTime localDateTime = localDateTimeMapper.asDate(COMPARABLE_DATE);

		assertThat(localDateTime, is(equalTo(localDateTime)));
	}

	/**
	 * Tests the &#064;NotNull constraint as method constraint parameter.
	 */
	@Test
	public void test03_localDateMapperAsDate_NotNull_Validation_Pass() {
		LOGGER.debug("test03_localDateMapperAsDate_NotNull_Validation_Pass");

		Method method = null;
		try {
			method = LocalDateTimeMapper.class.getMethod("asDate", String.class);
		} catch (final NoSuchMethodException e) {
			e.printStackTrace();
		}

		final Object[] parameterValues = { null };

		final Set<ConstraintViolation<LocalDateTimeMapper>> set = this.executableValidator
				.validateParameters(localDateTimeMapper, method, parameterValues);

		assertThat("darf nicht null sein", is(equalTo(set.iterator().next().getMessage())));

		assertThat(1, is(set.size()));
	}

	/**
	 * Tests the &#064;NotNull constraint as method constraint parameter.
	 */
	@Test
	public void test04_localDateMapperAsString_NotNull_Validation_Pass() {
		LOGGER.debug("test04_localDateMapperAsString_NotNull_Validation_Pass");

		Method method = null;
		try {
			method = LocalDateTimeMapper.class.getMethod("asString", LocalDateTime.class);
		} catch (final NoSuchMethodException e) {
			e.printStackTrace();
		}

		final Object[] parameterValues = { null };

		final Set<ConstraintViolation<LocalDateTimeMapper>> set = this.executableValidator
				.validateParameters(localDateTimeMapper, method, parameterValues);

		assertThat("darf nicht null sein", is(equalTo(set.iterator().next().getMessage())));

		assertThat(1, is(set.size()));
	}
}
