package org.example.sport_section.front.Views.Courts.ManageBookings;

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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.example.sport_section.Models.Courts.Booking_court;
import org.example.sport_section.Models.Users.User;
import org.example.sport_section.Services.CourtService.BookingCourtService;
import org.example.sport_section.Services.UserService.UserService;
import org.example.sport_section.Utils.Security.SecurityUtils;
import org.example.sport_section.front.Views.Home.HomePage;
import org.example.sport_section.front.Views.Courts.UserBookings.AddBookingView;
import org.example.sport_section.front.Views.Courts.UserBookings.AllBookingsView;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletionException;

import static org.example.sport_section.front.Views.Sidebar.createSidebarView;

@PageTitle("Управление бронированиями")
@Route("admin/manageBookings")
public class ManageBookingsView extends HorizontalLayout {
    private UserService userService;
    private BookingCourtService bookingCourtService;
    private final Div loadingSpinner = createLoadingSpinner();
    private User user;

    @Autowired
    public ManageBookingsView(UserService userService, BookingCourtService bookingCourtService) {
        this.userService = userService;
        this.bookingCourtService = bookingCourtService;
        add(loadingSpinner);
        setPadding(false);
        String email = SecurityUtils.getCurrentUserEmail();
        user = userService.getUserAsync(email).join().get();
        getStyle().set("background-color", "#F2F3F4");
        getStyle().setHeight("auto");
        List<Booking_court> bookings = bookingCourtService.getBookings().join();
        bookings.sort(Comparator.comparing(Booking_court::getDate).thenComparing(Booking_court::getTime).reversed());
        UI.getCurrent().access(() -> {
            add(createSidebarView(ManageBookingsView.class, UI.getCurrent(), user));
            remove(loadingSpinner);
            add(loadContent(bookings));
        });
    }

    private VerticalLayout loadContent(List<Booking_court> bookings) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.getStyle().set("background-color", "#F2F3F4");
        if (bookings.isEmpty()) {
            layout.setHeightFull();
            Text text = new Text("пока нет бронирований");
            layout.add(text);
            setHeightFull();
            return layout;
        }
        if (bookings.size() < 5) {
            setHeightFull();
        }
        Button button = new Button("Добавить бронирование");
        button.addClickListener(e -> {
            UI.getCurrent().navigate(AddBookingView.class);
        });
        layout.add(button);
        for (var booking : bookings) {
            layout.add(createCourtCard(booking));
        }
        layout.setPadding(false);
        return layout;
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
        Text number = new Text(booking.getCourt().getCourtName() + "  ");
        Text user = new Text(booking.getUser().getFirst_name() + " " + booking.getUser().getLast_name() + "  ");
        Text email = new Text(booking.getUser().getEmail() + "  ");
        Text phone = new Text(booking.getUser().getPhone());
        card.add(date, hour, number, user, email, phone);

        Time nowTime = Time.valueOf(LocalTime.now());
        Date nowDate = Date.valueOf(LocalDate.now());

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
