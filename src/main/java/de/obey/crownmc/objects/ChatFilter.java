package de.obey.crownmc.objects;
/*

    Author - EntixOG -> SkySlayer-v4
       31.11.2022 / 15:05

*/

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatFilter {

    UUID uniqueId;
    String lastMessage;
    long chatThrottle;

    public boolean hasLastMessage() {
        return !lastMessage.isEmpty();
    }

    public boolean hasChatThrottle() {
        return chatThrottle != -1L;
    }
}