package ba.unsa.etf.si.mainserver.serializers;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class CustomLocalDateTimeSerializer
        extends StdSerializer<LocalDateTime> {

    public CustomLocalDateTimeSerializer() {
        this(null);
    }

    public CustomLocalDateTimeSerializer(Class<LocalDateTime> t) {
        super(t);
    }

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider arg2) throws IOException {
        gen.writeString(String.valueOf(value.toInstant(OffsetDateTime.now().getOffset()).toEpochMilli()));
    }
}