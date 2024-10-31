package org.example.sport_section.front.Views;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinSession;
import org.example.sport_section.Models.Users.User;
import org.example.sport_section.Utils.Security.SecurityUtils;
import org.example.sport_section.front.Views.UsersViews.AllUsersView;
import org.example.sport_section.front.Views.Home.HomePage;
import org.example.sport_section.front.Views.Courts.ManageBookings.ManageBookingsView;
import org.example.sport_section.front.Views.Courts.UserBookings.AllBookingsView;
import org.example.sport_section.front.Views.UsersViews.CoachesView;

public class Sidebar {

    public static VerticalLayout createSidebarView(Class page, UI ui, User user) {
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
                ui.navigate(ScheduleView.class));
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

        Button update = new Button("Изменить мои контакты");
        update.getStyle().set("background-color", "#888888")  // Серый фон кнопки
                .set("color", "#ffffff")               // Белый текст
                .set("border", "none")                 // Убираем рамку
                .set("padding", "10px 20px")           // Отступы
                .set("border-radius", "5px");
        update.addClickListener(event -> {
            updateUser(user.getId(), ui);
        });
        sidebar.add(createSidebarViewUser(user.getEmail()));
        sidebar.add(update);
        sidebar.add(getExitButton(ui));


        return sidebar;
    }

    public static void updateUser(int userId, UI ui) {
        ui.navigate("user/update/" + userId);
    }
    private static Button getExitButton(UI ui) {
        Button exitButton = new Button("Выйти");

        exitButton.getStyle().set("background-color", "orange");
        exitButton.getStyle().set("color", "white");
        exitButton.getStyle().set("font-size", "12px");
        exitButton.getStyle().set("border", "none");
        exitButton.getStyle().set("padding", "5px 10px");

        exitButton.addClickListener(event -> {
            exit(ui);
        });
        return exitButton;
    }
    private static void exit(UI ui) {
        // Создаём диалоговое окно
        Dialog dialog = new Dialog();

        Text text = new Text("Вы точно хотите выйти?");
        Button proveButton = new Button("Выйти");
        proveButton.getStyle().set("background-color", "lightgray");
        proveButton.getStyle().set("color", "white");
        proveButton.addClickListener(event -> {
            SecurityUtils.deleteAuth();
            VaadinSession.getCurrent().close(); // Закройте текущую сессию
            // UI.getCurrent().navigate(StartPage.class);
        });

        Button cancelButton = new Button("Отмена");
        cancelButton.addClickListener(event -> {
            dialog.close(); // Закрываем диалоговое окно
            ui.navigate(HomePage.class);
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
    private static VerticalLayout createSidebarViewUser(String userEmail) {
        VerticalLayout sidebar = new VerticalLayout();
        // Создание контейнера с информацией о пользователе
        Div userContainer = new Div();
        userContainer.getStyle().set("background-color", "lightgray");
        userContainer.getStyle().set("padding", "5px");

        // Добавление текста с информацией о пользователе
        Span userText = new Span("Пользователь: " + userEmail);
        userContainer.add(userText);

        // Добавление контейнера с информацией о пользователе на боковую панель
        sidebar.add(userContainer);
        sidebar.setAlignItems(FlexComponent.Alignment.CENTER); // Центрирование по вертикали
        sidebar.setWidth("200px"); // Задание ширины боковой панели

        return sidebar;
    }

    private static boolean isUserAdmin() {
        return SecurityUtils.isAdmin();
    }
}
