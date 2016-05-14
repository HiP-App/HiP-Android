/*
 * Copyright (C) 2016 History in Paderborn App - Universität Paderborn
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
 */

package de.upb.hip.mobile.models.exhibit;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * A model class for all the pages for a single exhibit
 */
public class ExhibitPageList implements Serializable {

    private List<Page> mPages = new LinkedList<>();

    public List<Page> getPages() {
        return mPages;
    }
}
