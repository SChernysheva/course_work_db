package org.example.sport_section.front.Views.Schedule;

import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.example.sport_section.Models.Courts.Court;
import org.example.sport_section.Models.Groups.Group;
import org.example.sport_section.Models.Groups.Schedule;
import org.example.sport_section.Models.Users.Coach;
import org.example.sport_section.Models.Weekday.Weekday;
import org.example.sport_section.Services.CoachService.CoachService;
import org.example.sport_section.Services.CourtService.CourtService;
import org.example.sport_section.Services.GroupService.GroupService;
import org.example.sport_section.Services.ScheduleService.ScheduleService;
import org.example.sport_section.Services.UserService.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Time;
import java.util.List;

@Route("admin/addSchedule")
public class AddScheduleGroupAdmin extends VerticalLayout {
    private UserService userService;
    private CourtService courtService;
    private GroupService groupService;
    private ScheduleService scheduleService;
    private CoachService coachService;

    @Autowired
    public AddScheduleGroupAdmin(UserService userService, CourtService courtService, GroupService groupService,
                                 ScheduleService scheduleService, CoachService coachService) {
        this.userService = userService;
        this.courtService = courtService;
        this.groupService = groupService;
        this.scheduleService = scheduleService;
        this.coachService = coachService;
        setSizeFull();

        List<Coach> coaches = coachService.getCoaches().join();
        List<Court> courts = courtService.getCourtsAsync().join();
        List<Group> groups = groupService.getGroups().join();
        List<Weekday> weekdays = scheduleService.getAllWeekdays().join();

        getStyle().set("background-color", "#f5f5f5");

        VerticalLayout layout = new VerticalLayout();
        layout.getStyle().set("background-color", "white");
        layout.getStyle().set("border-radius", "10px");
        layout.setSizeFull();
        layout.setAlignItems(Alignment.CENTER);
        layout.setHorizontalComponentAlignment(Alignment.CENTER);
        layout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        // Создаем выпадающие списки
        ComboBox<Group> groupComboBox = new ComboBox<>("Выберите группу");
        groupComboBox.setItems(groups);
        groupComboBox.setItemLabelGenerator(Group::getName);

        ComboBox<Coach> coachComboBox = new ComboBox<>("Выберите тренера");
        coachComboBox.setItems(coaches);

        coachComboBox.setItemLabelGenerator(getItemLabelForCoach());

        ComboBox<Weekday> weekdayComboBox = new ComboBox<>("Выберите день недели");
        weekdayComboBox.setItems(weekdays);
        weekdayComboBox.setItemLabelGenerator(Weekday::getWeekday);

        ComboBox<Court> courtComboBox = new ComboBox<>("Выберите корт");
        courtComboBox.setItems(courts);
        courtComboBox.setItemLabelGenerator(Court::getCourtName);

        // Компонент для выбора времени, который будет активирован после выбора параметров
        ComboBox<Time> timeComboBox = new ComboBox<>("Выберите время");
        timeComboBox.setEnabled(false);

        // Кнопка для генерации доступного времени
        Button generateTimeButton = new Button("Показать доступное время");
        generateTimeButton.getStyle().set("margin", "10px");

        generateTimeButton.addClickListener(e -> {
            Group selectedGroup = groupComboBox.getValue();
            Coach selectedCoach = coachComboBox.getValue();
            Weekday selectedWeekday = weekdayComboBox.getValue();
            Court selectedCourt = courtComboBox.getValue();

            if (selectedGroup != null && selectedCoach != null && selectedWeekday != null && selectedCourt != null) {
                // Предположим, что у вас есть метод для получения доступного времени на основе выбранных параметров
                List<Time> availableTimes = scheduleService.getAvailableTimeForNewSchedule(selectedWeekday.getId(),
                        selectedCourt.getId()).join();
                if (!availableTimes.isEmpty()) {
                    timeComboBox.setItems(availableTimes);
                    timeComboBox.setEnabled(true);
                    Notification.show("Пожалуйста, выберите доступное время.");
                } else {
                    Notification.show("Нет доступного времени для выбранных параметров.");
                }
            } else {
                Notification.show("Пожалуйста, выберите все предварительные опции.");
            }
        });

        Button okButton = getButton("OK");
        okButton.setEnabled(false);
        Button backButton = getButton("Назад");
        backButton.addClickListener(e -> {
            UI.getCurrent().navigate("/schedule");
        });
        timeComboBox.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                okButton.setEnabled(true);
            } else {
                okButton.setEnabled(false);
            }
        });
        okButton.addClickListener(e -> {
            Time selectedTime = timeComboBox.getValue();
            Group selectedGroup = groupComboBox.getValue();
            Coach selectedCoach = coachComboBox.getValue();
            Weekday selectedWeekday = weekdayComboBox.getValue();
            Court selectedCourt = courtComboBox.getValue();
            if (selectedTime != null) {
                selectedGroup.setCoach(selectedCoach);
                UI.getCurrent().access( () -> {
                    Notification.show("Выполняется..", 2000, Notification.Position.MIDDLE);
                });
                scheduleService.addSchedule(new Schedule(selectedTime, selectedGroup, selectedCourt, selectedWeekday)).join();
                UI.getCurrent().access( () -> {
                    Notification.show("Готово!", 2000, Notification.Position.MIDDLE);
                });
            }
        });

        layout.add(groupComboBox, coachComboBox, weekdayComboBox, courtComboBox, generateTimeButton, timeComboBox,
                okButton, backButton);
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

