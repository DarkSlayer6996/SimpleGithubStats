package me.micrusa.githubstats.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import io.realm.Realm;
import me.micrusa.githubstats.R;
import me.micrusa.githubstats.objects.realm.User;
import me.micrusa.githubstats.utils.stats.UserData;
import me.micrusa.githubstats.utils.Utils;

public class UserAdapter extends ArrayAdapter<User> {

    public UserAdapter(Context context, ArrayList<User> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final User user = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);

            TextView name = convertView.findViewById(R.id.user_name);
            name.setText(Utils.capitalizeFirst(user.getName()));

            UserData data = new UserData(user);

            TextView followers = convertView.findViewById(R.id.user_followers);
            data.setFollowers(followers);
            TextView repos = convertView.findViewById(R.id.user_repos);
            data.setRepos(repos);

            convertView.setOnLongClickListener(view -> {
                Realm realm = Realm.getDefaultInstance();
                if (!realm.isInTransaction()) realm.beginTransaction();
                UserAdapter.super.remove(user);
                user.deleteFromRealm();
                realm.commitTransaction();
                realm.close();
                notifyDataSetChanged();
                return true;
            });
        }

        return convertView;
    }
}
