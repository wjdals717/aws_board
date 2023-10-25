package com.korit.board.repository;

import com.korit.board.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    public Integer saveUser(User user);
    public int checkDuplicate(User user);
    public User findUserByEmail(String email);
    public User findUserByOauth2Id(String oauth2Id);    //oauth2Id로 DB에서 찾음
    public int updateEnabledToEmail(String email);
    public int updateProfileUrl(User user);
    public int updatePassword(User user);
    public int updateOauth2IdAndProvider(User user);
}
