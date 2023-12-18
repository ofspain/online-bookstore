package com.interswitch.bookstore.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class GrantedAuthorityDeserializer extends StdDeserializer<Collection<? extends GrantedAuthority>> {

    public GrantedAuthorityDeserializer() {
        this(null);
    }

    protected GrantedAuthorityDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Collection<? extends GrantedAuthority> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        if (node.isArray()) {
            Iterator<JsonNode> elements = node.elements();
            while (elements.hasNext()) {
                JsonNode element = elements.next();
                //{"authority":"ROLE_USER"}
                Map<String, String> resultMap = new ObjectMapper().readValue(element.toString(), new TypeReference<Map<String, String>>() {});
                authorities.add(new SimpleGrantedAuthority(resultMap.get("authority")));
            }
        }

        return authorities;
    }
}