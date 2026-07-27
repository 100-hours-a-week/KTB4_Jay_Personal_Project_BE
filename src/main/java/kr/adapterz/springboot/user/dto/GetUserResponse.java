package kr.adapterz.springboot.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GetUserResponse {
    private Long userId;
    private String email;
    private String nickname;
    private String profileImage;
    private Boolean deleted;
}
