package org.example.sport_section.front.Views.UserBookings;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.example.sport_section.DTO.BookingDTO;
import org.example.sport_section.Models.User;
import org.example.sport_section.Services.CourtService.BookingCourtService;
import org.example.sport_section.Services.UserService.UserService;
import org.example.sport_section.Utils.Security.SecurityUtils;
import org.example.sport_section.front.Views.Home.HomePage;
import org.example.sport_section.front.Views.Sidebar;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.example.sport_section.front.Views.Sidebar.createSidebarView;


@Route("bookings")
public class Bookings extends HorizontalLayout {
    //private Integer userId;
    private UserService userService;
    private BookingCourtService bookingCourtService;
    private final Div loadingSpinner = createLoadingSpinner();

    @Autowired
    public Bookings(UserService userService, BookingCourtService bookingCourtService) {
        this.userService = userService;
        this.bookingCourtService = bookingCourtService;
        add(loadingSpinner);
        setPadding(false);
        getStyle().set("background-color", "#F2F3F4");
        getStyle().setHeight("auto");
        String email = SecurityUtils.getCurrentUserEmail();
        User user = userService.getUserAsync(email).join();
        List<BookingDTO> bookings = bookingCourtService.getBookingViewsForUserAsync(user.getId()).join();
        UI.getCurrent().access(() -> {
            addSidebar();
            add(loadContent(bookings));
            remove(loadingSpinner);
        });

    }

    private VerticalLayout loadContent(List<BookingDTO> bookings) {
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
        VerticalLayout sidebar = createSidebarView(Bookings.class, UI.getCurrent());
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

    private HorizontalLayout createCourtCard(BookingDTO booking) {
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

        Text date = new Text(booking.getDate().toString() + "  ");
        Text hour = new Text(booking.getHour() + ":00  ");
        Text number = new Text(booking.getCourtName());
        card.add(date, hour, number);

        ZonedDateTime moscowTime = ZonedDateTime.now(ZoneId.of("Europe/Moscow"));
        Instant moscowInstant = moscowTime.toInstant();
        int currentMoscowHour = moscowTime.getHour();
        Date currentMoscowDate = Date.from(moscowInstant);
        if (currentMoscowDate.before(booking.getDate()) ||
                (currentMoscowDate.equals(booking.getDate()) && currentMoscowHour < booking.getHour())) {
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
    private void cancelBooking(BookingDTO booking) {
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
            bookingCourtService.deleteBookingAsync(booking.getBookingId()).join();
            UI.getCurrent().access(() -> {
                Notification.show("Ваше бронирование отменено", 3000, Notification.Position.MIDDLE);
                dialog.close();
                UI.getCurrent().getPage().reload();
            });
        });

        Button cancelButton = new Button("Нет");
        cancelButton.addClickListener(event -> {
            dialog.close(); // Закрываем диалоговое окно
            UI.getCurrent().navigate(Bookings.class);
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
