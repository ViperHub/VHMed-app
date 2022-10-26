package com.doctris.care.ui.fragment;

import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.doctris.care.R;
import com.doctris.care.domain.ListResponse;
import com.doctris.care.entities.Blog;
import com.doctris.care.repository.BlogRepository;
import com.doctris.care.ui.adapter.BlogAdapter;
import com.doctris.care.ui.adapter.BlogHorizontalAdapter;

import java.util.ArrayList;
import java.util.List;

public class BlogFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    private RecyclerView recyclerViewBlog;
    private RecyclerView recyclerViewBlogHorizontal;
    private ProgressBar progressBar;
    private NestedScrollView nestedScrollView;
    private int page = 1;
    private static final int LIMIT = 10;
    private int totalPage = 0;
    private List<Blog> listBlog;
    private List<Blog> listBlogHorizontal;
    private final String fillter = null;

    private void bindingViews(View view) {
        recyclerViewBlogHorizontal = view.findViewById(R.id.recyclerview_blog_horizontal);
        recyclerViewBlog = view.findViewById(R.id.recyclerview_blog);
        progressBar = view.findViewById(R.id.progressBar);
        nestedScrollView = view.findViewById(R.id.idNestedSV);
    }

    private void getBlogData(List<Blog> blogList) {
        progressBar.setVisibility(View.VISIBLE);
        LiveData<ListResponse<Blog>> blogLiveData = BlogRepository.getInstance().getBlog(page, LIMIT, null, fillter);
        blogLiveData.observe(getActivity(), blogs -> {
            if (blogs != null) {
                totalPage = blogs.getTotalPages();
                blogList.addAll(blogs.getItems());
                BlogAdapter blogAdapter = new BlogAdapter(blogList, getActivity());
                recyclerViewBlog.setAdapter(blogAdapter);
            }
            progressBar.setVisibility(View.GONE);
        });
    }

    private void getBlogHorizontalData(List<Blog> listBlogHorizontal){
        progressBar.setVisibility(View.VISIBLE);
        LiveData<ListResponse<Blog>> blogHorizontalLiveData = BlogRepository.getInstance().getBlog(page, 5, "-viewer", fillter);
        blogHorizontalLiveData.observe(getActivity(), blogHorizontals -> {
            if (blogHorizontals != null) {
                totalPage = blogHorizontals.getTotalPages();
                listBlogHorizontal.addAll(blogHorizontals.getItems());
                BlogHorizontalAdapter blogHorizontalAdapter = new BlogHorizontalAdapter(listBlogHorizontal, getActivity());
                recyclerViewBlogHorizontal.setAdapter(blogHorizontalAdapter);
            }
            progressBar.setVisibility(View.GONE);
        });
    }

    private void initLinearLayout() {
        LinearLayoutManager linearLayoutManagerHORIZONTAL = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewBlogHorizontal.setLayoutManager(linearLayoutManagerHORIZONTAL);

        getBlogHorizontalData(listBlogHorizontal);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewBlog.setLayoutManager(linearLayoutManager);

        getBlogData(listBlog);

        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            progressBar.setVisibility(View.VISIBLE);
            if (scrollY > (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) * 0.8 && page < totalPage) {
                page++;
                getBlogHorizontalData(listBlogHorizontal);
                getBlogData(listBlog);
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindingViews(view);
        listBlog = new ArrayList<>();
        listBlogHorizontal = new ArrayList<>();
        initLinearLayout();
    }
}