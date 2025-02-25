package com.office.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.mapping.StatementType;
import com.office.entity.Message;

@Mapper
public interface MessageMapper {
    @Select("CALL send_message(#{senderId}, #{receiverId}, #{deptId}, #{title}, #{content}, #{messageId, mode=OUT, jdbcType=INTEGER})")
    @Options(statementType = StatementType.CALLABLE)
    void sendMessage(Message message);
    
    @Select("CALL mark_message_read(#{messageId}, #{userId})")
    @Options(statementType = StatementType.CALLABLE)
    void markMessageRead(@Param("messageId") Integer messageId, @Param("userId") Integer userId);
    
    // ... 其他方法
} 