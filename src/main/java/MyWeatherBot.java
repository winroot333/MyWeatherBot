import api.weather.OpenMeteoAPI;
import api.weather.OpenWeatherMapAPI;
import api.weather.WeatherAPI;

public class MyWeatherBot {
    public static void main(String[] args) {
        WeatherAPI weatherAPI = new OpenWeatherMapAPI();
        WeatherAPI weatherAPI2 = new OpenMeteoAPI();
        System.out.println(weatherAPI2.getWeather("Saint Petersburg"));
//        System.out.println(weatherAPI.getWeather("Saint Petersburg"));
    }
}
