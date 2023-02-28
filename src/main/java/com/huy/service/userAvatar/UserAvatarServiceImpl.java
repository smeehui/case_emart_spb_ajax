package com.huy.service.userAvatar;

import com.huy.model.UserAvatar;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class UserAvatarServiceImpl implements IUserAvatarService {


    @Override
    public List<UserAvatar> findAll() {
        return null;
    }

    @Override
    public Optional<UserAvatar> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public UserAvatar save(UserAvatar userAvatar) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public void delete(UserAvatar userAvatar) {

    }
}
