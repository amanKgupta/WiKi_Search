package wikisearch.aman.com.wikisearch.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import wikisearch.aman.com.wikisearch.R;
import wikisearch.aman.com.wikisearch.model.ContactDetails;

/**
 * Created by ravi on 16/11/17.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<ContactDetails> contactList;
    private OnItemClickListener mOnItemClickListener;
    private List<ContactDetails> contactListFiltered;

    public ContactsAdapter(Context context, List<ContactDetails> contactList) {
        this.context = context;
        this.contactList = contactList;
        this.contactListFiltered = contactList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.items_adapter, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        if (holder instanceof MyViewHolder) {
            MyViewHolder view = (MyViewHolder) holder;
            final ContactDetails contact = contactListFiltered.get(position);
            view.name.setText(contact.getTitle());
            if (contact.getImageurl() != null) {
                Picasso.get().load(contact.getImageurl()).error(R.drawable.ic_image).into(view.thumbnail);
            } else {
                view.thumbnail.setImageResource(R.drawable.ic_image);
            }
            view.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(v, contactListFiltered.get(position), position);
                    }

                    // showDialogAbout(view.itemView,"https://en.wikipedia.org/wiki/Sachin_Pilot");
                }
            });

        }
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    @Override
    public int getItemCount() {
        return contactListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contactListFiltered = contactList;
                } else {
                    List<ContactDetails> filteredList = new ArrayList<>();
                    for (ContactDetails row : contactList) {


                        if (row.getTitle().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    contactListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactListFiltered = (ArrayList<ContactDetails>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface OnItemClickListener {
        void onItemClick(View view, ContactDetails contact, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, description;
        public ImageView thumbnail;
        public LinearLayout linearLayout;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.text1);
            thumbnail = view.findViewById(R.id.thumbnail);
            linearLayout = view.findViewById(R.id.parent);
        }
    }

}
