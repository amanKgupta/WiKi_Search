package wikisearch.aman.com.wikisearch.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Instinct on 7/31/2018.
 */

public class WikiPedia {
    @SerializedName("continue")
    public Continues continues;
    @SerializedName("query")
    public Datum data;

    public class Continues {
        @SerializedName("picontinue")
        public int picontinue;
    }

    public class Datum {
        @SerializedName("pages")
        public List<UserList> pages;

        public class UserList {
            @SerializedName("pageid")
            public int pageid;
            @SerializedName("title")
            public String title;

            @SerializedName("thumbnail")
            public Thumbnail thumbnail;

            public class Thumbnail {
                @SerializedName("source")
                public String source;
            }


        }
    }
}
