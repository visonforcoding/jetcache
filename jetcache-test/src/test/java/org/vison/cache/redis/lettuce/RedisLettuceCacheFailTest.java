/**
 * Created on 2018/5/10.
 */
package org.vison.cache.redis.lettuce;

import org.vison.cache.*;
import org.vison.cache.support.Fastjson2KeyConvertor;
import io.lettuce.core.KeyValue;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author huangli
 */
public class RedisLettuceCacheFailTest {

    private RedisClient client;
    private RedisAsyncCommands asyncCommands;
    private Cache cache;
    private Function<byte[], Object> valueDecoder;

    @BeforeEach
    public void setup() {
        client = mock(RedisClient.class);
        StatefulRedisConnection connection = mock(StatefulRedisConnection.class);
        asyncCommands = mock(RedisAsyncCommands.class);
        when(client.connect((JetCacheCodec) any())).thenReturn(connection);
        when(connection.sync()).thenReturn(null);
        when(connection.async()).thenReturn(asyncCommands);

        valueDecoder = mock(Function.class);

        cache = RedisLettuceCacheBuilder.createRedisLettuceCacheBuilder()
                .redisClient(client)
                .valueDecoder(valueDecoder)
                .keyConvertor(Fastjson2KeyConvertor.INSTANCE) // logout the readable Key or Keys in error message
                .keyPrefix("fail_test")
                .buildCache();
    }

    @AfterEach
    public void teardown() {
        LettuceConnectionManager.defaultManager().removeAndClose(client);
    }

    private RedisFuture mockFuture(Object value, Throwable ex) {
        RedisFuture redisFuture = mock(RedisFuture.class);
        when(redisFuture.handle(any())).thenAnswer((invoke) -> {
            BiFunction function = invoke.getArgument(0);
            Object resultData = function.apply(value, ex);
            return CompletableFuture.completedFuture(resultData);
        });
        when(redisFuture.handleAsync(any(), any())).thenAnswer((invoke) -> {
            BiFunction function = invoke.getArgument(0);
            Executor executor = invoke.getArgument(1);
            CompletableFuture f = new CompletableFuture();
            executor.execute(() -> {
                Object resultData = function.apply(value, ex);
                f.complete(resultData);
            });
            return f;
        });
        return redisFuture;
    }

    @Test
    public void test_GET() {
        when(asyncCommands.get(any())).thenThrow(new RuntimeException("err"))
                .thenReturn(mockFuture(null, new RuntimeException()));
        CacheGetResult cr = cache.GET("K");
        assertEquals(CacheResultCode.FAIL, cr.getResultCode());
        assertNull(cr.getValue());

        cr = cache.GET("K");
        assertEquals(CacheResultCode.FAIL, cr.getResultCode());
        assertNull(cr.getValue());
    }

    @Test
    public void test_GET_DecodeValueFailed() {
        String exceptionMessage = "decodeValueFailed";
        when(valueDecoder.apply(any())).thenThrow(new RuntimeException(exceptionMessage));

        RedisFuture rf = mockFuture(new byte[]{0x01, 0x02}, null);
        when(asyncCommands.get(any())).thenReturn(rf);

        CacheGetResult cr = cache.GET("K");
        assertEquals(CacheResultCode.FAIL, cr.getResultCode());
        assertNull(cr.getValue());
        assertTrue(cr.getMessage().contains(exceptionMessage));
    }

    @Test
    public void test_GET_ALL() {
        when(asyncCommands.mget(any())).thenThrow(new RuntimeException("err"))
                .thenReturn(mockFuture(null, new RuntimeException()));
        HashSet s = new HashSet();
        s.add("K");

        MultiGetResult cr = cache.GET_ALL(s);
        assertEquals(CacheResultCode.FAIL, cr.getResultCode());
        assertNull(cr.getValues());

        cr = cache.GET_ALL(s);
        assertEquals(CacheResultCode.FAIL, cr.getResultCode());
        assertNull(cr.getValues());
    }

