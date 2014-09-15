package com.tesfayeabel.lolchat;

import android.app.Application;
import android.util.SparseArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class LOLChatApplication extends Application {
    private static String clientVersion;
    private static SparseArray<String> championArray;
    private static SparseArray<String> summonerSpellArray;

    public static String getRiotResourceURL() {
        return "http://ddragon.leagueoflegends.com/cdn/" + clientVersion;
    }

    private static String getHTML(String url) throws Exception {
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);
        in.close();
        return response.toString();
    }

    public static String getGameSubType(String name) {
        if (name.equals("NONE"))
            return "Custom";
        if (name.equals("RANKED_SOLO_5x5") || name.equals("RANKED_TEAM_3x3") || name.equals("RANKED_TEAM_5x5"))
            return "Ranked";
        if (name.equals("BOT"))
            return "Co-op vs AI";
        return "Normal";
    }

    public static String getGameMode(String name) {
        if (name.equals("CLASSIC"))
            return "Classic";
        if (name.equals("ODIN"))
            return "Dominion";
        if (name.equals("ARAM"))
            return "ARAM";
        if (name.equals("TUTORIAL"))
            return "Tutorial";
        if (name.equals("ONEFORALL"))
            return "One for All";
        if (name.equals("ASCENSION"))
            return "Ascension";
        return "?";
    }

    public static String getChampionName(int id) {
        return championArray.get(id);
    }

    public static String getSpellName(int id)
    {
        return summonerSpellArray.get(id);
    }

    public static String getMapName(int id) {
        switch (id) {
            case 1:
                return "Summoner's Rift";
            case 2:
                return "Summoner's Rift";
            case 3:
                return "The Proving Grounds";
            case 4:
                return "Twisted Treeline";
            case 8:
                return "The Crystal Scar";
            case 10:
                return "Twisted Treeline";
            case 12:
                return "Howling Abyss";
            default:
                return "";
        }
    }

    public void onCreate() {
        championArray = new SparseArray<String>();
        summonerSpellArray = new SparseArray<String>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject version = new JSONObject(getHTML("http://ddragon.leagueoflegends.com/realms/na.json"));
                    clientVersion = version.getString("v");

                    JSONObject champs = new JSONObject(getHTML("https://na.api.pvp.net/api/lol/static-data/na/v1.2/champion?api_key=" + getString(R.string.api_riot)));
                    JSONObject data = champs.getJSONObject("data");
                    JSONArray array = data.names();
                    for (int i = 0; i < array.length(); i++) {
                        String name = array.get(i).toString();
                        championArray.put(data.getJSONObject(name).getInt("id"), name.toLowerCase());
                    }

                    JSONObject spells = new JSONObject(getHTML("https://na.api.pvp.net/api/lol/static-data/na/v1.2/summoner-spell?api_key=" + getString(R.string.api_riot)));
                    JSONObject spellData = spells.getJSONObject("data");
                    JSONArray array2 = spellData.names();
                    for (int i = 0; i < array2.length(); i++) {
                        String name = array2.get(i).toString();
                        summonerSpellArray.put(spellData.getJSONObject(name).getInt("id"), name.replace("Summoner", "").toLowerCase());
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }).start();
    }
}
