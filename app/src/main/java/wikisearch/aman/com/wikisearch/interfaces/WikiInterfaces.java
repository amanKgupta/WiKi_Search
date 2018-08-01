package wikisearch.aman.com.wikisearch.interfaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Url;
import wikisearch.aman.com.wikisearch.model.WikiPedia;

/**
 * Created by Instinct on 7/31/2018.
 */

public interface WikiInterfaces {
    @GET
    Call<WikiPedia> savePost(
            @Url String url
    );
}
