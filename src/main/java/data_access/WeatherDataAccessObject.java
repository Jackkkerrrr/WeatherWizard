package data_access;

import entity.Weather;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import use_case.note.WeatherDataAccessInterface;

import java.io.IOException;


/**
 * This class runs the API and creates a weather DAO.
 **/

public class WeatherDataAccessObject implements WeatherDataAccessInterface {
    private static final String API_KEY = "7cce48d7f1f6785f54c0d08aa117ad83";
    private static final String MAIN = "main";
    private static String city;
    private static final int SUCCESS_CODE = 200;
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String STATUS_CODE_LABEL = "cod";
    private static final String WEATHER_LIST = "list";
    private static final String MESSAGE = "message";

    @Override
    public Weather getWeather(String city) throws IOException {
        // Make an API call to get the user object.
        final OkHttpClient client = new OkHttpClient().newBuilder().build();

        // creating file
        final Request request = new Request.Builder()
                .url(String.format("http://api.openweathermap.org/data/2.5/forecast?q=%s&appid="
                        + API_KEY + "&units=metric", city))
                .addHeader("Content-Type", CONTENT_TYPE_JSON)
                .build();
        try {
            final Response response = client.newCall(request).execute();

            final JSONObject responseBody = new JSONObject(response.body().string());

            if (responseBody.getInt(STATUS_CODE_LABEL) == SUCCESS_CODE) {
                final JSONObject weatherJSON = responseBody.getJSONArray(WEATHER_LIST).getJSONObject(0);

                // get individual items from the json object

                final int lat = (int) weatherJSON.getJSONObject(MAIN).getDouble("lat");
                final int lon = (int) weatherJSON.getJSONObject(MAIN).getDouble("lon");
                final int temp = (int) weatherJSON.getJSONObject(MAIN).getDouble("temp");
                final String looks = weatherJSON.getJSONObject("weather").getString(MAIN);
                String alertDescription = "No alerts";
                if (weatherJSON.has("alerts")) {
                    final JSONArray alertsArray = weatherJSON.getJSONArray("alerts");
                    if (alertsArray.length() > 0) {
                        alertDescription = alertsArray.getJSONObject(0).getString("description");
                    }
                }

                return new Weather(city, lon, lat, temp, looks, alertDescription);

            }
            else {
                throw new IOException(responseBody.getString(MESSAGE));
            }
        }
        catch (IOException | JSONException ex) {
            throw new IOException(ex);
        }
    }
}

