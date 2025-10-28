/**
 * Created on 2018/3/27.
 */
package org.vison.cache.anno.support;

import org.vison.cache.CacheConfigException;
import org.vison.cache.support.*;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author huangli
 */
public class DefaultEncoderParserTest {

    @Test
    public void parseQueryParameters() {
        assertEquals(0, DefaultEncoderParser.parseQueryParameters(null).size());
        assertEquals("b", DefaultEncoderParser.parseQueryParameters("a=b").get("a"));

        Map<String, String> m = DefaultEncoderParser.parseQueryParameters("a=b&c=d");
        assertEquals("b", m.get("a"));
        assertEquals("d", m.get("c"));
        m = DefaultEncoderParser.parseQueryParameters("a&b=");
        assertFalse(m.containsKey("a"));
        assertFalse(m.containsKey("b"));
    }

    @Test
    public void parseValueEncoder() {
        DefaultEncoderParser parser = new DefaultEncoderParser();
        AbstractValueEncoder encoder = (AbstractValueEncoder) parser.parseEncoder("kryo");
        assertEquals(KryoValueEncoder.class, encoder.getClass());
        assertTrue(encoder.isUseIdentityNumber());

        encoder = (AbstractValueEncoder) parser.parseEncoder("java?useIdentityNumber=false");
        assertEquals(JavaValueEncoder.class, encoder.getClass());
        assertFalse(encoder.isUseIdentityNumber());

        assertThrows(CacheConfigException.class, () -> parser.parseEncoder(null));
        assertThrows(CacheConfigException.class, () -> parser.parseEncoder("xxx"));
    }

    @Test
    public void parseValueDecoder() {
        DefaultEncoderParser parser = new DefaultEncoderParser();
        AbstractValueDecoder decoder = (AbstractValueDecoder) parser.parseDecoder("kryo");
        assertEquals(KryoValueDecoder.class, decoder.getClass());
        assertTrue(decoder.isUseIdentityNumber());

        decoder = (AbstractValueDecoder) parser.parseDecoder("java?useIdentityNumber=false");
        assertEquals(JavaValueDecoder.class, decoder.getClass());
        assertFalse(decoder.isUseIdentityNumber());

        assertThrows(CacheConfigException.class, () -> parser.parseDecoder(null));
        assertThrows(CacheConfigException.class, () -> parser.parseDecoder("xxx"));
    }
}
