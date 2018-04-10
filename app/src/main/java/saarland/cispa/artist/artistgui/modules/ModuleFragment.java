/*
 * The ARTist Project (https://artist.cispa.saarland)
 *
 * Copyright (C) 2017 CISPA (https://cispa.saarland), Saarland University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package saarland.cispa.artist.artistgui.modules;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import saarland.cispa.artist.artistgui.R;
import saarland.cispa.artist.artistgui.modules.adapter.ModuleListAdapter;

import static android.app.Activity.RESULT_OK;

public class ModuleFragment extends Fragment implements ModuleContract.View {

    private static final int FILE_CHOOSER_REQUEST_CODE = 64967;
    private static final String ZIP_MIME_TYPE = "application/zip";

    private ModuleContract.Presenter mPresenter;

    private ProgressBar mProgressBar;
    private RecyclerView mModulesListView;
    private ModuleListAdapter mAdapter;

    @Override
    public void setPresenter(ModuleContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        mProgressBar = rootView.findViewById(R.id.progress_bar);
        mModulesListView = rootView.findViewById(R.id.recycler_view);

        mModulesListView.setHasFixedSize(true);

        final Context context = getContext();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        mModulesListView.setLayoutManager(mLayoutManager);

        mAdapter = new ModuleListAdapter(getContext());
        mModulesListView.setAdapter(mAdapter);

        mProgressBar.setVisibility(View.GONE);
        mModulesListView.setVisibility(View.VISIBLE);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_modules, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_module:
                openFileChooser();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void openFileChooser() {
        Intent intent = new Intent()
                .setType(ZIP_MIME_TYPE)
                .setAction(Intent.ACTION_GET_CONTENT)
                .addCategory(Intent.CATEGORY_OPENABLE);
        Intent chooser = Intent.createChooser(intent, getString(R.string.select_module_zip));
        startActivityForResult(chooser, FILE_CHOOSER_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri zipPath = data.getData();
            if (zipPath == null) {
                Toast.makeText(getContext(), "Couldn't import selected module.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            ContentResolver contentResolver = getActivity().getContentResolver();
            Uri uri = data.getData();
            ModuleZipParser moduleZipParser = new ModuleZipParser(contentResolver, uri);

            Module module = moduleZipParser.parseAndExtractModule();
            moduleZipParser.extractModule(module, getContext());
            addModule(module);
        }
    }

    @Override
    public void addModule(Module module) {
        mAdapter.addModule(module);
    }

    @Override
    public void removeModule(Module module) {
        mAdapter.removeModule(module);
    }
}
