package org.devlive.sdk.openai.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.devlive.sdk.openai.exception.ParamException;
import org.devlive.sdk.openai.model.CompletionModel;
import org.devlive.sdk.openai.utils.EnumsUtils;

import java.util.List;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompletionChatEntity
{
    @JsonProperty(value = "model")
    private String model;

    @JsonProperty(value = "messages")
    private List<CompletionMessageEntity> messages;

    @JsonProperty(value = "temperature")
    private Double temperature;

    @JsonProperty(value = "max_tokens")
    private Integer maxTokens;

    @JsonProperty(value = "top_p")
    private Double topP;

    private CompletionChatEntity(CompletionChatEntityBuilder builder)
    {
        if (ObjectUtils.isEmpty(builder.model)) {
            builder.model(CompletionModel.GPT_35_TURBO.getName());
        }
        this.model = builder.model;
        this.messages = builder.messages;

        if (ObjectUtils.isEmpty(builder.temperature)) {
            builder.temperature(1D);
        }
        this.temperature = builder.temperature;

        if (ObjectUtils.isEmpty(builder.maxTokens)) {
            builder.maxTokens(256);
        }
        this.maxTokens = builder.maxTokens;

        if (ObjectUtils.isEmpty(builder.topP)) {
            builder.topP(1D);
        }
        this.topP = builder.topP;
    }

    public static class CompletionChatEntityBuilder
    {
        public CompletionChatEntityBuilder model(String model)
        {
            if (StringUtils.isEmpty(model)) {
                model = CompletionModel.GPT_35_TURBO.getName();
            }

            CompletionModel completionModel = EnumsUtils.getCompleteModel(model);
            if (ObjectUtils.isNotEmpty(completionModel)) {
                switch (completionModel) {
                    case GPT_35_TURBO:
                    case GPT_35_TURBO_16K:
                    case GPT_35_TURBO_0613:
                    case GPT_35_TURBO_16K_0613:
                    case GPT_4:
                    case GPT_4_32K:
                    case GPT_4_0613:
                    case GPT_4_32K_0613:
                    case TEXT_DAVINCI_002:
                    case TEXT_DAVINCI_003:
                    case CODE_DAVINCI_002:
                        model = completionModel.getName();
                        break;
                    default:
                        throw new ParamException(String.format("Not support completion model %s", model));
                }
            }
            this.model = model;
            return this;
        }

        public CompletionChatEntityBuilder temperature(Double temperature)
        {
            if (temperature < 0 || temperature > 2) {
                throw new ParamException(String.format("Invalid temperature: %s , between 0 and 2", temperature));
            }
            this.temperature = temperature;
            return this;
        }

        public CompletionChatEntityBuilder maxTokens(Integer maxTokens)
        {
            CompletionModel completionModel = EnumsUtils.getCompleteModel(this.model);
            if (ObjectUtils.isNotEmpty(this.model) && maxTokens > completionModel.getMaxTokens()) {
                throw new ParamException(String.format("Invalid maxTokens: %s, Cannot be larger than the model default configuration %s", maxTokens, completionModel.getMaxTokens()));
            }
            this.maxTokens = maxTokens;
            return this;
        }

        public CompletionChatEntity build()
        {
            return new CompletionChatEntity(this);
        }
    }
}
