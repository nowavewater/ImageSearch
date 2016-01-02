package com.example.imagesearch.model;

import android.database.Cursor;

import com.activeandroid.Cache;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.util.SQLiteUtils;
import com.example.imagesearch.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created by ypc on 12/30/2015.
 * Local DB model for search history
 * ActiveAndroid data model
 *
 */
@Table(name = "History")
public class History extends Model {

    @Column(name = "text")
    private String text;

    @Column(name = "date")
    private String date;

    public History() {
        super();
    }

    public History(String text, String date) {
        super();
        this.text = text;
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date;
    }

    public static List<History> getAll() {
        // This is how you execute a query
        return new Select()
                .from(History.class)
                .orderBy("date desc")
                .execute();
    }

    // Save search to history
    public static void saveHistory(String text) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss", Locale.getDefault());
        History history = new History(text, dateFormat.format(calendar.getTime()));
        history.save();
    }

    // Get search suggestions
    public static List<String> getSuggestion(String query) {
        List<History> queryResults = new Select()
                .from(History.class)
                .where("text LIKE ?", "%"+ query + "%")
                .execute();
        Set<String> resultHash = new HashSet<String>();
        int total = Constants.SUGGESTION_TOTAL;
        for (History history:queryResults){
            if (resultHash.size()>=total)
                break;
            resultHash.add(history.getText().toLowerCase());
        }
        List<String> results = new ArrayList<>();
        results.addAll(resultHash);
        return results;
    }

    // Clear search history
    public static void clear() {
        new Delete().from(History.class).execute();
    }
}
