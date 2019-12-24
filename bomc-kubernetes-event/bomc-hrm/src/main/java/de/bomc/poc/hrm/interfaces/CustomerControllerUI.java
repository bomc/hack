/**
 * Project: hrm
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 */
package de.bomc.poc.hrm.interfaces;

import java.security.Principal;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.keycloak.KeycloakSecurityContext;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.bomc.poc.hrm.application.log.method.Loggable;
import de.bomc.poc.hrm.application.service.crud.CustomerService;
import de.bomc.poc.hrm.interfaces.mapper.CustomerDto;

/**
 * The controller for customer handling.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
@Controller
@RequestMapping(value = "/ui/customer")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS,
        RequestMethod.PUT })
public class CustomerControllerUI {

//	public static final String MEDIA_TYPE_JSON_V1 = "application/vnd.hrm-customer-ui-v1+json;charset=UTF-8";

	private final CustomerService customerService;
	private final HttpServletRequest httpServletRequest;

	/**
	 * Creates a new instance of <code>CustomerControllerUI</code>.
	 * 
	 * @param customerService    the given customer service.
	 * @param httpServletRequest the given http request.
	 */
	// @Autowired: is here not necessary, is done by IOC container.
	public CustomerControllerUI(final CustomerService customerService, final HttpServletRequest httpServletRequest) {
		this.customerService = customerService;
		this.httpServletRequest = httpServletRequest;
	}

	/**
	 * curl -v http://192.168.99.100:31380/bomc-hrm/ui/customer/
	 * 
	 * @return the data from db.
	 */
	@GetMapping(path = "/")
	@Loggable(result = false, params = false, value = LogLevel.DEBUG, time = true)
	public String index() {

		return "ui/customer/external";
	}

	/**
	 * curl -v http://192.168.99.100:31380/bomc-hrm/ui/customer/customers
	 * 
	 * @param principal
	 * @param model
	 * @return the data from db.
	 */
	@GetMapping(path = "/customers")
	@Loggable(result = true, params = true, value = LogLevel.DEBUG, time = true)
	public String customers(final Principal principal, final Model model) {

		configCommonAttributes(model);

		final Iterable<CustomerDto> customerDtoIter = this.customerService.findAll();
		model.addAttribute("customers", customerDtoIter);

		if (principal != null) {
			model.addAttribute("username", principal.getName());
		} else {
			model.addAttribute("username", "bomc");
		}

		return "ui/customer/customers";
	}

	@GetMapping(value = "/logout")
	@Loggable(result = true, params = false, value = LogLevel.DEBUG, time = true)
	public String logout() throws ServletException {

		this.httpServletRequest.logout();

		return "redirect:/";
	}

	private void configCommonAttributes(final Model model) {

		model.addAttribute("name", getKeycloakSecurityContext().getIdToken().getGivenName());
	}

	private KeycloakSecurityContext getKeycloakSecurityContext() {

		return (KeycloakSecurityContext) this.httpServletRequest.getAttribute(KeycloakSecurityContext.class.getName());
	}
}
