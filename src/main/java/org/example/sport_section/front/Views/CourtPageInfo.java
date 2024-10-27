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
import java.time.LocalTime;
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
        //setSpacing(false);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        add(loadingSpinner);
        Text text = new Text("Выберите удобную дату и время");
        Court currentCourt = (Court) VaadinSession.getCurrent().getAttribute("court");
        Div div = getDiv(currentCourt.getId());
        remove(loadingSpinner);
        add(text, div);
    }
    public Div getDiv(long courtId) {
        Div div = new Div();
        DatePicker datePicker = getDatePicker(courtId);
        div.add(datePicker);
        return div;
    }

    public DatePicker getDatePicker(Long courtId) {
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
                String url = "/courts/getAvailableTime" + "?id=" + courtId + "&date=" + selectedDate[0];
                UI ui = UI.getCurrent();
                webClient.get().uri(url).retrieve().bodyToMono(List.class).subscribe(hours -> {
                    // Использование UI.access() для безопасного выполнения всех UI операций
                    ui.access(() -> {
                        List<Integer> aviableHours = (List<Integer>) hours;
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
                                bookCourt(selectedDate[0], selectedHour[0], courtId, ui);
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
                });
            } else {
                Notification.show("Выберите время", 3000, Notification.Position.MIDDLE);
            }
        });
        return datePicker;
    }


    private void bookCourt(LocalDate date, int hour, long courtId, UI ui) {
        String bookingUrl = "/courts/addBooking" + "?date=" + date + "&hour=" + hour + "&courtId=" + courtId;
        webClient.post()
                .uri(bookingUrl)
                .retrieve()
                .bodyToMono(Long.class)
                .subscribe(response -> {
                    // Обработка успешного ответа
                    ui.access(() -> {
                        Notification.show("Бронирование успешно создано!",  3000, Notification.Position.MIDDLE);
                        ui.navigate(HomePage.class);
                    });
                }, error -> {
                    // Обработка ошибки
                    ui.access(() -> {
                        Notification.show("Ошибка при создании бронирования.");
                    });
                });
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
