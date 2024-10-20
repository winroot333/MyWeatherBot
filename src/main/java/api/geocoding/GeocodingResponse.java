package api.geocoding;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

@Getter
@Builder
public class GeocodingResponse {
    private Double longitude;
    private Double latitude;
    private String city;
    private String country;
    private long cityId;
    @Setter
    private int id;

}
