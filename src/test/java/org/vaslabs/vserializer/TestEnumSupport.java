package org.vaslabs.vserializer;

import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * Created by vnicolaou on 04/06/16.
 */
public class TestEnumSupport {

    VSerializer vSerializer = new AlphabeticalSerializer();
    byte[] data;
    EnumEncapsulator enumEncapsulator;
    private EnumEncapsulator recoveredEnumEncapsulator;
    private EnumArrayEncapsulator enumArrayEncapsulator;

    @Before
    public void setUp() {
        vSerializer = new AlphabeticalSerializer();
    }

    @Test
    public void test_enum_serialization_deserialization() {
        whenInstantiatingClassWithEnums();
        serializeIt();
        shouldHaveSize(3);
        andDeserialize();
        shouldBeEquals();
    }

    @Test
    public void test_serialization_of_enum_arrays() {
        enumArrayEncapsulator = new EnumArrayEncapsulator();
        data = vSerializer.serialize(enumArrayEncapsulator);
        shouldHaveSize(8);
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        assertEquals(2, byteBuffer.getInt());
        assertEquals(1, byteBuffer.get());
        assertEquals(TimeUnit.DAYS.ordinal(), byteBuffer.get());
        assertEquals(1, byteBuffer.get());
        assertEquals(TimeUnit.SECONDS.ordinal(), byteBuffer.get());

        EnumArrayEncapsulator recoveredEnumArrayEncapsulator = vSerializer.deserialise(data, EnumArrayEncapsulator.class);
        assertEquals(enumArrayEncapsulator.timeUnits.length, recoveredEnumArrayEncapsulator.timeUnits.length);
        assertEquals(enumArrayEncapsulator.timeUnits[0], recoveredEnumArrayEncapsulator.timeUnits[0]);
        assertEquals(enumArrayEncapsulator.timeUnits[1], recoveredEnumArrayEncapsulator.timeUnits[1]);
    }

    @Test
    public void test_with_null_enums() {
        whenInstantiatingClassWithEnums();
        enumEncapsulator.timeUnitDays = null;
        serializeIt();
        andDeserialize();
        shouldBeEquals();
    }

    private void shouldBeEquals() {
        assertEquals(enumEncapsulator.timeUnitDays, recoveredEnumEncapsulator.timeUnitDays);
        assertEquals(enumEncapsulator.timeUnitMinutes, recoveredEnumEncapsulator.timeUnitMinutes);
        assertEquals(enumEncapsulator.timeUnitSeconds, recoveredEnumEncapsulator.timeUnitSeconds);
    }

    private void andDeserialize() {
        recoveredEnumEncapsulator = vSerializer.deserialise(data, EnumEncapsulator.class);
    }

    private void shouldHaveSize(int expectedSize) {
        assertEquals(expectedSize, data.length);
    }

    private void serializeIt() {
        data = vSerializer.serialize(enumEncapsulator);
    }

    private void whenInstantiatingClassWithEnums() {
        enumEncapsulator = new EnumEncapsulator();
        enumEncapsulator.timeUnitDays = TimeUnit.DAYS;
        enumEncapsulator.timeUnitMinutes = TimeUnit.MINUTES;
        enumEncapsulator.timeUnitSeconds = TimeUnit.SECONDS;
    }

    @Test
    public void test_circular_serializer_with_enums() {
        vSerializer = new ReferenceSensitiveAlphabeticalSerializer();
        whenInstantiatingClassWithEnums();
        serializeIt();
        shouldHaveSize(7);
        andDeserialize();
        shouldBeEquals();
    }


    protected static class EnumEncapsulator implements Serializable{
        protected TimeUnit timeUnitDays;
        protected TimeUnit timeUnitSeconds;
        protected TimeUnit timeUnitMinutes;
    }
    private static class EnumArrayEncapsulator {
        private TimeUnit[] timeUnits = new TimeUnit[] {TimeUnit.DAYS, TimeUnit.SECONDS};
    }
}
