package org.example.sport_section.front.Views;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
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
import org.example.sport_section.front.Views.Authorize.StartPage;
import org.example.sport_section.front.Views.Home.HomePage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;

import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Route("courts/info/")
public class CourtPageInfo extends HorizontalLayout {
    private final Div loadingSpinner = createLoadingSpinner();
    private final WebClient webClient;

    @Autowired
    public CourtPageInfo(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080/api").build();
        setSizeFull();
        setSpacing(false);
        add(loadingSpinner);
        Court currentCourt = (Court) VaadinSession.getCurrent().getAttribute("court");
        Div div = getDiv(currentCourt.getId());
        remove(loadingSpinner);
        div.setSizeFull();
        add(div);
        UI.getCurrent().setPollInterval(500);
    }
    public Div getDiv(long courtId) {
        Div div = new Div();
        DatePicker datePicker = new DatePicker("Выберите дату");
        Dialog timeDialog = new Dialog();
        VerticalLayout timeLayout = new VerticalLayout();
        timeDialog.add(timeLayout);

        final LocalDate[] selectedDate = new LocalDate[1];
        final Integer[] selectedHour = new Integer[1];

        datePicker.addValueChangeListener(event -> {
            selectedDate[0] = event.getValue();
            if (selectedDate[0] != null) {
                List<Integer> availableHours = getAvailableHours(selectedDate[0], courtId);
                timeLayout.removeAll();
                timeLayout.add(new Text("Выберите доступное время для " + selectedDate[0]));

                for (Integer hour : availableHours) {
                    Button timeButton = new Button(hour + ":00", e -> {
                        selectedHour[0] = hour;
                        Button bookButton = new Button("Забронировать", bookEvent -> {
                            if (selectedDate[0] != null && selectedHour[0] != null) {
                                bookCourt(selectedDate[0], selectedHour[0], courtId);
                                timeDialog.close();
                            } else {

                            }
                        });
                        timeLayout.add(bookButton);
                    });
                    timeLayout.add(timeButton);
                }

                timeDialog.open();
            }
        });
        div.add(datePicker);
        return div;
    }

    private void bookCourt(LocalDate date, int hour, long courtId) {
        String bookingUrl = "/courts/addBooking" + "?date=" + date + "&hour=" + hour + "&courtId=" + courtId;
        webClient.post()
                .uri(bookingUrl)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe(response -> {
                    // Обработка успешного ответа
                    UI.getCurrent().access(() -> {
                        Notification.show("Бронирование успешно создано!");
                        UI.getCurrent().navigate(HomePage.class);
                    });
                }, error -> {
                    // Обработка ошибки
                    UI.getCurrent().access(() -> {
                        Notification.show("Ошибка при создании бронирования.");
                    });
                });
    }


    List<Integer> getAvailableHours(LocalDate date, long courtId) {
        List<Integer> availableHours = new ArrayList<>();
        for (int i = 7; i <= 22; i++) {
            availableHours.add(i);
        }
        String url = "/courts/getBooking" + "?id=" + courtId + "&date=" + date;
        webClient.get().uri(url).retrieve().bodyToMono(List.class).subscribe(hours -> {
            List<Integer> bookingHours = hours;
            for (int hour : bookingHours) {
                if (availableHours.contains(hour)) {
                    availableHours.remove(hour);
                }
            }
        });
        return availableHours;
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
