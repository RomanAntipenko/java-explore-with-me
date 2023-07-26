package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.user.NewUserRequest;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.error.FieldUniqueException;
import ru.practicum.ewm.error.ObjectNotFoundException;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(NewUserRequest newUserRequest) {
        try {
            User user = userRepository.save(UserMapper.toUserFromNew(newUserRequest));
            return UserMapper.toUserDtoFromUser(user);
        } catch (DataIntegrityViolationException e) {
            throw new FieldUniqueException(e.getMessage());
        }
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException(String.format("User with id=\"%s\" was not found", userId));
        }
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        List<User> users;
        PageRequest pageRequest = PageRequest.of(from / size, size);
        if (ids != null) {
            users = userRepository.findAllById(ids);
        } else {
            users = userRepository.findAll(pageRequest).toList();
        }
        return users.stream()
                .map(UserMapper::toUserDtoFromUser)
                .collect(Collectors.toList());
    }
}
