package com.osmankartal.link_converter.model;

import com.osmankartal.link_converter.domain.model.Base62;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Base62Test {

    @Test
    void encodePositiveNumber() {
        assertEquals("3E", Base62.encode(200L));
    }

    @Test
    void encodeZero() {
        assertEquals("0", Base62.encode(0));
    }

    @Test
    void encodeLargestSnowflakeId() {
        assertEquals("AzL8n0Y58m7", Base62.encode((long) (Math.pow(2, 63) - 1)));
    }

    @Test
    void throwExceptionWhenNumberIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> Base62.encode(-1));
    }

}
