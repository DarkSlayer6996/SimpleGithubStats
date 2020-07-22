package me.micrusa.githubstats.utils.stats;

import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.tinylog.Logger;

import io.realm.Realm;
import me.micrusa.githubstats.objects.realm.Repo;
import me.micrusa.githubstats.utils.RequestsUtil;
import me.micrusa.githubstats.utils.utils;

public class RepoData extends StatsData {

    public RepoData(final Repo repo) {
        super(repo);
        if(response != null) return;

        String url = "https://api.github.com/repos/" + repo.getRepo();

        RequestsUtil.request(url, (isSuccess, response) -> {
            Logger.debug("Received response");
            success = isSuccess;
            if(!success) errorCode = Integer.parseInt(response);
            try {
                RepoData.super.response = new JSONObject(response);
                cache(repo, response);
            } catch(Exception ignored) {}
            runAll();
        });
    }

    public void setStars(TextView text){
        super.setup(text, "stargazers_count");
    }

    public void setIssues(TextView text){
        super.setup(text, "open_issues_count");
    }

    public void setForks(TextView text){
        super.setup(text, "forks_count");
    }

    public void setWatchers(TextView text){
        super.setup(text, "watchers_count");
    }

    private void cache(Repo repo, String response){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Repo managedRepo = realm.where(Repo.class).equalTo("id", repo.getId()).findFirst();
        managedRepo.setCachedResponse(response); //No possible NullPointerException because it's in the fragment
        managedRepo.setLatestCache(System.currentTimeMillis());
        realm.commitTransaction();
    }
}
