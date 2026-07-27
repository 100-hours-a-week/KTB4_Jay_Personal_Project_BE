package kr.adapterz.springboot.user;

import kr.adapterz.springboot.global.exception.BadRequestException;
import kr.adapterz.springboot.global.exception.NotFoundException;
import kr.adapterz.springboot.global.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserReader {

    private final UserRepository userRepository;

    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    public User getActiveUser(Long userId) {
        User user = getUser(userId);

        if (user.isDeleted()) {
            throw new BadRequestException("deleted_user");
        }

        return user;
    }
}