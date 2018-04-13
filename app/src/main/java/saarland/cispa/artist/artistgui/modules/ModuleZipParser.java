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
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class ModuleZipParser {

    private static final String TAG = "ModuleZipParser";
    private static final String MANIFEST_FILE_NAME = "Manifest.json";

    private static final int NO_MATCH = 0;
    private static final int MATCH_ARTIST_SO = 1;
    private static final int MATCH_CODELIB = 2;

    private ContentResolver mContentResolver;
    private Uri mUri;

    ModuleZipParser(@NonNull ContentResolver contentResolver, @NonNull Uri uri) {
        this.mContentResolver = contentResolver;
        this.mUri = uri;
    }

    public Module parseAndExtractModule() {
        Module module = null;
        try {
            String jsonString = extractManifestAsString();
            module = parseManifest(jsonString);
        } catch (IOException | JSONException e) {
            // TODO: Proper error handling
            e.printStackTrace();
        }
        return module;
    }

    private Module parseManifest(String manifest) throws JSONException {
        JSONObject root = new JSONObject(manifest);
        String name = root.getString(Module.NAME_ELEMENT);
        String description = root.getString(Module.DESCRIPTION_ELEMENT);
        String author = root.getString(Module.AUTHOR_ELEMENT);
        int version = root.getInt(Module.VERSION_ELEMENT);
        boolean hasCodeLib = root.getBoolean(Module.HAS_CODELIB_ELEMENT);

        // TODO: Currently we do only support 32bit
        JSONArray moduleSupportedArchs = root.getJSONArray(Module.SUPPORTED_ARCHS_ELEMENT);
        List<String> deviceSupportedArchs = Arrays.asList(Build.SUPPORTED_32_BIT_ABIS);

        // TODO: Build.SUPPORTED_32_BIT_ABIS return supported ABIS in most preferred order,
        // TODO: we should try to use the most preferred ABI
        String selectedArch = null;
        for (int i = 0; i < moduleSupportedArchs.length(); i++) {
            String c = moduleSupportedArchs.getString(i);
            if (deviceSupportedArchs.contains(c)) {
                selectedArch = c;
            }
        }

        return new Module(name.replace(" ", "_").toLowerCase(),
                description, author, version, selectedArch, hasCodeLib);
    }


    private String extractManifestAsString() throws IOException {
        String result;
        try (InputStream inputStream = mContentResolver.openInputStream(mUri)) {
            ZipInputStream zipStream = new ZipInputStream(inputStream);
            ZipEntry entry = zipStream.getNextEntry();

            while (entry == null || !MANIFEST_FILE_NAME.equals(entry.getName())) {
                entry = zipStream.getNextEntry();
            }

            // TODO: Set file size limit to prevent out of memory exceptions
            Long size = entry.getSize();
            byte[] manifestBytes = readEntryIntoByteArray(zipStream, entry);
            result = new String(manifestBytes, 0, size.intValue(), "UTF-8");
        }
        return result;
    }

    public boolean extractModule(Module module, Context context) {
        File modulesDir = context.getDir("modules", Context.MODE_PRIVATE);

        String dirName = module.name;
        File thisModuleDir = new File(modulesDir, dirName);
        if (thisModuleDir.exists() && thisModuleDir.delete()) {
            Log.d(TAG, "Deleted old module.");
        }

        String moduleSoPath = "lib/" + module.arch + "/artist-module.so";

        List<String> filesToExtract = new ArrayList<>(module.hasCodeLib ? 2 : 1);
        filesToExtract.add(moduleSoPath);
        if (module.hasCodeLib) {
            filesToExtract.add("codelib.apk");
        }

        if (thisModuleDir.mkdir()) {
            try (InputStream inputStream = mContentResolver.openInputStream(mUri)) {
                ZipInputStream zipInputStream = new ZipInputStream(inputStream);

                ZipEntry entry;
                while ((entry = zipInputStream.getNextEntry()) != null) {
                    String entryName = entry.getName();

                    int type = filesToExtract.indexOf(entryName);
                    int equalsType = type == 0 ? MATCH_ARTIST_SO :
                            type == 1 ? MATCH_CODELIB : NO_MATCH;

                    if (equalsType != NO_MATCH) {
                        switch (equalsType) {
                            case MATCH_ARTIST_SO:
                                File file = new File(thisModuleDir, "artist-module.so");
                                if (file.exists() && file.delete()) {
                                    Log.v(TAG, "Removed old artist-module.so");
                                }
                                extractZipEntry(zipInputStream, file);
                                break;
                            case MATCH_CODELIB:
                                File file2 = new File(thisModuleDir, "codelib.apk");
                                if (file2.exists() && file2.delete()) {
                                    Log.v(TAG, "Removed old codelib.apk");
                                }
                                extractZipEntry(zipInputStream, file2);
                                break;
                        }
                    }
                }

            } catch (IOException e) {
                // TODO: Proper error handling if we couldn't extract files.
                return false;
            }
            return true;
        }
        return false;
    }

    private byte[] readEntryIntoByteArray(ZipInputStream zipInputStream, ZipEntry entry)
            throws IOException {
        Long compressedSize = entry.getSize();
        byte[] bytes = new byte[compressedSize.intValue()];
        zipInputStream.read(bytes);
        return bytes;
    }

    private void extractZipEntry(ZipInputStream inputStream, File destination)
            throws IOException {
        FileOutputStream outputStream = new FileOutputStream(destination);
        byte[] buffer = new byte[1024];
        int numberOfReadBytes;
        while ((numberOfReadBytes = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, numberOfReadBytes);
        }
        outputStream.close();
    }
}
