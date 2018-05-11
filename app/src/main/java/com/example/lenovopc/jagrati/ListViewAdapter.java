package com.example.lenovopc.jagrati;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    private boolean forTransfer;
    private ArrayList<View> childViews = new ArrayList<>();

    public ListViewAdapter(Context context, List<VolunteerLink> volunteerLinkList, boolean forTransfer) {
        mContext = context;
        this.forTransfer = forTransfer;
        this.volunteerLinks = volunteerLinkList;
        inflater = LayoutInflater.from(mContext);
        this.volunteerLinksList = new ArrayList<>();
        this.volunteerLinksList.addAll(volunteerLinkList);
    }

    public class ViewHolder {
        Button name;
        TextView discipline;
        NetworkImageView dpIView;
        ImageButton optionsBtn;
        ImageView greenTick;
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
            holder.optionsBtn = (ImageButton) view.findViewById(R.id.options);
            holder.greenTick = (ImageView) view.findViewById(R.id.greenTick);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (this.forTransfer) {
            holder.optionsBtn.setVisibility(View.GONE);
        }

        final String fullName = volunteerLinks.get(position).getName();
        final String discipline = volunteerLinks.get(position).getDiscipline();
        final String dpURL = volunteerLinks.get(position).getDisplayPicURL();
        if (!dpURL.equals("null")) {
            holder.dpIView.setImageUrl(dpURL, VolleySingleton.getInstance(
                mContext
            ).getImageLoader());
            holder.dpIView.setBackground(null);
        }

        holder.name.setText(fullName);
        holder.discipline.setText(discipline);
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int userId = volunteerLinks.get(position).getId();
                if (ListViewAdapter.this.forTransfer) {
                    ((TransferVolunteerAttendance) mContext).selectUser(userId, fullName, discipline, dpURL);

                    for (int i=0; i < childViews.size(); i++) {
                        View childView = childViews.get(i);
                        ImageView greenTickView = (ImageView) childView.findViewById(R.id.greenTick);
                        greenTickView.setVisibility(View.GONE);
                    }

                    holder.greenTick.setVisibility(View.VISIBLE);
                } else {
                    ((VolunteerList) mContext).selectUser(userId, fullName, discipline, dpURL);
                }
            }
        });
        childViews.add(view);
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