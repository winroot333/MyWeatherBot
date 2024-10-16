package jdbc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class User {
    private int id;
    private String name;
    private long chatId;
    private long telegramUserId;
    private int status;
    
}
