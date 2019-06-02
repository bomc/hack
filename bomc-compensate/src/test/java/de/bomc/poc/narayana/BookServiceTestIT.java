package de.bomc.poc.narayana;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

@RunWith(Arquillian.class)
public class BookServiceTestIT {

	private static final String LOG_PREFIX = "BookServiceTestIT#";

	@Inject
	BookService bookService;

	@Deployment
	public static WebArchive createTestArchive() {

		WebArchive archive = ShrinkWrap.create(WebArchive.class, "test.war")
				.addPackages(true, BookService.class.getPackage().getName())
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

		return archive;
	}

	@Before
	public void resetInk() {

		InvoicePrinter.hasInk = true;
	}

	@Test
	public void test010_success() throws Exception {
		System.out.println(LOG_PREFIX + "test010_success - Running a test for the success case");

		bookService.buyBook(LOG_PREFIX + "test010_success - Java Transaction Processing: Design and Implementation",
				"paul.robinson@redhat.com");
	}

	@Test(expected = RuntimeException.class)
	public void test020_failure() throws Exception {

		System.out.println(LOG_PREFIX
				+ "test020_failure - Running a test for the failure case, where the printer has run out of ink");

		InvoicePrinter.hasInk = false;
		bookService.buyBook(LOG_PREFIX + "test020_failure - Java Transaction Processing: Design and Implementation",
				"paul.robinson@redhat.com");
	}

}
