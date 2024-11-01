package org.example.sport_section.front.Views.Courts;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.example.sport_section.DTO.CourtBookingDTO;
import org.example.sport_section.Models.Courts.Court;
import org.example.sport_section.Models.Groups.Schedule;
import org.example.sport_section.Models.Weekday.Weekday;
import org.example.sport_section.Services.CourtService.BookingCourtService;
import org.example.sport_section.Services.CourtService.CourtService;
import org.example.sport_section.front.Views.ScheduleView;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Route("admin/scheduleCourts")
public class ScheduleCourtsAdmin extends VerticalLayout {
    private final CourtService courtService;
    private final BookingCourtService bookingCourtService;

    @Autowired
    public ScheduleCourtsAdmin(CourtService courtService, BookingCourtService bookingCourtService) {
        this.courtService = courtService;
        this.bookingCourtService = bookingCourtService;
        add(addbackButton());
        HorizontalLayout mainLayout = new HorizontalLayout();
        setAlignItems(Alignment.CENTER);
        setHorizontalComponentAlignment(Alignment.CENTER, mainLayout);
        getStyle().set("background-color", "#f5f5f5");
        mainLayout.getStyle().set("background-color", "white");
        mainLayout.getStyle().set("border-radius", "10px"); // Закругленные края
        mainLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        List<Court> courts = courtService.getCourtsAsync().join();
        for (Court court : courts) {
            VerticalLayout courtColumn = new VerticalLayout();
            courtColumn.addClassName("weekday-column");
            courtColumn.setWidth("100%");
            // Создание Span с отступом сверху
            Span courtTitle = new Span(court.getCourtName());
            courtTitle.getStyle().set("font-weight", "bold");
            courtTitle.getStyle().set("text-decoration", "underline");
            courtTitle.getStyle().set("margin-top", "10px"); // Устанавливаем отступ сверху

            courtColumn.add(courtTitle);

            List<CourtBookingDTO> schedules = bookingCourtService.getScheduleCourtOnDate(court.getId(), LocalDate.now()).join();
            for (CourtBookingDTO schedule : schedules) {
                courtColumn.add(createScheduleCard(schedule));
            }
            mainLayout.add(courtColumn);
        }
        add(mainLayout);
    }
    private HorizontalLayout createScheduleCard(CourtBookingDTO schedule) {
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

        if (schedule.getGroup() != null) {
            Text group = new Text(schedule.getGroup().getName() + "  ");
            card.add(group);
        } else {
            Text user = new Text(schedule.getUser().getEmail() + "  ");
            card.add(user);
        }
        Text hour = new Text(sdf.format(schedule.getTime()) + "  ");
        Text court = new Text(schedule.getCourt().getCourtName());
        card.add(hour, court);

        return card;
    }

    private Button addbackButton() {
        Button backButton = new Button("Назад");
        backButton.addClickListener(e -> {
            UI.getCurrent().navigate(ScheduleView.class);
        });
        // Установка стилей для кнопки
        backButton.getStyle().set("background-color", "#d3d3d3"); // Светло-серый цвет фона
        backButton.getStyle().set("color", "#333333"); // Темно-серый цвет текста
        backButton.getStyle().set("border", "none"); // Убирает стандартную обводку
        backButton.getStyle().set("border-radius", "5px"); // Закругленные углы
        backButton.getStyle().set("padding", "10px 20px"); // Внутренние отступы для увеличенного размера
        backButton.getStyle().set("font-size", "16px"); // Размер текста
        backButton.getStyle().set("cursor", "pointer"); // Изменение курсора на указатель при наведении

// Установка эффекта при наведении
        backButton.getElement().getThemeList().add("primary");
        backButton.getStyle().set("transition", "background-color 0.3s"); // Плавный переход
        backButton.addFocusListener(e -> backButton.getStyle().set("background-color", "#c0c0c0"));
        backButton.addBlurListener(e -> backButton.getStyle().set("background-color", "#d3d3d3"));
        return backButton;
    }

}
