package my.project.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@AllArgsConstructor
@Service
public class MockMvcService {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private static ResultActions resultActions;

    @SneakyThrows
    public MockMvcService performGet(String uri, ResultMatcher status) {
        resultActions = mockMvc.perform(get(uri)).andExpect(status);
        return this;
    }

    @SneakyThrows
    public <T> MockMvcService performPost(String uri, T body, ResultMatcher status) {
        resultActions = mockMvc.perform(post(uri).content(serialize(body))).andExpect(status);
        return this;
    }

    @SneakyThrows
    public MockMvcService performPatch(String uri, ResultMatcher status) {
        resultActions = mockMvc.perform(patch(uri)).andExpect(status);
        return this;
    }

    @SneakyThrows
    public MockMvcService performDelete(String uri, ResultMatcher status) {
        resultActions = mockMvc.perform(delete(uri)).andExpect(status);
        return this;
    }

    @SneakyThrows
    public <T> String serialize(T value) {
        return objectMapper.writeValueAsString(value);
    }

    @SneakyThrows
    public <T> T getResponse(Class<T> responseType) {
        T response = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), responseType);
        return objectMapper.convertValue(response, responseType);
    }

    @SneakyThrows
    public <T> T getResponse(TypeReference<T> responseTypeReference) {
        T response = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), responseTypeReference);
        return objectMapper.convertValue(response, responseTypeReference);
    }
}
