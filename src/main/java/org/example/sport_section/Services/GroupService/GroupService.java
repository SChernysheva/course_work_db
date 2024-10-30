package org.example.sport_section.Services.GroupService;

import org.example.sport_section.Models.Group;
import org.example.sport_section.Repositories.GroupRepository.GroupRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class GroupService {
    private GroupRepository groupRepository;
    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Async
    public CompletableFuture<List<Group>> getGroups() {
        return CompletableFuture.supplyAsync(() -> groupRepository.findAll());
    }
}
