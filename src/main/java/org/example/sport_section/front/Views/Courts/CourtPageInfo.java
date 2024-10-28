package org.example.sport_section.front.Views.Courts;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.example.sport_section.Models.Court;
import org.example.sport_section.Models.User;
import org.example.sport_section.Services.CourtService.BookingCourtService;
import org.example.sport_section.Services.UserService.UserService;
import org.example.sport_section.Utils.Security.SecurityUtils;
import org.example.sport_section.front.Views.Home.HomePage;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Route("courts/info/")
public class CourtPageInfo extends HorizontalLayout {
    private final Div loadingSpinner = createLoadingSpinner();
    private final BookingCourtService courtService;
    private final UserService userService;
    private final BookingCourtService bookingCourtService;

    @Autowired
    public CourtPageInfo(BookingCourtService courtService, UserService userService, BookingCourtService bookingCourtService) {
        System.out.println("court page");
        this.courtService = courtService;
        this.userService = userService;
        setSizeFull();
        //setSpacing(false);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        add(loadingSpinner);
        Text text = new Text("Выберите удобную дату и время");
        Court currentCourt = (Court) VaadinSession.getCurrent().getAttribute("court");
        String email = SecurityUtils.getCurrentUserEmail();
        System.out.println("email court: " + email);
        Div div = getDiv(currentCourt.getId(), email);
        remove(loadingSpinner);
        add(text, div);
        this.bookingCourtService = bookingCourtService;
    }
    public Div getDiv(int courtId, String email) {
        Div div = new Div();
        DatePicker datePicker = getDatePicker(courtId, email);
        div.add(datePicker);
        return div;
    }

    public DatePicker getDatePicker(int courtId, String email) {
        DatePicker datePicker = new DatePicker();
        Dialog timeDialog = new Dialog();
        VerticalLayout timeLayout = new VerticalLayout();
        timeDialog.add(timeLayout);

        final LocalDate[] selectedDate = new LocalDate[1];
        final Integer[] selectedHour = new Integer[1];

        datePicker.setMin(LocalDate.now());  // Установить минимальную дату на сегодняшний день

        datePicker.addValueChangeListener(event -> {
            selectedDate[0] = event.getValue();
            if (selectedDate[0] != null) {
                List<Integer> aviableHours = getAviableHours(courtId, selectedDate[0]);

                UI.getCurrent().access(() -> {
                    timeLayout.removeAll();
                    timeLayout.add(new Text("Выберите доступное время для " + selectedDate[0]));
                    for (Integer hour : aviableHours) {
                        Button timeButton = new Button(hour + ":00");
                        timeButton.addClickListener(e -> {
                            selectedHour[0] = hour;
                            Notification.show("Выбрано " + hour + ":00",  3000, Notification.Position.MIDDLE);
                            timeLayout.getChildren()
                                    .filter(component -> component instanceof Button)
                                    .map(component -> (Button) component)
                                    .forEach(button -> {
                                        button.getStyle().remove("background-color");
                                        button.getStyle().remove("color");
                                    });
                            // Установить стиль для выбранной кнопки
                            timeButton.getStyle().set("background-color", "#808080");
                            timeButton.getStyle().set("color", "white");
                        });
                        timeLayout.add(timeButton);
                    }

                    Button bookButton = new Button("Забронировать", bookEvent -> {
                        if (selectedDate[0] != null && selectedHour[0] != null) {
                            Notification.show("Выполняется бронирование " + selectedDate[0] +  " " + selectedHour[0] + ":00", 3000, Notification.Position.MIDDLE);
                            System.out.println("email: " + email);
                            User user = getUser(email);
                            try {
                                bookCourt(selectedDate[0], selectedHour[0], courtId, user.getId());
                            } catch (SQLException e) {
                                //todo
                            }
                            Notification.show("Бронирование успешно создано!",  3000, Notification.Position.MIDDLE);
                            UI.getCurrent().navigate(HomePage.class);
                            timeDialog.close();
                        }
                    });
                    Button cancelButton = new Button("Отмена", bookEvent -> {
                        timeDialog.close();
                    });
                    timeLayout.add(bookButton);
                    timeLayout.add(cancelButton);
                    timeDialog.open();
                });
            } else {
                Notification.show("Выберите время", 3000, Notification.Position.MIDDLE);
            }
        });
        return datePicker;
    }

    private List<Integer> getAviableHours(int courtId, LocalDate date) {
        List<Integer> availableHours = new ArrayList<>();
        if (LocalDate.now().isAfter(date)) {
            //todo
        }
        int startHour = 7;
        if (date.isEqual(LocalDate.now())) {
            LocalTime currentTime = LocalTime.now();
            startHour = currentTime.getHour() + 1;
        }
        List<Integer> bookingHours = bookingCourtService.getBookingTimeForCourtAsync(courtId, date).join();
        for (int i = startHour; i <= 22; i++) {
            if (!bookingHours.contains(i)) {
                availableHours.add(i);
            }
        }
        return availableHours;

    }

    private void bookCourt(LocalDate date, int hour, int courtId, int id) throws SQLException {
        courtService.addBookingTimeForCourt(courtId, date, hour, id).join();
    }

    private User getUser(String email) {
        return userService.getUserAsync(email).join();
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
}
