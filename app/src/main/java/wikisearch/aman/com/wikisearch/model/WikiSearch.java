package wikisearch.aman.com.wikisearch.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Created by Instinct on 8/1/2018.
 */

public class WikiSearch {
    @SerializedName("query")
    public Datum data;

    public class Datum {
        @SerializedName("pages")
        private Map<String, UserList> result;

        public class UserList {
            @SerializedName("pageid")
            public int pageid;
            @SerializedName("title")
            public String title;

        }
    }
}
