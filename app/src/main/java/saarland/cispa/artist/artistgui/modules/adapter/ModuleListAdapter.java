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

package saarland.cispa.artist.artistgui.modules.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import saarland.cispa.artist.artistgui.R;
import saarland.cispa.artist.artistgui.modules.Module;

public class ModuleListAdapter extends RecyclerView.Adapter<ModuleListAdapter.ViewHolder> {

    private Context mContext;
    private List<Module> mDataset;

    // Reference for performance instead of slow findByView lookup
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mModuleIcon;
        TextView mModuleName;
        TextView mAuthor;

        ViewHolder(View v, ImageView moduleIcon, TextView moduleName, TextView author) {
            super(v);
            mModuleIcon = moduleIcon;
            mModuleName = moduleName;
            mAuthor = author;
        }
    }

    public ModuleListAdapter(Context context) {
        mContext = context;
        mDataset = new ArrayList<>();
        File modulesDir = context.getDir("modules", Context.MODE_PRIVATE);
        for (File file : modulesDir.listFiles()) {
            mDataset.add(new Module(file.getName(), "", "", 0,
                    "", ""));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_list_entry,
                parent, false);

        ImageView appIcon = view.findViewById(R.id.app_icon);
        TextView appName = view.findViewById(R.id.app_name);
        TextView packageName = view.findViewById(R.id.package_name);

        return new ViewHolder(view, appIcon, appName, packageName);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Module module = mDataset.get(position);
        String entry = module.name;

//        Drawable appIcon = mAppIconCache.get(packageEntry);
//        holder.mAppIcon.setImageDrawable(appIcon);

        holder.mModuleName.setText(entry);

//        holder.mPackageName.setText(packageName);
        holder.itemView.setOnClickListener((view) -> {
            removeModule(module);
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void addModule(Module module) {
        mDataset.add(module);
        notifyDataSetChanged();
    }

    public void removeModule(Module module) {
        File modulesDir = mContext.getDir("modules", Context.MODE_PRIVATE);
        File moduleDir = new File(modulesDir, module.name);
        deleteDirectory(moduleDir);

        mDataset.remove(module);
        notifyDataSetChanged();
    }

    /**
     * https://stackoverflow.com/questions/3775694/deleting-folder-from-java
     * @param directory
     * @return
     */
    private static boolean deleteDirectory(File directory) {
        if(directory.exists()){
            File[] files = directory.listFiles();
            if(null!=files){
                for(int i=0; i<files.length; i++) {
                    if(files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    }
                    else {
                        files[i].delete();
                    }
                }
            }
        }
        return(directory.delete());
    }
}
