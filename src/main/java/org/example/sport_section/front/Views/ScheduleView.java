package org.example.sport_section.front.Views;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.example.sport_section.Models.Groups.Schedule;
import org.example.sport_section.Models.Users.User;
import org.example.sport_section.Services.ScheduleService.ScheduleService;
import org.example.sport_section.Services.UserService.UserService;
import org.example.sport_section.Utils.Security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalTime;
import java.util.List;

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
        user = userService.getUserAsync(email).join().get();
        List<Schedule> schedules = scheduleService.getAllSchedules().join();
        add(createSidebarView(ScheduleView.class, UI.getCurrent(), user));
        VerticalLayout layout = new VerticalLayout();
        for (Schedule schedule : schedules) {
            layout.add(addScheduleEntry(schedule));
        }
        add(layout);
    }

    private HorizontalLayout addScheduleEntry(Schedule entry) {
        HorizontalLayout entryLayout = new HorizontalLayout();
        entryLayout.addClassName("schedule-entry");
        entryLayout.setWidthFull();
        entryLayout.setPadding(true);
        entryLayout.setSpacing(true);

        Span dayOfWeek = new Span(entry.getWeekday().getWeekday());
        Span time = new Span(entry.getTime().toString());
        Span group = new Span(entry.getGroup().getName());
        Span court = new Span(entry.getCourt().getCourtName());

        dayOfWeek.addClassName("entry-day");
        time.addClassName("entry-time");
        group.addClassName("entry-group");
        court.addClassName("entry-court");

        entryLayout.add(dayOfWeek, time, group, court);

        return entryLayout;
    }


}
