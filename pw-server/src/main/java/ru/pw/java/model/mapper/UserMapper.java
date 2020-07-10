package ru.pw.java.model.mapper;

import org.mapstruct.Mapper;
import ru.pw.java.model.dto.UserDto;
import ru.pw.java.tables.pojos.Users;

@Mapper
public interface UserMapper {

    UserDto toUser(Users user);

    Users toUser(UserDto userDto);
}
