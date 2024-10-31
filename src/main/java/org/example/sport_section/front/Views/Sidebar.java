package org.example.sport_section.front.Views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.example.sport_section.Utils.Security.SecurityUtils;
import org.example.sport_section.front.Views.UsersViews.AllUsersView;
import org.example.sport_section.front.Views.Home.HomePage;
import org.example.sport_section.front.Views.Courts.ManageBookings.ManageBookingsView;
import org.example.sport_section.front.Views.Courts.UserBookings.AllBookingsView;
import org.example.sport_section.front.Views.UsersViews.CoachesView;

public class Sidebar {

    public static VerticalLayout createSidebarView(Class page, UI ui) {
        VerticalLayout sidebar = new VerticalLayout();
        sidebar.setWidth("250px");

        // Изменяем цвет фона на чуть более темный и закругляем углы
        sidebar.getStyle()
                .set("background-color", "#F2F3F4")
                .set("border-radius", "10px")        // Закругляем углы
                .set("padding", "15px");             // Добавляем внутренние отступы для эстетики

        // Добавляем кнопки для навигации
        Button coachesButton = new Button("Наши тренеры");
        Button bookingsButton = new Button("Мои бронирования");
        bookingsButton.getStyle().set("background-color", "#FFFFFF")
                .set("padding", "10px")
                .set("border-radius", "8px")
                .set("box-shadow", "0px 2px 4px rgba(0, 0, 0, 0.1)")
                .set("color", "black");
        bookingsButton.addClickListener(e -> {
            ui.navigate(AllBookingsView.class);
        });
        coachesButton.addClickListener(event ->
                ui.navigate(HomePage.class));
        coachesButton.getStyle().set("background-color", "#FFFFFF")
                .set("padding", "10px")
                .set("border-radius", "8px")
                .set("box-shadow", "0px 2px 4px rgba(0, 0, 0, 0.1)")
                .set("color", "black");
        coachesButton.addClickListener(event ->
                ui.navigate(AllBookingsView.class));
        coachesButton.getStyle().set("background-color", "#FFFFFF")
                .set("padding", "10px")
                .set("border-radius", "8px")
                .set("box-shadow", "0px 2px 4px rgba(0, 0, 0, 0.1)")
                .set("color", "black");

        Button scheduleButton = new Button("Расписание занятий");
        scheduleButton.addClickListener(event ->
                ui.navigate(HomePage.class));
        scheduleButton.getStyle().set("background-color", "#FFFFFF")
                .set("padding", "10px")
                .set("border-radius", "8px")
                .set("box-shadow", "0px 2px 4px rgba(0, 0, 0, 0.1)")
                .set("color", "black");

        Button homeButton = new Button("Забронировать корт");
        homeButton.addClickListener(event ->
                ui.navigate(HomePage.class));
        homeButton.getStyle()
                .set("background-color", "#FFFFFF")
                .set("padding", "10px")
                .set("border-radius", "8px")
                .set("box-shadow", "0px 2px 4px rgba(0, 0, 0, 0.1)")
                .set("color", "black");

        sidebar.add(homeButton);
        sidebar.add(coachesButton);
        sidebar.add(scheduleButton);
        sidebar.add(bookingsButton);
        // Проверяем, является ли пользователь администратором
        if (isUserAdmin()) {
            Button manageBookingsButton = new Button("Все бронирования");
            manageBookingsButton.addClickListener(event ->
                    ui.navigate(ManageBookingsView.class)); // Предполагаем, что у вас есть класс ManageBookings

            manageBookingsButton.getStyle().set("background-color", "#FFFFFF")
                    .set("padding", "10px")
                    .set("border-radius", "8px")
                    .set("box-shadow", "0px 2px 4px rgba(0, 0, 0, 0.1)")
                    .set("color", "black");

            sidebar.add(manageBookingsButton);
            if (page.equals(ManageBookingsView.class)) {
                manageBookingsButton.getStyle().set("background-color", "#E8E8E8");
            }
            Button allUsers = new Button("Все пользователи");
            allUsers.addClickListener(event ->
                    ui.navigate(AllUsersView.class)); // Предполагаем, что у вас есть класс ManageBookings

            allUsers.getStyle().set("background-color", "#FFFFFF")
                    .set("padding", "10px")
                    .set("border-radius", "8px")
                    .set("box-shadow", "0px 2px 4px rgba(0, 0, 0, 0.1)")
                    .set("color", "black");

            if (page.equals(AllUsersView.class)) {
                allUsers.getStyle().set("background-color", "#E8E8E8");
            }
            sidebar.add(manageBookingsButton, allUsers);
        }
        if (page.equals(HomePage.class)) {
            homeButton.getStyle().set("background-color", "#E8E8E8");
        }
        if (page.equals(AllBookingsView.class)) {
            bookingsButton.getStyle().set("background-color", "#E8E8E8");
        }
        if (page.equals(CoachesView.class)) {
            coachesButton.getStyle().set("background-color", "#E8E8E8");
        }
        if (page.equals(ScheduleView.class)) {
            scheduleButton.getStyle().set("background-color", "#E8E8E8");
        }


        return sidebar;
    }
    private static boolean isUserAdmin() {
        return SecurityUtils.isAdmin();
    }
}
