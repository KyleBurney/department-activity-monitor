package com.store.app.departmentactivitymonitor.logging;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import java.io.StringWriter;
import java.util.Iterator;
import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.ObjectAppendingMarker;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static net.logstash.logback.marker.Markers.append;

public class LogMarks extends ObjectAppendingMarker {

    private static final JsonFactory FACTORY = new MappingJsonFactory().enable(JsonGenerator.Feature.ESCAPE_NON_ASCII);

    public LogMarks(String fieldName, Object object) {
        super(fieldName, object);
    }

    public static LogMarks withTraceContext(String traceContext) {
        return new LogMarks("trace_context", traceContext);
    }

    public static LogMarks withKafkaData(String topic, String data) {
        LogMarks marks = new LogMarks("kafka_topic", topic);
        marks.add(append("kafka_data", data));
        return marks;
    }

    public LogMarks addTraceContext(String traceContext) {
        this.add(append("trace_context", traceContext));
        return this;
    }

    public LogMarks addKafkaData(String topic, String data) {
        this.add(append("kafka_topic", topic));
        this.add(append("kafka_data", data));
        return this;
    }

    public LogMarks addStatus(HttpStatus status) {
        this.add(append("status_code", status.value()));
        return this;
    }

    public LogMarks addHttpRequest(HttpMethod method, HttpHeaders headers, String url) {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("method", method.name());
        request.put("url", url);

        { // headers
            if (headers == null) {
                headers = new HttpHeaders();
            }
            Map<String, String> h = new LinkedHashMap<>(headers.size());
            for (Entry<String, List<String>> entry : headers.entrySet()) {
                h.put(entry.getKey(), String.join(", ", entry.getValue()));
            }
            request.put("headers", h);
        }

        this.add(append("http_request", request));
        return this;
    }

    @Override
    public String toString() {
        return writeMarksToJson(this);
    }

    private String writeMarksToJson(LogstashMarker marks) {
        StringWriter writer = new StringWriter();
        try (JsonGenerator generator = FACTORY.createGenerator(writer)){
            generator.writeStartObject();
            marks.writeTo(generator);
            if (marks.hasReferences()) {
                for (Iterator<?> i = marks.iterator(); i.hasNext(); ) {
                    LogstashMarker next = (LogstashMarker) i.next();
                    next.writeTo(generator);
                }
            }
            generator.writeEndObject();
            generator.flush();
        } catch (Exception ignored) {}

        return writer.toString();
    }
}
