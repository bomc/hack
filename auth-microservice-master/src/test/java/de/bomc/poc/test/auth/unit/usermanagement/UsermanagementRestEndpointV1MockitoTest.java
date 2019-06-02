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
package de.bomc.poc.test.auth.unit.usermanagement;

import de.bomc.poc.api.generic.Parameter;
import de.bomc.poc.api.generic.transfer.request.RequestObjectDTO;
import de.bomc.poc.api.generic.transfer.response.ResponseObjectDTO;
import de.bomc.poc.api.generic.types.LongType;
import de.bomc.poc.api.generic.types.StringType;
import de.bomc.poc.api.jaxb.GenericResponseObjectDTOCollectionMapper;
import de.bomc.poc.api.mapper.transfer.RoleDTO;
import de.bomc.poc.auth.rest.endpoint.v1.usermanagement.impl.AuthUserManagementRestEndpointImpl;
import de.bomc.poc.auth.rest.endpoint.v2.usermanagement.AuthUserManagementRestEndpoint;
import de.bomc.poc.rest.client.RestClientBuilder;
import de.bomc.poc.rest.filter.authorization.AuthorizationTokenHeaderRequestFilter;
import org.apache.log4j.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.VerboseMockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Tests the facade for usermanagement v1 operations witk pure Mockito.
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 */
@RunWith(VerboseMockitoJUnitRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UsermanagementRestEndpointV1MockitoTest {

    @InjectMocks
    private final AuthUserManagementRestEndpointImpl sut = new AuthUserManagementRestEndpointImpl();
    @Mock
    private Logger loggerMock;
    @Mock
    private de.bomc.poc.auth.rest.endpoint.v2.usermanagement.AuthUserManagementRestEndpoint proxyMock;
    @Mock
    private UriInfo uriInfoMock;
    @Mock
    private Response responseMock;
    @Mock
    private RestClientBuilder restClientBuilderMock;
    @Mock
    private ResteasyWebTarget resteasyClientMock;
    @Mock
    private HttpHeaders httpHeadersMock;

    @Before
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test01_mockupSetup_Pass() throws Exception {
        System.out.println("UserManagementV1FacadeMockTest#test01_mockupSetup_Pass");

        // _____
        // given
        final String username = "dummy-user";
        final String testToken = "test-token";
        final RoleDTO roleDTO = new RoleDTO("myRole", "myDescription");
        roleDTO.setId(1L);

        final RequestObjectDTO requestObjectDTO = RequestObjectDTO.with(new Parameter("username", new StringType(username)));

        given(this.uriInfoMock.getBaseUri()).willReturn(new URI("http://localhost:8080/auth-usermanagement-v1/api/"));
        assertThat("http://localhost:8080/auth-usermanagement-v1/api/", is(equalTo(this.uriInfoMock.getBaseUri().toString())));

        given(this.httpHeadersMock.getRequestHeader(AuthorizationTokenHeaderRequestFilter.AUTHORIZATION_PROPERTY)).willReturn(Collections.singletonList(testToken));

        given(this.restClientBuilderMock.buildResteasyClient(testToken, this.uriInfoMock.getBaseUri())).willReturn(this.resteasyClientMock);
        assertThat(this.resteasyClientMock, is(notNullValue()));

        given(this.resteasyClientMock.proxy(AuthUserManagementRestEndpoint.class)).willReturn(this.proxyMock);
        assertThat(this.proxyMock, is(notNullValue()));

        given(this.proxyMock.findAllRolesByUsername(any(String.class))).willReturn(this.responseMock);
        assertThat(this.responseMock, is(notNullValue()));

        given(this.responseMock.getStatus()).willReturn(Response.Status.OK.getStatusCode());
        assertThat(this.responseMock.getStatus(), is(equalTo(Response.Status.OK.getStatusCode())));

//        given(this.responseMock.readEntity(new GenericType<List<RoleDTO>>() {})).willReturn(Arrays.asList(roleDTO));

        //
        // without lambda
        //
        when(this.responseMock.readEntity(new GenericType<List<RoleDTO>>() {})).thenAnswer(new Answer<List<RoleDTO>>() {
            @Override
            public List<RoleDTO> answer(final InvocationOnMock invocation) throws Throwable {
                System.out.println("UserManagementV1FacadeMockTest#test01_mockupSetup_Pass#answer");

                return Arrays.asList(roleDTO);
            }
        });

        //
        // with lambda
        //
//        when(this.responseMock.readEntity(new GenericType<List<RoleDTO>>() {})).thenAnswer(invocation -> {
//            System.out.println("UserManagementV1FacadeMockTest#test01_mockupSetup_Pass#answer");
//
//            return Arrays.asList(roleDTO);
//        });

        // ____
        // when
        final Response sutResponse = this.sut.findAllRolesByUsername(requestObjectDTO);
        assertThat(sutResponse, notNullValue());

        // ____
        // then
        // NOTE: Entity is not backed by an input stream, so using readEntity() is not possible.
        final GenericResponseObjectDTOCollectionMapper genericResponseObjectDTOCollectionMapper = (GenericResponseObjectDTOCollectionMapper)sutResponse.getEntity();
        assertThat(genericResponseObjectDTOCollectionMapper, notNullValue());
        assertThat(genericResponseObjectDTOCollectionMapper.getResponseObjectDTOList().size(), is(1));

        List<ResponseObjectDTO> responseObjectDTOList = genericResponseObjectDTOCollectionMapper.getResponseObjectDTOList();
        final List<Parameter> parameterList = responseObjectDTOList.iterator().next().parameters();

        assertThat(parameterList, hasItems(new Parameter("roleId", new LongType(1L)), new Parameter("roleName", new StringType("myRole")), new Parameter("roleDescription", new StringType("myDescription"))));
    }
}
