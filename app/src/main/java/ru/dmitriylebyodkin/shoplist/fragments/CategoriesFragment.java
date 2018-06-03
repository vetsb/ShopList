package ru.dmitriylebyodkin.shoplist.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.dmitriylebyodkin.shoplist.R;
import ru.dmitriylebyodkin.shoplist.adapters.CategoryAdapter;
import ru.dmitriylebyodkin.shoplist.models.CategoryModel;
import ru.dmitriylebyodkin.shoplist.presenters.CategoriesPresenter;
import ru.dmitriylebyodkin.shoplist.room.data.Category;
import ru.dmitriylebyodkin.shoplist.views.CategoriesView;

public class CategoriesFragment extends MvpAppCompatFragment implements CategoriesView {

    private static final String TAG = "myLogs";
    @InjectPresenter
    CategoriesPresenter presenter;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tvNoItems)
    TextView tvNoItems;

    private List<Category> categoryList;
    private CategoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lists, container, false);
        ButterKnife.bind(this, view);

        tvNoItems.setText(R.string.no_categories);

        presenter.init(getActivity());

        return view;
    }

    @OnClick(R.id.floatingActionButton)
    public void createCategory(View v) {
        View view = getLayoutInflater().inflate(R.layout.dialog_create_list, null);
        EditText etTitle = view.findViewById(R.id.etTitle);

        String categoryTitle = getString(R.string.new_category);

        int categoryCount = CategoryModel.getContainsCount(getContext(), categoryTitle);

        if (categoryCount > 0) {
            categoryTitle += " " + String.valueOf(categoryCount+1);
        }

        etTitle.setText(categoryTitle);
        etTitle.setSelection(0, categoryTitle.length());

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.new_category)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();

                    if (title.length() != 0) {
                        Category category = new Category();
                        category.setTitle(title);

                        presenter.create(getContext(), category);
                    }
                })
                .setNeutralButton(R.string.cancel, null)
                .create();

        etTitle.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }

        alertDialog.show();
    }

    @Override
    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    @Override
    public void setAdapter() {
        adapter = new CategoryAdapter(getActivity(), categoryList);
    }

    @Override
    public void initList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void showNoItems() {
        tvNoItems.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void hideNoItems() {
        tvNoItems.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showEditDialog(Category category, int position) {
        View view = getLayoutInflater().inflate(R.layout.dialog_create_list, null);
        EditText etTitle = view.findViewById(R.id.etTitle);

        etTitle.setText(category.getTitle());
        etTitle.setSelection(category.getTitle().length());

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.editing_category)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();

                    if (title.length() != 0) {
                        category.setTitle(etTitle.getText().toString().trim());
                        presenter.update(getContext(), category, position);
                    }
                })
                .setNeutralButton(R.string.cancel, null)
                .create();

        etTitle.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }

        alertDialog.show();
    }

    @Override
    public void showDeleteDialog(Category category, int position) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.deleting)
                .setMessage(R.string.deleting_message_category)
                .setPositiveButton(R.string.yes, (dialog, which) -> presenter.remove(getContext(), category, position))
                .setNegativeButton(R.string.no, null)
                .create();

        alertDialog.show();
    }

    @Override
    public void updateAdapterItem(int position, Category category) {
        adapter.set(position, category);

        if (adapter.getItemCount() == 1) {
            presenter.hideNoItems();
        }
    }

    @Override
    public void removeAdapterItem(int position) {
        adapter.remove(position);

        if (adapter.getItemCount() == 0) {
            presenter.showNoItems();
        }

        Toast.makeText(getActivity(), R.string.is_removed, Toast.LENGTH_LONG).show();
    }

    @Override
    public void addAdapterItem(Category category) {
        adapter.add(category);

        if (adapter.getItemCount() == 1) {
            presenter.hideNoItems();
        }
    }
}
