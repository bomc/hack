package de.bomc.poc.rest.mock;

/**
 * A mock data transfer object.
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: 6791 $ $Author: tzdbmm $ $Date: 2016-07-19 09:07:13 +0200 (Di, 19 Jul 2016) $
 * @since 14.07.2016
 */
public class MockDTO {

    private String mockStr;
    private Long mockLong;
    private Integer mockInteger;

    public MockDTO() {
        //
        // Indicates a pojo.
    }

    public MockDTO(final String mockStr, final Long mockLong, final Integer mockInteger) {
        this.mockStr = mockStr;
        this.mockLong = mockLong;
        this.mockInteger = mockInteger;
    }

    /**
     * Copy constructor.
     * @param mockDTO the object to copy.
     */
    public MockDTO(final MockDTO mockDTO) {
        this.mockInteger = mockDTO.getMockInteger();
        this.mockLong = mockDTO.getMockLong();
        this.mockStr = mockDTO.getMockStr();
    }

    public String getMockStr() {
        return this.mockStr;
    }

    public void setMockStr(final String mockStr) {
        this.mockStr = mockStr;
    }

    public Long getMockLong() {
        return this.mockLong;
    }

    public void setMockLong(final Long mockLong) {
        this.mockLong = mockLong;
    }

    public Integer getMockInteger() {
        return this.mockInteger;
    }

    public void setMockInteger(final Integer mockInteger) {
        this.mockInteger = mockInteger;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        final MockDTO mockDTO = (MockDTO)o;

        if (this.mockStr != null ? !this.mockStr.equals(mockDTO.mockStr) : mockDTO.mockStr != null) {
            return false;
        }
        if (this.mockLong != null ? !this.mockLong.equals(mockDTO.mockLong) : mockDTO.mockLong != null) {
            return false;
        }
        return !(this.mockInteger != null ? !this.mockInteger.equals(mockDTO.mockInteger) : mockDTO.mockInteger != null);
    }

    @Override
    public int hashCode() {
        int result = this.mockStr != null ? this.mockStr.hashCode() : 0;
        result = 31 * result + (this.mockLong != null ? this.mockLong.hashCode() : 0);
        result = 31 * result + (this.mockInteger != null ? this.mockInteger.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MockDTO{" +
               "mockStr='" + this.mockStr + '\'' +
               ", mockLong=" + this.mockLong +
               ", mockInteger=" + this.mockInteger +
               '}';
    }
}