    @Test
    public void test_GET_ALL_DecodeValueFailed() {
        String exceptionMessage = "decodeValueFailed";
        when(valueDecoder.apply(any())).thenThrow(new RuntimeException(exceptionMessage));

        KeyValue<byte[],byte[]> kv = KeyValue.just(new byte[]{0x01}, new byte[]{0x01});
        RedisFuture rf = mockFuture(Arrays.asList(kv), null);
        when(asyncCommands.mget(any())).thenReturn(rf);

        HashSet s = new HashSet();
        s.add("K");

        MultiGetResult cr = cache.GET_ALL(s);
        assertEquals(CacheResultCode.FAIL, cr.getResultCode());
        assertNull(cr.getValues());
        assertTrue(cr.getMessage().contains(exceptionMessage));
    }

    @Test
    public void test_PUT() {
        when(asyncCommands.psetex(any(), anyLong(), any()))
                .thenThrow(new RuntimeException("err"))
                .thenReturn(mockFuture(null, new RuntimeException()))
                .thenReturn(mockFuture("XXX", null));

        CacheResult cr = cache.PUT("K", "V");
        assertEquals(CacheResultCode.FAIL, cr.getResultCode());

        cr = cache.PUT("K", "V");
        assertEquals(CacheResultCode.FAIL, cr.getResultCode());

        cr = cache.PUT("K", "V");
        assertEquals(CacheResultCode.FAIL, cr.getResultCode());
        assertEquals("XXX", cr.getMessage());
    }

    @Test
    public void test_PUT_ALL() {
        when(asyncCommands.psetex(any(), anyLong(), any()))
                .thenThrow(new RuntimeException("err"));
        Map m = new HashMap();
        m.put("K", "V");
        CacheResult cr = cache.PUT_ALL(m);
        assertEquals(CacheResultCode.FAIL, cr.getResultCode());
    }

    @Test
    public void test_REMOVE() {
        when(asyncCommands.del((byte[]) any()))
                .thenThrow(new RuntimeException("err"))
                .thenReturn(mockFuture(null, new RuntimeException()))
                .thenReturn(mockFuture(null, null))
                .thenReturn(mockFuture(1000L, null));
        CacheResult cr = cache.REMOVE("K");
        assertEquals(CacheResultCode.FAIL, cr.getResultCode());

        cr = cache.REMOVE("K");
        assertEquals(CacheResultCode.FAIL, cr.getResultCode());

        cr = cache.REMOVE("K");
        assertEquals(CacheResultCode.FAIL, cr.getResultCode());

        cr = cache.REMOVE("K");
        assertEquals(CacheResultCode.FAIL, cr.getResultCode());
    }

    @Test
    public void test_REMOVE_ALL() {
        when(asyncCommands.del(ArgumentMatchers.any()))
                .thenThrow(new RuntimeException("err"))
                .thenReturn(mockFuture(null, new RuntimeException()));
        HashSet s = new HashSet();
        s.add("K");
        CacheResult cr = cache.REMOVE_ALL(s);
        assertEquals(CacheResultCode.FAIL, cr.getResultCode());

        cr = cache.REMOVE_ALL(s);
        assertEquals(CacheResultCode.FAIL, cr.getResultCode());
    }

    @Test
    public void test_PUT_IF_ABSENT() {
        when(asyncCommands.set(any(), any(), any()))
                .thenThrow(new RuntimeException("err"))
                .thenReturn(mockFuture(null, new RuntimeException()))
                .thenReturn(mockFuture("XXX", null));
        CacheResult cr = cache.PUT_IF_ABSENT("K", "V", 1, TimeUnit.SECONDS);
        assertEquals(CacheResultCode.FAIL, cr.getResultCode());

        cr = cache.PUT_IF_ABSENT("K", "V", 1, TimeUnit.SECONDS);
        assertEquals(CacheResultCode.FAIL, cr.getResultCode());

        cr = cache.PUT_IF_ABSENT("K", "V", 1, TimeUnit.SECONDS);
        assertEquals(CacheResultCode.FAIL, cr.getResultCode());
        assertEquals("XXX", cr.getMessage());
    }
}
