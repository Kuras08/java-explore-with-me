package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;

    @Override
    public UserDto createUser(NewUserRequest dto) {
        User user = UserMapper.toEntity(dto);
        return UserMapper.toDto(userRepo.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);

        if (ids == null || ids.isEmpty()) {
            return userRepo.findAll(pageable).stream()
                    .map(UserMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            return userRepo.findAllByIdIn(ids, pageable).stream()
                    .map(UserMapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new NotFoundException("User not found");
        }
        userRepo.deleteById(userId);
    }
}

