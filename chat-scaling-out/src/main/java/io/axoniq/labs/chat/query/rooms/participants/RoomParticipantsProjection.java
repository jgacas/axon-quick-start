package io.axoniq.labs.chat.query.rooms.participants;

import io.axoniq.labs.chat.coreapi.ParticipantJoinedRoomEvent;
import io.axoniq.labs.chat.coreapi.ParticipantLeftRoomEvent;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Profile("query")
@RestController
@RequestMapping("/rooms/{roomId}/participants")
@ProcessingGroup("participants")
public class RoomParticipantsProjection {

    private final RoomParticipantsRepository repository;

    public RoomParticipantsProjection(RoomParticipantsRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<String> participantsInRoom(@PathVariable String roomId) {
        return repository.findRoomParticipantsByRoomId(roomId)
                         .stream()
                         .map(RoomParticipant::getParticipant).sorted().collect(toList());
    }

    @EventHandler
    public void on(ParticipantJoinedRoomEvent event) {
        repository.save(new RoomParticipant(event.getRoomId(), event.getParticipant()));
    }

    @EventHandler
    public void on(ParticipantLeftRoomEvent event) {
        repository.deleteByParticipantAndRoomId(event.getParticipant(), event.getRoomId());
    }
}
