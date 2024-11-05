package org.example.sport_section.front.Views.Schedule;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.example.sport_section.Models.Groups.Schedule;
import org.example.sport_section.Models.Users.User;
import org.example.sport_section.Models.Weekday.Weekday;
import org.example.sport_section.Services.ScheduleService.ScheduleService;
import org.example.sport_section.Services.UserService.UserService;
import org.example.sport_section.Utils.Security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import static org.example.sport_section.front.Views.Sidebar.createSidebarView;

@Route("schedule")
@PageTitle("Расписание занятий")
//@CssImport("./styles/styles.css")
public class ScheduleView extends HorizontalLayout {
    private final ScheduleService scheduleService;
    private final UserService userService;
    private User user;

    @Autowired
    public ScheduleView(ScheduleService scheduleService, UserService userService) {
        this.scheduleService = scheduleService;
        this.userService = userService;
        addClassName("schedule-view");
        setSizeFull();

        String email = SecurityUtils.getCurrentUserEmail();
        Optional<User> userOpt = userService.getUserAsync(email).join();
        if (!userOpt.isPresent()) {
            UI.getCurrent().access(() -> {
                Notification.show("Что-то пошло не так, попробуйте авторизоваться еще раз", 5000, Notification.Position.MIDDLE);
                SecurityUtils.deleteAuth();
            });
        }
        User user = userOpt.get();
        List<Weekday> weekdays = scheduleService.getAllWeekdaysWithSchedules().join();

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        mainLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        add(createSidebarView(ScheduleView.class, UI.getCurrent(), user));

        HorizontalLayout weekLayout = new HorizontalLayout();
        weekLayout.setWidth("80%");
        weekLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        for (Weekday weekday : weekdays) {
            VerticalLayout weekdayColumn = new VerticalLayout();
            weekdayColumn.addClassName("weekday-column");
            weekdayColumn.setWidth("100%");

            // Создание Span с отступом сверху
            Span weekdayTitle = new Span(weekday.getWeekday());
            weekdayTitle.getStyle().set("font-weight", "bold");
            weekdayTitle.getStyle().set("text-decoration", "underline");
            weekdayTitle.getStyle().set("margin-top", "10px"); // Устанавливаем отступ сверху

            weekdayColumn.add(weekdayTitle);

            List<Schedule> schedules = weekday.getSchedules();
            for (Schedule schedule : schedules) {
                weekdayColumn.add(createScheduleCard(schedule));
            }

            weekLayout.add(weekdayColumn);
        }

        mainLayout.add(weekLayout);
        add(mainLayout);
        if (SecurityUtils.isAdminOrHigher()) {
            VerticalLayout vl = new VerticalLayout();
            vl.add(addScheduleButton());
            vl.add(addScheduleAddingButton());
            add(vl);
        }
    }

    private VerticalLayout addWeekdayEntry(Weekday entry) {
        VerticalLayout entryLayout = new VerticalLayout();
        entryLayout.addClassName("schedule-entry");
        entryLayout.setWidthFull();
        entryLayout.setPadding(true);
        entryLayout.setSpacing(true);
        Span text = new Span(entry.getWeekday());
        // Применяем стили для жирного и подчеркнутого текста
        text.getStyle().set("font-weight", "bold");
        text.getStyle().set("text-decoration", "underline");

        entryLayout.add(text);
        entryLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        entryLayout.setHorizontalComponentAlignment(Alignment.CENTER);
        List<Schedule> schedules = entry.getSchedules();
        for (Schedule schedule : schedules) {
            entryLayout.add(createScheduleCard(schedule));
        }

        return entryLayout;
    }
    private VerticalLayout createScheduleCard(Schedule schedule) {
        VerticalLayout card = new VerticalLayout();
        card.getStyle().set("background-color", "#FFFFFF")
                .set("padding", "15px")
                .set("border-radius", "10px")
                .set("box-shadow", "0px 4px 8px rgba(0, 0, 0, 0.1)") // Увеличиваем тень для более выраженного эффекта
                .set("margin", "10px 0") // Добавляем отступы между карточками
                .set("color", "#333"); // Возможно, лучше выбрать немного более светлый цвет текста для контраста с фоном
        card.setWidth("auto");
        card.setHeight("auto"); // Делаем высоту автоматической, чтобы текст не обрезался
        card.setPadding(true); // Устанавливаем отступы внутри карточки
        card.setAlignItems(FlexComponent.Alignment.CENTER); // Центрируем элементы по вертикали
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        Text group = new Text(schedule.getGroup().getName() + "  ");
        Text hour = new Text(sdf.format(schedule.getTime()) + "  ");
        Text court = new Text(schedule.getCourt().getCourtName());
        card.add(group, hour, court);
        if (SecurityUtils.isAdminOrHigher()) {
            Button delete = getDeleteButton(schedule.getId());
            card.add(delete);
        }

        return card;
    }

