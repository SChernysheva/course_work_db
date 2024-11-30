package org.example.sport_section.Services.GroupService;

import org.example.sport_section.Exceptions.NotFoundException;
import org.example.sport_section.Exceptions.ValueAlreadyExistsException;
import org.example.sport_section.Models.Groups.AgeGroup;
import org.example.sport_section.Models.Groups.Group;
import org.example.sport_section.Models.Groups.LevelGroup;
import org.example.sport_section.Repositories.AgeGroupRepository;
import org.example.sport_section.Repositories.GroupRepository.GroupRepository;
import org.example.sport_section.Repositories.LevelGroupRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Service
public class GroupService {
    private GroupRepository groupRepository;
    private AgeGroupRepository ageGroupRepository;
    private LevelGroupRepository levelGroupRepository;

    public GroupService(GroupRepository groupRepository, AgeGroupRepository ageGroupRepository,
                        LevelGroupRepository levelGroupRepository) {
        this.ageGroupRepository = ageGroupRepository;
        this.levelGroupRepository = levelGroupRepository;
        this.groupRepository = groupRepository;
    }

    @Async
    public CompletableFuture<List<Group>> getGroups() {
        return CompletableFuture.supplyAsync(() -> groupRepository.findAll());
    }

    @Async
    public CompletableFuture<List<AgeGroup>> getAgeGroups() {
        return CompletableFuture.supplyAsync(() -> ageGroupRepository.findAll());
    }

    @Async
    public CompletableFuture<List<LevelGroup>> getLevelGroups() {
        return CompletableFuture.supplyAsync(() -> levelGroupRepository.findAll());
    }

    @Async
    public CompletableFuture<Void> editCoach(int groupId, int coachId) {
        System.out.println("тренеру " + coachId + " группу " + groupId);
        return CompletableFuture.runAsync(() -> groupRepository.editCoach(groupId, coachId));
    }

    @Async
    public CompletableFuture<Group> addGroup(Group group) {
        return CompletableFuture.supplyAsync(() ->  groupRepository.save(group))
        .handle((result, ex) -> {
            if (ex != null) {
                throw new CompletionException(new ValueAlreadyExistsException("Группа с таким именем уже существует"));
            }
            return result;
        });
    }
}
