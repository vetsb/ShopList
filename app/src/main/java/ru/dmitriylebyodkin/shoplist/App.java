package ru.dmitriylebyodkin.shoplist;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ru.dmitriylebyodkin.shoplist.models.CategoryModel;
import ru.dmitriylebyodkin.shoplist.models.ProductModel;
import ru.dmitriylebyodkin.shoplist.models.UnitModel;
import ru.dmitriylebyodkin.shoplist.room.data.Category;
import ru.dmitriylebyodkin.shoplist.room.data.Product;
import ru.dmitriylebyodkin.shoplist.room.data.Unit;

public class App extends Application {

    public static final int RESULT_DELETE = 2;
    public static final int RESULT_ADD = 3;

    private static final String INIT_PRODUCTS_KEY = "init_products";
    private static final String INIT_CATEGORIES_KEY = "init_categories";
    private static final String INIT_UNITS_KEY = "init_units";
    private static final String TAG = "myLogs";

    public static int indexOfIgnoreCase(List<String> list, String text) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equalsIgnoreCase(text)) {
                return i;
            }
        }

        return -1;
    }

    public static void initApp(Activity activity) {
        SharedPreferences sPref = activity.getPreferences(Context.MODE_PRIVATE);

        try {
            if (!sPref.getBoolean(INIT_CATEGORIES_KEY, false)) {
                initCategories(activity.getApplicationContext(), sPref);
            }

            if (!sPref.getBoolean(INIT_PRODUCTS_KEY, false)) {
                initProducts(activity.getApplicationContext(), sPref);
            }

            if (!sPref.getBoolean(INIT_UNITS_KEY, false)) {
                initUnits(activity.getApplicationContext(), sPref);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void initUnits(Context context, SharedPreferences sPref) throws JSONException {
        JSONObject jsonObject = new JSONObject(loadJSONFromAsset(context, "units"));
        JSONArray jsonArray = jsonObject.getJSONArray("items");

        Unit unit;

        for (int i = 0; i < jsonArray.length(); i++) {
            unit = new Unit();
            unit.setTitle(jsonArray.getString(i));
            UnitModel.insert(context, unit);
        }

        SharedPreferences.Editor editor = sPref.edit();
        editor.putBoolean(INIT_UNITS_KEY, true);
        editor.apply();
    }

    public static void initProducts(Context context, SharedPreferences sPref) throws JSONException {
        JSONObject jsonObject = new JSONObject(loadJSONFromAsset(context, "products"));
        JSONArray jsonArray = jsonObject.getJSONArray("items");

        int countCategories = new JSONObject(loadJSONFromAsset(context, "categories")).getJSONArray("items").length();

        Product product;
        int categoryId;

        for (int i = 0; i < jsonArray.length(); i++) {
            categoryId = countCategories - jsonArray.getJSONObject(i).getInt("category_id") + 1;

            product = new Product();
            product.setTitle(jsonArray.getJSONObject(i).getString("title"));
            product.setCategoryId(categoryId);
            product.setUnit(jsonArray.getJSONObject(i).getInt("unit_id"));
            ProductModel.insert(context, product);
        }

        SharedPreferences.Editor editor = sPref.edit();
        editor.putBoolean(INIT_PRODUCTS_KEY, true);
        editor.apply();
    }

    public static void initCategories(Context context, SharedPreferences sPref) throws JSONException {
        JSONObject jsonObject = new JSONObject(loadJSONFromAsset(context, "categories"));
        JSONArray jsonArray = jsonObject.getJSONArray("items");

        Category category;

        for (int i = 0; i < jsonArray.length(); i++) {
            category = new Category();
            category.setId(jsonArray.length() - i);
            category.setTitle(jsonArray.getString(i));

            CategoryModel.insert(context, category);
        }

        SharedPreferences.Editor editor = sPref.edit();
        editor.putBoolean(INIT_CATEGORIES_KEY, true);
        editor.apply();
    }

    public static String loadJSONFromAsset(Context context, String name) {
        String json;

        try {
            InputStream is = context.getAssets().open("json/"+name+".json");
            int size = is.available();
            byte[] buffer = new byte[size];

            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return json;
    }

    /**
     * Считает расстояние между двумя точками на земле.
     * Результат в метрах.
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    public static int calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        lat1 = Math.toRadians(lat1);
        lng1 = Math.toRadians(lng1);
        lat2 = Math.toRadians(lat2);
        lng2 = Math.toRadians(lng2);

        double cos1 = Math.cos(lat1);
        double cos2 = Math.cos(lat2);
        double sin1 = Math.sin(lat1);
        double sin2 = Math.sin(lat2);
        double delta = lng2 - lng1;
        double cdelta = Math.cos(delta);
        double sdelta = Math.sin(delta);

        double y = Math.sqrt(Math.pow(cos2 * sdelta, 2) + Math.pow(cos1 * sin2 - sin1 * cos2 * cdelta, 2));
        double x = sin1 * sin2 + cos1 * cos2 * cdelta;

        double ad = Math.atan2(y, x);
        double dist = ad * 6372795;

        return (int) dist;
    }

    public static DateFormatSymbols getRussianLocale() {
        Locale locale = new Locale("ru");
        DateFormatSymbols dfs = DateFormatSymbols.getInstance(locale);
        String[] months = {
                "января", "февраля", "марта", "апреля", "мая", "июня",
                "июля", "августа", "сентября", "октября", "ноября", "декабря"};
        String[] shortMonths = {
                "янв", "фев", "мар", "апр", "май", "июн",
                "июл", "авг", "сен", "окт", "ноя", "дек"};
        dfs.setMonths(months);
        dfs.setShortMonths(shortMonths);
        String[] weekdays = {"", "Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"};
        String[] shortWeekdays = {"", "вс", "пн", "вт", "ср", "чт", "пт", "сб"};
        dfs.setWeekdays(weekdays);
        dfs.setShortWeekdays(shortWeekdays);

        return dfs;
    }
}
