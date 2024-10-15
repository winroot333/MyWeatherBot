package api.geocoding;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Value
@Builder
public class GeocodingResponse {
    Double longitude;
    Double latitude;
    String city;
}
