package org.example.sport_section.front.Views.Schedule;

import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.example.sport_section.Models.Courts.Court;
import org.example.sport_section.Models.Groups.AgeGroup;
import org.example.sport_section.Models.Groups.Group;
import org.example.sport_section.Models.Groups.LevelGroup;
import org.example.sport_section.Models.Groups.Schedule;
import org.example.sport_section.Models.Users.Coach;
import org.example.sport_section.Models.Weekday.Weekday;
import org.example.sport_section.Services.CoachService.CoachService;
import org.example.sport_section.Services.CourtService.CourtService;
import org.example.sport_section.Services.GroupService.GroupService;
import org.example.sport_section.Services.ScheduleService.ScheduleService;
import org.example.sport_section.Services.UserService.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.textfield.TextField;

import java.awt.*;
import java.sql.Time;
import java.util.List;
import java.util.concurrent.CompletionException;

@Route("admin/addGroup")
public class AddNewGroupAdminView extends VerticalLayout {
    private GroupService groupService;
    private CoachService coachService;

    @Autowired
    public AddNewGroupAdminView(GroupService groupService, CoachService coachService) {
        this.groupService = groupService;
        this.coachService = coachService;
        setSizeFull();
        List<Coach> coaches = coachService.getCoaches().join();
        List<AgeGroup> ageGroups = groupService.getAgeGroups().join();
        List<LevelGroup> levelGroups = groupService.getLevelGroups().join();

        getStyle().set("background-color", "#f5f5f5");

        VerticalLayout layout = new VerticalLayout();
        layout.getStyle().set("background-color", "white");
        layout.getStyle().set("border-radius", "10px");
        layout.setSizeFull();
        layout.setAlignItems(Alignment.CENTER);
        layout.setHorizontalComponentAlignment(Alignment.CENTER);
        layout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        TextField groupName = new TextField("Название группы");


        // Создаем выпадающие списки
        ComboBox<Coach> coachComboBox = new ComboBox<>("Выберите тренера для группы");
        coachComboBox.setItems(coaches);
        coachComboBox.setItemLabelGenerator(getItemLabelForCoach());

        ComboBox<AgeGroup> ageGroup = new ComboBox<>("Выберите возрастную категорию для группы");
        ageGroup.setItems(ageGroups);
        ageGroup.setItemLabelGenerator(getItemLabelForAgeGroup());

        ComboBox<LevelGroup> levelGroup = new ComboBox<>("Выберите уровень для группы");
        levelGroup.setItems(levelGroups);
        levelGroup.setItemLabelGenerator(getItemLabelForLevelGroup());

        com.vaadin.flow.component.button.Button okButton = getButton("OK");
        Button backButton = getButton("Назад");
        backButton.addClickListener(e -> {
            UI.getCurrent().navigate("/schedule");
        });
        okButton.addClickListener(e -> {
            Coach selectedCoach = coachComboBox.getValue();
            AgeGroup selectedAge = ageGroup.getValue();
            LevelGroup selectedLevel = levelGroup.getValue();
            if (groupName.getValue() != null && selectedLevel != null && selectedAge != null) {
                Group group = new Group();
                group.setCoach(selectedCoach);
                group.setAgeGroup(selectedAge);
                group.setName(groupName.getValue());
                group.setLevelGroup(selectedLevel);

                UI.getCurrent().access( () -> {
                    Notification.show("Выполняется..", 2000, Notification.Position.MIDDLE);
                });
                try {
                    groupService.addGroup(group).join();
                    UI.getCurrent().access( () -> {
                        Notification.show("Готово!", 2000, Notification.Position.MIDDLE);
                    });
                } catch (CompletionException ex) {
                    UI.getCurrent().access( () -> {
                        Notification.show("Ошибка, такое название группы уже существует", 2000, Notification.Position.MIDDLE);
                    });
                }
            } else {
                UI.getCurrent().access( () -> {
                    Notification.show("Пожалуйста, заполните все поля", 2000, Notification.Position.MIDDLE);
                });
            }
        });

        layout.add(groupName, coachComboBox, ageGroup, levelGroup, okButton, backButton);
        add(layout);


    }
    ItemLabelGenerator<Coach> getItemLabelForCoach() {
        ItemLabelGenerator<Coach> generator = new ItemLabelGenerator<Coach>() {
            @Override
            public String apply(Coach coach) {
                return coach.getUser().getLast_name() + " " + coach.getUser().getFirst_name();
            }
        };
        return generator;
    }
    ItemLabelGenerator<AgeGroup> getItemLabelForAgeGroup() {
        ItemLabelGenerator<AgeGroup> generator = new ItemLabelGenerator<AgeGroup>() {
            @Override
            public String apply(AgeGroup ageGroup) {
                return ageGroup.getAgeGroup();
            }
        };
        return generator;
    }
    ItemLabelGenerator<LevelGroup> getItemLabelForLevelGroup() {
        ItemLabelGenerator<LevelGroup> generator = new ItemLabelGenerator<LevelGroup>() {
            @Override
            public String apply(LevelGroup group) {
                return group.getLevelGroup();
            }
        };
        return generator;
    }

    private Button getButton(String text) {
        // Кнопка для подтверждения выбора времени
        Button okButton = new Button(text);
        okButton.getStyle().set("background-color", "#d3d3d3");
        okButton.getStyle().set("color", "#333333");
        okButton.getStyle().set("border", "none");
        okButton.getStyle().set("border-radius", "5px");
        okButton.getStyle().set("padding", "10px 20px");
        okButton.getStyle().set("font-size", "16px");
        okButton.getStyle().set("cursor", "pointer");

        return okButton;
    }
}
