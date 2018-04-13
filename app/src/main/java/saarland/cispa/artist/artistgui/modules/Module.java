package saarland.cispa.artist.artistgui.modules;

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
public class Module {

    // JSON element keys
    public static final String NAME_ELEMENT = "name";
    public static final String DESCRIPTION_ELEMENT = "description";
    public static final String AUTHOR_ELEMENT = "author";
    public static final String VERSION_ELEMENT = "version";
    public static final String SUPPORTED_ARCHS_ELEMENT = "supported_archs";
    public static final String HAS_CODELIB_ELEMENT = "has_codelib";

    public String name;
    public String description;
    public String author;
    public int version;
    public String arch;
    public boolean hasCodeLib;

    public Module(String name, String description, String author, int version, String arch,
           boolean hasCodeLib) {
        this.name = name;
        this.description = description;
        this.author = author;
        this.version = version;
        this.arch = arch;
        this.hasCodeLib = hasCodeLib;
    }

    @Override
    public String toString() {
        return "Module{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", author='" + author + '\'' +
                ", version=" + version +
                ", arch='" + arch + '\'' +
                ", hasCodeLib=" + hasCodeLib +
                '}';
    }
}
