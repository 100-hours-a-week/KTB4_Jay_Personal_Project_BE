package kr.adapterz.springboot.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatMessageRequest(
        @NotBlank(message = "메시지는 비어 있을 수 없습니다.")
        @Size(
                max = 1000,
                message = "메시지는 1000자를 초과할 수 없습니다."
        )
        String content
) {
}