package api.geocoding;

public class GeocodingResponse {
    Double longitude;
    Double latitude;
    String city;

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public String getCity() {
        return city;
    }

    private GeocodingResponse(Double longitude, Double latitude, String city) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.city = city;
    }

    public static class Builder {
        private Double longitude;
        private Double latitude;
        private String city;

        public Builder setLongitude(Double longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder setLatitude(Double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder setCity(String city) {
            this.city = city;
            return this;
        }

        public GeocodingResponse build() {
            return new GeocodingResponse(longitude, latitude, city);
        }
    }
}
