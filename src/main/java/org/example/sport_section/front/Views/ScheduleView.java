package org.example.sport_section.front.Views;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.example.sport_section.Models.Courts.Booking_court;
import org.example.sport_section.Models.Groups.Schedule;
import org.example.sport_section.Models.Users.User;
import org.example.sport_section.Models.Weekday.Weekday;
import org.example.sport_section.Services.ScheduleService.ScheduleService;
import org.example.sport_section.Services.UserService.UserService;
import org.example.sport_section.Utils.Security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
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

        weekdays.sort(Comparator.comparingInt(Weekday::getNumOfDay));

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
    private HorizontalLayout createScheduleCard(Schedule schedule) {
        HorizontalLayout card = new HorizontalLayout();
        card.getStyle().set("background-color", "#FFFFFF")
                .set("padding", "15px")
                .set("border-radius", "10px")
                .set("box-shadow", "0px 4px 8px rgba(0, 0, 0, 0.1)") // Увеличиваем тень для более выраженного эффекта
                .set("margin", "10px 0") // Добавляем отступы между карточками
                .set("color", "#333"); // Возможно, лучше выбрать немного более светлый цвет текста для контраста с фоном
        card.setWidth("90%");
        card.setHeight("150px"); // Делаем высоту автоматической, чтобы текст не обрезался
        card.setPadding(true); // Устанавливаем отступы внутри карточки
        card.setAlignItems(FlexComponent.Alignment.CENTER); // Центрируем элементы по вертикали
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        Text group = new Text(schedule.getGroup().getName() + "  ");
        Text hour = new Text(sdf.format(schedule.getTime()) + "  ");
        Text court = new Text(schedule.getCourt().getCourtName());
        card.add(group, hour, court);

        return card;
    }


}
