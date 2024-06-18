import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class WeatherAPI {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            String city;
            do {
                System.out.println("===========================================================");
                System.out.print("Enter City (Type No to Quit) : ");
                city = scanner.nextLine();

                if (city.equalsIgnoreCase("No")) break;

                JSONObject cityLocationData = (JSONObject) getLocationData(city);
                double latitude = (double) cityLocationData.get("latitude");
                double longitude = (double) cityLocationData.get("longitude");

                displayWeatherData(latitude, longitude);
            } while (!city.equalsIgnoreCase("No"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static JSONObject getLocationData(String city) {
        city = city.replaceAll(" ", "+");

        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                city + "&count=1&language=en&format=json";

        try {

            HttpURLConnection apiConnection = fetchApiResponse(urlString);


            if (apiConnection.getResponseCode() != 200) {
                System.out.println("Error : Could not connect to API");
                return null;
            }

            String jsonResponse = readApiResponse(apiConnection);

            JSONParser parser = new JSONParser();
            JSONObject resultsJsonObj = (JSONObject) parser.parse(jsonResponse);

            JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
            return (JSONObject) locationData.get(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String readApiResponse(HttpURLConnection apiConnection) {

        try {
            StringBuilder resultJson = new StringBuilder();

            Scanner scanner = new Scanner(apiConnection.getInputStream());

            while (scanner.hasNext()) {

                resultJson.append(scanner.nextLine());
            }

            scanner.close();

            return resultJson.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString) {

        try {

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            return conn;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static void displayWeatherData(double latitude, double longitude) {
        String url = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude + "&current_weather=true";
        HttpURLConnection apiConnection = null;
        try {
            apiConnection = fetchApiResponse(url);

            if (apiConnection.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to API");
                return;
            }

            String jsonResponse = readApiResponse(apiConnection);
            if (jsonResponse == null) {
                System.out.println("Error: Empty response from API");
                return;
            }


            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonResponse);
            JSONObject currentWeatherJson = (JSONObject) jsonObject.get("current_weather");

            if (currentWeatherJson == null) {
                System.out.println("Error: Current weather data not available.");
                return;
            }


            /*String time = (String) currentWeatherJson.get("time");
            if (time != null) {
                System.out.println("Current Time: " + time);
            } else {
                System.out.println("Time data not available.");
            }*/

            Number temperature = (Number) currentWeatherJson.get("temperature");
            if (temperature != null) {
                System.out.println("Current Temperature (C): " + temperature.doubleValue());
            } else {
                System.out.println("Temperature data not available.");
            }

            /*Number relativeHumidity = (Number) currentWeatherJson.get("relative_humidity");
            if (relativeHumidity != null) {
                System.out.println("Relative Humidity: " + relativeHumidity.longValue());
            } else {
                System.out.println("Relative Humidity data not available.");
            }*/

            Number windSpeed = (Number) currentWeatherJson.get("windspeed");
            if (windSpeed != null) {
                System.out.println("Wind Speed: " + windSpeed.doubleValue());
            } else {
                System.out.println("Wind Speed data not available.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}