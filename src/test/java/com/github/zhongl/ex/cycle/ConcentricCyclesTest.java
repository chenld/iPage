package com.github.zhongl.ex.cycle;

import com.github.zhongl.ex.codec.Codec;
import com.github.zhongl.ex.codec.ComposedCodecBuilder;
import com.github.zhongl.ex.codec.LengthCodec;
import com.github.zhongl.ex.codec.StringCodec;
import com.github.zhongl.util.FileTestContext;
import org.junit.Test;

/** @author <a href="mailto:zhong.lunfu@gmail.com">zhongl<a> */
public class ConcentricCyclesTest extends FileTestContext {
    @Test
    public void usage() throws Exception {
        dir = testDir("usage");
        Codec codec = ComposedCodecBuilder.compose(new StringCodec())
                                          .with(LengthCodec.class)
                                          .build();
        int blockSize = 64;
        int pageCapacityOfBlocks = 10;
        new ConcentricCycles(dir, codec, blockSize, pageCapacityOfBlocks);
    }
}
