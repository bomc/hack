/**
 * Project: MY_POC
 * <p/>
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
 * <p/>
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.test.auth.unit.model;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

/**
 * Tests that shows the functionality of {@link LocalDateTime}
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LocalDateTimeTest {

    @Test
   	public void test01_sortLocalDateTime_Pass() {
   		System.out.println("UserTest#test01_sortLocalDateTime_Pass");

   		final LocalDateTime oldestLocalDateTime = LocalDateTime.of(1999, 4, 5, 5, 22);
   		final LocalDateTime nowLocalDateFormat = LocalDateTime.now();

   		final List<LocalDateTime> l = new ArrayList<>();
   		l.add(nowLocalDateFormat);
   		l.add(LocalDateTime.of(2000, 2, 1, 2, 2));
   		l.add(LocalDateTime.of(2010, 3, 4, 5, 12));
   		l.add(oldestLocalDateTime);
   		l.add(LocalDateTime.of(2026, 4, 5, 5, 22));

   		System.out.println("----- unsorted");

   		// Get the oldest password from unsorted list.
   		final java.util.Optional<LocalDateTime> _oldestLocalDateTime = l.stream().sorted((l1, l2) -> l1.compareTo(l2))
   				.findFirst();
   		assertThat(oldestLocalDateTime, is(equalTo(_oldestLocalDateTime.get())));

   		// List before sorting.
   		l.stream().forEach(System.out::println);

   		System.out.println("----- sorted asc");

   		// Sort the list ascending.
   		final List<LocalDateTime> sortedAscList = l.stream().sorted((l1, l2) -> l1.compareTo(l2))
   				.collect(Collectors.toList());
   		sortedAscList.stream().forEach(System.out::println);
   		assertThat(sortedAscList,
   				contains(oldestLocalDateTime, // <1999-04-05T05:22>
   						LocalDateTime.of(2000, 2, 1, 2, 2), // <2000-02-01T02:02>
   						LocalDateTime.of(2010, 3, 4, 5, 12), // <2010-03-04T05:12>
   						nowLocalDateFormat, // <2016-03-16T08:24:25.735>
   						LocalDateTime.of(2026, 4, 5, 5, 22))); // <2026-04-05T05:22>

   		// Get the oldest password from asc sorted list.
   		final java.util.Optional<LocalDateTime> _oldestLocalDateTime_ = sortedAscList.stream()
   				.sorted((l1, l2) -> l1.compareTo(l2)).findFirst();
   		assertThat(oldestLocalDateTime, is(equalTo(_oldestLocalDateTime_.get())));

   		System.out.println("----- sorted desc");

   		// Sort the list descending.
   		final List<LocalDateTime> sortedDescList = l.stream().sorted((l1, l2) -> l2.compareTo(l1))
   				.collect(Collectors.toList());
   		sortedDescList.stream().forEach(System.out::println);
   		assertThat(sortedDescList,
   				contains(LocalDateTime.of(2026, 4, 5, 5, 22), // <2026-04-05T05:22>
   						nowLocalDateFormat, // <2016-03-16T08:24:25.735>
   						LocalDateTime.of(2010, 3, 4, 5, 12), // <2010-03-04T05:12>
   						LocalDateTime.of(2000, 2, 1, 2, 2), // <2000-02-01T02:02>
   						oldestLocalDateTime)); // <1999-04-05T05:22>

   		// Get the oldest password from desc sorted list.
   		final java.util.Optional<LocalDateTime> _oldestLocalDateTime__ = sortedDescList.stream()
   				.sorted((l1, l2) -> l1.compareTo(l2)).findFirst();
   		assertThat(oldestLocalDateTime, is(equalTo(_oldestLocalDateTime__.get())));
   	}

    @Test
   	public void test02_localDateTimeFromatter_Pass() {
   		System.out.println("UserTest#test02_localDateTimeFromatter_Pass");

   		final String formatted_ISO_Ldt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
   		System.out.println("UserTest#test02_localDateTimeFromatter_Pass [formatted_ISO_Ldt=" + formatted_ISO_Ldt + "]");

   		final String formatted_Custom_Ldt = LocalDateTime.now()
   				.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss.SSS"));
   		System.out.println(
   				"UserTest#test02_localDateTimeFromatter_Pass [formatted_Custom_Ldt=" + formatted_Custom_Ldt + "]");
   	}
}
