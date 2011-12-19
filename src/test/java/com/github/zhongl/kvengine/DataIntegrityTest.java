/*
 * Copyright 2011 zhongl
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.github.zhongl.kvengine;

import com.github.zhongl.util.FileBase;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/** @author <a href="mailto:zhong.lunfu@gmail.com">zhongl<a> */
public class DataIntegrityTest extends FileBase {

    private DataIntegrity dataIntegrity;


    @Test
    public void unsafeDataState() throws Exception {
        dir = testDir("unsafeDataState");
        dir.mkdirs();
        dataIntegrity = new DataIntegrity(dir);
        assertThat(dataIntegrity.validate(), is(false));
    }

    @Test
    public void safeDataState() throws Exception {
        dir = testDir("safeDataState");
        dir.mkdirs();
        dataIntegrity = new DataIntegrity(dir);
        dataIntegrity.safeClose();
        assertThat(dataIntegrity.validate(), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nonExistDir() throws Exception {
        dir = testDir("nonExistDir");
        new DataIntegrity(dir);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidDir() throws Exception {
        dir = testDir("invalidDir");
        dir.createNewFile();
        new DataIntegrity(dir);
    }
}