    private Button addScheduleButton() {
        Button schedule = new Button("Расписание кортов сегодня");
        schedule.addClickListener(e -> {
            UI.getCurrent().navigate(ScheduleCourtsAdmin.class);
        });
        // Установка стилей для кнопки
        schedule.getStyle().set("background-color", "#d3d3d3"); // Светло-серый цвет фона
        schedule.getStyle().set("color", "#333333"); // Темно-серый цвет текста
        schedule.getStyle().set("border", "none"); // Убирает стандартную обводку
        schedule.getStyle().set("border-radius", "5px"); // Закругленные углы
        schedule.getStyle().set("padding", "10px 20px"); // Внутренние отступы для увеличенного размера
        schedule.getStyle().set("font-size", "16px"); // Размер текста
        schedule.getStyle().set("cursor", "pointer"); // Изменение курсора на указатель при наведении

// Установка эффекта при наведении
        schedule.getElement().getThemeList().add("primary");
        schedule.getStyle().set("transition", "background-color 0.3s"); // Плавный переход
        schedule.addFocusListener(e -> schedule.getStyle().set("background-color", "#c0c0c0"));
        schedule.addBlurListener(e -> schedule.getStyle().set("background-color", "#d3d3d3"));
        return schedule;
    }

    private Button addScheduleAddingButton() {
        Button schedule = new Button("Добавить новый слот");
        schedule.addClickListener(e -> {
            UI.getCurrent().navigate(AddScheduleGroupAdmin.class);
        });
        // Установка стилей для кнопки
        schedule.getStyle().set("background-color", "#d3d3d3"); // Светло-серый цвет фона
        schedule.getStyle().set("color", "#333333"); // Темно-серый цвет текста
        schedule.getStyle().set("border", "none"); // Убирает стандартную обводку
        schedule.getStyle().set("border-radius", "5px"); // Закругленные углы
        schedule.getStyle().set("padding", "10px 20px"); // Внутренние отступы для увеличенного размера
        schedule.getStyle().set("font-size", "16px"); // Размер текста
        schedule.getStyle().set("cursor", "pointer"); // Изменение курсора на указатель при наведении

// Установка эффекта при наведении
        schedule.getElement().getThemeList().add("primary");
        schedule.getStyle().set("transition", "background-color 0.3s"); // Плавный переход
        schedule.addFocusListener(e -> schedule.getStyle().set("background-color", "#c0c0c0"));
        schedule.addBlurListener(e -> schedule.getStyle().set("background-color", "#d3d3d3"));
        return schedule;
    }

    private void confirm(int id) {
        // Создаём диалоговое окно
        Dialog dialog = new Dialog();

        Text text = new Text("Удалить слот?");
        Button proveButton = new Button("Удалить слот?");
        proveButton.getStyle().set("background-color", "lightgray");
        proveButton.getStyle().set("color", "white");
        proveButton.addClickListener(event -> {
            UI.getCurrent().access(() -> {
                Notification.show("Выполняется..");
            });
            scheduleService.deleteSchedule(id).join();
            UI.getCurrent().getPage().reload();
        });

        Button cancelButton = new Button("Отмена");
        cancelButton.addClickListener(event -> {
            dialog.close(); // Закрываем диалоговое окно
        });

        VerticalLayout layout = new VerticalLayout(text, proveButton, cancelButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER); // Выравнивание по центру
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER); // Вертикальное выравнивание по центру
        layout.setSizeFull(); // Занять всю доступную область

        dialog.add(layout); // Добавляем вёрстку в диалоговое окно
        dialog.setWidth("400px"); // Настройка ширины диалогового окна
        dialog.setHeight("200px"); // Настройка высоты диалогового окна
        dialog.open(); // Открываем диалоговое окно
    }
    private Button getDeleteButton(int id) {
        Button delete = new Button("Del");
        delete.getStyle().set("background-color", "#f08080"); // Нежно-красный цвет
        delete.getStyle().set("color", "#ffffff"); // Белый цвет текста
        delete.getStyle().set("border", "none");
        delete.getStyle().set("border-radius", "3px"); // Слегка скругленные углы
        delete.getStyle().set("padding", "5px 10px"); // Меньше отступы, чтобы кнопка была маленькой
        delete.getStyle().set("font-size", "12px"); // Меньший размер шрифта
        delete.getStyle().set("cursor", "pointer");
        delete.addClickListener( e -> {
            confirm(id);
        });
        return delete;
    }


}
