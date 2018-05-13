package ru.dmitriylebyodkin.shoplist.presenters;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import ru.dmitriylebyodkin.shoplist.models.ListModel;
import ru.dmitriylebyodkin.shoplist.room.data.IList;
import ru.dmitriylebyodkin.shoplist.room.data.IListWithItems;
import ru.dmitriylebyodkin.shoplist.views.InfoView;

@InjectViewState
public class InfoPresenter extends MvpPresenter<InfoView> {

    private static final String TAG = "myLogs";

    public void checkAll() {
        getViewState().checkAll();
    }

    public void resetAll() {
        getViewState().resetAll();
    }

    public void deleteChecked() {
        getViewState().deleteChecked();
    }

    public void setTitle(String title) {
        getViewState().setActivityTitle(title);
    }

    public void replaceFragment(Fragment fragment, String title) {
        getViewState().replaceFragment(fragment, title);
    }

    public void updateList(Context context, IListWithItems iListWithItems) {
        IList iList = iListWithItems.getList();

        if (iList != null) {
            getViewState().setActivityTitle(iList.getTitle());
        }

        getViewState().updateList(iListWithItems);
    }
}
