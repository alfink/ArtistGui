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

import android.content.Intent;

import java.util.List;

import saarland.cispa.artist.artistgui.base.BasePresenter;
import saarland.cispa.artist.artistgui.base.BaseView;
import saarland.cispa.artist.artistgui.database.Module;

public interface ModuleContract {
    interface View extends BaseView<ModuleContract.Presenter> {
        void openFileChooser();

        void addModules(List<Module> modules);

        void removeModules(Module[] modules);

        void moduleImportFailed();

        void showRemovalDialog(Module module);
    }

    interface Presenter extends BasePresenter {
        void addModule(Intent fileChooserIntent);

        void removeModule(Module module);
    }
}
