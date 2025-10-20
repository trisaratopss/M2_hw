package dogapi;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) {
        // return statement included so that the starter code can compile and run.
        HttpUrl url = HttpUrl.parse("https://api.dogapi.com/breeds/").newBuilder()
                .addPathSegment(breed.toLowerCase())
                .addPathSegment("list")
                .build();

        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()){
            if (response.body() == null) {
                throw new BreedNotFoundException(breed);
            }

            JSONObject json = new JSONObject(response.body().string());

            if (!response.isSuccessful() ||
                    !"success".equalsIgnoreCase(json.optString("status"))) {
                throw new BreedNotFoundException(breed);
            }

            JSONArray arr = json.getJSONArray("message");

            List<String> list = new ArrayList<>();

            for (int i = 0; i < arr.length(); i++) {
                list.add(arr.getJSONObject(i).getString("name"));
            }

            return list;

        } catch (IOException e) {
            throw new BreedNotFoundException(breed);
        }

    }
}