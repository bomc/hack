package de.bomc.poc.rest.mock;

import org.apache.log4j.Logger;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This endpoint works as a mock and is the implementation of the {@link MockResourceInterface}.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: 6791 $ $Author: tzdbmm $ $Date: 2016-07-19 09:07:13 +0200 (Di, 19 Jul 2016) $
 * @since 12.07.2016
 */
public class MockResource implements MockResourceInterface {

    private static final Logger LOGGER = Logger.getLogger(MockResource.class);
    private static final String LOG_PREFIX = "MockResource#";

    @Override
    public Response logToMe(final Long id) {
        LOGGER.debug(LOG_PREFIX + "logToMe [id=" + id + "]");

        return Response.status(Response.Status.OK)
                       .entity(String.valueOf(id))
                       .build();
    }

    @Override
    public Response getMockDTO(final MockDTO mockDTO) {
        LOGGER.debug(LOG_PREFIX + "getMockDTO [mockDTO=" + mockDTO + "]");

        final MockDTO copyMockDTO = new MockDTO(mockDTO);

        return Response.status(Response.Status.OK)
                       .entity(copyMockDTO)
                       .build();
    }

    @Override
    public Response getMockDTOAsList(final List<MockDTO> mockDTOList) {
        LOGGER.debug(LOG_PREFIX + "getMockDTO [mockDTOList.size=" + mockDTOList.size() + "]");

        final List<MockDTO>
            copyMockDTOList =
            mockDTOList.stream()
                       .collect(Collectors.toList());

        final GenericEntity<List<MockDTO>> copyMockDTOListToReturn = new GenericEntity<List<MockDTO>>(copyMockDTOList) {};

        return Response.status(Response.Status.OK)
                       .entity(copyMockDTOListToReturn)
                       .build();
    }
}
