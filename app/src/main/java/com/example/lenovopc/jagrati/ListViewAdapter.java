package com.example.lenovopc.jagrati;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListViewAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater inflater;
    private List<VolunteerLink> volunteerLinks = null;
    private ArrayList<VolunteerLink> volunteerLinksList;

    public ListViewAdapter(Context context, List<VolunteerLink> volunteerLinkList) {
        mContext = context;
        this.volunteerLinks = volunteerLinkList;
        inflater = LayoutInflater.from(mContext);
        this.volunteerLinksList = new ArrayList<VolunteerLink>();
        this.volunteerLinksList.addAll(volunteerLinkList);
    }

    public class ViewHolder {
        Button name;
        TextView discipline;
        NetworkImageView dpIView;
    }

    @Override
    public int getCount() {
        return volunteerLinks.size();
    }

    @Override
    public VolunteerLink getItem(int position) {
        return volunteerLinks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.profile_subject_button, null);

            holder.name = (Button) view.findViewById(R.id.volunteerName);
            holder.discipline = (TextView) view.findViewById(R.id.volunteerDiscipline);
            holder.dpIView = (NetworkImageView) view.findViewById(R.id.displayPicture);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        String dpURL = volunteerLinks.get(position).getDisplayPicURL();
        if (!dpURL.equals("null")) {
            holder.dpIView.setImageUrl(dpURL, VolleySingleton.getInstance(
                mContext
            ).getImageLoader());
            holder.dpIView.setBackground(null);
        }

        holder.name.setText(volunteerLinks.get(position).getName());
        holder.discipline.setText(volunteerLinks.get(position).getDiscipline());
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent profileActivity = new Intent("com.example.lenovopc.jagrati.PROFILE");
            Bundle bundle = new Bundle();
            bundle.putInt("userId", volunteerLinks.get(position).getId());
            profileActivity.putExtras(bundle);
            mContext.startActivity(profileActivity);
            }
        });
        return view;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        volunteerLinks.clear();
        if (charText.length() == 0) {
            volunteerLinks.addAll(volunteerLinksList);
        } else {
            for (VolunteerLink wp : volunteerLinksList) {
                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    volunteerLinks.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}