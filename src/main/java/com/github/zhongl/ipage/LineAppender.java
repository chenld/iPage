/*
 * Copyright 2012 zhongl
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

package com.github.zhongl.ipage;

import com.github.zhongl.util.RAFileChannel;
import com.google.common.io.Closeables;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/** @author <a href="mailto:zhong.lunfu@gmail.com">zhongl<a> */
public class LineAppender {

    private final InnerPage page;

    protected LineAppender(File dir, long position) {
        Offset offset = new Offset(position);
        page = new InnerPage(new File(dir, offset.toString() + ".line"), offset);
    }

    public int append(ByteBuffer buffer) {
        return page.append(buffer);
    }

    public void force() {
        page.force();
    }

    public Page page() {
        return page;
    }

    private static class InnerPage extends Page {

        private final RAFileChannel channel;

        protected InnerPage(File file, Offset number) {
            super(file, number);
            try {
                channel = new RAFileChannel(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public int append(ByteBuffer buffer) {
            try {
                return channel.write(buffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void force() {
            try {
                channel.force(false);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                Closeables.closeQuietly(channel);
            }
        }
    }
}
