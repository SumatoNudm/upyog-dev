package tech.sumato.statefinance.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import org.egov.common.contract.request.RequestInfo;
import tech.sumato.statefinance.web.models.GenericRequest;

import java.io.IOException;

public class GenericRequestDeserializer
        extends JsonDeserializer<GenericRequest<?>>
        implements ContextualDeserializer {

    private JavaType dataType;

    public GenericRequestDeserializer() {
    }

    private GenericRequestDeserializer(JavaType dataType) {
        this.dataType = dataType;
    }

    @Override
    public JsonDeserializer<?> createContextual(
            DeserializationContext ctxt,
            BeanProperty property) {

        JavaType wrapperType = ctxt.getContextualType();
        JavaType containedType = wrapperType.containedType(0);

        return new GenericRequestDeserializer(containedType);
    }

    @Override
    public GenericRequest<?> deserialize(
            JsonParser p,
            DeserializationContext ctxt) throws IOException {

        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode root = mapper.readTree(p);

        GenericRequest<Object> request = new GenericRequest<>();

        // Deserialize RequestInfo
        JsonNode requestInfoNode = root.get("RequestInfo");
        if (requestInfoNode != null) {
            RequestInfo requestInfo =
                    mapper.treeToValue(requestInfoNode, RequestInfo.class);
            request.setRequestInfo(requestInfo);
        }

        JsonNode tenantNode = root.get("tenantId");
        if (tenantNode != null && !tenantNode.isNull()) {
            request.setTenantId(tenantNode.asText());
        }


        // Deserialize generic Data
        JsonNode dataNode = root.get("data");
        if (dataNode != null && dataType != null) {
            Object data = mapper.convertValue(dataNode, dataType);
            request.setData(data);
        }

        return request;
    }
}
