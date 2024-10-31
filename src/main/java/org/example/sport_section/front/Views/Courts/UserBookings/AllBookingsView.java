package org.example.sport_section.front.Views.Courts.UserBookings;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.example.sport_section.Models.Courts.Booking_court;
import org.example.sport_section.Models.Users.User;
import org.example.sport_section.Services.CourtService.BookingCourtService;
import org.example.sport_section.Services.UserService.UserService;
import org.example.sport_section.Utils.Security.SecurityUtils;
import org.example.sport_section.front.Views.Home.HomePage;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionException;

import static org.example.sport_section.front.Views.Sidebar.createSidebarView;


@Route("bookings")
public class AllBookingsView extends HorizontalLayout {
    //private Integer userId;
    private UserService userService;
    private BookingCourtService bookingCourtService;
    private final Div loadingSpinner = createLoadingSpinner();

    @Autowired
    public AllBookingsView(UserService userService, BookingCourtService bookingCourtService) {
        this.userService = userService;
        this.bookingCourtService = bookingCourtService;
        add(loadingSpinner);
        setPadding(false);
        getStyle().set("background-color", "#F2F3F4");
        getStyle().setHeight("auto");
        String email = SecurityUtils.getCurrentUserEmail();
        Optional<User> userOpt = userService.getUserAsync(email).join();
        if (!userOpt.isPresent()) {
            //todo
        }
        User user = userOpt.get();
        List<Booking_court> bookings = bookingCourtService.getBookingsForUserAsync(user.getId()).join();
        bookings.sort(Comparator.comparing(Booking_court::getDate).thenComparing(Booking_court::getTime).reversed());
        UI.getCurrent().access(() -> {
            addSidebar();
            add(loadContent(bookings));
            remove(loadingSpinner);
        });

    }

    private VerticalLayout loadContent(List<Booking_court> bookings) {
        VerticalLayout layout = new VerticalLayout();
        //layout.setAlignItems(Alignment.CENTER);
        //layout.setJustifyContentMode(JustifyContentMode.CENTER);
        layout.setWidthFull();
        layout.getStyle().set("background-color", "#F2F3F4");
        if (bookings.isEmpty()) {
            layout.setHeightFull();
            Text text = new Text("У вас пока нет бронирований");
            layout.add(text);
            setHeightFull();
            return layout;
        }
        if (bookings.size() < 5) {
            setHeightFull();
        }
        for (var booking : bookings) {
            layout.add(createCourtCard(booking));
        }
        layout.setPadding(false);
        return layout;
    }

    private void addSidebar() {
        // Создаем и добавляем боковую панель
        VerticalLayout sidebar = createSidebarView(AllBookingsView.class, UI.getCurrent());
        String email = SecurityUtils.getCurrentUserEmail();
        sidebar.add(createSidebarViewUser(email));
        sidebar.add(getExitButton());
        add(sidebar);
    }


    private Div createLoadingSpinner() {
        Div spinner = new Div();
        spinner.add(new Span("Loading..."));
        spinner.getStyle().set("display", "flex");
        spinner.getStyle().set("width", "100%");
        spinner.getStyle().set("height", "100%");

        spinner.getStyle().set("align-items", "center");
        spinner.getStyle().set("justify-content", "center");
        spinner.getStyle().set("font-size", "24px");
        spinner.getStyle().set("font-weight", "bold");
        return spinner;
    }

    private HorizontalLayout createCourtCard(Booking_court booking) {
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
        Text date = new Text(booking.getDate().toString() + "  ");
        Text hour = new Text(sdf.format(booking.getTime()) + "  ");
        Text number = new Text(booking.getCourt().getCourtName());
        card.add(date, hour, number);

        Date nowDate = java.sql.Date.valueOf(LocalDate.now());
        Time nowTime = Time.valueOf(LocalTime.now());
        if (nowDate.before(booking.getDate()) ||
                (nowDate.equals(booking.getDate()) && nowTime.before(booking.getTime()))) {
            Button cancelButtton = new Button("Отменить бронирование");
            cancelButtton.addClickListener(e -> {
                cancelBooking(booking);
            });
            card.add(cancelButtton);
        }

        return card;
    }

    private VerticalLayout createSidebarViewUser(String userEmail) {
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
        sidebar.setAlignItems(Alignment.CENTER); // Центрирование по вертикали
        sidebar.setWidth("200px"); // Задание ширины боковой панели

        return sidebar;
    }

    private Button getExitButton() {
        Button exitButton = new Button("Выйти");

        exitButton.getStyle().set("background-color", "orange");
        exitButton.getStyle().set("color", "white");
        exitButton.getStyle().set("font-size", "12px");
        exitButton.getStyle().set("border", "none");
        exitButton.getStyle().set("padding", "5px 10px");

        exitButton.addClickListener(event -> {
            exit();
        });
        return exitButton;
    }
    private void exit() {
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
            // Можем (необязательно) добавить логику возврата на домашнюю страницу
            UI.getCurrent().navigate(HomePage.class);
        });

        VerticalLayout layout = new VerticalLayout(text, proveButton, cancelButton);
        layout.setAlignItems(Alignment.CENTER); // Выравнивание по центру
        layout.setJustifyContentMode(JustifyContentMode.CENTER); // Вертикальное выравнивание по центру
        layout.setSizeFull(); // Занять всю доступную область

        dialog.add(layout); // Добавляем вёрстку в диалоговое окно
        dialog.setWidth("400px"); // Настройка ширины диалогового окна
        dialog.setHeight("200px"); // Настройка высоты диалогового окна
        dialog.open(); // Открываем диалоговое окно
    }
    private void cancelBooking(Booking_court booking) {
        // Создаём диалоговое окно
        Dialog dialog = new Dialog();

        Text text = new Text("Отменить бронирование?");
        Button proveButton = new Button("Да");
        proveButton.getStyle().set("background-color", "lightgray");
        proveButton.getStyle().set("color", "white");
        proveButton.addClickListener(event -> {
            //cancel
            UI.getCurrent().access(() -> {
                Notification.show("Выполняется отмена бронирования", 1500, Notification.Position.MIDDLE);
            });
            try {
                bookingCourtService.deleteBookingAsync(booking.getId()).join();
                UI.getCurrent().access(() -> {
                    Notification.show("Ваше бронирование отменено", 3000, Notification.Position.MIDDLE);
                    dialog.close();
                    UI.getCurrent().getPage().reload();
                });
            } catch (CompletionException ex) {
                UI.getCurrent().access(() -> {
                    Notification.show("Ошибка: такого бронирования нет");
                });
                dialog.close();
            }
        });

        Button cancelButton = new Button("Нет");
        cancelButton.addClickListener(event -> {
            dialog.close(); // Закрываем диалоговое окно
            UI.getCurrent().navigate(AllBookingsView.class);
        });

        VerticalLayout layout = new VerticalLayout(text, proveButton, cancelButton);
        layout.setAlignItems(Alignment.CENTER); // Выравнивание по центру
        layout.setJustifyContentMode(JustifyContentMode.CENTER); // Вертикальное выравнивание по центру
        layout.setSizeFull(); // Занять всю доступную область

        dialog.add(layout); // Добавляем вёрстку в диалоговое окно
        dialog.setWidth("400px"); // Настройка ширины диалогового окна
        dialog.setHeight("200px"); // Настройка высоты диалогового окна
        dialog.open(); // Открываем диалоговое окно
    }

}
